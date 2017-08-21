package com.enation.app.shop.core.member.service;

import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.IEopProcessor;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.HttpUtil;

import net.sf.json.JSONObject;
/**
 * 自动登陆处理器
 * @author Kanon
 *
 */
@Service("autoLoginProcessor")
public class AutoLoginProcessor implements IEopProcessor {

	@Autowired
	private IMemberManager memberManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(AutoLoginProcessor.class);

	public boolean process() {
		try {
			HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
			String url = httpRequest.getRequestURI();
			if (url != null) {
				url = url.toLowerCase();
				if (url.endsWith("/") || url.endsWith(".html")|| url.endsWith(".do")) {
					
					Member member = UserConext.getCurrentMember();
					if (member == null) {
						String cookieValue = HttpUtil.getCookieValue(ThreadContextHolder.getHttpRequest(), "JavaShopUser");
						if (cookieValue != null && !cookieValue.equals("")) {
							cookieValue = URLDecoder.decode(cookieValue,"UTF-8");
							cookieValue = EncryptionUtil1.authcode(cookieValue,"DECODE", "", 0);
							if (cookieValue != null && !cookieValue.equals("")) {

								Map map = (Map) JSONObject.toBean(JSONObject.fromObject(cookieValue),Map.class);
								if (map != null) {
									String username = map.get("username").toString();
									String password = map.get("password").toString();
									int result = memberManager.loginWithCookie(username,password);
									if(result != 1) {
										HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser","", 0);
									}
								}

							}
						}
					} 
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Auto Login Error", ex);
		}
		return true;
	}
	
	public static void main(String[] args) {
		// String str =
		// "{username:\"hhyear@163.com\",password:\"256c6da2403dcda7aae0df34337359e8\"}";
		// String str1 = EncryptionUtil.authcode(str,"ENCODE","",0);
		// //System.out.println(str1);
		String str1 = "db21SPFxwCWgshcLqsIxFzS9YeEusB/qzRdC1OKk2R47uLdLCuai1BPUMh5xNJhSkwuu1v09po2qNuLPsWjLg/+p4aaeZZ70LyCEGwcwMZGuHCY9zmKDv1sXBZKQ6OXjFV04MQ";
		//System.out.println(EncryptionUtil1.authcode(str1, "DECODE", "", 0));
	}

}
