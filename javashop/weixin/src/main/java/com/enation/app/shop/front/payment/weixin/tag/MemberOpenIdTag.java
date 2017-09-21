package com.enation.app.shop.front.payment.weixin.tag;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.payment.plugin.weixin.WeixinUtil;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 获取当前登录会员的微信openid，存放到数据库中
 * @author xulipeng
 * 2015年12月17日
 */

@Component
public class MemberOpenIdTag extends BaseFreeMarkerTag {

	@Autowired
	private IPaymentManager paymentManager;
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		String member_id = ThreadContextHolder.getHttpRequest().getParameter("member_id");
		this.logger.error("xlp_memberid="+member_id);
		
		//判断是否已经获取过poenid
		String openid = "";
		
		List<Map> list = this.daoSupport.queryForList("select m.wx_openid from es_member m where m.member_id=?", Integer.parseInt(member_id));
		if(list!=null && !list.isEmpty()){
			Map map = list.get(0);
			openid = (String) map.get("wx_openid");
		}
		
		if(!StringUtil.isEmpty(openid)){
			return openid;
		}
		
		String code = this.getRequest().getParameter("code");
		
		if(StringUtil.isEmpty(code)){ //如果没有接收到微信code则跳转到授权页面
			String oauthurl= getOAuth2Url(  );
			try {
				//System.out.println("跳");
				ThreadContextHolder.getHttpResponse().sendRedirect(oauthurl);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "";
		}else{
			Map<String,String> cfgparams = paymentManager.getConfigParams("weixinPayPlugin");
			String appid = cfgparams.get("appid");
			String appsecret = cfgparams.get("appsecret");
			
			String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appsecret+"&code="+code+"&grant_type=authorization_code";
			String weixinJson = this.httpget(url);
			openid =JSONObject.fromObject(weixinJson).get("openid").toString();

			//ThreadContextHolder.getSessionContext().setAttribute(WeixinPayPlugin.OPENID_SESSION_KEY, openid);
			
			//将openid 存放在当前登录会员的库中。
			
			this.daoSupport.execute("update es_member set wx_openid=? where member_id=?", openid, member_id);
			
			return openid;
		}
	}
	
	private String getOAuth2Url(  ){
		
		Map<String,String> cfgparams = paymentManager.getConfigParams("weixinPayPlugin");
		String appid = cfgparams.get("appid");

		String url =WeixinUtil.getWholeUrl();
		System.out.println(url);
		try {
			url = URLEncoder.encode(url,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url= "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+url+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
		System.out.println(url);
		return url; 
	}
	
	
	public static void main(String[] args) {
		String url="http://weixin.javamall.com.cn/javamall/weChatOpenId.html";
		try {
			url = URLEncoder.encode(url,"UTF-8");
			System.out.println(url);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
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
		return "error";
	}
	
	
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }

    
    

}
