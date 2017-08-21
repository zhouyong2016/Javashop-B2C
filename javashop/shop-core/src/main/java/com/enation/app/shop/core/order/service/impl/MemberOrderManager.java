package com.enation.app.shop.core.order.service.impl;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.order.service.IMemberOrderManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 
 * @author Kanon 2016-2-25;6.0版本改造
 *
 */
@Service("memberOrderManager")
public class MemberOrderManager implements IMemberOrderManager {

	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private OrderPluginBundle orderPluginBundle;
	
	public Page pageOrders(int pageNo, int pageSize) {
		Member member = UserConext.getCurrentMember();
		
		String sql = "select * from es_order where member_id = ? and disabled=0 order by create_time desc";
		Page rpage = this.daoSupport.queryForPage(sql, pageNo, pageSize, member.getMember_id());
		List<Map> list = (List<Map>) (rpage.getResult());
		return rpage;
	}
	
	@Transactional(propagation = Propagation.REQUIRED) 
	public Page pageOrders(int pageNo, int pageSize,String status, String keyword){
		this.cancelOrder();//检查订单是否过期
		
		Member member = UserConext.getCurrentMember();
		
		String sql = "select * from es_order where member_id = '" + member.getMember_id() + "' and disabled=0";
		if(!StringUtil.isEmpty(status)){
			int statusNumber = -999;
			statusNumber = StringUtil.toInt(status);
			//等待付款的订单 按付款状态查询
			if(statusNumber==0){
				sql+=" AND status!="+OrderStatus.ORDER_CANCELLATION+" AND pay_status="+OrderStatus.PAY_NO;
			}else{
				sql += " AND status='" + statusNumber + "'";
			}
		}
		if(!StringUtil.isEmpty(keyword)){
			sql += " AND order_id in (SELECT i.order_id FROM es_order_items i LEFT JOIN es_order o ON i.order_id=o.order_id WHERE o.member_id='"+member.getMember_id()+"' AND i.name like '%" + keyword + "%')";
		}
		sql += " order by create_time desc";
		Page rpage = this.daoSupport.queryForPage(sql,pageNo, pageSize, Order.class);
		 
		return rpage;
	}
	
	/**
	 * 检查订单是否过期，若已过期，将其状态置为取消  
	 * 添加人：DMRain 2015-12-08
	 */
	private void cancelOrder(){
		String sql = "select * from es_order where status = 0 and pay_status = 0 and ship_status = 2 and payment_type != 'cod'";
		List<Map> list = this.daoSupport.queryForList(sql);
		
		if(list.size() != 0){
			for(Map order : list){
				long createTime = (Long) order.get("create_time");
				long nowTime = DateUtil.getDateline();
				if((nowTime - createTime) > 259200){
					Integer order_id = (Integer) order.get("order_id");
					this.daoSupport.execute("update es_order set status = 8,cancel_reason = '订单过期，系统自动将其取消' where order_id = ?", order_id);
					
					Order orderBean=new Order();
					try {
						BeanUtils.populate(orderBean, order);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					this.orderPluginBundle.onCanel(orderBean);//相应订单取消事件  add by jianghongyan 2016-05-6-16
					//添加订单日志
					orderManager.addLog(order_id, "订单过期，系统自动将其取消", "平台系统");
				}
			}
		}
	}
	
	public Page pageGoods(int pageNo, int pageSize,String keyword){
		Member member = UserConext.getCurrentMember();
		
		String sql = "select * from es_goods where goods_id in (SELECT i.goods_id FROM es_order_items i LEFT JOIN order o ON i.order_id=o.order_id WHERE o.member_id='"+member.getMember_id()
				+"' AND o.status in (" + OrderStatus.ORDER_COMPLETE +","+OrderStatus.ORDER_ROG+ " )) ";
		if(!StringUtil.isEmpty(keyword)){
			sql += " AND (sn='" + keyword + "' OR name like '%" + keyword + "%')";
		}
		sql += " order by goods_id desc";
		Page rpage = this.daoSupport.queryForPage(sql, pageNo, pageSize);
		List<Map> list = (List<Map>) (rpage.getResult());
		return rpage;
	}
	
	
	public Delivery getOrderDelivery(int order_id) {
		Delivery delivery = (Delivery)this.daoSupport.queryForObject("select * from es_delivery where order_id = ?", Delivery.class, order_id);
		return delivery;
	}
	
	
	public List listGoodsItems(int order_id) {
		String sql = "select * from es_order_items where order_id = ?";
		List list = this.daoSupport.queryForList(sql, order_id);
		list = list == null ? new ArrayList() : list;
		return list;
	}

	
	public List listGiftItems(int orderid) {
		String sql  ="select * from es_order_gift where order_id=?";
		return this.daoSupport.queryForList(sql, orderid);
	}


	public boolean isBuy(int goodsid) {
		Member member = UserConext.getCurrentMember();
		if(member==null) return false;
		String sql  ="select count(0) from es_order_items"+
					 " where  order_id in(select order_id from es_order where member_id=?) and goods_id =? ";
		int count  = this.daoSupport.queryForInt(sql, member.getMember_id(),goodsid);
		return count>0;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.order.service.IStoreOrderManager#getSubOrderPayStatus(java.lang.Integer)
	 */
	@Override
	public Page pageCommentOrders(int pageNo, int pageSize) {
		
		Member member = UserConext.getCurrentMember();
		
		String sql = "select * from es_order where member_id = ? and disabled=0 "
				   +" AND order_id in (	SELECT i.order_id FROM es_member_order_item i LEFT JOIN es_order o "
				   +" ON i.order_id=o.order_id WHERE o.member_id= ?  and i.commented=0 ) " ;
		sql += " order by create_time desc";
		Page rpage = this.daoSupport.queryForPage(sql,pageNo, pageSize, Order.class,member.getMember_id(),member.getMember_id());
		return rpage;
	}

}
