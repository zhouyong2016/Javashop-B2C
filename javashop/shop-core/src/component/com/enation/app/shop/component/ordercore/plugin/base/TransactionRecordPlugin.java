package com.enation.app.shop.component.ordercore.plugin.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.plugin.order.IOrderRogconfirmEvent;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;
/**
 * 确认收货后添加成交交易
 * @author LiFenLong
 *
 */
@Component
public class TransactionRecordPlugin extends AutoRegisterPlugin implements IOrderRogconfirmEvent{
	private IDaoSupport daoSupport;
	private IMemberManager	memberManager;
	private IOrderManager orderManager;
	/**
	 * @param order 订单
	 * order.order_id 订单Id
	 * order.member_id 会员Id
	 * orderItemList 订单货品列表
	 * orderItem.price 货品价格
	 * orderItem.num 货品数量
	 * orderItem.goods_id 货品商品Id
	 */
	@Override
	public void rogConfirm(Order order) {
		Map map=new HashMap();
		map.put("order_id", order.getOrder_id());
		if(order.getMember_id()==null){
			map.put("uname", "游客");
			map.put("member_id", 0);
		}else{
			map.put("uname", memberManager.get(order.getMember_id()).getUname());
			map.put("member_id", order.getMember_id());
		}
		map.put("rog_time", DateUtil.getDateline());
		List<OrderItem> orderItemList=orderManager.listGoodsItems(order.getOrder_id());
		
		for (OrderItem orderItem : orderItemList) {
			map.put("price",orderItem.getPrice());
			map.put("goods_num",orderItem.getNum());
			map.put("goods_id", orderItem.getGoods_id());
			daoSupport.insert("es_transaction_record", map);
			String updatesql = "update es_goods set buy_count=buy_count+? where goods_id=?";
			this.daoSupport.execute(updatesql,orderItem.getNum(),orderItem.getGoods_id());
		}
		
		
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
