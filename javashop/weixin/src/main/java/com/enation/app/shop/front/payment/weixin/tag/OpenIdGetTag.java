package com.enation.app.shop.front.payment.weixin.tag;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.payment.plugin.weixin.WeixinPayPlugin;
import com.enation.app.shop.component.payment.plugin.weixin.WeixinUtil;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 已废弃请使用 UnionIdGetTag   xulipeng 
 * 
 * 获取微信openId Tag
 * v2.0，去除自动登录
 * @author Sylow
 * @version v2.0,2016年7月11日
 * @since v6.1
 */
@SuppressWarnings("deprecation")
@Component
public class OpenIdGetTag extends BaseFreeMarkerTag {
	@Autowired
	private IPaymentManager paymentManager;
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		//判断是否已经获取过openid
		String openid = (String)ThreadContextHolder.getSession().getAttribute(WeixinPayPlugin.OPENID_SESSION_KEY);
		if(!StringUtil.isEmpty(openid)){
			// 新版逻辑, 只自动登录，不自动注册
			autoLogin(openid);
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
			System.out.println(weixinJson);
			openid =JSONObject.fromObject(weixinJson).get("openid").toString();
			//System.out.println(openid);
			ThreadContextHolder.getSession().setAttribute(WeixinPayPlugin.OPENID_SESSION_KEY, openid);
			ThreadContextHolder.getSession().setAttribute("isWeChat", 1);
			
			// 新版逻辑，不自动注册
//			// 自动注册并登陆
//			this.autoRegAndLogin(openid);
			return openid;
		}
	}
	
	/**
	 * 自动登录
	 * 若openId已经绑定有帐号了，则自动登录
	 * @param openId
	 */
	private void autoLogin(String openId){
		Map<String,String> cfgparams = paymentManager.getConfigParams("weixinPayPlugin");
		String autoreg = cfgparams.get("autoreg");
		if(!"yes".equals(autoreg)){
			return;
		}
		
		Member member = this.getByMemberByOpenid(openId);

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
	
	/**
	 * 自动注册并登陆
	 * @param openid 微信的openid
	 */
	private void autoRegAndLogin(String openid){
		Map<String,String> cfgparams = paymentManager.getConfigParams("weixinPayPlugin");
		String autoreg = cfgparams.get("autoreg");
		if(!"yes".equals(autoreg)){
			return;
		}
		// 试着用openid查找会员
		Member member = this.getByMemberByOpenid(openid);

		//没有找到openid的会员，用openid注册新会员，并自动登录
		if (member == null) {
			int count = this.daoSupport.queryForInt("select count(0) from es_member ") + 1;

			member = new Member();

			// 生成用户名：weixin+会员总数
			String uname = "weixin" + count;
			member.setUname(uname);
			member.setName(uname);

			// 随机生成6位数做为密码
			String password = StringUtil.getRandStr(6);
			member.setPassword(password);

			// 注册并获取会员id
			int result = this.memberManager.register(member);
			
			if(result==1){
				// 记录此会员的openid
				this.daoSupport.execute("update es_member set wx_openid=? where member_id=?", openid, member.getMember_id());
			
			// 会此会员登陆
			this.memberManager.login(uname, password);
			}else{
				throw new RuntimeException("自动注册发生错误");
			}
		}else{
			//看是否有会员登陆
			Member curMem  = UserConext.getCurrentMember();
			
			//没有登陆，则自动为其登陆
			if(curMem==null){
				// 会此会员登陆
				this.memberManager.login(member.getUname(), member.getPassword());
			}
		}
	}
	
	
	
	/**
	 * 根据openid 获取会员<br>
	 * @param openid 微信opendi
	 * @return
	 *  如果没有找到返回NULL
	 *  <br>
	 *  如果找到返回此会员对象 {@link Member}
	 */
	private Member getByMemberByOpenid(String openid){
		String sql ="select * from es_member where wx_openid=?";
		List<Member> memberList= this.daoSupport.queryForList(sql, Member.class,openid);
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
		System.out.println("redirect_uri_______"+url);
		try {
			url = URLEncoder.encode(url,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url= "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+url+"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
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
    
	public static void main1(String[] args) {
		 Map params = new HashMap();
		 params.put("appid", "xxxxx");
		 params.put("auth_code", "123456");
		 params.put("body", "test");
		 params.put("device_info", "123");
		 params.put("mch_id", "xxxxx");
		 params.put("nonce_str", "960f228109051b9969f76c82bde183ac");
		 params.put("out_trade_no", "1400755861");
		 params.put("spbill_create_ip", "127.0.0.1");
		 params.put("total_fee", "1");
		 
		 String url = createLinkString(params);
		 
		 url=url+"&key=xxxxxxx";
		 System.out.println(url);
//		 url= DigestUtils.md5Hex(url);
		 System.out.println(url);
	}

	
}
