package com.enation.app.shop.front.api.order;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.component.payment.plugin.alipay.AlipayRefund;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;

/**
 * 
 * (支付宝退款回调api) 
 * @author zjp
 * @version v6.3
 * @since v6.3
 * 2017年6月28日 下午2:59:36
 */
@Controller
@RequestMapping("/api/shop/*refund-callback")
public class RefundCallbackApiController {

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
			AlipayRefund alipayRefund = SpringContextHolder.getBean("alipayRefund");
			String result = alipayRefund.onCallBack(ordertype);
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
		String pattern = ".*/(\\w+)_(\\w+)_(refund-callback)/execute.do(.*)";
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
		String url = "/credit_alipay_refund-callback.do";
		String pattern = ".*/(\\w+)_(\\w+)_(refund-callback).do(.*)";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			String ordertype = m.replaceAll("$1");
			String pluginid = m.replaceAll("$2");
		}
	}
}
