package com.enation.app.shop.component.payment.plugin.alipay.escow;

import java.io.UnsupportedEncodingException;
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
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 支付宝担保交易支付插件
 * @author kingapex
 *2015年9月23日上午11:51:17
 * @version 1.1 kanon 2016-7－26 修改支付回调方法，添加买家支付宝账号进行纪录。（支付宝担保交易接口已下线）
 */
@Component
public class AlipayEscowPlugin extends AbstractPaymentPlugin  implements IPaymentEvent {
	
	private static String paygateway = "https://www.alipay.com/cooperate/gateway.do?";
	
	

	@Override
	public String onCallBack(String ordertype) {
		try {
			
			
			Map<String,String> cfgparams = paymentManager.getConfigParams(this.getId());
			
			String key =cfgparams.get("key");
			String partner =cfgparams.get("partner");
			AlipayConfig.key=key;
			AlipayConfig.partner=partner;
			String param_encoding = cfgparams.get("param_encoding");  

			
			HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
	
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			//商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
	
			//支付宝交易号
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
	
			//交易状态
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
	
			//买家支付宝账号
			String buyer_email = new String(request.getParameter("buyer_email").getBytes("ISO-8859-1"), "UTF-8");
			
			if(JavashopAlipayUtil.verify(param_encoding)){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码
				this.paySuccess(out_trade_no,trade_no, buyer_email,ordertype);
				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				
				if(trade_status.equals("TRADE_FINISHED")){
					//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
				} else if (trade_status.equals("TRADE_SUCCESS")){
					//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//付款完成后，支付宝系统发送该交易状态通知
				}
	
				//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
					
				return ("success");	//请不要修改或删除
	
				//////////////////////////////////////////////////////////////////////////////////////////
			}else{//验证失败
				return ("fail");
			}
		} catch (Exception e) {
			return "fail";
		}
	} 

	
	public String onPay(PayCfg payCfg, PayEnable order) {
		try{
			Map<String,String> params = this.getConfigParams();
			
			String out_trade_no = order.getSn(); // 商户网站订单
			String partner =params.get("partner");  // 支付宝合作伙伴id (账户内提取)
			String key =  params.get("key");  // 支付宝安全校验码(账户内提取)
			String seller_email = params.get("seller_email"); // 卖家支付宝帐户
			String content_encoding = params.get("content_encoding"); // 卖家支付宝帐户
			
			
			AlipayConfig.key=key;
			AlipayConfig.partner=partner;
			AlipayConfig.seller_email=seller_email;
		 
		 
			//支付类型
			String payment_type = "1";
			//必填，不能修改
			//服务器异步通知页面路径
			String notify_url =this.getCallBackUrl(payCfg, order);
			//需http://格式的完整路径，不能加?id=123这类自定义参数

			//页面跳转同步通知页面路径
			String return_url = this.getReturnUrl(payCfg, order);
			//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/

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
			//必填
	  
			//商品数量
			String quantity = "1";
			//必填，建议默认为1，不改变值，把一次交易看成是一次下订单而非购买一件商品
			//物流费用
			String logistics_fee = "0.00";
			//必填，即运费
			//物流类型
			String logistics_type = "EXPRESS";
			//必填，三个值可选：EXPRESS（快递）、POST（平邮）、EMS（EMS）
			//物流支付方式
			String logistics_payment = "SELLER_PAY";
			//必填，两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）
			//订单描述

			
			//商品展示地址
			String show_url =this.getShowUrl(order);
			//需以http://开头的完整路径，如：http://www.商户网站.com/myorder.html

			//收货人姓名
			String receive_name = new String("".getBytes("ISO-8859-1"),"UTF-8");
			//如：张三

			//收货人地址
			String receive_address = new String("".getBytes("ISO-8859-1"),"UTF-8");
			//如：XX省XXX市XXX区XXX路XXX小区XXX栋XXX单元XXX号

			//收货人邮编
			String receive_zip = new String("".getBytes("ISO-8859-1"),"UTF-8");
			//如：123456

			//收货人电话号码
			String receive_phone = new String("".getBytes("ISO-8859-1"),"UTF-8");
			//如：0571-88158090

			//收货人手机号码
			String receive_mobile = new String("".getBytes("ISO-8859-1"),"UTF-8");
			//如：13312341234
			
			
			//////////////////////////////////////////////////////////////////////////////////
			
			//把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", "create_partner_trade_by_buyer");
	        sParaTemp.put("partner", AlipayConfig.partner);
	        sParaTemp.put("seller_email", AlipayConfig.seller_email);
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("payment_type", payment_type);
			sParaTemp.put("notify_url", notify_url);
			sParaTemp.put("return_url", return_url);
			sParaTemp.put("out_trade_no", out_trade_no);
			sParaTemp.put("subject", subject);
			sParaTemp.put("price", price);
			sParaTemp.put("quantity", quantity);
			sParaTemp.put("logistics_fee", logistics_fee);
			sParaTemp.put("logistics_type", logistics_type);
			sParaTemp.put("logistics_payment", logistics_payment);
			sParaTemp.put("body", body);
			sParaTemp.put("show_url", show_url);
			sParaTemp.put("receive_name", receive_name);
			sParaTemp.put("receive_address", receive_address);
			sParaTemp.put("receive_zip", receive_zip);
			sParaTemp.put("receive_phone", receive_phone);
			sParaTemp.put("receive_mobile", receive_mobile);
			
			//建立请求
			//String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","确认");
			
			
			return AlipaySubmit.buildRequest(sParaTemp,"POST","确认");

		 
		} catch (UnsupportedEncodingException e) {
			return "转码错误";
		}
	}
	
	
	
	
	public void register() {

	}

	
	public String getAuthor() {
		return "kingapex";
	}

	
	public String getId() {
		return "alipayEscowPlugin";
	}

	
	public String getName() {
		return "支付宝担保交易接口";
	}

	 

	@Override
	public String onReturn(String ordertype) {
		try {
			
			
			Map<String,String> cfgparams = paymentManager.getConfigParams(this.getId());
			HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
			String key =cfgparams.get("key");
			String partner =cfgparams.get("partner");
			
			AlipayConfig.key=key;
			AlipayConfig.partner=partner;
			String param_encoding = cfgparams.get("param_encoding");  
			 
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			//商户订单号
			String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
	
			//支付宝交易号
			String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
	
			//交易状态
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");
	
			//买家支付宝账号
			String buyer_email = new String(request.getParameter("buyer_email").getBytes("ISO-8859-1"), "UTF-8");
			
			if(JavashopAlipayUtil.verify(param_encoding)){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码
	
				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
					//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//如果有做过处理，不执行商户的业务程序
				}
				
				//该页面可做页面美工编辑
				this.paySuccess(out_trade_no,trade_no,buyer_email,ordertype);
				return out_trade_no;
	
				//////////////////////////////////////////////////////////////////////////////////////////
			}else{
				//该页面可做页面美工编辑
				throw new RuntimeException("验证失败");  
			}
		} catch (Exception e) {
			throw new RuntimeException("验证失败");  
		}
	}


	@Override
	public String onRefund(PayEnable order, Refund refund, PaymentLog paymentLog) {
		AlipayRefund alipayRefund = SpringContextHolder.getBean("alipayRefund");
		return alipayRefund.onRefund(order, refund,paymentLog);
	}

}
