package com.enation.app.shop.core.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.GoodsSnapshot;
import com.enation.app.shop.core.order.service.IOrderSnapshotManager;
import com.enation.framework.database.IDaoSupport;
/**
 * 
 * (订单快照接口) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2016年12月13日 上午10:46:28
 */
@Service("orderSnapshot")
public class OrderSnapshotManager implements IOrderSnapshotManager{
	@Autowired
	private IDaoSupport daoSupport;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderSnapshotManager#addSnapshot(com.enation.app.shop.core.order.model.OrderSnapshot)
	 */
	@Override
	public void addSnapshot(GoodsSnapshot orderSnapshot) {
		daoSupport.insert("es_goods_snapshot", orderSnapshot);
	}

}
