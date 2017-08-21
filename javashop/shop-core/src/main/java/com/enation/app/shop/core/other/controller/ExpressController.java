package com.enation.app.shop.core.other.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.order.service.IExpressManager;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;

/**
 * 查询快递平台action
 * @author xulipeng 2016年02月24日  修改springmvc
 */

@Scope("prototype")
@Controller 
@RequestMapping("/shop/admin/express")
public class ExpressController{
	
	@Autowired
	private IExpressManager expressManager;
	
	/**
	 * 快递平台列表页
	 * @return
	 */
	@RequestMapping(value="/list")
	public String list(){
		return "/shop/admin/express/express_list";
	}
	
	/**
	 * 快递平台列表json
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(){
		List list = this.expressManager.getList();
		return JsonResultUtil.getGridJson(list);
	}
	
	/**
	 * 修改平台参数数据
	 * @param id	平台id
	 * @param code	平台code
	 * @return
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer id,String code){
		ModelAndView view = new ModelAndView();
		try {
			view.addObject("platform", this.expressManager.getPlateform(id));
			view.addObject("platformHtml",this.expressManager.getPlateformHtml(code, id));
			view.addObject("id", id);
			view.setViewName("/shop/admin/express/express_edit");
		} catch (Exception e) {
			
			Logger logger = Logger.getLogger(getClass());
			logger.error("修改快递平台参数出现错误",e);
		}
		return view;
	}
	
	/**
	 * 保存修改
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(Integer id){
		try{
			HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
			Enumeration<String> names = request.getParameterNames();
			Map<String,String> params = new HashMap<String, String>();
			while(names.hasMoreElements()){
				String name= names.nextElement();
				if("id".equals(name)) continue;
				String value = request.getParameter(name);
				params.put(name, value);
			}
			this.expressManager.setParam(id, params);
			return JsonResultUtil.getSuccessJson("设置成功");
		}catch(Exception e){
			Logger logger = Logger.getLogger(getClass());
			
			logger.error("设置快递网关参数出错", e);
			return JsonResultUtil.getErrorJson("设置失败");
		}
	}
	
	/**
	 * 设置启用的快递平台
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/set-open")
	public JsonResult setOpen(Integer id){
		try {
			this.expressManager.open(id);
			return JsonResultUtil.getSuccessJson("设置成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("出现错误，请稍后重试");
		}
	}

}
