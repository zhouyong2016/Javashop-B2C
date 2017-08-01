package com.enation.framework.context.webcontext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import io.apache.org.template.SafeHttpResponseWraper;


/**
 *  用ThreadLocal来存储Session,以便实现Session any where 
 * @author kingapex
 * <p>2009-12-17 下午03:10:09</p>
 * @version 1.1
 * 新增request any where
 */
public class ThreadContextHolder  {
	protected static final Logger logger = Logger.getLogger(ThreadContextHolder.class);
	
	private static ThreadLocal<HttpSession> SessionThreadLocalHolder = new ThreadLocal<HttpSession>();
	private static ThreadLocal<HttpServletRequest> HttpRequestThreadLocalHolder = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<HttpServletResponse> HttpResponseThreadLocalHolder = new ThreadLocal<HttpServletResponse>();
	
	
	public static void setHttpRequest(HttpServletRequest request){
		
		HttpRequestThreadLocalHolder.set(request);
	}
	
	public static HttpServletRequest getHttpRequest(){
		return  HttpRequestThreadLocalHolder.get();
	}
	
	
	
	public static void remove(){
		SessionThreadLocalHolder.remove();
		HttpRequestThreadLocalHolder.remove();
		HttpResponseThreadLocalHolder.remove();
	}
	
	
	public static void setHttpResponse(HttpServletResponse response){
		response = new SafeHttpResponseWraper(response);
		HttpResponseThreadLocalHolder.set(response);	
	}
	
	public static HttpServletResponse getHttpResponse(){
		
		return HttpResponseThreadLocalHolder.get();
	}
	
	
	
	public static void setSession(HttpSession session) {
		SessionThreadLocalHolder.set(session);
	}

//	public static void destorySessionContext() {
//		WebSessionContext context = SessionThreadLocalHolder.get();
//		if (context != null) {
//			context.destory();
//		}
//	}

	public static HttpSession getSession() {
		if (SessionThreadLocalHolder.get() == null) {
			HttpServletRequest httpRequest = getHttpRequest();
			SessionThreadLocalHolder.set( (httpRequest != null ) ? httpRequest.getSession() : null );
		}else{
//			if(logger.isDebugEnabled())
//				logger.debug(" webSessionContext not null and return ...");
		}
		return SessionThreadLocalHolder.get();
	}
	
}
