package com.enation.framework.gzip;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GZIPFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res,FilterChain chain) throws IOException, ServletException {
    	
    	//
    	if (req instanceof HttpServletRequest) {
    		HttpServletRequest request = (HttpServletRequest) req;
    		HttpServletResponse response = (HttpServletResponse) res;
    		String ae = request.getHeader("accept-encoding");
    		
    		//判断浏览器支持的编码类型
    		if (ae != null && ae.indexOf("gzip") != -1) {
    			
	            GZIPResponseWrapper gZIPResponseWrapper = new GZIPResponseWrapper(response);
	            chain.doFilter(req, gZIPResponseWrapper);
	            gZIPResponseWrapper.finishResponse();
	            return;
    		}
        chain.doFilter(req, res);            
   }else{
   }
}

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
        //
    }
}
