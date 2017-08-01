package com.enation.app.shop.core.goods.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.model.GoodsStores;
import com.enation.app.shop.core.goods.model.support.GoodsEditDTO;
import com.enation.framework.database.Page;

/**
 * 
 * (商品快照管理) 
 * @author zjp
 * @version v1.0
 * @since v6.1
 * 2016年12月12日 上午2:27:03
 */
public interface IGoodsSnapshotManager {

 
	/**
	 * 读取一个商品的快照
	 * @param Goods_id
	 * @return Map
	 */
	public Map get(Integer snapshot_id);
	
}