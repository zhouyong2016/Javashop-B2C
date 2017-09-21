package com.enation.app.shop.component.payment.plugin.weixin.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.component.payment.plugin.weixin.WeixinPayPlugin;
import com.enation.app.shop.component.payment.plugin.weixin.WeixinUtil;
import com.enation.app.shop.component.payment.plugin.weixin.service.IWeixinManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;

/**
 * 微信管理 
 * @author kingapex
 *2015年7月29日下午4:09:32
 */
@Service("weixinManager")
public class WeixinManager implements IWeixinManager {
	
	@Autowired
	private IPaymentManager paymentManager;

	@Override
	public String createConfigScript(String current_url) {
		
		String jsapi_ticket=this.getJsapiTicket();
		
		Map<String,String> params  = new HashMap<String, String>();
		
		Map<String,String> settings = getWeixinSetting();
		String appid = settings.get("appid");
		params = sign(jsapi_ticket,current_url);
		 
		//生成签名
		params.put("appid", appid);
		
		FreeMarkerPaser fp = FreeMarkerPaser.getInstance();
		fp.setClz(this.getClass());
		fp.setPageName("config_script");
		fp.putData(params);
		
		return fp.proessPageContent();
		
	}
	
 

    public static Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
        System.out.println(string1);

        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }


	    private static String byteToHex(final byte[] hash) {
	        Formatter formatter = new Formatter();
	        for (byte b : hash)
	        {
	            formatter.format("%02x", b);
	        }
	        String result = formatter.toString();
	        formatter.close();
	        return result;
	    }

	    private static String create_nonce_str() {
	        return UUID.randomUUID().toString();
	    }

	    private static String create_timestamp() {
	        return Long.toString(System.currentTimeMillis() / 1000);
	    }
	    
	    
	    
//	public static void main(String[] args) {
//		WeixinManager weixinManager = new WeixinManager();
//		String noncestr="123456";
//		String jsapi_ticket=weixinManager.getJsapiTicket();
//		String timestamp=""+ DateUtil.getDateline();
//		String url ="http://";
//		
//	}
	
	private static String jsapi_ticket="";
	private static long jsapi_ticket_time_out=0;
	
 
	
	/**
	 * 获取JsapiTicket
	 * @return
	 */
	private  String getJsapiTicket(){
		
		//不为空，检查有效期，如果没有失效直接返回token
		if(!StringUtil.isEmpty( jsapi_ticket ) && jsapi_ticket_time_out!=0){
			//检查有效期
			long now =getCurrentTime(); //当前时间
			if(now <jsapi_ticket_time_out){//在有效期内
				return jsapi_ticket; //返回有效的token
			}
		}

		//走到此处说明没有有效的token
				
				
		String accessToken= getAccessToken();
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accessToken+"&type=jsapi";
		String json = WeixinUtil.httpget(url);
		JSONObject jsonObj= JSONObject.fromObject(json);
		Integer errcode =(Integer)jsonObj.get("errcode");
		if(errcode==0){
			String ticket  =(String)jsonObj.get("ticket");
			
			int expires_in =(Integer) jsonObj.get("expires_in");
			
			jsapi_ticket_time_out= getCurrentTime()+expires_in; //计算失效时间
			jsapi_ticket=ticket;//缓存
			
			return ticket;

		}else{
			String errmsg  =(String)jsonObj.get("errmsg");

			throw new RuntimeException("获取Jsapi ticket出错，errcode:"+errcode+",errmsg:"+errmsg);
		}
		
	}
	
	
	private static String access_token="";
	private static long access_token_time_out=0;
	
	
	/**
	 * 通过微信api获取access_token
	 * @return
	 */
	private  String getAccessToken(){
		
		//不为空，检查有效期，如果没有失效直接返回token
		if(!StringUtil.isEmpty( access_token ) && access_token_time_out!=0){
			//检查有效期
			long now =getCurrentTime(); //当前时间
			if(now <access_token_time_out){//在有效期内
				return access_token; //返回有效的token
			}
		}

		//走到此处说明没有有效的token
		Map<String,String> settings = getWeixinSetting();
		
		String appid = settings.get("appid");
		String appsecret = settings.get("appsecret");
		String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appsecret;
		
		String json = WeixinUtil.httpget(url);
		JSONObject jsonObj= JSONObject.fromObject(json);
		String accessToken =(String)jsonObj.get("access_token");
		
		if(StringUtil.isEmpty(accessToken)){
			Integer errcode  =(Integer)jsonObj.get("errcode");
			String errmsg  =(String)jsonObj.get("errmsg");
			throw new RuntimeException("access_token获取异常: errcode:"+errcode+",errmsg:"+errmsg);
		}
		int expires_in =(Integer) jsonObj.get("expires_in");
		
		access_token_time_out= getCurrentTime()+expires_in; //计算失效时间
		access_token=accessToken;//缓存
//		System.out.println("去微信获取了token");
		return accessToken;
		
	}
	
	private static long current_time=0;
	private static long getCurrentTime(){
		
	//return current_time; //测试时请打开此注释
		return DateUtil.getDateline();
	}

//	public static void main(String[] args) throws InterruptedException {
//		testJsapiToken();
//	}
	
	

//	private static void testJsapiToken() throws InterruptedException{
//		current_time = DateUtil.getDateline(); //设置为当前系统时间
//		String token1 = getJsapiTicket(); //先获取一次
//		Thread.sleep(5000); //停5秒
//		
//		String token2 = getJsapiTicket(); //再获取一次
//		
//		System.out.println("以下结果应该一样：");
//		System.out.println("token1:"+token1+"-->token2:"+token2);
//		
//		Thread.sleep(5000); //停5秒
//		current_time = DateUtil.getDateline()+7201; //使token超时
//		String token3 = getJsapiTicket(); //再获取一次
//		System.out.println("以下结果应该不一样：");
//		System.out.println("token1:"+token1+"-->token3:"+token3);
//		
//	}
	
	
	
//	private static void testAccessToken() throws InterruptedException{
//		current_time = DateUtil.getDateline(); //设置为当前系统时间
//		String token1 = getAccessToken(); //先获取一次
//		Thread.sleep(5000); //停5秒
//		
//		String token2 = getAccessToken(); //再获取一次
//		
//		System.out.println("以下结果应该一样：");
//		System.out.println("token1:"+token1+"-->token2:"+token2);
//		
//		Thread.sleep(5000); //停5秒
//		current_time = DateUtil.getDateline()+7201; //使token超时
//		String token3 = getAccessToken(); //再获取一次
//		System.out.println("以下结果应该不一样：");
//		System.out.println("token1:"+token1+"-->token3:"+token3);
//	}
	
	
	private   Map<String,String> getWeixinSetting(){
		WeixinPayPlugin plugin = new WeixinPayPlugin();
		Map<String, String> cfgparams =new HashMap();
		cfgparams= paymentManager.getConfigParams(plugin.getId());
		
//		String json ="";
//		JSONObject jsonObject = JSONObject.fromObject( json );  
//		cfgparams = (Map)jsonObject.toBean(jsonObject, Map.class);
		
		return cfgparams;
		
	}

	
}
