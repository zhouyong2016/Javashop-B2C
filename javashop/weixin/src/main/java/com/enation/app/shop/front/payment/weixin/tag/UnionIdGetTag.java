package com.enation.app.shop.front.payment.weixin.tag;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.payment.plugin.weixin.WeixinPayPlugin;
import com.enation.app.shop.component.payment.plugin.weixin.WeixinUtil;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 获取微信UnionId（真正唯一）
 * 改造来自OpenIdGetTag
 * @author Sylow
 * @version v1.0,2016年7月13日
 * @since v6.1
 */
@SuppressWarnings("deprecation")
@Component
public class UnionIdGetTag extends BaseFreeMarkerTag {
	@Autowired
	private IPaymentManager paymentManager;
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		//判断是否已经获取过poenid
		//String openid = (String)ThreadContextHolder.getSession().getAttribute(WeixinPayPlugin.OPENID_SESSION_KEY);
		String unionId = (String)ThreadContextHolder.getSession().getAttribute(WeixinPayPlugin.UNIONID_SESSION_KEY);
		if(!StringUtil.isEmpty(unionId)){
			// 新版逻辑, 只自动登录，不自动注册
			autoLogin(unionId);
			return unionId;
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
			
			Map<String, Object> tokenMap = JsonUtil.toMap(weixinJson);
			String openid = tokenMap.get("openid").toString();
			String accesstoken = tokenMap.get("access_token").toString();
			
			// 获取unionId（真正唯一） add_by_Sylow 2016-07-13
			String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accesstoken + "&openid=" + openid + "&lang=zh_CN";
			String userInfoJson = this.httpget(userInfoUrl);
			Map<String, Object> userInfo = JsonUtil.toMap(userInfoJson);
			unionId = userInfo.get("unionid").toString();
			
			//System.out.println(openid);
			ThreadContextHolder.getSession().setAttribute(WeixinPayPlugin.OPENID_SESSION_KEY, openid);
			ThreadContextHolder.getSession().setAttribute(WeixinPayPlugin.UNIONID_SESSION_KEY, unionId);
			ThreadContextHolder.getSession().setAttribute("isWeChat", 1);
			
			// 新版逻辑，不自动注册
//			// 自动注册并登陆
//			this.autoRegAndLogin(openid);
			return openid;
		}
	}
	
	/**
	 * 自动登录
	 * 若unionId已经绑定有帐号了，则自动登录
	 * @param unionId
	 */
	private void autoLogin(String unionId){
		Map<String,String> cfgparams = paymentManager.getConfigParams("weixinPayPlugin");
		String autoreg = cfgparams.get("autoreg");
		if(!"yes".equals(autoreg)){
			return;
		}
		
		Member member = this.getByMemberByUnionId(unionId);

		// 有会员就自动登录
		if (member != null) {
			//看是否有会员登录
			Member curMem  = UserConext.getCurrentMember();
			//没有登录，则自动为其登录
			if(curMem==null){
				// 此会员登录
				this.memberManager.login(member.getUname());
			}
		}
	}
	
	private Member getByMemberByUnionId(String unionId){
		String sql ="select * from es_member where wx_unionid=?";
		List<Member> memberList= this.daoSupport.queryForList(sql, Member.class,unionId);
		if(memberList.isEmpty()){
			return null;
		}else{
			return memberList.get(0);
		}
			
	}
	
	
	private String getOAuth2Url(  ){
		
		Map<String,String> cfgparams = paymentManager.getConfigParams("weixinPayPlugin");
		if (cfgparams == null) {
			return "/500.html";
		}
		String appid = cfgparams.get("appid");

		String url =WeixinUtil.getWholeUrl();
		System.out.println(url);
		try {
			url = URLEncoder.encode(url,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url= "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+url+"&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect";
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
