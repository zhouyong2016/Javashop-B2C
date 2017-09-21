package com.enation.app.shop.core.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.SellBackStatus;
import com.enation.app.shop.core.order.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentLogManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.IRefundManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.DateUtil;

/**
 * 退款单管理类
 * @author Kanon
 *
 */
@Service("refundManager")
public class RefundManager implements IRefundManager {

	@Autowired
	private IDaoSupport daoSupport;

	@Autowired
	private OrderPluginBundle orderPluginBundle;

	@Autowired
	private ISellBackManager sellBackManager;

	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IPaymentManager paymentManager;
	@Autowired
	private IPaymentLogManager paymentLogManager;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#addRefund(com.enation.app.shop.core.order.model.Refund)
	 */
	@Override
	public void addRefund(Refund refund) {
		daoSupport.insert("es_refund", refund);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#editRefund(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Log(type=LogType.ORDER,detail="退款单ID为${id}的退款单由${username}已确认退款,退款金额：${refund_money}")
	public String editRefund(Integer id, Integer status,Double refund_money,String username) {

		//AdminUser adminUser = UserConext.getCurrentAdminUser();


		/**
		 * 修改售后申请状态
		 */
		Refund refund=this.getRefund(id);
		/** 订单信息 */
		Order order=orderManager.get(refund.getOrder_id());
		if(order.getPaymoney()>=refund_money){
			/**修改退款金额*/
			refund.setRefund_money(refund_money);
		}
		String result = "";
		/** 获取支付方式配置信息 */
		PayCfg payCfg = this.paymentManager.get(order.getPayment_type());
		if (payCfg == null) {
			payCfg = new PayCfg();
			payCfg.setIs_retrace(0);
		}
		/** 如果退款方式为原支付方式返回 */
		if(payCfg.getIs_retrace() == 1){
			
			IPaymentEvent paymentPlugin = SpringContextHolder.getBean(payCfg.getType());
			/** 获取收款单信息 */
			PaymentLog paymentLog=paymentLogManager.get(order.getOrder_id());
			/** 判断退款方式是否需要跳转页面 */
			result = paymentPlugin.onRefund(order, refund, paymentLog);
			String message="退款中";
			System.out.println("==result=="+result);
			if(!("FAIL".equals(result) || result==null)){
				daoSupport.execute("update es_sellback_list set tradestatus=?,alltotal_pay=? where id=?",SellBackStatus.payment.getValue(),refund_money,refund.getSellback_id());
				/**
				 * 修改退款单状态，修改退款单金额
				 */
				daoSupport.execute("update es_refund set status=?,refund_user=?,refund_time=?,refund_money=? where id=?", status,username,DateUtil.getDateline(),refund_money,id);
			}else{
				/**
				 * 提交申请失败 视为退款失败
				 */
				daoSupport.execute("update es_refund set status=?,refund_user=?,refund_time=?,refund_money=? where id=?", 3,username,DateUtil.getDateline(),refund_money,id);
				/** 修改给前台展示给用户的状态是退款中 */
				daoSupport.execute("update es_sellback_list set tradestatus=?,alltotal_pay=? where id=?",SellBackStatus.payment.getValue(),refund_money,refund.getSellback_id());
				message="退款申请失败，请稍后重试";
				result="FAIL";
			}
			/**
			 * 记录订单日志
			 */
			orderManager.addLog(refund.getOrder_id(), message+"，金额："+refund_money);

			/**
			 * 记录维权订单日志
			 */
			sellBackManager.saveLog(refund.getSellback_id(), message+"，金额："+refund_money);
			
		}else{
			daoSupport.execute("update es_refund set status=?,refund_user=?,refund_time=?,refund_money=? where id=?", status,username,DateUtil.getDateline(),refund_money,id);
			/** 其他退款方式不需要原支付方式返回 */
			daoSupport.execute("update es_sellback_list set tradestatus=?,alltotal_pay=? where id=?",SellBackStatus.refund.getValue(),refund_money,refund.getSellback_id());
			/**
			 * 记录订单日志
			 */
			orderManager.addLog(refund.getOrder_id(), "已退款，金额："+refund_money);
			/**
			 * 记录维权订单日志
			 */
			sellBackManager.saveLog(refund.getSellback_id(), "已退款，金额："+refund_money);

			result = "SUCCESS";
		}
		/**
		 * 激发退款事件 add by jianghongyan 
		 * 激发退款结算后时间
		 */
		orderPluginBundle.onRefund(order, refund);//
		return result;
		//orderPluginBundle.afterReturnOrderConfirm(refund.getOrder_id(), -refund_money);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#getRefund(java.lang.Integer)
	 */
	@Override
	public Refund getRefund(Integer id) {
		return (Refund) daoSupport.queryForObject("select * from es_refund where id=?", Refund.class, id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#refundList(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page refundList(String state,Integer page, Integer pageSize) {
		StringBuffer sql=new StringBuffer();
		sql.append("select r.*,sl.alltotal_pay as alltotal_pay,sl.ordersn,o.payment_name from es_refund r left join es_sellback_list sl on r.sellback_id = sl.id LEFT JOIN es_order o ON o.sn = sl.ordersn");
		if(!state.equals("") && state != null && !state.equals("-1")){
			sql.append(" where r.status=" +state );
		}
		sql.append(" order by r.create_time desc");
		return daoSupport.queryForPage(sql.toString(),page, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#getRefundBySellbackId(java.lang.Integer)
	 */
	@Override
	public Refund getRefundBySellbackId(Integer id) {

		return (Refund) daoSupport.queryForObject("select * from es_refund where sellback_id=?", Refund.class, id);
	}

	@Override
	public Refund getRefundByOrderId(Integer orderId) {
		return (Refund) daoSupport.queryForObject("select * from es_refund where order_id=?", Refund.class, orderId);
	}

	@Override
	public void manualRefundStatus(Integer id,String username) {
		/** 获取退款单 */
		Refund refund=this.getRefund(id);
		/**
		 * 记录订单日志
		 */
		this.orderManager.addLog(refund.getOrder_id(), "手动退款，已退款，金额："+refund.getRefund_money());
		/**
		 * 记录维权订单日志
		 */
		this.sellBackManager.saveLog(refund.getSellback_id(), "手动退款，已退款，金额："+refund.getRefund_money());
		/** 提交申请失败 视为退款失败*/
		daoSupport.execute("update es_refund set status=?,refund_user=?,refund_time=?,refund_money=? where id=?", 1,username,DateUtil.getDateline(),refund.getRefund_money(),id);
		/** 修改给前台展示给用户的状态是已退款 */
		daoSupport.execute("update es_sellback_list set tradestatus=?,alltotal_pay=? where id=?",SellBackStatus.refund.getValue(),refund.getRefund_money(),refund.getSellback_id());
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#addRefundTxntime(java.lang.Integer, java.lang.String)
	 */
	@Override
	public void addRefundTxntime(Integer id, String txn_time) {
		this.daoSupport.execute("update es_refund set txn_time=? where id=?", txn_time,id);
		
	}

}
