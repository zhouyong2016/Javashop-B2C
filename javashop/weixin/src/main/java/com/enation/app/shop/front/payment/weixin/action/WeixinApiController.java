package com.enation.app.shop.front.payment.weixin.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.component.payment.plugin.weixin.service.IWeixinManager;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.framework.context.webcontext.ThreadContextHolder;
/**
 * @author kingapex
 *2014-11-19上午11:49:32
 */
@Controller
@RequestMapping("/api/shop/weixin")
public class WeixinApiController  {

	@Autowired
	private IWeixinManager weixinManager;
	
	@ResponseBody
	@RequestMapping(value="/config-script")
	public Object configScript(String pagename){
		try {
			HttpServletResponse response  = ThreadContextHolder.getHttpResponse();
			String current_url = this.getWholeUrl(pagename);
			response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_JAVASCRIPT);
			return this.weixinManager.createConfigScript(current_url);
		} catch (Exception e) {
			e.printStackTrace();
			String json="alert('生成微信script失败')";
			return json;
		}
		
	}
	
	@ResponseBody
	@RequestMapping(value="/get-open-id")
	public String getOpenId(String code){
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxa6ceb2373cbd6b53&secret=88e96db80aef361403a76fa494d85cc9&code="+code+"&grant_type=authorization_code";
		String weixinJson = this.httpget(url);
		return JSONObject.fromObject(weixinJson).get("openid").toString();
	}
	
	public static   String getWholeUrl( String path){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String servername =request.getServerName();
		int port = request.getServerPort();
		
		String portstr="";
		if(port!=80){
			portstr=":"+port;
		}
		String contextPath = request.getContextPath();
		if(contextPath.equals("/")){
			contextPath="";
		}
		
		String url  = "http://"+servername+portstr+contextPath+path;
		return url;
		
	}
 
	
	
	private String httpget(String uri) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(uri);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			String content = EntityUtils.toString(entity, "utf-8");
		 
			return content;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
}
