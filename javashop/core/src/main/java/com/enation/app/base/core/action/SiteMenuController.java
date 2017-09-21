package com.enation.app.base.core.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.SiteMenu;
import com.enation.app.base.core.service.ISiteMenuManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

import net.sf.json.JSONArray;

/**
 * 后台导航栏管理
 * @author DMRain 2016年2月20日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/core/admin/site-menu")
public class SiteMenuController extends GridController{

	@Autowired
	private ISiteMenuManager siteMenuManager ;
	@Autowired
	private ISiteMenuManager siteMenuDbManager ;
	
	/**
	 * 跳转至导航栏列表
	 * @return
	 */
	@RequestMapping(value="/list")
	public String list(){
		
		return "/core/admin/siteMenu/site_menu_list";
	}
	
	/**
	 * 获取导航栏列表json
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(){
		List menuList = this.siteMenuManager.list(0);
		return JsonResultUtil.getGridJson(menuList);
	}
	
	/**
	 * 异步加载获取导航栏列表json
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-list-by-parentid-json")
	public String getListJson(Integer parentid){
		List menuList = this.siteMenuDbManager.list(parentid);
		String s = JSONArray.fromObject(menuList).toString();
		return s.replace("name", "text").replace("menuid", "id");
	}
	
	/**
	 * 更改导航栏排序
	 * @param menuidArray 导航栏ID列表
	 * @param sortArray 排序列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/update-sort")
	public JsonResult updateSort(Integer[] menuidArray, Integer[] sortArray){
		try {
			this.siteMenuManager.updateSort(menuidArray, sortArray);
			return JsonResultUtil.getSuccessJson("保存排序成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("当前没有数据保存排序失败！");
		}
	}
	
	/**
	 * 跳转至添加导航栏
	 * @return
	 */
	@RequestMapping(value="/add")
	public ModelAndView add(){
		ModelAndView view = new ModelAndView();
		view.addObject("isEdit", false);
		view.addObject("menuList", this.siteMenuManager.list(0));
		view.addObject("siteMenu", new SiteMenu());
		view.setViewName("/core/admin/siteMenu/menu_input");
		return view;
	}
	
	/**
	 * 跳转至添加子导航
	 * @param isEdit 是否为修改
	 * @param menuList 导航栏列表
	 * @param menuid 导航栏ID
	 * @param siteMenu 导航栏
	 */
	@RequestMapping(value="/add-children")
	public ModelAndView addchildren(Integer menuid){
		ModelAndView view = new ModelAndView();
		view.addObject("isEdit", false);
		view.addObject("menuList", this.siteMenuManager.list(0));
		view.addObject("menuid", this.siteMenuManager.get(menuid).getMenuid());
		view.addObject("siteMenu", new SiteMenu());
		view.setViewName("/core/admin/siteMenu/menu_input");
		return view;
	}
	
	/**
	 * 跳转至修改导航栏
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer menuid){
		ModelAndView view = new ModelAndView();
		view.addObject("isEdit", true);
		view.addObject("menuList", this.siteMenuManager.list(0));
		view.addObject("siteMenu", this.siteMenuManager.get(menuid));
		view.setViewName("/core/admin/siteMenu/menu_input");
		return view;
	}
	
	/**
	 * 保存导航栏
	 * @param menuid 导航栏id
	 * @param siteMenu 导航栏
	 * @return 添加状态
	 */
	@ResponseBody
	@RequestMapping(value="/save")
	public JsonResult save(Integer menuid, SiteMenu siteMenu){
		//判断是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能添加这些示例数据，请下载安装包在本地体验这些功能！");
		}
		
		//判断导航栏id是否为空如果为空则为添加导航栏
		if(menuid == null){
			this.siteMenuManager.add(siteMenu);
			return JsonResultUtil.getSuccessJson("菜单添加成功");
		}else{
			if(menuid!=siteMenu.getParentid()){
				siteMenu.setMenuid(menuid);
				this.siteMenuManager.edit(siteMenu);
				return JsonResultUtil.getSuccessJson("菜单修改成功");
			}else{
				return JsonResultUtil.getErrorJson("修改失败,上级菜单不能为自己");
			}
			
		}
	}
	
	/**
	 * 删除导航栏
	 * @param menuid 导航栏Id
	 * @return 删除状态
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer menuid){
		//判断是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			if(menuid <= 21){
				return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			}
		}
		
		//删除导航栏
		try {
			if(menuid==null){
				return JsonResultUtil.getErrorJson("删除失败:请选择导航栏");
			}
			this.siteMenuManager.delete(menuid);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("删除失败:"+e.getMessage());
		}
	}
}
