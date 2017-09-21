package com.enation.app.sms.component.ztsms.plugin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.sms.AbstractSmsPlugin;
import com.enation.app.base.core.plugin.sms.ISmsSendEvent;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 助通短信 插件
 * @author Sylow
 * @version v1.0,2017-02-13
 * @since v6.2.1
 */
@Component
public class SmsZtPlugin extends AbstractSmsPlugin implements ISmsSendEvent {

	@Override
	public boolean onSend(String phone, String content, Map param) {
		try {

			// 创建StringBuffer对象用来操作字符串
			StringBuffer sb = new StringBuffer("http://www.api.zthysms.com/sendSms.do?");

			// 用户名
			sb.append("username=" + param.get("username"));
			
			// 产品id
			sb.append("&productid=" + param.get("productid"));
			
			// 包装密码
			String password = "";
			String md5Psd = this.string2MD5(param.get("password").toString());
			String time = DateUtil.toString(DateUtil.getDateline(),"yyyyMMddHHmmss");
			
			password = this.string2MD5(md5Psd + time);
			
			// 密码
			sb.append("&password=" + password);

			// tkey
			sb.append("&tkey=" + time);

			// 向StringBuffer追加手机号码
			sb.append("&mobile=" + phone);

			// 向StringBuffer追加消息内容转URL标准码
			sb.append("&content=" + URLEncoder.encode(content, "UTF-8"));
			
			String xh = param.get("xh").toString();
			
			if (!StringUtil.isEmpty(xh)) {
				sb.append("&xh=" + xh);
			}
			
			// 创建url对象
			URL url = new URL(sb.toString());
			
			// 打开url连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// 设置url请求方式 ‘get’ 或者 ‘post’
			connection.setRequestMethod("POST");

			// 发送
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			// 返回发送结果
			String inputline = in.readLine();
			if(!inputline.startsWith("1,")){
				throw new RuntimeException(inputline);
			}else{
				return true;
			}

		} catch (Exception e) {
			//e.pr
		}
		return false;
	}

    
    
    /*** 
     * MD5加码 生成32位md5码 
     */  
    public static String string2MD5(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
  
    }  
  
    /** 
     * 加密解密算法 执行一次加密，两次解密 
     */   
    public static String convertMD5(String inStr){  
  
        char[] a = inStr.toCharArray();  
        for (int i = 0; i < a.length; i++){  
            a[i] = (char) (a[i] ^ 't');  
        }  
        String s = new String(a);  
        return s;  
  
    }  

	@Override
	public String getId() {
		return "smsZtPlugin";
	}

	@Override
	public String getName() {
		return "助通网关短信插件";
	}

}
