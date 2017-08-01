package com.enation.framework.util;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {

	/**
	 * 根据网址获取顶级域名，不包括二级域名
	 * @param url
	 * @return
	 */
	public static String getTopDomain(String url){
		String host = "";
		try{
			host = new URL(url).getHost().toLowerCase();// 此处获取值转换为小写
		}catch(Exception ex){}
		Pattern pattern = Pattern.compile("[^.]+(\\.com\\.cn|\\.net\\.cn|\\.org\\.cn|\\.gov\\.cn|\\.com|\\.net|\\.cn|\\.org|\\.cc|\\.me|\\.tel|\\.mobi|\\.asia|\\.biz|\\.info|\\.name|\\.tv|\\.hk|\\.公司|\\.中国|\\.网络)");
		Matcher matcher = pattern.matcher(host);
		while (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
	
}
