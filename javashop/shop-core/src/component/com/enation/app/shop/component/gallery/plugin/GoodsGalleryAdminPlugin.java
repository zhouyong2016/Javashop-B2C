package com.enation.app.shop.component.gallery.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.fdfs.FastdfsBundle;
import com.enation.app.base.core.service.ISettingService; 
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.goods.plugin.AbstractGoodsPlugin;
import com.enation.app.shop.core.goods.plugin.IGoodsDeleteEvent;
import com.enation.app.shop.core.goods.plugin.IGoodsTabShowEvent;
import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.jms.IJmsProcessor;
import com.enation.framework.util.StringUtil;


/**
 * 相册后台管理
 * @author enation
 *
 */
@Component
public class GoodsGalleryAdminPlugin extends AbstractGoodsPlugin implements	IGoodsDeleteEvent, IGoodsTabShowEvent {

	@Autowired
	private ISettingService settingService;
	@Autowired
	private IGoodsGalleryManager goodsGalleryManager;
	@Autowired
	private IJmsProcessor goodsGalleryProcessor;
	private String getSettingValue(String code) {
		return settingService.getSetting("photo", code);
	}
	
	/**
	 * 处理图片，包括商品新增时和修改时的图片数据<br/>
	 * 处理原则为向页面输出为全地址，保存在库里的为相对地址，以fs:开头，以区分网络远程图片。<br/>
	 * 在显示的时候将fs:替换为静态资源服务器地址 页面中传递过来的图片地址为:http://<staticserver>/<image path>如:<br/>
	 * http://static.enationsoft.com/attachment/goods/1.jpg<br/>
	 * 存在库中的为/attachment/goods/1.jpg
	 */
 
	protected void proessPhoto(String[] picnames, Map goods, String image_default) {
		if (picnames == null) {
			return;
		}		

		// 生成相册列表，待jsm处理器使用
		List<GoodsGallery> galleryList = new ArrayList<GoodsGallery>();

		for (int i = 0; i < picnames.length; i++) {
			GoodsGallery gallery = new GoodsGallery();

			String filepath = picnames[i];
			// 生成小缩略图
			String tiny = getThumbPath(filepath, "_tiny");
			// 生成缩略图
			String thumbnail = getThumbPath(filepath, "_thumbnail");
			// 生成小图
			String small = getThumbPath(filepath, "_small");
			// 生成大图
			String big = getThumbPath(filepath, "_big");

			gallery.setOriginal(filepath); // 相册原始路径
			gallery.setBig(big);
			gallery.setSmall(small);
			gallery.setThumbnail(thumbnail);
			gallery.setTiny(tiny);
			galleryList.add(gallery);
			
			//设置为默认图片
			if(!StringUtil.isEmpty(image_default) && image_default.equals(filepath)){
				gallery.setIsdefault(1);
			}
		}
		Map<String, Object> param = new HashMap<String, Object>(2);
		param.put("galleryList", galleryList);
		param.put("goods", goods);

		//改非异步的方式-2015-06-09
		this.goodsGalleryProcessor.process(param);
	}
	
	private String getThumbPath(String filePath, String shortName) {
		return StaticResourcesUtil.getThumbPath(filePath, shortName);
	}

	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {

	}

	public void onAfterGoodsAdd(Map goods, HttpServletRequest request) {
		
		String[] picnames = request.getParameterValues("picnames");
		String image_default = request.getParameter("image_default");
		
		// 如果没有默认图片 就用第一张做默认图片
		if ((image_default == null || "".equals(image_default)) && picnames != null && picnames.length > 0) {
			image_default = picnames[0];
		}
		
		proessPhoto(picnames, goods, image_default);
	}

	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request) {

	}

	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
		
		// 从StoreGoodsPlugin中移植过来的，兼容b2c和b2b2c
		String[] imgFs=request.getParameterValues("del_pic");
		if(imgFs!=null){
			for (int i = 0; i < imgFs.length; i++) {
				goodsGalleryManager.delete(imgFs[i]);
			}
		}
		
		String[] picnames = request.getParameterValues("picnames");
		String image_default = request.getParameter("image_default");
		
		// 如果没有默认图片 就用第一张做默认图片
		if ((image_default == null || "".equals(image_default)) && picnames != null && picnames.length > 0) {
			image_default = picnames[0];
		}
		proessPhoto(picnames, goods, image_default);
	}

	public String getEditHtml(Map goods, HttpServletRequest request) {
		String contextPath = request.getContextPath();
		// 设置需要传递到页面的数据
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();

		// 关于图片的处理
		String image_default = (String) goods.get("original");
		if (!StringUtil.isEmpty(image_default)) {
			image_default = StaticResourcesUtil.convertToUrl(image_default);
		}

		List<GoodsGallery> galleryList = goodsGalleryManager.list(Integer.valueOf(goods.get("goods_id").toString()));

		if (galleryList != null && galleryList.size() > 0) {
			for (GoodsGallery gallery : galleryList) {
				String image = gallery.getOriginal();
				if (!StringUtil.isEmpty(image)) {
					image = StaticResourcesUtil.convertToUrl(image);
					gallery.setOriginal(image);
				}
			}
		}
		freeMarkerPaser.putData("ctx", contextPath);
		freeMarkerPaser.putData("image_default", image_default);
		freeMarkerPaser.putData("thumbnail_images", galleryList);

		freeMarkerPaser.setPageName("album");
		String html = freeMarkerPaser.proessPageContent();
		return html;
	}

	public String getAddHtml(HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setPageName("album");
		freeMarkerPaser.putData("image_default", null);
		freeMarkerPaser.putData("thumbnail_images", null);
		String html = freeMarkerPaser.proessPageContent();
		return html;
	}
	
	/**
	 * 响应设置的保存事件，保存各种图片的大小
	 * @param request
	 * @return
	 */
	public Map<String, String> beforeSettingSave(HttpServletRequest request) {
		Map<String, String> settingMap = new HashMap<String, String>();

		String tiny_pic_width = request.getParameter("photo.tiny_pic_width");
		String tiny_pic_height = request.getParameter("photo.tiny_pic_height");
		String thumbnail_pic_width = request.getParameter("photo.thumbnail_pic_width");
		String thumbnail_pic_height = request.getParameter("photo.thumbnail_pic_height");
		String detail_pic_width = request.getParameter("photo.detail_pic_width");
		String detail_pic_height = request.getParameter("photo.detail_pic_height");
		String album_pic_height = request.getParameter("photo.album_pic_height");
		String album_pic_width = request.getParameter("photo.album_pic_width");

		settingMap.put("tiny_pic_width", tiny_pic_width);
		settingMap.put("tiny_pic_height", tiny_pic_height);
		settingMap.put("thumbnail_pic_width", thumbnail_pic_width);
		settingMap.put("thumbnail_pic_height", thumbnail_pic_height);

		settingMap.put("detail_pic_width", detail_pic_width);
		settingMap.put("detail_pic_height", detail_pic_height);

		settingMap.put("album_pic_height", album_pic_height);
		settingMap.put("album_pic_width", album_pic_width);

		return settingMap;
	}
 
	/**
	 * 响应商品删除事件
	 * 
	 * @param goodsid
	 */

	public void onGoodsDelete(Integer[] goodsid) {
		this.goodsGalleryManager.delete(goodsid);
	}

	public String getAuthor() {
		return "lzf";
	}

	public String getType() {
		return "";
	}

	public String getVersion() {
		return "2.0";
	}	

 

	public void perform(Object... params) {

	}

	@Override
	public String getTabName() {
		return "相册";
	}

	@Override
	public int getOrder() {
		return 3;
	}

	
	

}
