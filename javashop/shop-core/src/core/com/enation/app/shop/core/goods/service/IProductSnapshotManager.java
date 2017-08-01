package com.enation.app.shop.core.goods.service;

import java.util.List;

import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.goods.model.ProductSnapshot;
import com.enation.app.shop.core.goods.model.Specification;
import com.enation.framework.database.Page;

/**
 * 
 * (快照货品管理) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2017年1月5日 下午11:04:37
 */
public interface IProductSnapshotManager {

	/**
	 * 查询某个快照商品的货品
	 * 
	 * @param goods_id
	 * @param snapshot_id
	 * @return
	 */
	public List<ProductSnapshot> list(Integer goods_id,Integer snapshot_id,Integer product_id);
	/**
	 * 查询某个快照商品的规格
	 * 
	 * @param goods_id
	 * @return
	 */
	public List<Specification> listSpecs(Integer goods_id,Integer product_id);
	/**
	 * 添加货品快照
	 */
	public void add(ProductSnapshot productSnapshot);
}
