/**
 * 
 */
package com.enation.eop.processor.facade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enation.eop.SystemSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 静态页解析器
 * @author kingapex
 *2015-3-26
 */
public class StaticPageParser {
	
	public  boolean parse(String uri) throws ServletException, IOException {
		
		HttpServletRequest httpRequest =ThreadContextHolder.getHttpRequest();
		HttpServletResponse httpResponse=ThreadContextHolder.getHttpResponse();
		
		//wap站不访问静态页
		if(SystemSetting.getWap_open()==1 && httpRequest.getServerName().equals( SystemSetting.getWap_domain() )){
			return false;
		}
		//如果开启wap站访问，且是手机访问，重定向到wap站
		if(SystemSetting.getWap_open()==1&& this.isMobile()){
			int port = httpRequest.getServerPort();

			String portstr="";
			if(port!=80){
				portstr=":"+port;
			}
			String contextPath = httpRequest.getContextPath();
			if(contextPath.equals("/")){
				contextPath="";
			}

			String severname= SystemSetting.getWap_domain();
			String url  = "http://"+severname+portstr+contextPath+uri;
			httpResponse.sendRedirect(url);
			return true;
		}
		if("/".equals(uri)){
			uri="/index.html";
		}
		if(hasStatic(uri)){
			String path = this.getDispatcherPath(uri);
			httpRequest.getRequestDispatcher(path).forward(httpRequest, httpResponse);
			return true ;
			
		}
		
		return false;
	}
	 
	/**
	 * 获取转发后的路径
	 * @param uri 原路径
	 * @return 如原路径为:/goods-1.html，则新路径为/html/goods/goods-1.html ，即将goods提取出来作为文件夹
	 * 如果是register.html这种不是伪静态的，则不提取为文件夹
	 */
	private String getDispatcherPath(String uri){
		String str ="/(\\w+)-(.*).html";
		Pattern pattern = Pattern.compile("^" + str+ "$", 2 | Pattern.DOTALL);
		Matcher m = pattern.matcher(uri);
		String folder ="";
		if(m.find()){
			folder= (m.group(1));
		}
		String path  = "/html/"+folder+uri;
		return  path;
	}
	
	public static void main(String[] args) {
		
		String str ="/(\\w+)-(.*).html";
		String uri="/goods-1.html";
		Pattern pattern = Pattern.compile("^" + str+ "$", 2 | Pattern.DOTALL);
		Matcher m = pattern.matcher(uri);
		if(m.find()){
			System.out.println(m.group(1));
		}
	}
	
	private boolean hasStatic(String uri){
		
		List<String> list = new ArrayList<String>();
		list.add("/index.html");
		list.add("/goods-(\\d+).html");
		list.add("/help-(\\d+)-(\\d+).html");
		list.add("/register.html");
		list.add("/login.html");
		for (String string : list) {
			Pattern pattern = Pattern.compile("^" + string+ "$", 2 | Pattern.DOTALL);
			Matcher m = pattern.matcher(uri);
			if (m.find()){
				return true;
			}
			
		}
		
		return false;
	}
	
	/**
	 * 检测是不是手机访问
	 * @return
	 */
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
