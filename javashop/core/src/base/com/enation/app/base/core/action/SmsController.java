package com.enation.app.base.core.action;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.SmsPlatform;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;


/**
 * 短信平台Action
 * @author xulipeng
 * @author Kanon 2015-11-16 version 1.1 添加注释
 * @author xulipeng 2016年02月20日  修改springMVC
 */

@Controller 
@RequestMapping("/core/admin/sms")
public class SmsController extends GridController {

	@Autowired
	private ISmsManager smsManager;
	
	/**
	 * 跳转至短信平台列表页
	 * @return
	 */
	@RequestMapping(value="list")
	public String list(){
		return "/core/admin/sms/sms_platform";
	}
	
	/**
	 * 设置短信参数页
	 * @param platform
	 * @param param_html	参数
	 * @param smsid			短信网关id
	 * @param pluginid		短信插件id
	 * @return
	 */
	@RequestMapping(value="/get-html")
	public ModelAndView gethtml(SmsPlatform platform,String param_html,Integer smsid,String pluginid){
		
		ModelAndView view = new ModelAndView();
		try {
			view.addObject("platform", this.smsManager.get(smsid));
			view.addObject("param_html", this.smsManager.getSmsPlatformHtml(pluginid, smsid));
			view.setViewName("/core/admin/sms/edit");
		} catch (Exception e) {
			this.logger.error("设置短信参数出错", e);
		}
		
		return view;
	}
	
	/**
	 * 查询短信平台列表json数据
	 * @param list 短信网关列表
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@ResponseBody  
	@RequestMapping(value="/sms-listJson")
	public GridJsonResult listJson(){
		List list = this.smsManager.getList();
		return JsonResultUtil.getGridJson(list);
	}
	
	/**
	 * 保存参数
	 * @param smsid
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(Integer smsid){
		try{
			HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
			Enumeration<String> names = request.getParameterNames();
			Map<String,String> params = new HashMap<String, String>();
			while(names.hasMoreElements()){
				String name= names.nextElement();
				if("smsid".equals(name)) continue;
				String value = request.getParameter(name);
				params.put(name, value);
			}
			this.smsManager.setParam(smsid, params);
			
			return JsonResultUtil.getSuccessJson("设置成功");
		}catch(Exception e){
			this.logger.error("设置短信网关参数出错", e);
			return JsonResultUtil.getErrorJson("设置失败");
		}
	}
	
	/**
	 * 设置默认启用sms平台
	 * @param smsid 短信网关id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/set-open")
	public JsonResult setOpen(Integer smsid){
		try {
			this.smsManager.open(smsid);
			return JsonResultUtil.getSuccessJson("已启用");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("启用失败");
		}
	}
	
}
