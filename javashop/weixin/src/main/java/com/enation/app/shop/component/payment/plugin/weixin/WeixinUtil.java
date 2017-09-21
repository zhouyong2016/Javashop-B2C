package com.enation.app.shop.component.payment.plugin.weixin;

import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;


@SuppressWarnings("deprecation")
public class WeixinUtil {

	
	/**
	 * 进行sha1签名
	 * @param params
	 * @return
	 */
	public static String sha1Sign(Map params  ) {
		 
		String url = createLinkString(params);
		String sign =  Sha1.encode(url);
	 
		return sign;
	}

	
	
	/**
	 * 生成签名
	 * 
	 * @param params
	 *            参数map
	 * @param key
	 *            支付key(API密钥)
	 * @return
	 */
	public static String createSign(Map params, String key) {
		 
		String url = createLinkString(params);
		url = url + "&key=" + key;
		 
		String sign="";
		try {
			sign = DigestUtils.md5Hex(url.getBytes("UTF-8")).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		} 
		return sign;
	}

	public static String mapToXml(Map map) {
		Document document = DocumentHelper.createDocument();
	 
		Element nodeElement = document.addElement("xml");
		for (Object obj : map.keySet()) {
			Element keyElement = nodeElement.addElement(String.valueOf(obj));
			String text = String.valueOf(map.get(obj));
			if (!"total_fee".equals(obj)) {
				keyElement.addCDATA(text);
			} else {
				keyElement.setText(text);
			}

		}
		return doc2String(document);
	}
	
	/**
	 * 将一个xml转为map
	 * @param document
	 * @return
	 */
	public static Map xmlToMap(Document document)
	{
		Map map = new HashMap();
		
		Element rootEl = document.getRootElement();
		List<Element> elList =rootEl.elements();
		for (Element el : elList) {
			String name =el.getName();
			String text= el.getText();
			map.put(name,text);
		}
		return map;
	}
	public static String doc2String(Document document) {
		String s = "";
		try {
			// 使用输出流来进行转化
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// 使用UTF-8编码
		 
			OutputFormat format = new OutputFormat("   ", true, "UTF-8");
			XMLWriter writer = new XMLWriter(out, format);
			writer.write(document);
			s = out.toString("UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}

	public static Document post(String wsPart, String doc_str) {
		try {
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(wsPart);
			HttpEntity requestEntity = new StringEntity(doc_str,"UTF-8");
			
			httppost.setEntity(requestEntity);
			httppost.setHeader(HTTP.CONTENT_ENCODING, HTTP.UTF_8);
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity rentity = httpresponse.getEntity();
			InputStream in = rentity.getContent();
			// String res= StringUtil.inputStream2String(in);
			// System.out.println(res);
			if (in != null) {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(in);
				return doc;
			} else {
				throw new RuntimeException("post uri[" + wsPart
						+ "]发生错误:[stream 返回Null]");
			}
		} catch (Throwable e) {

			throw new RuntimeException("post uri [" + wsPart + "]发生错误", e);

		}
	}
	
	
	public static  String httpget(String uri) {
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
	

	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			
			if("sign".equals(key)){
				continue;
			}
			
			if ("".equals(prestr)) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr +"&" +key + "=" + value ;
			}
		}
		return prestr;
	}
	
	
	public static   String getWholeUrl( ){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String servername =request.getServerName();
		String path  = request.getServletPath();
		int port = request.getServerPort();
		
		 // 虚拟路径问题 兼容微信
		
		if(port!=80){
			servername=servername+":"+port;
		}
		String contextPath = request.getContextPath();
		if(contextPath.equals("/")){
			contextPath="";
		}
		
		if("/".equals(path)){
			path="/index.html";
		}
 
		
		String url  = "http://"+servername+contextPath+path;
		String queryString = (request).getQueryString();
		
		if(!StringUtil.isEmpty(queryString)){
			url=url+"?"+queryString;
		}
		
		return url;
		
	}
	
	public static Document verifyCertPost(String wsPart, String doc_str,String mchid){
		try {
			
		    KeyStore keyStore  = KeyStore.getInstance("PKCS12");
	        FileInputStream instream = new FileInputStream(new File("/root/apiclient_cert.p12"));
	        
	        try {
	            keyStore.load(instream, mchid.toCharArray());
	        } finally {
	            instream.close();
	        }

	        // Trust own CA and all self-signed certs
	        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchid.toCharArray()).build();
	        // Allow TLSv1 protocol only
	        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
	        		sslcontext,new String[] { "TLSv1" },null, 
	        		SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		    CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			
			
			HttpPost httppost = new HttpPost(wsPart);
			HttpEntity requestEntity = new StringEntity(doc_str,"UTF-8");
			
			httppost.setEntity(requestEntity);
			httppost.setHeader(HTTP.CONTENT_ENCODING, HTTP.UTF_8);
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity rentity = httpresponse.getEntity();
			InputStream in = rentity.getContent();
			// String res= StringUtil.inputStream2String(in);
			// System.out.println(res);
			if (in != null) {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(in);
				return doc;
			} else {
				throw new RuntimeException("post uri[" + wsPart+ "]发生错误:[stream 返回Null]");
			}
		} catch (Throwable e) {
			throw new RuntimeException("post uri [" + wsPart + "]发生错误", e);

		}
	}
	/**
	 * 是否是微信打开
	 * @return
	 */
	public static int isWeChat(){
		String agent = ThreadContextHolder.getHttpRequest().getHeader("User-agent");
//		System.out.println(agent);
		if(agent.toLowerCase().indexOf("micromessenger")>0){
			return 1;
		}
		return 0;
	}
	
	
	/**
	 * 获取session中的openId
	 * 这个方法不会主动去获取openId，需要页面中去获取，放到session当中
	 * @return
	 * @throws IOException 
	 */
	public static String getUnionId() throws IOException{
		String unionId = (String)ThreadContextHolder.getSession().getAttribute(WeixinPayPlugin.UNIONID_SESSION_KEY);
		if(!StringUtil.isEmpty(unionId)){
			return unionId;
		} else {
			//kingapex 注释于2016年9月22日，解决微信注册时，没有正确的配置微信时，会报404的错误
			//ThreadContextHolder.getHttpResponse().sendRedirect("");
			return "程序错误";
		}
		
	}
	
	/**
	 * 获取session中的openId
	 * 这个方法不会主动去获取openId，需要页面中去获取，放到session当中
	 * @return
	 * @throws IOException 
	 */
	public static String getOpenId() throws IOException{
		String openid = (String)ThreadContextHolder.getSession().getAttribute(WeixinPayPlugin.OPENID_SESSION_KEY);
		if(!StringUtil.isEmpty(openid)){
			return openid;
		} else {
			//kingapex 注释于2016年9月22日，解决微信注册时，没有正确的配置微信时，会报404的错误
			//ThreadContextHolder.getHttpResponse().sendRedirect("");
			return "程序错误";
		}
		
	}
	
	public static void main(String[] args) {
			
	}
}
