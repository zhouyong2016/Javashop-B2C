package com.enation.app.shop.component.orderreturns.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.order.model.OrderLog;
import com.enation.app.shop.core.order.model.ReturnsOrder;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.database.StringMapper;
import com.enation.framework.util.DateUtil;

/**
 * 退货管理
 * @author Sylow
 * @version v2.0,2016年2月18日 版本改造
 * @since v6.0
 */
@Service("returnsOrderManager")
public class ReturnsOrderManager implements IReturnsOrderManager {

	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IDaoSupport daoSupport;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#add(com.enation.app.shop.core.order.model.ReturnsOrder, int, int, int[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(ReturnsOrder returnsOrder,int orderid,int state,int[] goodids) {
		returnsOrder.setState(ReturnsOrderStatus.APPLY_SUB);
		returnsOrder.setAdd_time(DateUtil.getDateline());
		for (int i = 0; i < goodids.length; i++) {
			this.daoSupport.execute("update es_order_items set state = ? where order_id = ? and goods_id= ?", state,orderid,goodids[i]);
		}
		this.daoSupport.insert("es_returns_order", returnsOrder);

	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#get(java.lang.Integer)
	 */
	@Override
	public ReturnsOrder get(Integer id) {
		ReturnsOrder order =(ReturnsOrder)this.daoSupport.queryForObject("select * from es_returns_order where id=?", ReturnsOrder.class, id);
		return order;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#getByOrderSn(java.lang.String)
	 */
	@Override
	public ReturnsOrder getByOrderSn(String ordersn){
		ReturnsOrder order =(ReturnsOrder)this.daoSupport.queryForObject("select * from es_returns_order where ordersn=?", ReturnsOrder.class, ordersn);
		return order;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#listAll(int, int)
	 */
	@Override
	public Page listAll(int pageNo, int pageSize) {
		String sql ="select r.*,m.uname as membername from es_returns_order r,es_member m where m.disabled!=1 and r.memberid=m.member_id order by r.add_time desc";
		return this.daoSupport.queryForPage(sql, pageNo, pageSize, ReturnsOrder.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#listMemberOrder()
	 */
	@Override
	public List<ReturnsOrder> listMemberOrder() {
		Member member = UserConext.getCurrentMember();
		return this.daoSupport.queryForList("select * from es_returns_order where memberid =? ", ReturnsOrder.class, member.getMember_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#updateState(java.lang.Integer, int)
	 */
	@Override
	public void updateState(Integer returnOrderId, int state) {
		this.daoSupport.execute("update es_returns_order set state=? where id=?", state,returnOrderId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#refuse(java.lang.Integer, java.lang.String, int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void refuse(Integer return_id,String refuse_reason,int return_state) {
		this.daoSupport.execute("update es_returns_order set state=?,refuse_reason=? where id=?",return_state,refuse_reason,return_id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#pageReturnOrder(int, int)
	 */
	@Override
	public Page pageReturnOrder(int pageNo, int pageSize) {
			Member member = UserConext.getCurrentMember();
			String sql = "select * from es_returns_order where memberid = ? order by add_time desc";
			Page rpage = this.daoSupport.queryForPage(sql, pageNo, pageSize, member.getMember_id());
			return rpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#getSnByOrderSn(java.lang.String)
	 */
	@Override
	public String getSnByOrderSn(String orderSn) {
		return (String)this.daoSupport.queryForString("select goodsns from es_returns_order where ordersn = ?",  orderSn);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#updateOrderItemsState(java.lang.Integer, int)
	 */
	@Override
	public void updateOrderItemsState(Integer itemsId,int state) {
		this.daoSupport.execute("update es_order_items set state = ? where item_id = ?", state,itemsId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#queryItemPrice(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Double queryItemPrice(Integer orderid,Integer state) {
		Double temp=(Double)this.daoSupport.queryForDouble("SELECT sum(price) as price FROM es_order_items  where order_id = ? and state= ?" ,orderid,state);
		return temp;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#updateItemChange(java.lang.String, int, int)
	 */
	@Override
	public void updateItemChange(String change_goods_name, int change_goods_id,int itemId) {
		this.daoSupport.execute("update es_order_items set change_goods_name =?,change_goods_id=? where item_id = ?", change_goods_name,change_goods_id,itemId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#updateItemStatusByOrderidAndStatus(int, int, int)
	 */
	@Override
	public void updateItemStatusByOrderidAndStatus(int newStatus,
			int prevStatus, int orderid) {
		this.daoSupport.execute("update es_order_items set state = ? where order_id = ? and state=?", newStatus,orderid,prevStatus);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#queryOrderidByReturnorderid(int)
	 */
	@Override
	public int queryOrderidByReturnorderid(int returnorderid) {
		return orderManager.get(this.get(returnorderid).getOrdersn()).getOrder_id().intValue();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#updatePriceByItemid(int, double)
	 */
	@Override
	public void updatePriceByItemid(int itemid, double price) {
		this.daoSupport.execute("update es_order_items set price=? where item_id=?", price,itemid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#listAll(int, int, int)
	 */
	@Override
	public Page listAll(int pageNo, int pageSize, int state) {
		return this.daoSupport.queryForPage("select r.*,m.uname as membername from es_returns_order r,es_member m where m.disabled!=1 and r.memberid=m.member_id and r.state = ?   order by r.add_time desc", pageNo, pageSize, ReturnsOrder.class,state);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#getOrderidByReturnid(java.lang.Integer)
	 */
	@Override
	public Integer getOrderidByReturnid(Integer returnorderid){
		Integer orderid=this.orderManager.get(this.get(returnorderid).getOrdersn()).getOrder_id();
		return orderid;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager#updateItemsState(java.lang.Integer, int, int)
	 */
	@Override
	public void updateItemsState(Integer orderid, int nowstate, int prestate) {
		this.daoSupport.execute("update es_order_items set state = ?  where order_id =? and state = ? ", nowstate,orderid,prestate);
	}
}
