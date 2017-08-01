package com.enation.app.shop.front.api.order;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;

/**
 * 支付回调api
 * @author Sylow
 * @version v2.0,2016年2月20日
 * @since v6.0
 */
@Controller
@RequestMapping("/api/shop/*payment-callback")
public class PaymentCallbackApiController {

	private Logger logger = Logger.getLogger(getClass());
	
	@ResponseBody
	@RequestMapping(value="/execute")
	public String execute() {
		try {
			HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
			String url = RequestUtil.getRequestUrl(httpRequest);
			String pluginid = null;
			String ordertype = null;
			String[] params = this.getPluginid(url);

			ordertype = params[0];
			pluginid = params[1];

			String error = "参数不正确";

			if (null == pluginid) {
				return error;
			}

			if (null == ordertype) {
				return error;
			}

			IPaymentEvent paymentPlugin = SpringContextHolder.getBean(pluginid);
			String result = paymentPlugin.onCallBack(ordertype);

			this.logger.debug("支付回调结果:" + result);
			return result;
		} catch (Exception e) {
			this.logger.error("支付回调发生错误", e);
			return "error";
		}

	}

	/**
	 * @param url
	 * @return 
	 */
	private String[] getPluginid(String url) {
		String pluginid = null;
		String ordertype = null;
		String[] params = new String[2];
		String pattern = ".*/(\\w+)_(\\w+)_(payment-callback)/execute.do(.*)";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			ordertype = m.replaceAll("$1");
			pluginid = m.replaceAll("$2");
			params[0] = ordertype;
			params[1] = pluginid;
			return params;
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		String url = "/credit_alipay_payment-callback.do";
		String pattern = ".*/(\\w+)_(\\w+)_(payment-callback).do(.*)";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			String ordertype = m.replaceAll("$1");
			String pluginid = m.replaceAll("$2");
		}
	}
}
