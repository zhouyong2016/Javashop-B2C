package com.enation.app.shop.core.goods.service.batchimport.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.enation.app.shop.core.goods.model.Depot;
import com.enation.app.shop.core.goods.model.ImportDataSource;
import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.app.shop.core.goods.service.batchimport.IGoodsDataImporter;
import com.enation.framework.database.IDaoSupport;

/**
 * 商品规格导入器
 * 此规格导入器，商品是未开启规格的
 * @author kingapex
 *
 */
public class GoodsSpecImporter implements IGoodsDataImporter {
	protected IProductManager productManager;
	protected IDaoSupport baseDaoSupport;
	private IDepotManager depotManager;
	@Override
	public void imported(Object value, Element node, ImportDataSource importDs,
			Map goods) {
		Integer goodsid  = (Integer )goods.get("goods_id");
		Product product = new Product();
		product.setGoods_id(goodsid);
		product.setCost(  Double.valueOf( ""+goods.get("cost") ) );
		product.setPrice(   Double.valueOf( ""+goods.get("price"))  );
		product.setSn((String)goods.get("sn"));
		product.setStore(100);
		product.setWeight(Double.valueOf( ""+goods.get("weight")));
		product.setName((String)goods.get("name"));
		
		List<Product> productList = new ArrayList<Product>();
		productList.add(product);
		this.productManager.add(productList);
		
		List<Depot> depotList = this.depotManager.list();
		for(Depot depot:depotList){
			this.baseDaoSupport.execute("insert into goods_depot(goodsid,depotid,iscmpl)values(?,?,?)", goodsid,depot.getId(),0);
		}
		
	}
	public IProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}
	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}
	public IDepotManager getDepotManager() {
		return depotManager;
	}
	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}

}
