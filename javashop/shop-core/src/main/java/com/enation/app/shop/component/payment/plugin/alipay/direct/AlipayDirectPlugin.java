package com.enation.app.shop.component.payment.plugin.alipay.direct;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.alipay.AlipayRefund;
import com.enation.app.shop.component.payment.plugin.alipay.JavashopAlipayUtil;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.config.AlipayConfig;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.AlipaySubmit;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.order.plugin.payment.IPaymentQrCodeEvent;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 支付宝即时到账插件
 * 
 * @author kingapex
 * @version 1.1 kanon 2016-7－26 修改支付回调方法，添加买家支付宝账号进行纪录。
 */
@Component
public class AlipayDirectPlugin extends AbstractPaymentPlugin implements IPaymentEvent,IPaymentQrCodeEvent {

	@Override
	public String onCallBack(String ordertype) {
		try {

			Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());

			String key = cfgparams.get("key");
			String partner = cfgparams.get("partner");
			AlipayConfig.key = key;
			AlipayConfig.partner = partner;

			String param_encoding = cfgparams.get("param_encoding");

			HttpServletRequest request = ThreadContextHolder.getHttpRequest();

			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			// 商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

			// 支付宝交易号
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

			// 交易状态
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
			
			//买家支付宝账号
			String buyer_email = new String(request.getParameter("buyer_email").getBytes("ISO-8859-1"), "UTF-8");

			if (JavashopAlipayUtil.verify(param_encoding)) {// 验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				// 请在这里加上商户的业务逻辑程序代码
				
				// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

				if (trade_status.equals("TRADE_FINISHED")) {
					// 判断该笔订单是否在商户网站中已经做过处理
					// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					// 如果有做过处理，不执行商户的业务程序
					this.paySuccess(out_trade_no, trade_no,buyer_email, ordertype);
					// 注意：
					// 退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
				} else if (trade_status.equals("TRADE_SUCCESS")) {
					// 判断该笔订单是否在商户网站中已经做过处理
					// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					// 如果有做过处理，不执行商户的业务程序
					this.paySuccess(out_trade_no, trade_no,buyer_email, ordertype);
					// 注意：
					// 付款完成后，支付宝系统发送该交易状态通知
				}

				// ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

				return ("success"); // 请不要修改或删除

				//////////////////////////////////////////////////////////////////////////////////////////
			} else {// 验证失败
				return ("fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ("fail");
		}
	}

	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		try {
			Map<String, String> params = paymentManager.getConfigParams(this.getId());
			String out_trade_no = order.getSn(); // 商户网站订单
			String partner =params.get("partner");  // 支付宝合作伙伴id (账户内提取)
			String key =  params.get("key");  // 支付宝安全校验码(账户内提取)
			String seller_email = params.get("seller_email"); // 卖家支付宝帐户
			String content_encoding = params.get("content_encoding"); // 卖家支付宝帐户
			
			AlipayConfig.key=key;
			AlipayConfig.partner=partner;
			AlipayConfig.seller_email=seller_email;
			
			String payment_type = "1";
			//必填，不能修改
			//服务器异步通知页面路径
			String notify_url =this.getCallBackUrl(payCfg, order);
			//需http://格式的完整路径，不能加?id=123这类自定义参数

			//页面跳转同步通知页面路径
			String return_url = this.getReturnUrl(payCfg, order);
			//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/

			String show_url= this.getShowUrl(order);
			
			//商户订单号
			out_trade_no = new String( out_trade_no.getBytes("ISO-8859-1"),"UTF-8")  ;
			//商户网站订单系统中唯一订单号，必填

			String sitename = EopSite.getInstance().getSitename();
			//订单名称
			String subject =sitename+"订单";
			if(!StringUtil.isEmpty(content_encoding)){
				 subject = new String(subject.getBytes("ISO-8859-1"),content_encoding);
			}
			//必填

			String body =  ("订单："+out_trade_no);
			if(!StringUtil.isEmpty(content_encoding)){
				body=new String( body.getBytes("ISO-8859-1"),content_encoding);
			}
			
			//付款金额
			String price = new String(String.valueOf( order.getNeedPayMoney()).getBytes("ISO-8859-1"),"UTF-8");
			

			//把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", "create_direct_pay_by_user");
	        sParaTemp.put("partner", AlipayConfig.partner);
	        sParaTemp.put("seller_email", AlipayConfig.seller_email);
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("payment_type", payment_type);
			sParaTemp.put("notify_url", notify_url);
			sParaTemp.put("return_url", return_url);
			sParaTemp.put("out_trade_no", out_trade_no);
			sParaTemp.put("subject", subject);
			sParaTemp.put("total_fee", price);
			sParaTemp.put("body", body);
			sParaTemp.put("show_url", show_url);
			sParaTemp.put("anti_phishing_key", "");
			sParaTemp.put("exter_invoke_ip", "");

			return AlipaySubmit.buildRequest(sParaTemp, "POST", "确认");
		} catch (Exception e) {
			return "转码失败";
		}
		

	}

