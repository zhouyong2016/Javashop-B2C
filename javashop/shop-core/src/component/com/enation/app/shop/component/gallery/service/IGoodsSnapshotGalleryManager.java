package com.enation.app.shop.component.gallery.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.enation.app.shop.component.gallery.model.GoodsGallery;

/**
 * 
	* @Description:快照相册 
	* @author zjp 
	* @time:2016年12月1日下午3:27:26
 */
@Component
public interface IGoodsSnapshotGalleryManager {
		
	/**
	 * 读取某个商品的快照相册
	 * @param goods_id
	 * @return
	 */
	public List<GoodsGallery> list(int goods_id);

}
