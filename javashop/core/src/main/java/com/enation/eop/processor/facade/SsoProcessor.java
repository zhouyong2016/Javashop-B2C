package com.enation.eop.processor.facade;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.enation.framework.context.webcontext.ThreadContextHolder;

public class SsoProcessor {

	public static String THE_SSO_SCRIPT = "";

	public void parse() throws IOException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		String content = "";
		 
		if ("y".equals(request.getParameter("cpr"))) {
			content += THE_SSO_SCRIPT;
			ThreadContextHolder.getHttpResponse().getWriter().write(content);
		}
		
		
	}
}
