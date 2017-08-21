package com.enation.eop.processor.facade;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.eop.IEopProcessor;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
/**
 * 安装处理器，响应/install来的响应
 * @author kingapex
 *2015-3-13
 */
public class InstallProcessor implements IEopProcessor {

	@Override
	public boolean process() throws IOException {
		
		HttpServletResponse httpResponse = ThreadContextHolder.getHttpResponse();
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String uri = httpRequest.getServletPath();
		
		if (!uri.startsWith("/install")	&& EopSetting.INSTALL_LOCK.toUpperCase().equals("NO")) {
			httpResponse.sendRedirect(httpRequest.getContextPath() + "/install");
			return true;
		}
		
		if (uri.startsWith("/install")) {
			if( EopSetting.INSTALL_LOCK.toUpperCase().equals("NO")){
				return false; //要由chain处理
			}else{
				return true; //拒绝再执行
			}
			 
		}
		
		return true;
	}

	
}
