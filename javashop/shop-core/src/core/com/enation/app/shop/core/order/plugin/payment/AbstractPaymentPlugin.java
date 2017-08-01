package com.enation.app.shop.core.order.plugin.payment;

import java.text.NumberFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 支付插件基类<br/>
 * @author kingapex
 * 2010-9-25下午02:55:10
 */
public abstract class AbstractPaymentPlugin extends AutoRegisterPlugin {
	
	@Autowired
	protected IPaymentManager paymentManager;
	
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private OrderPluginBundle orderPluginBundle;
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 供支付插件获取回调url
	 * @return
	 */
	protected String getCallBackUrl(PayCfg payCfg,PayEnable order){
//		if(callbackUrl!=null)
//			return callbackUrl;
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
//		if(SystemSetting.getWap_open()==1 && request.getServerName().equals(SystemSetting.getWap_domain()) ){
//			return this.getReturnWapUrl(payCfg, order);
//		}
		
		
		String serverName =request.getServerName();
		int port = request.getServerPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		String contextPath = request.getContextPath();
		return "http://"+serverName+portstr+contextPath+"/api/shop/"+order.getOrderType()+"_"+payCfg.getType() +"_payment-callback/execute.do";
	}
	
	protected String getReturnUrl(PayCfg  payCfg,PayEnable order){
	 
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();
		int port =request.getServerPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		String contextPath = request.getContextPath();
		
		if("credit".equals(order.getOrderType())){
			return "http://"+serverName+portstr+contextPath+"/"+order.getOrderType()+"_"+payCfg.getType()+"_credit-result.html" ;
		}else{
			return "http://"+serverName+portstr+contextPath+"/"+order.getOrderType()+"_"+payCfg.getType()+"_payment-result.html" ;
		}
	}
	
	/**
	 * 获取手机版支付成功（失败）页面
	 * @param payCfg
	 * @param order
	 * @return
	 */
	protected String getReturnWapUrl(PayCfg  payCfg,PayEnable order){
		 
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();
		int port =request.getServerPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		String contextPath = request.getContextPath();
		
		return "http://"+serverName+portstr+contextPath+"/"+order.getOrderType()+"_"+payCfg.getType()+"_payment-wap-result.html" ;
		
	}
	
	/**
	 * 返回价格字符串
	 * @param price
	 * @return
	 */
	protected String formatPrice(Double price){
		NumberFormat nFormat=NumberFormat.getNumberInstance();
        nFormat.setMaximumFractionDigits(0); 
        nFormat.setGroupingUsed(false);
        return nFormat.format(price);
	}
	
	/**
	 * 供支付插件获取显示url
	 * @return
	 */
	protected String getShowUrl(PayEnable order){
//		if(showUrl!=null) return showUrl;

		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();	
		int port =request.getServerPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		
		
		String contextPath = request.getContextPath();		
		
		if("s".equals(order.getOrderType())){
			return "http://"+serverName+portstr+contextPath+"/orderdetail_"+order.getSn()+".html";
		}else{
			return  "http://"+serverName+portstr+contextPath+"/"+order.getOrderType()+"_"+order.getSn()+".html";
		}
	}
	
	/**
	 * 供支付插件获取显示url
	 * @return
	 */
	protected String getWapShowUrl(PayEnable order){
//		if(showUrl!=null) return showUrl;

		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();	
		int port =request.getServerPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		
		
		String contextPath = request.getContextPath();		
		
		// 如果是标准订单
		if("s".equals(order.getOrderType())){
			return "http://"+serverName+portstr+contextPath+"/orderlist.html";
		}else{
			return  "http://"+serverName+portstr+contextPath+"/index.html";
		}
	}
	
	/**
	 * 供支付插件获取显示url  
	 * @deprecated 暂时没有先用pc的
	 * @return
	 */
	protected String getAppShowUrl(PayEnable order){
//		if(showUrl!=null) return showUrl;

		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();	
		int port =request.getServerPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		
		
		String contextPath = request.getContextPath();		
		
		// 如果是标准订单
		if("s".equals(order.getOrderType())){
			return "http://"+serverName+portstr+contextPath+"/orderdetail_"+order.getSn()+".html";
		}else{
			return  "http://"+serverName+portstr+contextPath+"/"+order.getOrderType()+"_"+order.getSn()+".html";
		}
	}
	
	/**
	 * 获取支付插件设置参数
	 * @return key为参数名称，value为参数值
	 */
	protected Map<String,String> getConfigParams(){
		return this.paymentManager.getConfigParams(this.getId());
	}
	 
	/**
	 * 支付成功后调用此方法来改变订单的状态
	 * @param ordersn 订单编号
	 * @param tradeno 交易流水号
	 * @param payment_account 支付账号
	 * @param ordertype 订单类型，standard 标准订单，credit:信用账户充值
	 */
	protected void paySuccess(String ordersn,String tradeno ,String payment_account,String ordertype){
		
//		Order order = orderManager.get(ordersn);
		
		//订单的付款状态如果等于未付款(OrderStatus.PAY_NO) add_by DMRain 2016-7-19
		//这里会影响其他支付的逻辑 如果想添加请咨询kingapex delete_by jianghongyan 2016-11-16
//		if (order.getPay_status() == OrderStatus.PAY_NO) {
			PaySuccessProcessorFactory.getProcessor(ordertype).paySuccess(ordersn, tradeno, payment_account,ordertype,this.getId());
			
//		}
	}
	
	/**
	 * 支付宝中间跳转页
	 * @param payCfg
	 * @param order
	 * @return
	 */
	protected String getAlipayJumpUrl(PayCfg  payCfg,PayEnable order){
		
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();
		int port =request.getServerPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		String contextPath = request.getContextPath();
		
		return "http://"+serverName+portstr+contextPath+"/pay-back-jumping.html?orderId="+order.getOrder_id()+"&pluginId=alipayDirectPlugin";
	}
	
	
	/**
	 * 为支付插件定义唯一的id
	 * @return
	 */
	public abstract String getId();
	
	
	/**
	 * 定义插件名称
	 * @return
	 */
	public abstract String getName();
	
}
