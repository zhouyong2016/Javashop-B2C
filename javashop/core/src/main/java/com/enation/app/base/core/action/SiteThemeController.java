package com.enation.app.base.core.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.Theme;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 站点主题管理
 * 
 * @author lzf
 *         <p>
 *         2009-12-30 上午11:01:08
 *         </p>
 * @version 1.0
 * @author Kanon 2015-11-16 version 1.1 添加注释
 */
@Controller
@RequestMapping("/core/admin/user/siteTheme")
public class SiteThemeController  {

	@Autowired
	private IThemeManager themeManager;
	
	/**
	 * 获取站点主题列表
	 * @param ctx 虚拟目录
	 * @param site 站点信息
	 * @param themeinfo 当前模板
	 * @param listTheme 模板列表
	 * @param previewpath 模板主题图片
	 */
	@RequestMapping(value="/info")
	public ModelAndView info() throws Exception {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ctx = request.getContextPath();
		EopSite site  =EopSite.getInstance();
		String previewBasePath = ctx+ "/themes/";

		Theme themeinfo = themeManager.getTheme( site.getThemeid());
		List<Theme> listTheme = themeManager.list();
		String previewpath = previewBasePath + themeinfo.getPath() + "/preview.png";
		
		
		ModelAndView view=new ModelAndView();
		view.addObject("previewBasePath", previewBasePath);
		view.addObject("themeinfo", themeinfo);
		view.addObject("listTheme", listTheme);
		view.addObject("previewpath", previewpath);
		view.setViewName("/core/admin/user/sitetheme");
		return view;
	}
	
//	/**
//	 * 跳转添加模板主题
//	 * @return
//	 */
//	public String add(){
//		return this.INPUT;
//	}
	
//	/**
//	 * 保存模板主题
//	 * @param theme 模板
//	 * @return
//	 */
//	public String save(Theme theme){
//		//this.msgs.add("模板创建成功");
//		//this.urls.put("模板列表", "siteTheme.do");
//		try {
//			this.showSuccessJson("模板创建成功");
//			this.themeManager.addBlank(theme);
//		} catch (Exception e) {
//			this.showErrorJson("模板创建失败");
//		}
//		return this.MESSAGE;
//	}
	
	/**
	 * 更换主题
	 * @param themeid 模板id
	 */
	@ResponseBody
	@RequestMapping(value="/change")
	public String change(Integer themeid)throws Exception {
		themeManager.changetheme(themeid);
		return "/core/admin/user/siteadmintheme"; 
	}

}
