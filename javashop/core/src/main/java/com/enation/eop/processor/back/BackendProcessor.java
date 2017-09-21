package com.enation.eop.processor.back;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.enation.eop.IEopProcessor;
import com.enation.eop.SystemSetting;
import com.enation.eop.resource.IAdminThemeManager;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.AdminTheme;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.Menu;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;
/**
 * 后台模板处理器<br>
 * 负责权限的判断及后台模板的解析<br>
 * @author kingapex
 *2015-3-13
 */
public class BackendProcessor implements IEopProcessor {
	
	
	public boolean process() throws IOException{
		
		//IAdminUserManager adminUserManager = SpringContextHolder.getBean("adminUserManager");
		AdminUser adminUser  = UserConext.getCurrentAdminUser();
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		HttpServletResponse httpResponse=  ThreadContextHolder.getHttpResponse();
		
		String ctx = httpRequest.getContextPath();
		
		
		if("/".equals(ctx)){
			ctx="";
		}
		String uri = httpRequest.getServletPath();
		
		if( uri.startsWith("/core/admin/ueditor/get-config-json.do") ){
			return false;
		}
		if( uri.startsWith("/admin/login.do")){ //登录界面放过
			return false;
		}
		
		if(uri.startsWith("/core/admin/admin-user/login.do")){//登录验证放过
			return false;
		}
		 
		
		String redirectUrl ="";
		if(uri.startsWith("/admin") ){
			if(adminUser==null){
				redirectUrl=(ctx+"/admin/login.do");
			}else{ //已经登录过了
				if((!uri.startsWith("/admin/"))||"/admin/".equals(uri)){//如果不是访问的登录界，跳到登录界面
					httpResponse.sendRedirect(ctx+"/admin/main.do");
					return true;
				}
				return false;
			}
			httpResponse.sendRedirect(redirectUrl);
			return true;
		}else{ // 访问应用下的功能
			
			
			if(adminUser==null){//超时了
				String referer = RequestUtil.getRequestUrl(httpRequest);
				referer=URLEncoder.encode(referer, "utf-8");//add by jianghongyan 对url进行处理 
				
				PrintWriter out = httpResponse.getWriter();  
		        out.println("<html>");      
		        out.println("<script>");      
		        out.println("window.open('"+ctx+"/admin/login.do','_top')");      
		        out.println("</script>");      
		        out.println("</html>"); 
				return true;
			}else{
				
				IMenuManager menuManager = SpringContextHolder.getBean("menuManager");
				List<Menu> menuList = null;
				//从session中获取菜单列表
				Object sessionMenuList = ThreadContextHolder.getSession().getAttribute(SystemSetting.menuListKey.toString());
				//判断session是否存储了菜单列表，如果不存在则查询存入
				if(sessionMenuList==null){
					menuList = menuManager.getMenuByUser(adminUser);
					ThreadContextHolder.getSession().setAttribute("menuListKey", menuList);
				}else{
					menuList = (List<Menu>) sessionMenuList;
				}
				
				// 判断是否有权限，如果该url存在菜单内，就可以直接判断该管理员有没有全新啊
				// 如果访问一个不存在菜单内的菜单项，直接通过
				
				Menu nowMenu = null;	// 找到当前uri的菜单对象
				
				// 1. 第一次循环，判断是否有权限
				boolean result = false;
				for(Menu menu : menuList) {
					
					// 如果有了 不用继续判断 跳出循环吧
					if(result) {
						break;
					}
					String url = menu.getUrl();
					
					// 去除问号之后的参数
					if (url!=null && url.indexOf('?') > 0) { 
						url = url.substring(0, url.indexOf('?'));
					}
					
					if (uri.equals(url)) {
						result = true;
						nowMenu = menu;
					}
				}
				
				// 2.如果没有权限，就判断 该菜单是否存在菜单列表里
				if (!result) {
					
					// 先假设不存在 可以通过
					result = true;
					
					List<Menu> allMenus = menuManager.getMenuList();
					
					for(Menu menu : allMenus) {
						
						// 如果存在url
						if (!result) {
							break;
						}
						String url = menu.getUrl();
						if(url==null){
							url="";
							menu.setUrl("");
						}
						
						// 去除问号之后的参数
						if (url.indexOf('?') > 0) { 
							url = url.substring(0, url.indexOf('?'));
						}
						if (uri.equals(url)) {
							result = false;
						}
					}
				}
				if(result){
					if(uri.indexOf("shop/admin/goods/trash-list.do") >0){
						result=false;
					}
					if(uri.indexOf("b2b2c/admin/store-order/not-ship-order.do") > 0){
						result=false;
					}
				}
				// 如果不通过并且不是超级管理员 则跳转到权限不足页面
				if (!result && adminUser.getFounder() != 1) {
					redirectUrl=(ctx + "/admin/permission-denied.do?message_type=quanxian");
					httpResponse.sendRedirect(redirectUrl);
					return true;
				
				} 
				// 如果通过了，还得判断是否是演示站点，是否禁止显示
				// 如果是演示站点 并且能在菜单列表里找到该菜单
				if(nowMenu!= null && EopSetting.IS_DEMO_SITE){
					
					//  如果当前菜单是禁止演示站点显示的
					if (nowMenu.getIs_display() == 1) {
						redirectUrl=(ctx + "/admin/permission-denied.do?message_type=xianzhi");
						httpResponse.sendRedirect(redirectUrl);
						return true;
					}
				}
				
				EopSite site=EopSite.getInstance();
				String product_type = EopSetting.PRODUCT;
				httpRequest.setAttribute("site",site);
				httpRequest.setAttribute("ctx",ctx);
				httpRequest.setAttribute("product_type",product_type);
				httpRequest.setAttribute("theme",getAdminTheme(site.getAdminthemeid() ));
				
				HttpSession session = ThreadContextHolder.getHttpRequest().getSession();
				if(session!=null){
					httpRequest.setAttribute("prompt", session.getAttribute("userSetPrompt"));	//关闭全局提示，1为关闭
				}
				
				
			}
		}
		
		return false;
	}
	
	private String getAdminTheme(int themeid){
		
		IAdminThemeManager adminThemeManager =SpringContextHolder.getBean("adminThemeManager");
		// 读取后台使用的模板
		AdminTheme theTheme = adminThemeManager.get(themeid);
		String theme = "default";
		if (theTheme != null) {
			theme = theTheme.getPath();
		}
		return theme;
	}
	
}
