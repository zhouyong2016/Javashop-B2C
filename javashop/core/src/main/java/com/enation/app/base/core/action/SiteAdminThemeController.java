package com.enation.app.base.core.action;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enation.eop.resource.IAdminThemeManager;
import com.enation.eop.resource.model.AdminTheme;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 站点主题管理
 * 
 * @author lzf
 *         <p>
 *         2009-12-30 上午11:01:08
 *         </p>
 * @version 1.0
 */
@Controller
@RequestMapping("/core/admin/user/siteAdminTheme")
public class SiteAdminThemeController  {

	@Autowired
	private IAdminThemeManager adminThemeManager;
	
	@RequestMapping(value="/info")
	public ModelAndView info() throws Exception {
		String contextPath = ThreadContextHolder.getHttpRequest().getContextPath();
		String previewBasePath =  contextPath+ "/adminthemes/";
		AdminTheme adminTheme = adminThemeManager.get( EopSite.getInstance().getAdminthemeid());
		List<AdminTheme> listTheme = adminThemeManager.list();
		String previewpath = previewBasePath + adminTheme.getPath() + "/preview.png";
		
		ModelAndView view=new ModelAndView();
		view.addObject("contextPath", contextPath);
		view.addObject("previewBasePath", previewBasePath);
		view.addObject("adminTheme", adminTheme);
		view.addObject("listTheme", listTheme);
		view.addObject("previewpath", previewpath);
		view.setViewName("/core/admin/user/siteadmintheme");
		return view;
	}
	
//	public String change()throws Exception {
//	//	siteManager.changeAdminTheme(themeid);
//		return this.execute();
//	}


}
