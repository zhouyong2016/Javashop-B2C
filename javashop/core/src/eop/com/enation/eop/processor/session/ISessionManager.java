package com.enation.eop.processor.session;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.enation.eop.processor.SafeHttpRequestWrapper;

public interface ISessionManager{
	
	/**
	 * 创建一个session对象 
	 * @param request
	 * @param httpResponse
	 * @param requestEventSubject
	 * @param create
	 * @return
	 */
	public HttpSession createSession(SafeHttpRequestWrapper request,
			HttpServletResponse httpResponse,
			RequestEventSubject requestEventSubject, boolean create);
	
}
