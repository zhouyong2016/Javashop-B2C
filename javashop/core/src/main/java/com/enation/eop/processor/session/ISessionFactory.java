package com.enation.eop.processor.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.ClusterSetting;
import com.enation.eop.processor.SafeHttpRequestWrapper;
import com.enation.framework.context.spring.SpringContextHolder;
/**
 * session工厂类
 * @author Administrator
 *
 */
@Component
public class ISessionFactory {

	private ISessionFactory(){};
	
	public static HttpSession getSession(SafeHttpRequestWrapper safeHttpRequest,HttpServletRequest httpRequest, HttpServletResponse httpResponse, RequestEventSubject requestEventSubject){
		//获得session开关情况,根据开关情况开启对应的session
		int session_open=ClusterSetting.getSession_open();
		if(session_open==1){
			HttpSession session =(HttpSession)SpringContextHolder.getBean("clusterHttpSession");	
			ISessionManager sessionManager =(ISessionManager)SpringContextHolder.getBean("iSessionManager");
			//创建session
			session=sessionManager.createSession(safeHttpRequest, httpResponse, requestEventSubject, true);
			return session;
		}else{
			return httpRequest.getSession();
		}
	}
}

