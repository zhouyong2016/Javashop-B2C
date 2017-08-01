package com.enation.app.shop.component.gallery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.framework.component.IComponent;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.data.IDataOperation;

/**
 * 新的相册
 * @author lzf
 * 2012-10-19上午6:55:50
 * ver 1.0
 */
@Component
public class GalleryComponent implements IComponent {
	
	@Autowired
	private IDataOperation dataOperation;
	
	
	private IDaoSupport daoSupport;
	private IGoodsGalleryManager goodsGalleryManager;
	
	@Override
	public void install() {
		dataOperation.imported("file:com/enation/app/shop/component/gallery/gallery_install.xml");
	}

	@Override
	public void unInstall() {
		dataOperation.imported("file:com/enation/app/shop/component/gallery/gallery_install.xml");

	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

 

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}

	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}


}
