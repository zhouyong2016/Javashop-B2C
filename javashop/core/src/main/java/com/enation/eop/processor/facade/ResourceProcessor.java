package com.enation.eop.processor.facade;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.enation.eop.IEopProcessor;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.framework.context.webcontext.ThreadContextHolder;
/**
 * web资源处理器，响应js,css,图片和flash
 * @author kingapex
 *2010-2-27上午12:11:26
 */
public class ResourceProcessor implements IEopProcessor {

	
	public boolean process() throws IOException {
		 HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		 HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		 
			
			
		 String path  = httpRequest.getServletPath();
		 
		 if (!path.startsWith("/resource/")) {
			 return false;
		 }
		 
		 path = path.replaceAll("/resource/","");
	 
			if(path.toLowerCase().endsWith(".js")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_JAVASCRIPT);
			if(path.toLowerCase().endsWith(".css")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_CSS);
			if(path.toLowerCase().endsWith(".jpg")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_JPG);
			if(path.toLowerCase().endsWith(".gif")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_GIF);
			if(path.toLowerCase().endsWith(".png")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_PNG);
			if(path.toLowerCase().endsWith(".swf")) response.setContentType(HttpHeaderConstants.CONTEXT_TYPE_FLASH);
			
			InputStream in =   getClass().getClassLoader().getResourceAsStream(path);
			if (in != null) {
				byte[] inbytes = IOUtils.toByteArray(in);
			//	response.setCharacterEncoding("UTF-8");

				OutputStream output = response.getOutputStream();
				IOUtils.write(inbytes, output);
				return true;
			} else {
				 return false;
			}
		 
		
	}

}
