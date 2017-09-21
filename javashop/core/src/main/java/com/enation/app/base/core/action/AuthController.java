package com.enation.app.base.core.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.TestUtil;

/**
 * 权限点Action
 * @author kingapex
 * 2010-11-9下午05:55:11
 * @author LiFenLong 2014-6-4;4.0改版
 * @author Kanon 2015-9-24 version 1.1 添加注释
 * @author wangxin  2016-2-24 6.0 版本升级
 */
@Controller 
@Scope("prototype")
@RequestMapping("/core/admin/auth")
public class AuthController extends GridController{

	@Autowired
	private IAuthActionManager authActionManager;

	/**
	 * 跳转至权限点列表页
	 * @return 权限点列表页
	 */
	@RequestMapping(value="/list")
	public String list(){
		return "/core/admin/auth/auth_list";
	}

	/**
	 * 跳转至权限点添加页面
	 * @param isEdit 是否为修改0为修改，1为添加
	 * @return 权限点添加页面
	 */
	@RequestMapping(value="/add")
	public ModelAndView add(){
		ModelAndView view = new ModelAndView();
		view.addObject("isEdit",0);
		view.setViewName("/core/admin/auth/auth_input");
		return view;
	} 

	/**
	 * 跳转至权限点修改页面
	 * @param isEdit 是否为修改0为修改，1为添加
	 * @param auth 权限
	 * @return 权限点修改页面
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(int authid){
		ModelAndView view = new ModelAndView();
		view.addObject("isEdit",1);
		view.addObject("auth",authActionManager.get(authid));
		view.setViewName("/core/admin/auth/auth_input");
		return view;
	}

	/**
	 * 获取权限点列表JSON
	 * @return 权限点列表JSON
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(){
		List list=authActionManager.list();
		return JsonResultUtil.getGridJson(list);
	}

	/**
	 * 保存修改权限点
	 * @param act 权限点
	 * @param name 权限点名称
	 * @param authid 权限ID
	 * @param menuids 权限列表
	 * @return 权限点修改状态
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(String name,Integer authid,Integer choose,String menuids){
		try{
			com.enation.app.base.core.model.AuthAction act = new com.enation.app.base.core.model.AuthAction();
			act.setName(name.replace(" ", ""));
			act.setType("menu");
			act.setActid(authid);
			act.setChoose(choose);
			if(menuids == null || menuids.equals("")){
				act.setObjvalue("0");
			}else{
				act.setObjvalue(menuids);
			}
			//检测权限点名称是否重复
			Integer num=this.authActionManager.checkMenu(act);
			if(num > 0){
				return JsonResultUtil.getErrorJson("权限点名称重复,修改失败!");
			}
			this.authActionManager.edit(act);
			return JsonResultUtil.getSuccessJson("修改成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e.fillInStackTrace());
			return JsonResultUtil.getErrorJson("修改失败:"+e.getMessage());

		}
	}
	/**
	 * 保存权限点
	 * @param act 权限点
	 * @param name 权限点名称
	 * @param menuids 权限列表
	 * @param authid 权限点 ID
	 * @return 保存添加权限点状态
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(String name,String menuids){
		try{
			com.enation.app.base.core.model.AuthAction act = new com.enation.app.base.core.model.AuthAction();
			act.setName(name.replace(" ", ""));
			act.setType("menu");
			act.setChoose(0);
			if(menuids == null || menuids.equals("")){
				act.setObjvalue("0");
			}else{
				act.setObjvalue(menuids);
			}
			//检测权限点名称是否重复
			Integer num=this.authActionManager.checkMenu(act);
			if(num > 0){
				return JsonResultUtil.getErrorJson("权限点名称重复,添加失败!");
			}
			this.authActionManager.add(act);
			return JsonResultUtil.getSuccessJson("添加成功");
		}catch(RuntimeException e){
			TestUtil.print(e);
			this.logger.error(e.getMessage(), e.fillInStackTrace());
			return JsonResultUtil.getErrorJson("添加失败:"+e.getMessage());
		}
	}

	/**
	 * 删除权限点
	 * @param authid 权限点 ID
	 * @param authaction 权限点
	 * @return 权限点删除状态
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(int authid){
		try{
			com.enation.app.base.core.model.AuthAction authaction=this.authActionManager.get(authid);
			if(authaction.getChoose()==0){
				this.authActionManager.delete(authid);
				return JsonResultUtil.getSuccessJson("删除成功");

			}
			else{
				return JsonResultUtil.getErrorJson("此权限点为系统默认权限点，不能进行删除!");
			}
		}catch(RuntimeException e){
			return JsonResultUtil.getErrorJson("删除失败:"+e.getMessage());
		}
	}


}
