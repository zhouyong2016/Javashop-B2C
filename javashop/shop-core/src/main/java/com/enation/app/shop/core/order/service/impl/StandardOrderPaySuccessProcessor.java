package com.enation.app.shop.core.order.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PaymentDetail;
import com.enation.app.shop.core.order.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.order.plugin.payment.IPaySuccessProcessor;
import com.enation.app.shop.core.order.service.IOrderFlowManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IOrderReportManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.framework.database.IDaoSupport;

/**
 * 标准订单支付成功处理器
 * @author kingapex
 *2013-9-24上午11:17:19
 */
@Service("standardOrderPaySuccessProcessor")
public class StandardOrderPaySuccessProcessor implements IPaySuccessProcessor {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IOrderFlowManager orderFlowManager;
	
	@Autowired
	private IOrderReportManager orderReportManager;
	
	@Autowired
	private IPaymentManager paymentManager;
	
	@Autowired
	private OrderPluginBundle orderPluginBundle;
	
	@Override
	public void paySuccess(String ordersn, String tradeno, String payment_account,String ordertype,String pluginId) {
		Order order  = orderManager.get(ordersn);
		if (order.getPay_status() == OrderStatus.PAY_NO) {
			if( order.getPay_status().intValue()== OrderStatus.PAY_YES ){ //如果是已经支付的，不要再支付
				return ;
			}

			this.orderFlowManager.payConfirm(order.getOrder_id());

			try{
				//添加支付详细对象 @author LiFenLong
				Double needPayMoney= order.getNeed_pay_money(); //在线支付的金额
				int paymentid = orderReportManager.getPaymentLogId(order.getOrder_id());

				PaymentDetail paymentdetail=new PaymentDetail();

				paymentdetail.setAdmin_user("系统");
				paymentdetail.setPay_date(new Date().getTime());
				paymentdetail.setPay_money(needPayMoney);
				paymentdetail.setPayment_id(paymentid);
				orderReportManager.addPayMentDetail(paymentdetail);

				PayCfg payCfg = this.paymentManager.get(pluginId);

				//修改订单状态为已付款付款
				this.daoSupport.execute("update es_payment_logs set paymoney=paymoney+? , pay_method=? ,trasaction_id = ? where payment_id=?",
						needPayMoney,payCfg.getName(),tradeno,paymentid);

				//更新订单的已付金额
				this.daoSupport.execute("update es_order set paymoney=paymoney+?,payment_account=?,payment_id=?,payment_name=?,payment_type=? where order_id=?",
						needPayMoney,payment_account,payCfg.getId(),payCfg.getName(),payCfg.getType(),order.getOrder_id());

			}catch(Exception e){
				e.printStackTrace();

			}
			orderPluginBundle.confirm(order.getOrder_id(),order.getNeed_pay_money());
		}
		
	}
	
	
}
