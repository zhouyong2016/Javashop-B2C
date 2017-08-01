package com.enation.app.shop.core.order.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.ShopApp;
import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.DeliveryItem;
import com.enation.app.shop.core.order.model.PaymentDetail;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.PaymentLogType;
import com.enation.app.shop.core.order.model.RefundLog;
import com.enation.app.shop.core.order.service.IOrderReportManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 订单报表
 * @author Sylow
 * @version v2.0,2016年2月18日 版本改造
 * @since v6.0
 */
@Service("orderReportManager")
public class OrderReportManager implements IOrderReportManager {

	@Autowired
	private IDaoSupport daoSupport;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#getDelivery(java.lang.Integer)
	 */
	@Override
	public Delivery getDelivery(Integer deliveryId) {
		String sql = "select l.*, m.uname as member_name, o.sn from "
				+ " es_delivery l left join "
				+ " es_member "
				+ " m on m.member_id = l.member_id left join "
				+ " es_order "
				+ " o on o.order_id = l.order_id where m.disabled!=1 and l.delivery_id = ?";
		Delivery delivery = (Delivery) this.daoSupport.queryForObject(sql,
				Delivery.class, deliveryId);
		return delivery;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#getDeliveryList(int)
	 */
	@Override
	public List<Delivery> getDeliveryList(int orderId) {
		String sql = "select * from es_delivery where order_id=" + orderId;
		return this.daoSupport.queryForList(sql, Delivery.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#getPayment(java.lang.Integer)
	 */
	@Override
	public PaymentLog getPayment(Integer paymentId) {
		String sql = "select l.*, m.uname as member_name, o.sn from "
				+ " es_payment_logs l left join "
				+ " es_member "
				+ " m on m.member_id = l.member_id left join "
				+ " es_order "
				+ " o on o.order_id = l.order_id where m.disabled!=1 and l.payment_id = ?";
		PaymentLog paymentLog = (PaymentLog) this.daoSupport.queryForObject(
				sql, PaymentLog.class, paymentId);
		return paymentLog;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#listDeliveryItem(java.lang.Integer)
	 */
	@Override
	public List<DeliveryItem> listDeliveryItem(Integer deliveryId) {
		String sql = "select * from es_delivery_item where delivery_id = ?";
		return this.daoSupport.queryForList(sql, DeliveryItem.class,
				deliveryId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#listPayment(java.util.Map, int, int)
	 */
	@Override
	public Page listPayment(Map map, int pageNo, int pageSize,String order) {
		String sql = createTempSql(map);
		return this.daoSupport.queryForPage(sql, pageNo, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#listPayLogs(java.lang.Integer)
	 */
	@Override
	public List<PaymentLog> listPayLogs(Integer orderId) {
		return this.daoSupport.queryForList(
				"select * from es_payment_logs where order_id = ? ",
				PaymentLog.class, orderId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#listRefundLogs(java.lang.Integer)
	 */
	@Override
	public List<RefundLog> listRefundLogs(Integer order_id) {

		return this.daoSupport.queryForList(
				"select * from es_refund_logs where order_id = ? ",
				RefundLog.class, order_id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#listReturned(int, int, java.lang.String)
	 */
	@Override
	public Page listReturned(int pageNo, int pageSize, String order,String type) {
		String sql = "select * from es_sellback_list where 1=1";
		if(type!=null){
			sql +=" and type="+type;
		}
		/**
		 * 如果是多店系统 说明在自营查询
		 */
		if("b2b2c".equals(EopSetting.PRODUCT) ){
			 sql +=" and store_id="+ShopApp.self_storeid;
		}
		if (!StringUtil.isEmpty(order)) {
			sql += " and tradestatus = "+order+"";
		}
		
		sql+="  order by id desc";
		Page webpage = this.daoSupport.queryForPage(sql, pageNo, pageSize);
		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#listShipping(int, int, java.lang.String)
	 */
	@Override
	public Page listShipping(int pageNo, int pageSize, String order) {
		order = (order == null) ? "delivery_id desc" : order;
		String sql = "select l.*, m.uname as member_name, o.sn from "
				+ " es_delivery l left join "
				+ " es_member "
				+ " m on m.member_id = l.member_id left join "
				+ " es_order "
				+ " o on o.order_id = l.order_id where m.disabled!=1 and l.type = " + 1;
		sql += " order by " + order;
		return this.daoSupport.queryForPage(sql, pageNo, pageSize,
				Delivery.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#listDelivery(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List listDelivery(Integer orderId, Integer type) {
		return this.daoSupport.queryForList(
				"select * from es_delivery where order_id = ? and type = ?",
				orderId, type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#getPayMoney(java.lang.Integer)
	 */
	@Override
	public double getPayMoney(Integer paymentId) {
		return  this.daoSupport.queryForDouble( "select sum(pay_money) from es_payment_detail where payment_id=?", paymentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#addPayMentDetail(com.enation.app.shop.core.order.model.PaymentDetail)
	 */
	@Override
	public void addPayMentDetail(PaymentDetail paymentdetail) {
		this.daoSupport.insert("es_payment_detail", paymentdetail);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#getPaymentLogId(java.lang.Integer)
	 */
	public Integer getPaymentLogId(Integer orderId) {

		return this.daoSupport.queryForInt("select payment_id from es_payment_logs where order_id=? and type="
						+ PaymentLogType.receivable.getValue(), orderId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderReportManager#listPayMentDetail(java.lang.Integer)
	 */
	@Override
	public List<PaymentDetail> listPayMentDetail(Integer paymentId) {

		return this.daoSupport.queryForList(
				"select * from es_payment_detail where payment_id=?",
				PaymentDetail.class, paymentId);
	}

	/**
	 * 查询付款单列表
	 * @param map
	 * @return
	 */
	private String createTempSql(Map map) {

		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		Integer status = (Integer) map.get("status");
		String sn = (String) map.get("sn");
		Integer paystatus = (Integer) map.get("paystatus");
		Integer payment_id = (Integer) map.get("payment_id");

		String sql = "select * from es_payment_logs where payment_id>0 and type="
				+ PaymentLogType.receivable.getValue();

		if (stype != null &&!StringUtil.isEmpty(keyword)) {
			if (stype == 0&&!StringUtil.isEmpty(keyword)) {
				sql += " and order_sn like '%" + keyword + "%'";
				sql += " or pay_user like '%" + keyword + "%'";
			}
		}

		if (sn != null && !StringUtil.isEmpty(sn)) {
			sql += " and order_sn like '%" + sn + "%'";
		}

		if (paystatus != null) {
			sql += " and status=" + paystatus;
		}

		if (payment_id != null) {
			sql += " and order_id in (select order_id from es_order where payment_id="
					+ payment_id + ")";
		}

		if (start_time != null && !StringUtil.isEmpty(start_time)) {
			long stime = com.enation.framework.util.DateUtil
					.getDateline(start_time + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
			sql += " and create_time>" + stime;
		}
		if (end_time != null && !StringUtil.isEmpty(end_time)) {
			long etime = com.enation.framework.util.DateUtil.getDateline(
					end_time + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql += " and create_time<" + etime;
		}

		sql += " order by payment_id desc";
		//System.out.println(sql);
		return sql;
	}
	
}
