package com.enation.app.shop.component.payment.plugin.chinapay;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;

@Component("chinaPay")
public class ChinapayPlugin extends AbstractPaymentPlugin implements IPaymentEvent {
	
	/**
	 * 生成自动提交表单
	 * @param actionUrl
	 * @param paramMap
	 * @return
	 */
	private String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap) {
		StringBuilder html = new StringBuilder();
		html.append("<script language=\"javascript\">window.onload=function(){document.pay_form.submit();}</script>\n");
		html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");

		for (String key : paramMap.keySet()) {
			html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
		}
		html.append("</form>\n");
		return html.toString();
	}
	
	protected String payBack(String ordertype) {
		Map<String,String> params = paymentManager.getConfigParams(this.getId());
		String merPath = params.get("merPath");

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String merId = request.getParameter("merid");
		String orderno = request.getParameter("orderno");
		String transdate = request.getParameter("transdate");
		String amount = request.getParameter("amount");
		String currencycode = request.getParameter("currencycode");
		String transtype = request.getParameter("transtype");
		String status = request.getParameter("status");
		String checkvalue = request.getParameter("checkvalue");
		String Priv1 = request.getParameter("Priv1");

		chinapay.PrivateKey key = new chinapay.PrivateKey();
		chinapay.SecureLink t;
		boolean flag = key.buildKey(merId, 0, merPath);
		if (flag == false) {
			//System.out.println("build key error!");
			return "<div>系统设置错误</div>";
		}
		t = new chinapay.SecureLink(key);
		flag = t.verifyTransResponse(merId, orderno, amount, currencycode,
				transdate, transtype, status, checkvalue); // ChkValue为ChinaPay应答传回的域段
		if (flag == false) {
			// 签名验证错误处理
		}

		// 对一段字符串的签名验证
		String MsgBody = merId + orderno +  amount + currencycode + transdate + transtype + Priv1;
		flag = t.verifyAuthToken(MsgBody, checkvalue); // ChkValue2为ChinaPay应答传回的域段
		String ordersn = orderno.substring(2);
		if (flag) {
			// 签名验证错误处理
		} else {
			this.paySuccess(ordersn, transdate, "",ordertype);
		}

		return ordersn;
	}
	
	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		Map<String,String> params = paymentManager.getConfigParams(this.getId());
		String merId = params.get("merId");
		String merPath = params.get("merPath");
		String payUrl = params.get("payUrl");
		
		DecimalFormat df_amount = new DecimalFormat("000000000000");
		
		String ordId = order.getSn();
		if(ordId.length()>16) {
			ordId = ordId.substring(0,16);
		} else if(ordId.length()<16){
			String zero = "";
			for(int i=0;i<16-ordId.length();i++) {
				zero = zero + "0";
			}
			ordId = zero + ordId;
		}
		
		String amount = df_amount.format((int)(order.getNeedPayMoney() * 100));
		String CuryId = "156";
		String TransDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String TransType = "0001";
		String Priv1 = "memo";
		
		chinapay.PrivateKey key = new chinapay.PrivateKey();

		boolean flag = key.buildKey(merId, 0, merPath);
		if (flag == false) {
			//System.out.println("build key error!");
			return "<div>系统设置错误</div>";
		}
		chinapay.SecureLink t = new chinapay.SecureLink(key);
		String MsgBody = merId + ordId +  amount + CuryId + TransDate + TransType + Priv1;

		String ChkValue = t.Sign(MsgBody);

		Map<String,String> param = new HashMap<String,String>();
		param.put("MerId", merId);
		param.put("OrdId",  ordId);
		param.put("TransAmt", amount);
		param.put("CuryId", CuryId);
		param.put("TransDate", TransDate);
		param.put("TransType", TransType);
		param.put("Version", "20070129");
		param.put("BgRetUrl", this.getCallBackUrl(payCfg, order));
		param.put("PageRetUrl", this.getReturnUrl(payCfg, order));
		param.put("Priv1", Priv1);
		param.put("ChkValue", ChkValue);
		String html = "<div style='margin:50px auto;width:500px'>正在跳转到银联在线支付平台，请稍等...</div>";
		html = html + generateAutoSubmitForm(payUrl,param);//跳转到银联页面支付
		
		return html;
	}

	@Override
	public String onCallBack(String ordertype) {
		return payBack(ordertype);
	}
	
	@Override
	public String onReturn(String ordertype) {
		return payBack(ordertype);
	}

	@Override
	public String getId() {
		return "chinaPay";
	}

	@Override
	public String getName() {
		return "银联在线支付";
	}

	@Override
	public String onRefund(PayEnable order, Refund refund, PaymentLog paymentLog) {
		// TODO Auto-generated method stub
		return null;
	}

}
