package com.enation.app.shop.component.payment.plugin.paypal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.paypal.api.payments.Amount;
import com.enation.app.shop.component.payment.plugin.paypal.api.payments.Details;
import com.enation.app.shop.component.payment.plugin.paypal.api.payments.Item;
import com.enation.app.shop.component.payment.plugin.paypal.api.payments.ItemList;
import com.enation.app.shop.component.payment.plugin.paypal.api.payments.Links;
import com.enation.app.shop.component.payment.plugin.paypal.api.payments.Payer;
import com.enation.app.shop.component.payment.plugin.paypal.api.payments.Payment;
import com.enation.app.shop.component.payment.plugin.paypal.api.payments.PaymentExecution;
import com.enation.app.shop.component.payment.plugin.paypal.api.payments.RedirectUrls;
import com.enation.app.shop.component.payment.plugin.paypal.api.payments.Transaction;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.APIContext;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalRESTException;
import com.enation.app.shop.component.payment.plugin.paypal.base.rest.PayPalResource;
import com.enation.app.shop.component.payment.plugin.paypal.util.GenerateAccessToken;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.context.webcontext.ThreadContextHolder;


/**
 * Paypay支付插件
 * @author kingapex
 *2016年3月25日下午3:27:01
 */
@Component
public class PaypalPaymentPlugin extends AbstractPaymentPlugin implements IPaymentEvent {
private static 	Map<String, String> map = new HashMap<String, String>();

	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		
		this.init(payCfg);
		String href="";
		