	@Override
	public String onReturn(String ordertype) {
		
		try {
			Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());

			String key = cfgparams.get("key");
			String partner = cfgparams.get("partner");
			AlipayConfig.key = key;
			AlipayConfig.partner = partner;

			String param_encoding = cfgparams.get("param_encoding");

			HttpServletRequest request = ThreadContextHolder.getHttpRequest();

			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			// 商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

			// 支付宝交易号
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

			// 交易状态
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

			//买家支付宝账号
			String buyer_email = new String(request.getParameter("buyer_email").getBytes("ISO-8859-1"), "UTF-8");
			
			if (JavashopAlipayUtil.verify(param_encoding)) {// 验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				// 请在这里加上商户的业务逻辑程序代码
				// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
					// 判断该笔订单是否在商户网站中已经做过处理
					// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					// 如果有做过处理，不执行商户的业务程序
				}

				// 该页面可做页面美工编辑
				this.paySuccess(out_trade_no, trade_no, buyer_email,ordertype);
				return out_trade_no;

				//////////////////////////////////////////////////////////////////////////////////////////
			} else {
				// 该页面可做页面美工编辑
				throw new RuntimeException("验证失败");
			}
		} catch (Exception e) {
			throw new RuntimeException("验证失败");
		}
		

	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.plugin.payment.IPaymentQrCodeEvent#onPayQrCode(com.enation.app.shop.core.order.model.PayCfg, com.enation.app.shop.core.order.model.PayEnable)
	 */
	@Override
	public String onPayQrCode(PayCfg payCfg, PayEnable order) {
		try {
			Map<String, String> params = paymentManager.getConfigParams(this.getId());
			String out_trade_no = order.getSn(); // 商户网站订单
			String partner =params.get("partner");  // 支付宝合作伙伴id (账户内提取)
			String key =  params.get("key");  // 支付宝安全校验码(账户内提取)
			String seller_email = params.get("seller_email"); // 卖家支付宝帐户
			String content_encoding = params.get("content_encoding"); // 卖家支付宝帐户
			
			AlipayConfig.key=key;
			AlipayConfig.partner=partner;
			AlipayConfig.seller_email=seller_email;
			
			String payment_type = "1";
			//必填，不能修改
			//服务器异步通知页面路径
			String notify_url =this.getCallBackUrl(payCfg, order);
			//需http://格式的完整路径，不能加?id=123这类自定义参数

			//页面跳转同步通知页面路径
			//String return_url = this.getReturnUrl(payCfg, order);
			//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/
			
			//支付宝扫码支付中间跳转页路径
			String jump_url= this.getAlipayJumpUrl(payCfg, order);
			
			String show_url= this.getShowUrl(order);
			
			//商户订单号
			out_trade_no = new String( out_trade_no.getBytes("ISO-8859-1"),"UTF-8")  ;
			//商户网站订单系统中唯一订单号，必填

			String sitename = EopSite.getInstance().getSitename();
			//订单名称
			String subject =sitename+"订单";
			if(!StringUtil.isEmpty(content_encoding)){
				 subject = new String(subject.getBytes("ISO-8859-1"),content_encoding);
			}
			//必填

			String body =  ("订单："+out_trade_no);
			if(!StringUtil.isEmpty(content_encoding)){
				body=new String( body.getBytes("ISO-8859-1"),content_encoding);
			}
			
			//付款金额
			String price = new String(String.valueOf( order.getNeedPayMoney()).getBytes("ISO-8859-1"),"UTF-8");
			
			
			//把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", "create_direct_pay_by_user");
	        sParaTemp.put("partner", AlipayConfig.partner);
	        sParaTemp.put("seller_email", AlipayConfig.seller_email);
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("payment_type", payment_type);
			sParaTemp.put("notify_url", notify_url);
			sParaTemp.put("return_url", jump_url);
			sParaTemp.put("out_trade_no", out_trade_no);
			sParaTemp.put("subject", subject);
			sParaTemp.put("total_fee", price);
			sParaTemp.put("body", body);
			sParaTemp.put("show_url", show_url);
			sParaTemp.put("anti_phishing_key", "");
			sParaTemp.put("exter_invoke_ip", "");
			sParaTemp.put("qr_pay_mode", "4");
			sParaTemp.put("qrcode_width", "200");
			
			return AlipaySubmit.buildRequest(sParaTemp,"get","确认");
		} catch (Exception e) {
			return "转码失败";
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.plugin.payment.IPaymentQrCodeEvent#onPayStatus(com.enation.app.shop.core.order.model.PayCfg, com.enation.app.shop.core.order.model.PayEnable)
	 */
	@Override
	public String onPayStatus(PayCfg payCfg, PayEnable order) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public String getId() {

		return "alipayDirectPlugin";
	}

	@Override
	public String getName() {

		return "支付宝即时到帐接口";
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.plugin.payment.IPaymentEvent#onRefund(com.enation.app.shop.core.order.model.PayEnable, com.enation.app.shop.core.order.model.Refund, com.enation.app.shop.core.order.model.PaymentLog)
	 */
	@Override
	public String onRefund(PayEnable order, Refund refund, PaymentLog paymentLog) {
		AlipayRefund alipayRefund = SpringContextHolder.getBean("alipayRefund");
		return alipayRefund.onRefund(order, refund,paymentLog);
	}

	

	

}
