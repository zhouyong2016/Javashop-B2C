package com.enation.eop.processor.facade;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.app.base.core.service.IShortUrlManager;
import com.enation.eop.IEopProcessor;
import com.enation.eop.SystemSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

/**
 * 短链接处理器
 * @author Sylow
 * @version v1.0,2015-11-22
 * @since v1.0
 */
public class ShortUrlIProcessor implements IEopProcessor {

	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.IEopProcessor#process()
	 */
	@Override
	public boolean process() throws IOException, ServletException {
		
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		HttpServletResponse httpResponse=  ThreadContextHolder.getHttpResponse();

		String uri = httpRequest.getServletPath();
		
		//如果是短链接
		if(uri.startsWith("/su")) {
			IShortUrlManager shortUrlManager = SpringContextHolder.getBean("shortUrlManager");
			String longUrl = shortUrlManager.getLongUrl(uri);
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			if(SystemSetting.getWap_open()==1&& this.isMobile()){
				longUrl ="http://"+SystemSetting.getWap_domain()+":"+request.getServerPort()+ SystemSetting.getContext_path()+longUrl;  
			}else{
				longUrl = RequestUtil.getDomain() + longUrl;
			}
			httpResponse.sendRedirect(longUrl);
			return true;
		}
		return false;
	}

	private static boolean isMobile(){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		//判断请求是否为空
		if(request==null){
			return false;
		}
		
		String user_agent = request.getHeader("user-agent");
		
		//判断user-agent是否为空
		if(StringUtil.isEmpty(user_agent)){
			 return false;
		}
		 
		String userAgent = user_agent.toLowerCase();

		if(userAgent.contains("android" ) || userAgent.contains("iphone")){
			return true;
		}
		
		return false;
	}
}
