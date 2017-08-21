package com.enation.app.base.core.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMethod;
import com.enation.eop.resource.IAdminThemeManager;

import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.AdminTheme;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.Menu;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 后台界面控制器
 * @author kingapex
 * @version v2.0 改为spring mvc
 * 2016年2月10日下午6:05:47
 * @since v6.0
 */
@Controller 
@RequestMapping("/admin/")
public class BackendUiController {
	
 
	
	@Autowired
	private IAdminThemeManager adminThemeManager;
	
	@Autowired
	private IMenuManager menuManager;
	
	
	
	/**
	 * 后台登陆界面
	 * @return 后台登陆界面
	 */
	@RequestMapping(value="/login")
	public ModelAndView login(String referer) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("referer", referer);
		mv.setViewName("/adminthemes/"+this.getTheme()+"/login");
		// 存放站点信息
		return mv;
	}
	
 
	/**
	 * 跳转至后台主页面
	 * @param user 后台管理员
	 * @param version 版本
	 * @param product_type 程序模型：b2c、b2b2c、o2o
	 * @param ctx 虚拟目录
	 * @return 后台主页面
	 */
	@RequestMapping(value="/main")
	public ModelAndView main() {
		AdminUser user = UserConext.getCurrentAdminUser();
		List<Menu> menuList =null;
		// 判断当前管理员是否为超级管理员, 获取权限点列表
		if (user.getFounder() != 1) {
			menuList = this.menuManager.newMenutree(0, user);
		} else {
			menuList = this.menuManager.getMenuTree(0);
		}
	 
		ModelAndView view = new ModelAndView();
		view.addObject("menuList",menuList);
		view.setViewName("/adminthemes/"+this.getTheme()+"/main_page");
		return view;
	}
	
	/**
	 * 权限不足页面
	 * @return
	 */
	@RequestMapping(value="/permission-denied")
	public ModelAndView PermissionDenied(String message_type) {
		
		String message = "";
		
		if("quanxian".equals(message_type)) {
			message = "对不起，权限不足";
		} else if ("xianzhi".equals(message_type)) {
			message = "对不起，因该页面包含敏感信息，禁止显示";
		}
		
		ModelAndView view = new ModelAndView();
		view.addObject("message", message);
		view.setViewName("/adminthemes/"+this.getTheme()+"/permission_denied");
		return view;
	}
	
	
 
	public String getTheme() {
		
		EopSite site = this.getSite();
		
		// 读取后台使用的模板
		AdminTheme theTheme = adminThemeManager.get(site.getAdminthemeid());
		
		String theme = "default";
		if (theTheme != null) {
			theme = theTheme.getPath();
		}
		
		
		return theme;
	}

	@ModelAttribute("site")
	public EopSite getSite() {
		EopSite site = EopSite.getInstance().getInstance();
		return site;
	}

	@ModelAttribute("ctx")
	public String getCtx() {
		String ctx = ThreadContextHolder.getHttpRequest().getContextPath();
		
		// 获取虚拟目录
		if ("/".equals(ctx)) {
			ctx = "";
		}		
		return ctx;
	}
	@RequestMapping(value="/{errorPage}",method = RequestMethod.GET) 
	public String getLogin(@PathVariable("errorPage") String errorPage){
		return "/adminthemes/"+this.getTheme()+"/" + errorPage;
	}
}