		try {
			Payment payment = createPayment(payCfg,order);
			List<Links> links  =payment.getLinks();
			for (int i = 0; i < links.size(); i++) {
				Links link = links.get(i);
				if ( "REDIRECT".equals( link.getMethod() )){
					href = link.getHref();
				}
			}
		} catch (Exception e) {
			this.logger.error("paypal支付插件出现错误:"+e.getMessage());
			return "跳转Paypal支付出现问题，请检查PayPal参数是否正确。";
		}
		
		
		return "正在转向Paypal...<script> location.href='"+href+"'; </script>";
	}

	
	
	private static final Logger LOGGER = Logger
			.getLogger(PaypalPaymentPlugin.class);
 

	
	/**
	 * 初始化SDK参数
	 * @param payCfg
	 */
	public void init( PayCfg payCfg)  {
		
		// ##Load Configuration
		// Load SDK configuration for
		// the resource. This intialization code can be
		Map<String, String> params = paymentManager.getConfigParams(this.getId());

		Properties properties = new Properties();
		properties.put("service.EndPoint",params.get("service"));
		properties.put("clientId",params.get("clientId"));
		properties.put("clientSecret", params.get("clientSecret"));
		
		PayPalResource.initConfig(properties);
		
 

	}

 
 
	/**
	 * 调用api生成payment
	 * @param payCfg
	 * @param order
	 * @return
	 */
	public Payment createPayment(PayCfg payCfg,PayEnable order ) {
			String ordersn = order.getSn(); // 商户网站订单

			Payment createdPayment = null;
			// ###AccessToken
			// Retrieve the access token from
			// OAuthTokenCredential by passing in
			// ClientID and ClientSecret

			APIContext apiContext = getAPIContext();
			 NumberFormat nf = new DecimalFormat("###.00");
				String needPayMoney=  nf.format(order.getNeedPayMoney());
				
			// ###Details
			// Let's you specify details of a payment amount.
			Details details = new Details();
			details.setShipping("0");
			details.setSubtotal(needPayMoney);
			details.setTax("0");

			// ###Amount
			// Let's you specify a payment amount.
			Amount amount = new Amount();
			amount.setCurrency("USD");
			// Total must be equal to sum of shipping, tax and subtotal.
			amount.setTotal(needPayMoney);
			amount.setDetails(details);

			// ###Transaction
			// A transaction defines the contract of a
			// payment - what is the payment for and who
			// is fulfilling it. Transaction is created with
			// a `Payee` and `Amount` types
			Transaction transaction = new Transaction();
			transaction.setAmount(amount);
			EopSite site  =EopSite.getInstance();
			transaction .setDescription("支付"+site.getSitename()+"订单");
			transaction.setInvoiceNumber(ordersn);
			
			// ### Items
			Item item = new Item();
			item.setName("来自"+site.getSitename()+"的订单").setQuantity("1").setCurrency("USD").setPrice(needPayMoney);
			ItemList itemList = new ItemList();
			List<Item> items = new ArrayList<Item>();
			items.add(item);
			itemList.setItems(items);
			
			transaction.setItemList(itemList);
		 
			
			// The Payment creation API requires a list of
			// Transaction; add the created `Transaction`
			// to a List
			List<Transaction> transactions = new ArrayList<Transaction>();
			transactions.add(transaction);

		 
			// ###Payer
			// A resource representing a Payer that funds a payment
			// Payment Method
			// as 'paypal'
			Payer payer = new Payer();
			payer.setPaymentMethod("paypal");

			// ###Payment
			// A Payment Resource; create one using
			// the above types and intent as 'sale'
			Payment payment = new Payment();
			payment.setIntent("sale");
			payment.setPayer(payer);
			payment.setTransactions(transactions);
		 
			// ###Redirect URLs
			RedirectUrls redirectUrls = new RedirectUrls();
			
			String reutrnUrl = this.getReturnUrl(payCfg, order);
			String cancelUrl ="";
			redirectUrls.setCancelUrl(cancelUrl +"?ordersn="+ ordersn);
			
			redirectUrls.setReturnUrl(reutrnUrl+"?ordersn=" + ordersn);
			
			payment.setRedirectUrls(redirectUrls);

			
			// Create a payment by posting to the APIService
			// using a valid AccessToken
			// The return object contains the status;
			try {
				createdPayment = payment.create(apiContext);
				map.put(ordersn, createdPayment.getId());
			} catch (PayPalRESTException e) {
				 e.printStackTrace();
			}
		 
		return createdPayment;
	}
	
	@Override
	public String onCallBack(String ordertype) {
		
		try {
			return this.valid(ordertype);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return "error";
	 
	}

	/**
	 * 响应IPN，进行校验，如果校验成功，则更改订单状态
	 * @param ordertype
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private String valid(String ordertype) throws IOException, NoSuchAlgorithmException, KeyManagementException{
		Map<String, String> params = paymentManager.getConfigParams(this.getId());
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		String str = "cmd=_notify-validate";
 
		// 从 PayPal 出读取 POST 信息同时添加变量„cmd‟ Enumeration en = request.getParameterNames(); String str = "cmd=_notify-validate"; while(en.hasMoreElements()){
		Enumeration en = request.getParameterNames();
		while(en.hasMoreElements()){
			String paramName = (String)en.nextElement();
			String paramValue = request.getParameter(paramName);
			try {
				str = str + "&" + paramName + "=" +  URLEncoder.encode(paramValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	
		}
//		System.out.println("str->"+str);
		//建议在此将接受到的信息 str 记录到日志文件中以确认是否收到 IPN 信息
		//将信息 POST 回给 PayPal 进行验证
		//设置 HTTP 的头信息
//		 	System.out.println("正在连接...");
		  SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
	      String protocol = sslContext.getProtocol();
	      sslContext.init(null, null, null);
	      HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	      
		URL u= new URL(params.get("ipn"));
		HttpsURLConnection uc =(HttpsURLConnection) u.openConnection();
		
		uc.setDoOutput(true);
		uc.setConnectTimeout(50000);
		uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
		
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		pw.println(str);
		pw.close();
		
		//接受 PayPal 对 IPN 回发的回复信息
		BufferedReader in= new BufferedReader(new InputStreamReader(uc.getInputStream())); 
		String res = in.readLine();
		in.close();
		
		//将 POST 信息分配给本地变量,可以根据您的需要添加 //该付款明细所有变量可参考: 
		//https://www.paypal.com/IntegrationCenter/ic_ipn-pdt-variable-reference.html
		
		String paymentStatus = request.getParameter("payment_status");
		String txnId = request.getParameter("txn_id");
		String invoice = request.getParameter("invoice");
// 		System.out.println("res ->"+res);
// 		System.out.println("invoice ->"+invoice);
		
		if(res==null){return "error";}
		//获取 PayPal 对回发信息的回复信息,判断刚才的通知是否为 PayPal 发出的 
		if(res.equals("VERIFIED")) {
			if("Completed".equals(paymentStatus)){
				this.paySuccess(invoice, txnId, txnId, ordertype);
				return "ok";
			}
	 
		}else if(res.equals("INVALID")) {  
			return "error";
		}else {
			return "error";
		//处理其他错误
		}
		
		return "error";
	}
	
	
	/**
	 * 生成API Context
	 * @return
	 */
	private APIContext getAPIContext(){
		APIContext apiContext = null;
		String accessToken = null;
		try {
			accessToken = GenerateAccessToken.getAccessToken();

			// ### Api Context
			// Pass in a `ApiContext` object to authenticate
			// the call and to send a unique request id
			// (that ensures idempotency). The SDK generates
			// a request id if you do not pass one explicitly.
			apiContext = new APIContext(accessToken);
			// Use this variant if you want to pass in a request id
			// that is meaningful in your application, ideally
			// a order id.
			/*
			 * String requestId = Long.toString(System.nanoTime(); APIContext
			 * apiContext = new APIContext(accessToken, requestId ));
			 */
		} catch (PayPalRESTException e) {
			 this.LOGGER.error("生成 api context 出错", e);
		}
		return  apiContext;
	}
	
	/**
	 * 响应paypay return ，执行付款操作
	 */
	@Override
	public String onReturn(String ordertype) {
		APIContext apiContext = getAPIContext();
		HttpServletRequest req  = ThreadContextHolder.getHttpRequest();
		Payment payment = new Payment();
		String ordersn= req.getParameter("ordersn") ;
		if (ordersn != null) {
			
			payment.setId(map.get(ordersn));
		}

		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(req.getParameter("PayerID"));
		try {
			  payment.execute(apiContext, paymentExecution);
			  map.remove(ordersn);
			return ordersn;
		} catch (PayPalRESTException e) {
			 this.LOGGER.error("执行payment出错", e);
			 throw new RuntimeException("执行付款出错");
		}
		
	}

	@Override
	public String getId() {
		
		return "paypalPaymentPlugin";
	}

	@Override
	public String getName() {
		
		return "paypal支付插件";
	}



	@Override
	public String onRefund(PayEnable order, Refund refund, PaymentLog paymentLog) {
		// TODO Auto-generated method stub
		return null;
	}

}
