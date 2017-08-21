package com.enation.app.base.core.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enation.eop.resource.IIndexItemManager;

/**
 * 后台首页
 * 
 * @author kingapex 2010-10-12下午04:53:52
 * @author Kanon 2015-10-14 version 1.1 添加注释
 * @author Kanon 2016-2-29; 6.0版本改造
 */
@Controller
@Scope("prototype")
@RequestMapping("/core/admin/index")
public class IndexController  {
	
	@Autowired
	private IIndexItemManager indexItemManager;
	
	/**
	 * 后台首页
	 * @param itemList 首页项列表
	 */
	@RequestMapping("/show")
	public ModelAndView show() {
		
		ModelAndView view=new ModelAndView();
		view.setViewName("/core/admin/index");
		view.addObject("itemList", indexItemManager.list());
		return view;
	}

}
