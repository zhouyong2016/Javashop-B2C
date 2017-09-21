package com.enation.app.base.core.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONArray;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.component.ComponentView;
import com.enation.framework.component.IComponentManager;
import com.enation.framework.component.PluginView;
import com.enation.framework.util.JsonResultUtil;

/**
 * 组件管理Action
 * @author kingapex
 * @version 2.0 maven版本升级改造 6.0   wangxin 2016-2-24
 */
@Controller 
@Scope("prototype")
@RequestMapping("/core/admin/component")
public class ComponentController extends GridController {
	@Autowired
	private IComponentManager componentManager;
	/**
	 * 跳转至组件列表页面
	 * @return 组件列表页面
	 */
	@RequestMapping(value="/list")
	public String list() {
		return "/core/admin/component/list";
	}
	
	/**
	 * 获取组件列表JSON
	 * @param componentList 组件列表
	 * @return 组件列表JSON
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GridJsonResult listJson() {
		List<ComponentView> componentList = this.componentManager.list();
		
		List l = new ArrayList();
		for(ComponentView view : componentList){
			Map map  = new HashMap();
			map.put("id", view.getId());
			map.put("name", view.getName());
			map.put("install_state", view.getInstall_state());
			map.put("enable_state", view.getEnable_state());
			map.put("error_message", view.getError_message());
			map.put("componentid", view.getComponentid());
			
			int size = view.getPluginList().size();
			if(size!=0){
				//map.put("state", "closed");
				map.put("children", view.getPluginList());
			}
			l.add(map);
		}
		//String json = JSONArray.fromObject(l).toString();
		return JsonResultUtil.getGridJson(l);

	}

	/**
	 * 安装组件
	 * @param componentid 组件ID
	 * @return 安装状态
	 */
	@ResponseBody
	@RequestMapping(value="/install")
	public JsonResult install(String componentid) {
		//检测是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");	
		}
		//安装组件
		try {
			this.componentManager.install(componentid);
			return JsonResultUtil.getSuccessJson("安装成功");
		} catch (RuntimeException e) {
			this.logger.error("安装组件[" + componentid + "]", e);
			return JsonResultUtil.getErrorJson(e.getMessage());	

		}
	}

	/**
	 * 卸载组件
	 * @param componentid 组件ID
	 * @return 卸载状态
	 */
	@ResponseBody
	@RequestMapping(value="/un-install")
	public JsonResult unInstall(String componentid) {
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");	
		}
		try {
			this.componentManager.unInstall(componentid);
			return JsonResultUtil.getSuccessJson("卸载成功");

		} catch (RuntimeException e) {
			this.logger.error("卸载组件[" + componentid + "]", e);
			return JsonResultUtil.getErrorJson(e.getMessage());	
		}
	}

	/**
	 * 启用组件
	 * @param componentid 组件ID
	 * @return 启动状态
	 */
	@ResponseBody
	@RequestMapping(value="/start")
	public JsonResult start(String componentid) {
		
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");	
		}
		
		try {
			this.componentManager.start(componentid);
			return JsonResultUtil.getSuccessJson("启动成功");
		} catch (RuntimeException e) {
			this.logger.error("启动组件[" + componentid + "]", e);
			return JsonResultUtil.getErrorJson(e.getMessage());	

		}
	}

	/**
	 * 停用组件
	 * @param componentid 组件ID
	 * @return 停用状态
	 */
	@ResponseBody
	@RequestMapping(value="/stop")
	public JsonResult stop(String componentid) {
		
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");	
			}
		
		try {
			this.componentManager.stop(componentid);
			return JsonResultUtil.getSuccessJson("停用成功");

		} catch (RuntimeException e) {
			this.logger.error("停用组件[" + componentid + "]", e);
			return JsonResultUtil.getErrorJson(e.getMessage());	
		}

	}


}
