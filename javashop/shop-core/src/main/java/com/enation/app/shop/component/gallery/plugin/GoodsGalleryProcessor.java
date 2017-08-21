package com.enation.app.shop.component.gallery.plugin;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tools.ant.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.ClusterSetting;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.IClusterFileManager;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.jms.IJmsProcessor;
import com.enation.framework.util.StringUtil;

/**
 * 商品相册JMS处理器
 * @author kingapex
 *
 */
@Component
public class GoodsGalleryProcessor implements IJmsProcessor {

	@Autowired
	private ISettingService settingService ;

	@Autowired
	private IGoodsGalleryManager goodsGalleryManager;

	@Autowired
	private IDaoSupport daoSupport;

	/**
	 * 获取是否开启集群fastDFS，来寻找管理类 
	 * zh
	 */
	private static IClusterFileManager getClusterFileManager(){
		if(ClusterSetting.getFdfs_open()==1){
			return (IClusterFileManager)SpringContextHolder.getBean("clusterFileManager");
		}
		return null;

	}


	/**
	 * 上传文件到文件集群
	 * @param relativePath	相对路径，如fs://attachment/a.jpg
	 * @return
	 */
	private static String uploadToCluster(String relativePath){
		IClusterFileManager fileManager = getClusterFileManager();
		if(fileManager == null)
			return relativePath;
		String filePath = relativePath;
		if(relativePath.startsWith(EopSetting.FILE_STORE_PREFIX)){
			filePath = StringUtils.replace(relativePath, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_path());
		}
		String[] filePaths = relativePath.split("\\/");
		InputStream stream=null;
		try {
			stream=new URL(filePath).openStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileManager.upload(stream, filePaths[filePaths.length-1], relativePath);
	}

	private String getSettingValue(String code){
		return  settingService.getSetting("photo", code);
	}

	private void createThumb(String filepath, String targetpath, int pic_width, int pic_height) {
		try {
			this.goodsGalleryManager.createThumb(filepath, targetpath, pic_width, pic_height);
		} catch (Exception e) {
			Logger logger = Logger.getLogger(getClass());
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void process(Object data) {

		/**
		 * 各种图片尺寸的大小
		 */
		int tiny_pic_width = 60;
		int tiny_pic_height = 60;
		int thumbnail_pic_width = 180;
		int thumbnail_pic_height = 180;
		int small_pic_width = 460;
		int small_pic_height = 460;
		int big_pic_width = 800;
		int big_pic_height = 800;

		String temp = getSettingValue("tiny_pic_width");
		tiny_pic_width = temp == null ? tiny_pic_width : StringUtil.toInt(temp,	true);

		temp = getSettingValue("tiny_pic_height");
		tiny_pic_height = temp == null ? tiny_pic_height : StringUtil.toInt(temp, true);

		temp = getSettingValue("thumbnail_pic_width");
		thumbnail_pic_width = temp == null ? thumbnail_pic_width : StringUtil.toInt(temp, true);

		temp = getSettingValue("thumbnail_pic_height");
		thumbnail_pic_height = temp == null ? thumbnail_pic_height : StringUtil.toInt(temp, true);		

		temp = getSettingValue("small_pic_width");
		small_pic_width = temp == null ? small_pic_width : StringUtil.toInt(temp, true);

		temp = getSettingValue("small_pic_height");
		small_pic_height = temp == null ? small_pic_height : StringUtil.toInt(temp, true);

		temp = getSettingValue("big_pic_width");
		big_pic_width = temp == null ? big_pic_width : StringUtil.toInt(temp, true);

		temp = getSettingValue("big_pic_height");
		big_pic_height = temp == null ? big_pic_height : StringUtil.toInt(temp,	true);	


		Map<String, Object> param = (Map) data;
		List<GoodsGallery> list = (List<GoodsGallery>) param.get("galleryList"); // 相册列表
		Map goods = (Map) param.get("goods"); // 本商品
		int goodsid = StringUtil.toInt(goods.get("goods_id").toString(), true);

		// 数据库中的相册
		List<GoodsGallery> dbList = this.goodsGalleryManager.list(goodsid);

		GoodsGallery defaultGallery = null;

		int sort = 0;

		// 生成所有的相册图片的各种规格图片
		for (GoodsGallery gallery : list) {

			// 排序
			sort++;
			gallery.setSort(sort);

			//原始图片
			String filepath = gallery.getOriginal();
			gallery.setOriginal(transformPath(filepath));


			// 寻找默认图片
			if (gallery.getIsdefault() == 1) {
				defaultGallery = gallery;
			}

			// 检测是否已经在数据库中，如果是则不再操作
			if (this.checkInDb(gallery.getOriginal(), dbList)) {
				this.goodsGalleryManager.updateSort(transformPath(filepath), sort);
				continue;
			}
			//截取图片后缀
			String suffix=gallery.getOriginal().substring(gallery.getOriginal().lastIndexOf("."), gallery.getOriginal().length());
			if(ClusterSetting.getFdfs_open()==1){
				//集群环境下生成大图 小图 缩略图等
				//生成大图
				gallery.setBig(gallery.getOriginal()+"_"+big_pic_width+"x"+big_pic_height+suffix);
				// 生成小图
				gallery.setSmall(gallery.getOriginal()+"_"+small_pic_width+"x"+small_pic_height+suffix);
				// 生成缩略图
				gallery.setThumbnail(gallery.getOriginal()+"_"+thumbnail_pic_width+"x"+thumbnail_pic_height+suffix);
				//生成小图
				gallery.setTiny(gallery.getOriginal()+"_"+tiny_pic_width+"x"+tiny_pic_height+suffix);
				
			}else{
				//处理原图
				gallery.setOriginal(uploadToCluster(gallery.getOriginal()));
				// 生成大图
				String targetpath = gallery.getBig();
				createThumb(filepath, targetpath, big_pic_width, big_pic_height);
				targetpath = transformPath(targetpath);
				gallery.setBig(uploadToCluster(targetpath));

				// 生成小图
				targetpath = gallery.getSmall();
				createThumb(filepath, targetpath, small_pic_width, small_pic_height);
				targetpath = transformPath(targetpath);
				gallery.setSmall(uploadToCluster(targetpath));	 		

				// 生成缩略图
				targetpath = gallery.getThumbnail();
				createThumb(filepath, targetpath, thumbnail_pic_width, thumbnail_pic_height);
				targetpath = transformPath(targetpath);
				gallery.setThumbnail(uploadToCluster(targetpath));	 		


				//生成小图
				targetpath = gallery.getTiny();
				createThumb(filepath, targetpath, tiny_pic_width, tiny_pic_height);
				targetpath = transformPath(targetpath);
				gallery.setTiny(uploadToCluster(targetpath));
			}
			// 插入相册表
			gallery.setGoods_id(goodsid);
			this.goodsGalleryManager.add(gallery);	 		
		}

		// 更新商品默认图片信息
		if (defaultGallery != null) {
			String original= transformPath(defaultGallery.getOriginal());
			String big =transformPath(defaultGallery.getBig());
			String small = transformPath(defaultGallery.getSmall());
			String thumbnail = transformPath(defaultGallery.getThumbnail());
			if(ClusterSetting.getFdfs_open()==1){
				String suffix=original.substring(original.lastIndexOf("."), original.length());
				//大图
				big=big.replaceAll("_big", suffix+"_"+big_pic_width+"x"+big_pic_height);
				//小图
				small=small.replaceAll("_small", suffix+"_"+small_pic_width+"x"+small_pic_height);
				//缩略图
				thumbnail=thumbnail.replaceAll("_thumbnail", suffix+"_"+thumbnail_pic_width+"x"+thumbnail_pic_height);	
			}

			this.daoSupport.execute(
					"update es_goods set original=?,big=?,small=?,thumbnail=? where goods_id=?",
					original,
					big,
					small,
					thumbnail,
					goodsid);
			this.daoSupport.execute("update es_goods_gallery set isdefault=0 where goods_id=?", goodsid);
			this.daoSupport.execute("update es_goods_gallery set isdefault=1 where goods_id=? and original=?",	goodsid, transformPath(defaultGallery.getOriginal()));

			goods.put("original", original);
			goods.put("big", big);
			goods.put("small", small);
			goods.put("thumbnail", thumbnail);
		}
	}

	/**
	 * 检测某个图片是否已经存在
	 * 
	 * @param path
	 * @param dbList
	 * @return
	 */
	private boolean checkInDb(String path, List<GoodsGallery> dbList) {
		for (GoodsGallery gallery : dbList) {
			if (gallery.getOriginal().equals(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 页面中传递过来的图片地址为:http://<staticserver>/<image path>如:
	 * http://static.enationsoft.com/attachment/goods/1.jpg
	 * 存在库中的为fs:/attachment/goods/1.jpg 生成fs式路径
	 * 
	 * @param path
	 * @return
	 */
	private String transformPath(String path) {
		String static_server_domain= SystemSetting.getStatic_server_domain();

		String regx =static_server_domain;
		path = path.replace(regx, EopSetting.FILE_STORE_PREFIX);
		return path;
	}

	


}
