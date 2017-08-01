package com.enation.app.shop.front.tag.goods.snapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.component.gallery.service.IGoodsSnapshotGalleryManager;
import com.enation.eop.SystemSetting;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * (商品快照图片) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2016年12月12日 上午2:53:13
 */
@Component
@Scope("prototype")
public class GoodsSnapshotGalleryTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IGoodsSnapshotGalleryManager goodsSnapshotGalleryManager;
	
	/**
	 * 商品快照相册标签	
	 * 特殊说明：调用商品属性标签前，必须先调用商品基本信息标签
	 * @param 无
	 * @return 商品快照相册 ，类型：List<GoodsGallery>
	 * {@link GoodsGallery}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request  = this.getRequest();
		Map snapshot =(Map)request.getAttribute("goods");
		if(snapshot==null) throw new TemplateModelException("调用商品属性标签前，必须先调用商品基本信息标签");
		String default_img_url = SystemSetting.getDefault_img_url();
		List<GoodsGallery> galleryList = this.goodsSnapshotGalleryManager.list((Integer)snapshot.get("snapshot_id"));
		if(galleryList==null || galleryList.size()==0){
			
			galleryList=new ArrayList<GoodsGallery>();

			String img  =default_img_url;
			GoodsGallery gallery = new GoodsGallery();
			gallery.setSmall(img);
			gallery.setBig(img);
			gallery.setThumbnail(img);
			gallery.setTiny(img);
			gallery.setOriginal(img);
			gallery.setIsdefault(1);
			galleryList.add(gallery);
			
		}
		return galleryList;
	}


}
