package com.enation.app.base.core.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.Menu;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;

/**
 * 菜单管理
 * @author kingapex
 * 2010-8-20下午12:36:03
 * @author Kanon 2015-10-14 version 1.1 添加注释
 * @author Kanon 2016-2-18;6.1版本改造
 */
@Scope("prototype")
@Controller
@RequestMapping("/core/admin/menu")
public class MenuController  {
	
	protected  Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IMenuManager menuManager; 
	
	@Autowired
	private IAuthActionManager authActionManager;
	
	/**
	 * 菜单树形列表页面
	 * @return
	 */
	@RequestMapping(value="/tree")
	public String tree(){
		
		return "/core/admin/menu/tree";
	}
	
	/**
	 * 移动菜单
	 * @param id 移动菜单Id
	 * @param targetid 移动后菜单父Id
	 * @param movetype 移动类型
	 * @return 移动状态
	 */
	@ResponseBody
	@RequestMapping(value="/move")
	public JsonResult move(Integer id,int targetid,String movetype){
		
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
		}
		
		try{
			this.menuManager.move(id, targetid, movetype);
			//将session中的菜单列表数据移除
			ThreadContextHolder.getSession().removeAttribute(SystemSetting.menuListKey);
			return JsonResultUtil.getSuccessJson("移动成功");
		}catch(Throwable e){
			this.logger.error("move menu",e);
			return JsonResultUtil.getErrorJson("移动菜单出错"+e.getMessage());
		}
	}
	/**
	 * 菜单列表JSON
	 * @param menuList 菜单列表
	 * @param json 菜单列表JSON
	 * @return 菜单列表JSON
	 */
	@ResponseBody
	@RequestMapping(value="/json")
	public List json(){
		List list=new ArrayList();
		List<Menu> menuList  = this.menuManager.getMenuTree(0);
		for(Menu menu:menuList){
			list.add(this.toJson(menu));
		}
		return list;
	}
	
	
	/**
	 * 跳转到菜单添加页面
	 * @return
	 */
	@RequestMapping(value="/add")
	public ModelAndView add(Integer parentid){
		ModelAndView view=new ModelAndView();
		view.addObject("menuList",  this.menuManager.getMenuTree(0));
		view.addObject("parentid", parentid);
		view.setViewName("/core/admin/menu/add");
		return view;
	}
	
	/**
	 * 添加菜单
	 * @param menu 菜单
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(Menu menu){
		
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
		}
		
		try{
			Integer menuid=this.menuManager.add(menu);
			//将session中的菜单列表数据移除
			ThreadContextHolder.getSession().removeAttribute(SystemSetting.menuListKey);
			return JsonResultUtil.getObjectJson(menuid);
		}catch(RuntimeException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			JsonResultUtil.getErrorJson(e.getMessage());
		}
		return JsonResultUtil.getErrorJson("抱歉，保存失败");
	}
	
	/**
	 * 跳转到修改菜单页面
	 * @param id 菜单id
	 * @return
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer id){
		ModelAndView view =new ModelAndView();
		view.addObject("menuList", this.menuManager.getMenuTree(0));
		view.addObject("menu", this.menuManager.get(id));
		view.setViewName("/core/admin/menu/edit");
		return view;
	}
 
	/**
	 * 修改菜单
	 * @param menu 菜单
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(Menu menu){
		
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
		}
		
		try{
			this.menuManager.edit(menu);
			//将session中的菜单列表数据移除
			ThreadContextHolder.getSession().removeAttribute(SystemSetting.menuListKey);
			return JsonResultUtil.getSuccessJson("添加成功");
		}catch(RuntimeException e){
			logger.error(e.getMessage(), e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
	
	/**
	 * 更改排序
	 * @param menu_ids 菜单id
	 * @param menu_sorts 菜单排序
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/update-sort")
	public JsonResult updateSort(Integer[] menu_ids,Integer[] menu_sorts){
		try{
			this.menuManager.updateSort(menu_ids, menu_sorts);
			//将session中的菜单列表数据移除
			ThreadContextHolder.getSession().removeAttribute(SystemSetting.menuListKey);
			return JsonResultUtil.getSuccessJson("");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			return JsonResultUtil.getErrorJson("+e.getMessage()+");
		}
	}
	
	/**
	 * 删除菜单
	 * @param id 菜单Id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer id){
		
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
		}
		
		try{
			this.menuManager.delete(id);
			//将session中的菜单列表数据移除
			ThreadContextHolder.getSession().removeAttribute(SystemSetting.menuListKey.toString());
			return JsonResultUtil.getSuccessJson("删除成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			return JsonResultUtil.getErrorJson("删除失败:"+e.getMessage());
		}		
	}
	
	/**
	 * 获取菜单json
	 * @param authid 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-menu-json")
	public String getMenuJson(Integer authid){
		StringBuffer data  = new StringBuffer();
		data.append("[");
		List<Menu> menuList  = this.menuManager.getMenuTree(0);
		com.enation.app.base.core.model.AuthAction authAction=null;
		
		if(authid!=0)
			authAction=  authActionManager.get(authid);
		int i=0;
		for(Menu menu:menuList){
			if(i!=0){			
				data.append(",");			
			}
				data.append(this.menutoJson(menu,authAction));
			i++;
		}
		data.append("]");
		return data.toString();
	}
	
	/**
	 * 
	 * @param menu 菜单
	 * @param authAction 权限点
	 * @return
	 */
	private String menutoJson(Menu menu,com.enation.app.base.core.model.AuthAction authAction){
		StringBuffer data  = new StringBuffer();
		data.append("{\"id\":"+menu.getId()+", \"name\":\""+menu.getTitle()+"\""+",\"open\":true");	
			if(authAction!=null){
			String[] menuids=authAction.getObjvalue().split(",");
			if(authAction!=null){
				for (int i = 0; i < menuids.length; i++) {
					if(Integer.parseInt(menuids[i])==menu.getId()){
							data.append(",\"checked\":true");
					}
				}
			}
		}
		if(menu.getHasChildren()){ //如果有子，继续
			data.append(",\"children\":[");
			int i=0;
			List<Menu> menuList= menu.getChildren();
			for(Menu child:menuList){
				if(i!=0){			
					data.append(",");			
				}
				if(authAction!=null){
					data.append(this.menutoJson(child,authAction));
				}else{
					data.append(this.menutoJson(child,null));
				}
				i++;
			}
			data.append("]");
		}
		data.append("} ");
		return data.toString();
	}
	/**
	 * 获取子菜单列表JSON
	 * @param menu 菜单
	 * @param menuList 菜单子列表
	 * @return 子菜单列表JSON
	 */
	private Map toJson(Menu menu){
		Map map=new HashMap();
		map.put("menuid", menu.getId());
		map.put("name", menu.getTitle());
		map.put("isParent", menu.getHasChildren());
		if(menu.getHasChildren()){ //如果有子，继续
			List children=new ArrayList();
			List<Menu> menuList= menu.getChildren();
			for(Menu child:menuList){
				children.add(this.toJson(child));
			}
			map.put("children", children);
		}
		return map;
	}
}
