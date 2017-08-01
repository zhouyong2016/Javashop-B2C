package com.enation.app.shop.front.tag.order;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.PaymentResult;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;

import freemarker.template.TemplateModelException;

/**
 * 支付结果标签
 * @author kingapex
 *2013-9-5上午9:34:50
 */
@Component
@Scope("prototype")
public class PaymentResultTag extends BaseFreeMarkerTag {

	
	/**
	 * 支付结果标签
	 * 些标签必须写在路径为：/payment_result.html的模板中。用于第三方支付后，显示支付结果。
	 * @param 无
	 * @return 支付结果，PaymentResult型
	 * {@link PaymentResult}
	 */
	@Override
	protected Object exec(Map p) throws TemplateModelException {
		PaymentResult paymentResult = new PaymentResult();
		
		try{
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			
			String url = RequestUtil.getRequestUrl(request);
			String pluginid = null;
			String ordertype =null;
			String[] params =this.getPluginid(url);
			
			ordertype = params[0];
			pluginid = params[1];
			if (null == pluginid) {
				paymentResult.setResult(0);
				paymentResult.setError("参数不正确");
			} else {
				IPaymentEvent paymentPlugin = SpringContextHolder.getBean(pluginid);
				String ordersn = paymentPlugin.onReturn(ordertype);
				paymentResult.setResult(1);
				paymentResult.setOrdersn(ordersn);
				paymentResult.setOrderType(ordertype);
			}
			
			
		}catch(Exception e){
			//e.printStackTrace();
			this.logger.error("支付失败",e);
			paymentResult.setResult(0);
			paymentResult.setError(e.getMessage());
			
		}		
		
		
		return paymentResult;
	}
	
	private String[] getPluginid(String url) {
		String pluginid = null;
		String ordertype= null;
		String[] params = new String[2];
		String pattern = ".*/(\\w+)_(\\w+)_(payment-result).html(.*)";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			ordertype = m.replaceAll("$1");
			pluginid = m.replaceAll("$2");
			params[0]=ordertype;
			params[1]=pluginid;
			return params;
		} else {
			String pattern2 = ".*/(\\w+)_(\\w+)_(credit-result).html(.*)";
			Pattern p2 = Pattern.compile(pattern2, 2 | Pattern.DOTALL);
			Matcher m2 = p2.matcher(url);
			if(m2.find()){
				ordertype = m2.replaceAll("$1");
				pluginid = m2.replaceAll("$2");
				params[0]=ordertype;
				params[1]=pluginid;
				return params;
			}
			return null;
		}
	}
}
