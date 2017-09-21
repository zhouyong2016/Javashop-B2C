package com.enation.app.shop.component.payment.plugin.alipay;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.alipay.sdk33.config.AlipayConfig;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.AlipaySubmit;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.UtilDate;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.SellBackStatus;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.IRefundManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
/**
 * 
 * (支付宝统一退款方法) 
 * @author zjp
 * @version  v6.3
 * @since v6.3
 * 2017年6月28日 下午2:43:29
 */
@Component
public class AlipayRefund  {
	@Autowired
	IPaymentManager paymentManager;
	
	@Autowired
	IOrderManager orderManager;
	
	@Autowired
	IRefundManager refundManager;
	
	@Autowired
	ISellBackManager sellBackManager;
	
	@Autowired
	IDaoSupport daoSupport;
	/**
	 * 支付宝退款方法实现
	 * @param order
	 * @param refund
	 * @param paymentLog
	 * @return
	 */
	public String onRefund(PayEnable payEnable, Refund refund, PaymentLog paymentLog) {
		////////////////////////////////////请求参数//////////////////////////////////////
		
		Order order = orderManager.get(payEnable.getOrder_id());
		Map<String, String> params = paymentManager.getConfigParams(order.getPayment_type());
		String partner = params.get("partner"); // 支付宝合作伙伴id (账户内提取)
		String key = params.get("key"); // 支付宝安全校验码(账户内提取)
		String seller_email = params.get("seller_email"); // 卖家支付宝帐户
		String content_encoding = params.get("content_encoding"); // 卖家支付宝帐户
		
		AlipayConfig.key = key;
		AlipayConfig.partner = partner;
		AlipayConfig.seller_email = seller_email;
		// 批次号，必填，格式：当天日期[8位]+序列号[3至24位]，如：201603081000001
		String date = UtilDate.getDate();
		String batch_no = date + payEnable.getSn();
		batch_no = batch_no.replace("-", "");
		String detail_data = paymentLog.getTrasaction_id() + "^" + refund.getRefund_money() + "^支付宝退款";
		String batch_num = "1";
		
		String notify_url = this.getCallBackUrl(payEnable);
		
		//////////////////////////////////////////////////////////////////////////////////
		
		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "refund_fastpay_by_platform_pwd");
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("seller_email", AlipayConfig.seller_email);
		sParaTemp.put("refund_date", UtilDate.getDateFormatter());
		sParaTemp.put("batch_no", batch_no);
		sParaTemp.put("batch_num", batch_num);
		sParaTemp.put("detail_data", detail_data);
		
		// 建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		return sHtmlText;
	}
	/**
	 * 供支付插件获取回调url
	 * @return
	 */
	protected String getCallBackUrl(PayEnable order){

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();
		int port = request.getServerPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		String contextPath = request.getContextPath();
		return "http://"+serverName+portstr+contextPath+"/api/shop/"+order.getOrderType()+"_alipayPlugin_refund-callback/execute.do";
	}
	
	/**
	 * 退款回调 
	 * @param ordertype
	 * @return
	 */
	public String onCallBack(String ordertype) {
		try {
			
			System.out.println("-------退款回调开始-------");
			HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
	
			/**获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)*/
			
			/**退款批次号*/
			String batch_no = new String(request.getParameter("batch_no").getBytes("ISO-8859-1"),"UTF-8");
			/**获取订单号*/
			String sn = batch_no.substring(8);
			StringBuilder stringBuilder = new  StringBuilder(sn);
			/**判断是否为多店*/
			if(EopSetting.PRODUCT.equals("b2b2c")){
				sn = stringBuilder.insert(12, "-").toString();
			}
			/**获取订单实体*/
			Order order = orderManager.get(sn);
			
			Map<String,String> cfgparams = paymentManager.getConfigParams(order.getPayment_type());
			
			String key =cfgparams.get("key");
			String partner =cfgparams.get("partner");
			AlipayConfig.key=key;
			AlipayConfig.partner=partner;
			String param_encoding = cfgparams.get("param_encoding");  
			/**获取订单实体*/
			Refund refund = refundManager.getRefundByOrderId(order.getOrder_id());
			/**退款结果明细*/
			String result_details = new String(request.getParameter("result_details").getBytes("ISO-8859-1"),"UTF-8");
			String[] split = result_details.split("\\^");
			if(JavashopAlipayUtil.verify(param_encoding)){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码
				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				if(split[split.length-1].equals("SUCCESS")){
					//注意：
					//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
					/**
					 * 记录订单日志
					 */
					orderManager.addLog(refund.getOrder_id(), "已退款，金额："+refund.getRefund_money());
					/**
					 * 记录维权订单日志
					 */
					sellBackManager.saveLogBySystem(refund.getSellback_id(), "已退款，金额："+refund.getRefund_money());
					/**
					 * 修改退款单状态
					 */
					this.daoSupport.execute("update es_refund set status = 1 where id = ?", refund.getId());
					/**
					 * 修改退货单状态
					 */
					this.daoSupport.execute("update es_sellback_list set tradestatus=? where id=?",SellBackStatus.refund.getValue(),refund.getSellback_id());
				} else {
						
					//注意：
					//付款完成后，支付宝系统发送该交易状态通知
					/**
					 * 记录订单日志
					 */
					orderManager.addLog(refund.getOrder_id(), "退款失败，金额："+refund.getRefund_money());

					/**
					 * 记录维权订单日志
					 */
					sellBackManager.saveLog(refund.getSellback_id(), "退款失败，金额："+refund.getRefund_money());
					/**
					 * 修改退款单状态
					 */
					this.daoSupport.execute("update es_refund set status = 3 where id = ?", refund.getId());
					/**
					 * 修改退货单状态
					 */
					this.daoSupport.execute("update es_sellback_list set tradestatus=? where id=?",SellBackStatus.payment.getValue(),refund.getSellback_id());
				}
	
				//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
					
				return "success";	//请不要修改或删除
	
				//////////////////////////////////////////////////////////////////////////////////////////
			}else{//验证失败
				return "fail";
			}
		} catch (Exception e) {
			return "fail";
		}
	
}


}
