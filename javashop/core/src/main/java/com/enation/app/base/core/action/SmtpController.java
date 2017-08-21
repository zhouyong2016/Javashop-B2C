package com.enation.app.base.core.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.Smtp;
import com.enation.app.base.core.service.ISmtpManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * smtp管理 
 * @author kingapex
 * @date 2011-11-1 下午12:27:51 
 * @version V1.0
 * @author LiFenLong 2014-4-1;4.0版本改造
 * @author Kanon 2015-11-16 version 1.1 添加注释
 * @author xulipeng 2016年02月20日 改为spring mvc
 */

@Controller 
@RequestMapping("/core/admin/smtp")
public class SmtpController extends GridController  {
	
	@Autowired
	private ISmtpManager smtpManager;
	
	/**
	 * 跳转至添加SMTP页面
	 * @param isedit 是否为修改 0为添加，1为修改
	 * @return 添加SMTP页面
	 */
	@RequestMapping(value="/add")
	public String add(){
		return "/core/admin/smtp/add";
	}

	/**
	 * 跳转至修改SMTP页面
	 * @param isedit 是否为修改 0为添加，1为修改
	 * @param smtpId SMTPId
	 * @param smtp SMTP
	 * @return 修改SMTP页面
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer smtpId){
		ModelAndView view=new ModelAndView();
		view.addObject("smtp", this.smtpManager.get(smtpId));
		view.setViewName("/core/admin/smtp/edit");
		
		return view;

	}
	
	/**
	 * 保存添加
	 * @param smtp SMTP
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(Smtp smtp){
		
		//判断是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson(EopSetting.DEMO_SITE_TIP);
		}
		try{
			this.smtpManager.add(smtp);
			return JsonResultUtil.getSuccessJson("smtp添加成功");
		}catch(RuntimeException e){
			logger.error("smtp添加失败",e);
			return JsonResultUtil.getErrorJson("smtp添加失败");
		}
	}
	
	/**
	 * 保存修改
	 * @param smtp SMTP
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(Smtp smtp){
		//是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson(EopSetting.DEMO_SITE_TIP);
		}
		try{
			//判断smtp密码是否为空
			if( StringUtil.isEmpty(smtp.getPassword()) ) {
				smtp.setPassword(this.smtpManager.get(smtp.getId()).getPassword()) ;
			}
			this.smtpManager.edit(smtp);
			return JsonResultUtil.getSuccessJson("smtp修改成功");
		}catch(RuntimeException e){
			logger.error("smtp修改失败", e);
			return JsonResultUtil.getErrorJson("smtp修改失败");
		}
	}
	  
	/**
	 * 跳转至smtp列表
	 * @return
	 */
	@RequestMapping(value="/list")
	public String list(){
		return "/core/admin/smtp/list";
	}
	
	/**
	 * 获取smtp列表JSON
	 * @author LiFenLong
	 * @param smtpList smtp列表
	 * @return smtp列表JSON
	 */
	@ResponseBody  
	@RequestMapping(value="/smtp-listJson")
	public GridJsonResult listJson(){
		 List smtpList = this.smtpManager.list();	
		 return JsonResultUtil.getGridJson(smtpList);
	}
	
	/**
	 * 删除SMTP
	 * @param id SMTPId
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer[] id){
		//判断是否演示站点
		if(EopSetting.IS_DEMO_SITE){
			return JsonResultUtil.getErrorJson(EopSetting.DEMO_SITE_TIP);
		}
		try{
			this.smtpManager.delete(id);
			return JsonResultUtil.getSuccessJson("smtp删除成功");
		}catch(RuntimeException e){
			this.logger.error("smtp删除失败", e);
			return JsonResultUtil.getErrorJson("smtp删除失败");
		}
	}
	
	
	/**
	 * 测试发送邮件
	 * @param smtp smtp实体
	 * @param send_to 发送地址
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="test-send")
	public JsonResult testSend(Smtp smtp,String send_to){
		try {
			this.smtpManager.testSend(smtp,send_to);
			return JsonResultUtil.getSuccessJson("smtp发送测试邮件成功");
		} catch (Exception e) {
			this.logger.error("smtp发送测试邮件失败", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
}
