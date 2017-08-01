package com.enation.app.shop.core.goods.service.batchimport.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.enation.app.shop.core.goods.model.ImportDataSource;
import com.enation.app.shop.core.goods.service.batchimport.IGoodsDataImporter;
import com.enation.app.shop.core.goods.service.batchimport.util.GoodsImageReader;
import com.enation.framework.database.IDaoSupport;

/**
 * 商品图片导入器
 * @author kingapex
 *
 */
public class GoodsImageImporter implements IGoodsDataImporter {
	protected final Logger logger = Logger.getLogger(getClass());
	private IDaoSupport baseDaoSupport;
	
	private GoodsImageReader goodsImageReader;
 
	public void imported(Object value, Element node, ImportDataSource importDs,
			Map goods) {
		Integer goodsid  = (Integer)goods.get("goods_id");
		String excludeStr = node.getAttribute("exclude");
		
		if(this.logger.isDebugEnabled()){
			logger.debug("开始导入商品["+goodsid+"]图片...");
		}
		
		String[] images= goodsImageReader.read(importDs.getDatafolder(), goodsid.toString(), excludeStr);
		if(images!=null)
		this.baseDaoSupport.execute("update goods set image_file=? ,image_default=? where goods_id=?", images[0],images[1],goodsid);
		
		if(this.logger.isDebugEnabled()){
			logger.debug(" 商品["+goodsid+"]图片导入完成");
		}
	}

	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}

	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}

	public GoodsImageReader getGoodsImageReader() {
		return goodsImageReader;
	}

	public void setGoodsImageReader(GoodsImageReader goodsImageReader) {
		this.goodsImageReader = goodsImageReader;
	}
	
	 
}
