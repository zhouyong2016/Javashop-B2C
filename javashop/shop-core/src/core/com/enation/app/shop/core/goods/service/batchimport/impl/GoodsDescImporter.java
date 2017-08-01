package com.enation.app.shop.core.goods.service.batchimport.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.enation.app.shop.core.goods.model.ImportDataSource;
import com.enation.app.shop.core.goods.service.batchimport.IGoodsDataImporter;
import com.enation.app.shop.core.goods.service.batchimport.util.GoodsDescReader;
import com.enation.framework.database.IDaoSupport;

/**
 * 商品描述导入器
 * @author kingapex
 *
 */
public class GoodsDescImporter implements IGoodsDataImporter {
	protected final Logger logger = Logger.getLogger(getClass());
	private IDaoSupport daoSupport;
	private IDaoSupport baseDaoSupport;
	private GoodsDescReader goodsDescReader;
	
	@Override
	public void imported(Object value, Element node, ImportDataSource importDs,
			Map goods) {
		
		Integer goodsid  = (Integer )goods.get("goods_id");
		
		
		if(this.logger.isDebugEnabled()){
			logger.debug("开始导入商品["+goodsid+"]描述...");
		}		
		
		String bodyhtml = goodsDescReader.read(importDs.getDatafolder(), goodsid.toString());
	 
		if(bodyhtml!=null){
			this.baseDaoSupport.execute("update goods set intro=? where goods_id=?", bodyhtml,goodsid);
		}
		 
		 
		if(this.logger.isDebugEnabled()){
			logger.debug("导入商品["+goodsid+"]描述 完成");
		}		
		
	}
	
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

 	
	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}

	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}

	public GoodsDescReader getGoodsDescReader() {
		return goodsDescReader;
	}

	public void setGoodsDescReader(GoodsDescReader goodsDescReader) {
		this.goodsDescReader = goodsDescReader;
	}

	public static void main(String[] args){
		//System.out.println(FileUtil.read("D:/goodsimport/goods/彩片/3/desc.htm", "GBK"));
	}

}
