package com.enation.app.shop.component.gallery.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsSnapshotGalleryManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.StringUtil;
/**
 * 
	* @Description:快照相册 
	* @author zjp 
	* @time:2016年12月1日下午3:27:26
 */
@Service("goodsSnapshotGalleryManager")
public class GoodsSnapshotGalleryManager implements IGoodsSnapshotGalleryManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.gallery.service.IGoodsSnapshotGalleryManager#list(int)
	 */
	@Override
	public List<GoodsGallery> list(int snapshot_id) {
		List<GoodsGallery> result = this.daoSupport.queryForList("select gg.*,g.params from es_goods_snapshot_gallery gg left join es_goods_snapshot g on gg.snapshot_id=g.snapshot_id where g.snapshot_id = ? ORDER BY gg.sort", GoodsGallery.class, snapshot_id);
		for (GoodsGallery gallery : result) {
			if (!StringUtil.isEmpty(gallery.getOriginal()))
				gallery.setOriginal(gallery.getOriginal());
			if (!StringUtil.isEmpty(gallery.getBig()))
				gallery.setBig(StaticResourcesUtil.convertToUrl(gallery.getBig()));
			if (!StringUtil.isEmpty(gallery.getSmall()))
				gallery.setSmall(StaticResourcesUtil.convertToUrl(gallery.getSmall()));
			if (!StringUtil.isEmpty(gallery.getThumbnail()))
				gallery.setThumbnail(StaticResourcesUtil.convertToUrl(gallery.getThumbnail()));
			if (!StringUtil.isEmpty(gallery.getTiny()))
				gallery.setTiny(StaticResourcesUtil.convertToUrl(gallery.getTiny()));			
		}
		return result;
	}
}