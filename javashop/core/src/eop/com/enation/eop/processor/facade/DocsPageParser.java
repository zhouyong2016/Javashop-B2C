package com.enation.eop.processor.facade;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.utils.FreeMarkerUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.TagCreator;
import com.enation.framework.util.StringUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 文档显示处理器。响应由/docs目录来的请求
 * @author kingapex
 *2015-3-13
 */
public class DocsPageParser  {
 
	public void parse(String uri) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		if (uri.indexOf('?') > 0) {
			uri = uri.substring(0, uri.indexOf('?'));
		}
		
		String ctx =request.getContextPath();
		if(ctx.equals("/")){
			ctx="";
		}
//		/**
//		 * 如果路径开始是 这个路径，则替换修改
//		 */
//		if(uri.indexOf(ctx)>0){
//			uri=uri.replaceFirst(ctx, "");
//		}
		 Map<String, Object> widgetData= new HashMap<String, Object>();
		
		try {
			Enumeration<String> paramNames = request.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String name = paramNames.nextElement();
				String value = request.getParameter(name);
				widgetData.put(name, value);
			}

			
			widgetData.put("newTag", new TagCreator());
			widgetData.put("staticserver", SystemSetting.getStatic_server_domain());
			widgetData.put("ctx", request.getContextPath());
			// FreeMarkerUtil.test();
			String themeFld = StringUtil.getRootPath();
			Configuration cfg = FreeMarkerUtil.getFolderCfg(themeFld);

			Template temp = cfg.getTemplate(uri);

			temp.process(widgetData, ThreadContextHolder.getHttpResponse().getWriter());


			 
		} catch (FileNotFoundException e) {
			throw new UrlNotFoundException();
		} catch (Exception e) {
			// e.printStackTrace();
			HttpServletResponse httpResponse = ThreadContextHolder
					.getHttpResponse();
			httpResponse.setStatus(HttpHeaderConstants.status_500);
			try {
				e.printStackTrace(httpResponse.getWriter());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			 
		}
	}
	
	

}
