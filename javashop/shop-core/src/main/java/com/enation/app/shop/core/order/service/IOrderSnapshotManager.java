package com.enation.app.shop.core.order.service;

import com.enation.app.shop.core.goods.model.GoodsSnapshot;
import com.enation.app.shop.core.order.service.impl.OrderSnapshotManager;

/**
 * 
 * (订单快照接口) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2016年12月13日 上午10:47:19
 */
public interface IOrderSnapshotManager {
	/**
	 * 
		 * @Title: addSnapshot
		 * @Description: 添加快照信息
		 * @return:void
		 * @throws
		 * @data:2016年11月29日
		 * @user:zjp
	 */
	public void addSnapshot(GoodsSnapshot orderSnapshot);
}
