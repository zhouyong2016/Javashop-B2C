package com.enation.app.shop.component.payment.plugin.unionpay;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.unionpay.sdk.AcpService;
import com.enation.app.shop.component.payment.plugin.unionpay.sdk.LogUtil;
import com.enation.app.shop.component.payment.plugin.unionpay.sdk.SDKConfig;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IRefundManager;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;
 
 
/**
 * 中国银联在线支付
 * @author xulipeng
 *	2015年09月03日13:29:34
 *
 * v2.0 ：重构，采用界面化配置参数，并将sdk源码引入，去掉jar的依赖
 * by kingapex 2017年01月22日 13:29:34
 */
@Component("unPay")
public class UnionpayPlugin extends AbstractPaymentPlugin implements IPaymentEvent {
	
	public static String encoding = "UTF-8";
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IRefundManager refundManager;
	/**
	 * 5.0.0
	 */
	public static String version = "5.0.0";
	
	private static int is_load=0;

	
	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
	 
		String html = getPayHtml(payCfg,order);
		return  html;
	} 

	@Override
	public String onCallBack(String ordertype) {
		
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String respCode = request.getParameter("respCode");	//应答码			参考：https://open.unionpay.com/ajweb/help/respCode/respCodeList
			String respMsg = request.getParameter("respMsg");	//应答信息
			
			//因为 银联要求的订单号不能有-号，在javashop中的子订单是有这个符号的，所以改用orderid
			String orderid = request.getParameter("orderId");	//商户订单号
			String queryId = request.getParameter("queryId");	//流水号
			String tradeno = request.getParameter("traceNo");	//系统追踪号
			 
			
			//因为 银联要求的订单号不能有-号，在javashop中的子订单是有这个符号的，所以改用orderid
			if(!StringUtil.isEmpty(orderid)){
				orderid  = orderid.replaceAll("A","");
			}
			
			//所以在这里要读一次订单，再得到ordersn
			Order order  = orderManager.get(StringUtil.toInt(orderid,0));
			if(order == null ){
				throw new RuntimeException("订单不存在");
			}
			String ordersn  = order.getSn();
			
			if(respCode.equals("00")){	//交易成功
				
				if(validaeData()){
					this.paySuccess(ordersn, queryId, "",ordertype);
					return ordersn;
				}else{
					throw new RuntimeException("验证失败");
				}
				
			}else{
				throw new RuntimeException("验证失败，错误信息:"+respMsg);
			}
			
		} catch (Exception e) {
			throw new RuntimeException("验证失败");
		}
	}

	@Override
	public String onReturn(String ordertype) {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String respCode = request.getParameter("respCode");	//应答码			参考：https://open.unionpay.com/ajweb/help/respCode/respCodeList
			String respMsg = request.getParameter("respMsg");	//应答信息
			
			//因为 银联要求的订单号不能有-号，在javashop中的子订单是有这个符号的，所以改用orderid
			String orderid = request.getParameter("orderId");	//商户订单号
			String queryId = request.getParameter("queryId");	//流水号
			String tradeno = request.getParameter("traceNo");	//系统追踪号
			
			if(respCode.equals("00")){	//交易成功
				if(validaeData()){
					//因为 银联要求的订单号不能有-号，在javashop中的子订单是有这个符号的，所以改用orderid
					if(!StringUtil.isEmpty(orderid)){
						orderid  = orderid.replaceAll("A","");
					}
					
					//所以在这里要读一次订单，再得到ordersn
					Order order  = orderManager.get(StringUtil.toInt(orderid,0));
					if(order == null ){
						throw new RuntimeException("订单不存在");
					}
					String ordersn  = order.getSn();
					
					
					this.paySuccess(ordersn, queryId,"", ordertype);
					return ordersn;
				}else{
					throw new RuntimeException("验证失败");
				}
			}else{
				throw new RuntimeException("验证失败，错误信息:"+respMsg);
			}
			
		} catch (Exception e) {
			throw new RuntimeException("验证失败");
		}
		
	}

	@Override
	public String getId() {
		return "unPay";
	}

	@Override
	public String getName() {
		return "中国银联支付";
	}
	
	/**
	 * 加载参数配置
	 * 思路：先加载默认的properties中的，然后再将用户配置的覆盖过去
	 * @param params
	 */
	private synchronized void loadConfig(Map<String,String> params){
				
		
		//是否是测试模式
		String testModel = params.get("testModel");
 
		//如果已经加载，并且是生产环境则不用再加载
		if(is_load==1 && "no".equals(testModel) ){
			return ;
		}
		
		//签名证书路径
		String signCert = params.get("signCert");
		
		//签名证书密码
		String pwd = params.get("pwd");
		
		//验证签名证书目录
		String validateCert =  params.get("validateCert");
		
		//敏感信息加密证书路径
		String encryptCert = params.get("encryptCert");
		
		
		if(StringUtil.isEmpty(signCert)){
			throw new RuntimeException("参数配置错误");
		}
		if(StringUtil.isEmpty(pwd)){
			throw new RuntimeException("参数配置错误");
		}
		if(StringUtil.isEmpty(validateCert)){
			throw new RuntimeException("参数配置错误");
		}
	 
		
		InputStream in  = FileUtil.getResourceAsStream("com/enation/app/shop/component/payment/plugin/unionpay/acp_sdk.properties");
		Properties pro = new  Properties();
		try {
			
			pro.load(in);
			pro.setProperty("acpsdk.signCert.path", signCert);
			pro.setProperty("acpsdk.signCert.pwd", pwd);
			pro.setProperty("acpsdk.validateCert.dir", validateCert);
			
			
			//设置测试环境的提交地址
			if("yes".equals(testModel)){
				pro.setProperty("acpsdk.frontTransUrl", "https://gateway.test.95516.com/gateway/api/frontTransReq.do");
				pro.setProperty("acpsdk.backTransUrl", "https://gateway.test.95516.com/gateway/api/backTransReq.do");
				pro.setProperty("acpsdk.singleQueryUrl", "https://gateway.test.95516.com/gateway/api/queryTrans.do");
				pro.setProperty("acpsdk.batchTransUrl", "https://gateway.test.95516.com/gateway/api/batchTrans.do");
				pro.setProperty("acpsdk.fileTransUrl", "https://filedownload.test.95516.com/");
				pro.setProperty("acpsdk.appTransUrl", "https://gateway.test.95516.com/gateway/api/appTransReq.do");
				pro.setProperty("acpsdk.cardTransUrl", "https://gateway.test.95516.com/gateway/api/cardTransReq.do");
			}
			
			//设置生产环境的提交地址
			if("no".equals(testModel)){
				pro.setProperty("acpsdk.frontTransUrl", "https://gateway.95516.com/gateway/api/frontTransReq.do");
				pro.setProperty("acpsdk.backTransUrl", "https://gateway.95516.com/gateway/api/backTransReq.do");
				pro.setProperty("acpsdk.singleQueryUrl", "https://gateway.95516.com/gateway/api/queryTrans.do");
				pro.setProperty("acpsdk.batchTransUrl", "https://gateway.95516.com/gateway/api/batchTrans.do");
				pro.setProperty("acpsdk.fileTransUrl", "https://filedownload.95516.com/");
				pro.setProperty("acpsdk.appTransUrl", "https://gateway.95516.com/gateway/api/appTransReq.do");
				pro.setProperty("acpsdk.cardTransUrl", "https://gateway.95516.com/gateway/api/cardTransReq.do");
			
			}
			
			
			if(!StringUtil.isEmpty(encryptCert)){
				pro.setProperty("acpsdk.encryptCert.path", encryptCert);
			}
			
			SDKConfig.getConfig().loadProperties(pro);

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		is_load=1;
		
		
	}
	
	
	/**
	 * 获取付款的html
	 * @param payCfg
	 * @param order
	 * @return
	 */
	public String getPayHtml(PayCfg payCfg, PayEnable order){
	
		
		Map<String,String> params = paymentManager.getConfigParams(this.getId());
		String merId = params.get("merId");
		
		try {
			this.loadConfig(params);
		} catch (RuntimeException e) {
			return e.getMessage();
		}
		
		/**
		 * 组装请求报文
		 */
		Map<String, String> data = new HashMap<String, String>();
		// 版本号
		data.put("version", "5.0.0");
		// 字符集编码 默认"UTF-8"
		data.put("encoding", "UTF-8");
		// 签名方法 01 RSA
		data.put("signMethod", "01");
		// 交易类型 01-消费
		data.put("txnType", "01");
		// 交易子类型 01:自助消费 02:订购 03:分期付款
		data.put("txnSubType", "01");
		// 业务类型
		data.put("bizType", "000201");
		// 渠道类型，07-PC，08-手机
		data.put("channelType", "07");
		// 前台通知地址 ，控件接入方式无作用
		data.put("frontUrl", this.getReturnUrl(payCfg, order));
		// 后台通知地址
		data.put("backUrl", this.getCallBackUrl(payCfg, order));
		// 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
		data.put("accessType", "0");
		// 商户号码，请改成自己的商户号
		data.put("merId", merId);
		// 商户订单号，8-40位数字字母
		//因为 银联要求的订单号不能有-号，在javashop中的子订单是有这个符号的，所以改用orderid
		String  orderid = "AAAAAAAA"+order.getOrder_id();
		
		int length  =  orderid.length();
		//保证不小于8位，且不大于40位
		orderid = orderid.substring( (length-40 ) <0?0:length-40,length);
		
		data.put("orderId", orderid);
		// 订单发送时间，取系统时间
		data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// 交易金额，单位分
		Double txnAmt = CurrencyUtil.mul(order.getNeedPayMoney(), 100);
		 
		data.put("txnAmt", ""+txnAmt.intValue());
		// 交易币种
		data.put("currencyCode", "156");
		// 请求方保留域，透传字段，查询、通知、对账文件中均会原样出现
		// data.put("reqReserved", "透传信息");
		// 订单描述，可不上送，上送时控件中会显示该信息
		// data.put("orderDesc", "订单描述");

		Map<String, String> submitFromData = AcpService.sign(data,encoding);  //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		
		// 交易请求url 从配置文件读取
		String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();
		
		/**
		 * 创建表单
		 */
		String html = AcpService.createAutoFormHtml(requestFrontUrl, submitFromData,encoding);   //生成自动跳转的Html表单
		
//		System.out.println("打印请求HTML，此为请求报文，为联调排查问题的依据："+html);
		return html;
	}
 
	 
	private boolean validaeData(){
		HttpServletRequest req = ThreadContextHolder.getHttpRequest();
		Map<String, String> respParam = getAllRequestParam(req);

		// 打印请求报文
		LogUtil.printRequestLog(respParam);

		Map<String, String> valideData = null;
		if (null != respParam && !respParam.isEmpty()) {
			Iterator<Entry<String, String>> it = respParam.entrySet()
					.iterator();
			valideData = new HashMap<String, String>(respParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				try {
					value = new String(value.getBytes(encoding), encoding);
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			 
				valideData.put(key, value);
			}
		}
		if (!AcpService.validate(valideData, encoding)) {
			LogUtil.writeLog("验证签名结果[失败].");
			return false;
			
		} else {
			LogUtil.writeLog("验证签名结果[成功].");
			return true;
		}
	}
	
	
	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(
			final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				if (res.get(en) == null || "".equals(res.get(en))) {
					// System.out.println("======为空的字段名===="+en);
					res.remove(en);
				}
			}
		}
		return res;
	}
/*
 * (non-Javadoc)
 * @see com.enation.app.shop.core.order.plugin.payment.IPaymentEvent#onRefund(com.enation.app.shop.core.order.model.PayEnable, com.enation.app.shop.core.order.model.Refund, com.enation.app.shop.core.order.model.PaymentLog)
 */
	@Override
	public String onRefund(PayEnable order, Refund refund, PaymentLog paymentLog) {
		/** 调用退款方法 */
		UnionpayRefund unionpayRefund=SpringContextHolder.getBean("unionpayRefund");
		return unionpayRefund.onRefund(order, refund, paymentLog);
	}
}
