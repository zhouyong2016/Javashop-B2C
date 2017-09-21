package com.enation.app.base.core.action;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.PluginTab;
import com.enation.app.base.core.plugin.setting.SettingPluginBundle;
import com.enation.app.base.core.service.ISettingService;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;

/**
 * @author apexking
 * @author Kanon 2015-10-14 version 1.1 添加注释
 * @author xulipeng 2016年02月24日	修改spring mvc
 */

@Scope("prototype")
@Controller 
@RequestMapping("/core/admin/setting")
public class SettingController {
	
	@Autowired
	private ISettingService settingService;
	
	@Autowired
	private SettingPluginBundle settingPluginBundle;

	/**
	 * 跳转至系统设置页面
	 * @param settings 设置列表
	 * @param htmls 系统设置HTMl
	 * @param tabs 系统设置TAB 
	 */
	@RequestMapping(value="/edit-input")
	public ModelAndView editInput(){
		
		Map settings = settingService.getSetting();
		ModelAndView view=new ModelAndView();
		List<PluginTab> tabList = this.settingPluginBundle.onInputShow(settings);
		
		view.addObject("tabs", tabList);
		view.setViewName("/core/admin/setting/input");
		
		return view;
	}
	
	/**
	 * 保存配置
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save")
	public JsonResult save(){
		//判断是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson(EopSetting.DEMO_SITE_TIP);
		}
		
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		Enumeration<String> names = request.getParameterNames();
		Map<String,Map<String,String>> settings = new HashMap<String, Map<String,String>>();
		
	    while(names.hasMoreElements()){
	    	String name= names.nextElement();
	    	String[]name_ar = name.split("\\.");
	    	if(name_ar.length!=2) continue;
	    	String groupName = name_ar[0];
	    	String paramName  = name_ar[1];
	    	String paramValue = request.getParameter(name);
	    	
	    	Map<String,String> params = settings.get(groupName);
	    	if(params==null){
	    		params = new HashMap<String, String>();
	    		settings.put(groupName, params);
	    	}
	    	params.put(paramName, paramValue);
	    }
		settingService.save( settings );
		return JsonResultUtil.getSuccessJson("配置修改成功");
	}

	/**
	 * 全局关闭提示
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/close-prompt")
	public JsonResult closePrompt(){
		HttpSession session = ThreadContextHolder.getHttpRequest().getSession();
		if(session!=null){
			session.setAttribute("userSetPrompt", 1);
			return JsonResultUtil.getSuccessJson("设置成功");
		}
		return JsonResultUtil.getErrorJson("设置失败");
	}

	
}
