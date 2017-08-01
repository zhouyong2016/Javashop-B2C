package com.enation.app.shop.core.other.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.other.service.ILogsManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 操作日志管理
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2016年12月8日 下午4:32:33
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/logs")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class LogsController   extends GridController {

	@Autowired
	private ILogsManager logManager;
	
	@Autowired
	private IAdminUserManager adminUserManager;
	/**
	 * 跳转到日志列表页
	 * @return
	 */
	@RequestMapping(value="/list")
	public ModelAndView list() {
		ModelAndView view =this.getGridModelAndView();
		view.addObject("adminList", this.adminUserManager.list());
		view.setViewName("/shop/admin/logs/logs_list");
		return view;
	}
	
	/**
	 * 查询日志记录json
	 * @param name 操作人名字
	 * @param type 操作类型标识
	 * @param start_time 操作开始时间
	 * @param end_time  操作结束时间 
	 * @param userid  操作人主键
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(String name,String type,String start_time,String end_time,Integer userid) {
		
		Map logsMap = new HashMap();
		logsMap.put("name", name);
		logsMap.put("userid", userid);
		logsMap.put("type", type);
		logsMap.put("start_time", start_time);
		logsMap.put("end_time", end_time);
		
		webpage = logManager.getAllLogs(logsMap,getPage(), getPageSize(),this.getSort(),this.getOrder());
		return JsonResultUtil.getGridJson(webpage);

	}
	
	
}
