package com.enation.app.shop.front.payment.weixin.tag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

@Component
public class WeixinOAuth2UrlTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IPaymentManager paymentManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		String orderid = (String)params.get("orderid");
		
		if(StringUtil.isEmpty(orderid)){
			throw new RuntimeException("orderid参数是必须的");
		}
		Map<String,String> cfgparams = paymentManager.getConfigParams("weixinPayPlugin");
		String appid = cfgparams.get("appid");
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();
		int port = request.getServerPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}

		String url ="http://"+serverName+"/weixin.html?orderid="+orderid;
		try {
			url = URLEncoder.encode(url,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url= "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+url+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
		return url; 
		
	
	}
	
	public static void main(String[] args) {
		String url ="http://www.ab.com:8080/weixin.html?orderid=3";
		try {
			url = URLEncoder.encode(url,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		System.out.println(url);
	}
	
}
