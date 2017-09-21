package com.enation.app.shop.component.payment.plugin.weixin;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.order.plugin.payment.IPaymentQrCodeEvent;
import com.enation.app.shop.core.order.plugin.payment.IPaymentRedPackEvent;
import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;


@SuppressWarnings("unchecked")
@Component
public class WeixinPayPlugin extends AbstractPaymentPlugin implements
		IPaymentEvent,IPaymentRedPackEvent,IPaymentQrCodeEvent {
	public static final String OPENID_SESSION_KEY = "weixin_openid";
	public static final String UNIONID_SESSION_KEY = "weixin_unionid";
	public static Random r = new Random();

	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());
		String mchid = cfgparams.get("mchid");
		String appid = cfgparams.get("appid");
		String key = cfgparams.get("key");

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String openid = (String) ThreadContextHolder.getSession()
				.getAttribute(WeixinPayPlugin.OPENID_SESSION_KEY);

		String unionid = (String) ThreadContextHolder.getSession()
				.getAttribute(WeixinPayPlugin.UNIONID_SESSION_KEY);

		if (StringUtil.isEmpty(openid)&&StringUtil.isEmpty(unionid)) {
			boolean isMobile = HttpRequestDeviceUtils.isMobileDevice(request);

			//微信支付是在微信内置浏览器中进行支付，取消weixinjump跳转的方式2016.12.23 by LYH
			return "<script>if(confirm('请在微信内置浏览器中进行支付')){"
					+ "window.location.href='"+SystemSetting.getContext_path()+"/index.html'}else{"//返回到商城首页
					+ "window.location.href='"+SystemSetting.getContext_path()+"/index.html'} "//返回到商城首页
					+ "</script>";
		}
 
		Map params = new TreeMap();
		params.put("appid", appid);
		params.put("mch_id", mchid);
		params.put("nonce_str", StringUtil.getRandStr(10));
		String original_sn=order.getSn();
		String body = "网店订单["+ original_sn +"]";
		params.put("body", body);
		String OrderNo=order.getSn()+StringUtil.getRandStr(6);//"20141104000003789253";
		params.put("out_trade_no",OrderNo);

		// 应付转为分
		Double money = order.getNeedPayMoney();
		params.put("total_fee", toFen(money));
		params.put("spbill_create_ip", request.getRemoteAddr());
		params.put("notify_url", this.getCallBackUrl(payCfg, order));
		params.put("trade_type", "JSAPI");
		params.put("openid", openid);

		String sign = WeixinUtil.createSign(params, key);
		params.put("sign", sign);
		String result = "";
		try {
			String xml = WeixinUtil.mapToXml(params);
			Document resultDoc = WeixinUtil.post(
					"https://api.mch.weixin.qq.com/pay/unifiedorder", xml);

			/** 调试时可打开此处注释，以观察微信返回的数据 **/
			// this.logger.debug("-----------return xml ------------");
			// this.logger.debug( WeixinUtil.doc2String(resultDoc) );
			Element rootEl = resultDoc.getRootElement();
			String return_code = rootEl.element("return_code").getText(); // 返回码
			if ("SUCCESS".equals(return_code)) {

				String result_code = rootEl.element("result_code").getText(); // 业务码

				if ("SUCCESS".equals(result_code)) {
					String prepay_id = rootEl.element("prepay_id").getText(); // 预支付订单id
					
					result = this.getPayScript(prepay_id, appid, key,original_sn);

				} else {
					
					String err_code = rootEl.element("err_code").getText();
					String err_code_des = rootEl.element("err_code_des").getText();
					result = "<script>alert('支付意外错误，请联系技术人员:"
							+ err_code + "【"+err_code_des+"】')</script>";
				}
			} else {
				result = "<script>alert('支付意外错误，请联系技术人员:" + return_code + "')</script>";
				if ("FAIL".equals(return_code)) {
					String return_msg = resultDoc.getRootElement()
							.element("return_msg").getText(); // 错误信息
					this.logger.error("微信端返回错误" + return_code + "["
							+ return_msg + "]");
					this.logger.debug("-----------post xml-----------");
					this.logger.debug(xml);
				}
			}

		} catch (RuntimeException e) {
			result = "alert('支付意外错误，请联系技术人员')";
			this.logger.error("支付意外错误", e);
		}
		System.out.println(result);
		return result;
	}

	/**
	 * 生成支付的脚本
	 * 
	 * @param prepay_id
	 *            预支付订单id
	 * @return
	 */
	private String getPayScript(String prepay_id, String appid, String weixinkey,String original_sn) {

		Map<String, String> params = new TreeMap();
		params.put("appId", appid);
		params.put("nonceStr", StringUtil.getRandStr(10));
		params.put("timeStamp", String.valueOf(DateUtil.getDateline()));
		params.put("package", "prepay_id=" + prepay_id);
		params.put("signType", "MD5");
		String sign = WeixinUtil.createSign(params, weixinkey);
		params.put("paySign", sign);

		FreeMarkerPaser fp = FreeMarkerPaser.getInstance();
		fp.setClz(this.getClass());
		fp.setPageName("payscript");

		StringBuffer payStr = new StringBuffer();
		payStr.append("WeixinJSBridge.invoke('getBrandWCPayRequest',{");

		int i = 0;
		for (String key : params.keySet()) {
			String value = params.get(key);

			if (i != 0)
				payStr.append(",");
			payStr.append("'" + key + "':'" + value + "'");
			i++;

		}

		payStr.append("}");
		String ctx =ThreadContextHolder.getHttpRequest().getContextPath();
		if(  ctx.equals("/")){
			ctx="";
		}
		payStr.append(",function(res){  if( 'get_brand_wcpay_request:ok'==res.err_msg ) { alert('支付成功'); }else{ alert('支付失败'); }  location.href='"
				+ ctx
				+ "/member/order-detail.html?ordersn="+original_sn+"';  }");
		
		payStr.append(");");
		fp.putData("payStr", payStr);
		return fp.proessPageContent();
	}

	private String toFen(Double money) {
		BigDecimal fen=   BigDecimal.valueOf(money).multiply(new BigDecimal(100)) ;
		NumberFormat nFormat=NumberFormat.getNumberInstance();
        nFormat.setMaximumFractionDigits(0); 
        nFormat.setGroupingUsed(false);
		return  nFormat.format(fen);


	}

	public static void main(String[] args) {
		// String doc
		// =FileUtil.read("/Users/kingapex/work/temp/test.xml","UTF-8");
		// System.out.println(doc);
		// WeixinUtil.post("http://localhost:8080/api/shop/weixin!test.do",
		// doc);
//		Double a = 99D;
//		String str = String.valueOf(a);
//		str = str.substring(0, str.indexOf("."));
//		System.out.println(str);
		
//		String s  = StringUtil.toUTF8("网店订单[93939933]");
//		System.out.println(s);
//		try {
//			System.out.println(URLDecoder.decode("%E7%BD%91%E5%BA%97%E8%AE%A2%E5%8D%95","utf-8"));
//			
//			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//			//System.out.println(format.format(new Date()));
//			System.out.println(getRandomString(32));
//			
//			NumberFormat nf = new DecimalFormat("#");
//			double d =23.00;
//			System.out.println(nf.format(d));
//			
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public String onCallBack(String ordertype) {

		Map<String, String> cfgparams = paymentManager.getConfigParams(this
				.getId());
		String key = cfgparams.get("key");

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Map map = new HashMap();

		try {
			SAXReader saxReadr = new SAXReader();
			Document document = saxReadr.read(request.getInputStream());

			/** 调试时可以打开下面注释 ，以观察通知的xml内容 **/
			// String docstr = WeixinUtil.doc2String(document);
			// this.logger.debug("--------post xml-------");
			// this.logger.debug(docstr);
			// this.logger.debug("--------end-------");

			Map<String, String> params = WeixinUtil.xmlToMap(document);

			String return_code = params.get("return_code");
			String result_code = params.get("result_code");
			/** 微信生成的订单号 */
			String transaction_id = params.get("transaction_id");
			if ("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)) {
				String ordersn = params.get("out_trade_no");
				

				String sign = WeixinUtil.createSign(params, key);
				if (sign.equals(params.get("sign"))) {
					ordersn=ordersn.substring(0, ordersn.length()-6);
					this.paySuccess(ordersn, transaction_id,"", ordertype);
					map.put("return_code", "SUCCESS");
					this.logger.debug("签名校验成功");
				} else {
					this.logger.debug("-----------签名校验失败---------");
					this.logger.debug("weixin sign:" + params.get("sign"));
					this.logger.debug("my sign:" + sign);
					map.put("return_code", "FAIL");
					map.put("return_msg", "签名失败");
				}
			} else {
				map.put("return_code", "FAIL");
				this.logger.debug("微信通知的结果为失败");
			}

		} catch (IOException e) {
			map.put("return_code", "FAIL");
			map.put("return_msg", "");
			e.printStackTrace();
		} catch (DocumentException e) {
			map.put("return_code", "FAIL");
			map.put("return_msg", "");
			e.printStackTrace();
		}
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		response.setHeader("Content-Type", "text/xml");
		return WeixinUtil.mapToXml(map);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.payment.IPaymentRedPackEvent#sendRedPack(com.enation.app.base.core.model.Member, java.util.Map)
	 */
	@Override
	public String sendRedPack(Map member,Double money,PayCfg payCfg, Map other) {
		Map params = new TreeMap();
		    
		Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());
		String mchid = cfgparams.get("mchid");
		String appid = cfgparams.get("appid");
		String key = cfgparams.get("key");
		
		//HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		//String openid = (String) ThreadContextHolder.getSessionContext().getAttribute(WeixinPayPlugin.OPENID_SESSION_KEY);
		
		params.put("nonce_str", getRandomString(32));		//随机字符串
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String date = format.format(new Date());
		NumberFormat nf = new DecimalFormat("#");
		
		params.put("mch_billno", mchid+""+date+""+getRandom());	//商户订单号
		params.put("mch_id", mchid);					//商户号
		params.put("wxappid", appid);					//公众账号appid
		params.put("send_name", "javashop");			//商户名称
		params.put("re_openid", member.get("wx_openid").toString());		//用户openid
		params.put("total_amount", nf.format(money*100)+"");		//付款金额 单位：分
		params.put("total_num", "1");					//红包发放总人数
		params.put("wishing", "test");					//红包祝福语
		params.put("client_ip", "123.57.21.139");		//Ip地址
		params.put("act_name", "test");					//活动名称
		params.put("remark", "test");					//备注
		
		try {
			
			String sign = WeixinUtil.createSign(params, key);
			params.put("sign", sign);			//签名
			
			String xml = WeixinUtil.mapToXml(params);
			Document resultDoc = WeixinUtil.verifyCertPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack", xml,mchid);
			Element rootEl = resultDoc.getRootElement();
			
			if(rootEl.element("return_code").getText().endsWith("SUCCESS")){
				
			}else{
				throw new RuntimeException(rootEl.element("err_code_des").getText());
			}
			
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage(),e);
			
		} 
		return null;
	}
	
	@Override
	public String onReturn(String ordertype) {
		
		return null;
	}

	@Override
	public String getId() {

		return "weixinPayPlugin";
	}

	
	@Override
	public String getName() {
		return "微信支付";
	}

	
	public static String getRandom(){
        long num = Math.abs(r.nextLong() % 10000000000L);
        String s = String.valueOf(num);
        for(int i = 0; i < 10-s.length(); i++){
            s = "0" + s;
        }
        return s;
    }
	
	private static int getRandom(int count) {
	    return (int) Math.round(Math.random() * (count));
	}
	 
	private static String string = "abcdefghijklmnopqrstuvwxyz1234567890";   
	 
	private static String getRandomString(int length){
	    StringBuffer sb = new StringBuffer();
	    int len = string.length();
	    for (int i = 0; i < length; i++) {
	        sb.append(string.charAt(getRandom(len-1)));
	    }
	    return sb.toString();
	}
	/**
	 * 获取wap的url
	 * @return
	 */
	public static String getWapDomainUrl(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int port = request.getServerPort();
		
		String portstr="";
		if(port!=80){
			portstr=":"+port;
		}
		String contextPath = request.getContextPath();
		if(contextPath.equals("/")){
			contextPath="";
		}
	 
		String severname= SystemSetting.getWap_domain();
		String url  = "http://"+severname+portstr+contextPath;
		
		return url;
		
	}

	/**
	 * 生成支付二维码
	 * 此处微信支付扫码支付，使用的方式是 微信扫码支付 模式二
	 * @param payCfg
	 * @param order
	 * @return
	 */
	@Override
	public String onPayQrCode(PayCfg payCfg, PayEnable order) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());
		if(cfgparams==null){
			throw new RuntimeException("请设置微信支付商户参数!");
		}
		String mchid = cfgparams.get("mchid");
		String appid = cfgparams.get("appid");
		String key = cfgparams.get("key");
		
		String original_sn=order.getSn();
		String body = "网店订单["+ original_sn +"]";
		String OrderNo=order.getSn();
		
		Map params = new TreeMap();
		params.put("appid", appid);
		params.put("mch_id", mchid);
		params.put("nonce_str", StringUtil.getRandStr(10));
		params.put("body", body);
		params.put("out_trade_no",OrderNo);

		// 应付转为分
		Double money = order.getNeedPayMoney();
		params.put("total_fee", toFen(money));
		params.put("spbill_create_ip", localIp());
		params.put("notify_url", this.getCallBackUrl(payCfg, order));
		params.put("trade_type", "NATIVE");
		String sign = WeixinUtil.createSign(params, key);
		params.put("sign", sign);
		String code_url = "";
		
		try {
			String xml = WeixinUtil.mapToXml(params);
			//System.out.println(xml);
			Document resultDoc = WeixinUtil.post(
					"https://api.mch.weixin.qq.com/pay/unifiedorder", xml);

			/** 调试时可打开此处注释，以观察微信返回的数据 **/
			//this.logger.debug("-----------return xml ------------");
			//this.logger.debug( WeixinUtil.doc2String(resultDoc) );
			//System.out.println(WeixinUtil.doc2String(resultDoc));
			Element rootEl = resultDoc.getRootElement();
			code_url= rootEl.element("code_url").getText(); // 返回码
			

		} catch (RuntimeException e) {
			this.logger.error("微信生成支付二维码错误", e);
		}
		
		return code_url;
	}

	/**
	 * 查询订单微信扫码支付后，调用此方法查询付款状态
	 * @author xulipeng
	 * 2016年07月20日
	 */
	@Override
	public String onPayStatus(PayCfg payCfg, PayEnable order) {

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());
		
		if(cfgparams==null){
			throw new RuntimeException("请设置微信支付商户参数!");
		}
		
		String mchid = cfgparams.get("mchid");
		String appid = cfgparams.get("appid");
		String key = cfgparams.get("key");
		
		Map params = new TreeMap();
		params.put("appid", appid);
		params.put("mch_id", mchid);
		params.put("nonce_str", StringUtil.getRandStr(10));
		params.put("out_trade_no",order.getSn());
		String sign = WeixinUtil.createSign(params, key);
		params.put("sign", sign);
		
		try {
			String xml = WeixinUtil.mapToXml(params);
			Document resultDoc = WeixinUtil.post(
					"https://api.mch.weixin.qq.com/pay/orderquery", xml);

			/** 调试时可打开此处注释，以观察微信返回的数据 **/
			//this.logger.debug("-----------return xml ------------");
			//this.logger.debug( WeixinUtil.doc2String(resultDoc) );
			//System.out.println(WeixinUtil.doc2String(resultDoc));
			Element rootEl = resultDoc.getRootElement();
			String trade_state = rootEl.element("trade_state").getText();
			String transaction_id = rootEl.element("transaction_id").getText();
			//判断支付状态是否成功
			if("SUCCESS".equals(trade_state)){
				this.paySuccess(order.getSn(), transaction_id,"", order.getOrderType());
				return "SUCCESS";
			}
			
		} catch (Exception e) {
			this.logger.debug("微信扫码支付失败",e);
			
		}
		
		return null;
	}
	
	// 暂时用此方法代替 request.getRemoteAddr() 获取本机ip
	private static  String localIp(){
	        String ip = null;
	        Enumeration allNetInterfaces;
	         try {
	        allNetInterfaces = NetworkInterface.getNetworkInterfaces();            
	            while (allNetInterfaces.hasMoreElements()) {
	               NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
	               List<InterfaceAddress> InterfaceAddress = netInterface.getInterfaceAddresses();
	               for (InterfaceAddress add : InterfaceAddress) {
	                   InetAddress Ip = add.getAddress();
	                   if (Ip != null && Ip instanceof Inet4Address) {
	                       ip = Ip.getHostAddress();
	                   }
	               }
	           }
	       } catch (SocketException e) {
	           // TODO Auto-generated catch block        
	           //this.logger.warn("获取本机Ip失败:异常信息:"+e.getMessage());
	       }
	       return ip;
	   }
	/**
	 * 微信退款实现
	 */
	@Override
	public String onRefund(PayEnable order,Refund refund,PaymentLog paymentLog) {
		/** 调用退款方法 */
		WeixinRefund winxinRefund=SpringContextHolder.getBean("weixinRefund");
		return winxinRefund.onRefund(order, refund, paymentLog);
	}
	
}
