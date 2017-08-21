package com.enation.app.base.core.action;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enation.eop.resource.model.EopSite;

/**
 * 首页项(基本)Action
 * @author kingapex
 * 2010-10-13下午05:16:45
 * @author kanon 2015-9-24 version 1.1 添加注释
 * @author Kanon 2016-2-29;6.0版本改造
 */
@Controller
@Scope("prototype")
@RequestMapping("/core/admin/indexItem")
public class BaseIndexItemController  {
	/**
	 * 显示首页基本项信息
	 * @param site 站点基本信息
	 * @return 首页基本项信息页面
	 */
	@RequestMapping("/base")
	public ModelAndView base() {
		ModelAndView view =new ModelAndView();
		view.addObject("javashopSite", EopSite.getInstance());
		view.setViewName("/core/admin/index/base");
		return view;
	}

}
