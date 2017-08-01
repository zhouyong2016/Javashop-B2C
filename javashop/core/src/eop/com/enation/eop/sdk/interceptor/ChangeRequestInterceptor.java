package com.enation.eop.sdk.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 替换request拦截器
 * 主要用于用户提交表单时，如果请求类型为enctype="multipart/form-data"，
 * 就将httpRequest类型装换为DefaultMultipartHttpServletRequest类型
 * @author DMRain 2016-5-20
 *
 */
public class ChangeRequestInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		ThreadContextHolder.setHttpRequest(request);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
