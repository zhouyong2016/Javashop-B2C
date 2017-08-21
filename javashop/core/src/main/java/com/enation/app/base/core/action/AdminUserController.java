package com.enation.app.base.core.action;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.ValidCodeServlet;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.HttpUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 管理员Action
 * @author kingapex
 * @author kanon 2015-9-24 version 1.1 添加注释
 */
 
@Controller 
@RequestMapping("/core/admin/admin-user")
public class AdminUserController  {
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IAdminUserManager adminUserManager;
 
	
	/**
	 * 管理员登陆
	 * @param valid_code 验证码
	 * @param username 管理员账号
	 * @param password 管理员密码
	 * @return 登陆状态
	 */
	@ResponseBody
	@RequestMapping(value="/login")
	public JsonResult login(String username,String password,String valid_code,String remember_login_name){
		try {
			//校验验证码
			if (valid_code == null)
			{
				return JsonResultUtil.getErrorJson("验证码输入错误");
				 
			}
			valid_code = valid_code.toLowerCase();
			HttpSession sessonContext = ThreadContextHolder.getSession();
			Object realCode = ("" + sessonContext.getAttribute(ValidCodeServlet.SESSION_VALID_CODE + "admin")).toLowerCase();

			if (!valid_code.equals(realCode)) {
				return JsonResultUtil.getErrorJson("验证码输入错误");
				 
			}

			//登录校验
			adminUserManager.login(username, password);
		 
			HttpServletResponse httpResponse = ThreadContextHolder.getHttpResponse();
			// 记住用户名
			if (!StringUtil.isEmpty(remember_login_name)) { 
				HttpUtil.addCookie(httpResponse, "loginname", username,	365 * 24 * 60 * 60);
			} else { 
				// 删除用户名
				HttpUtil.addCookie(httpResponse, "loginname", "", 0);
			}
			//=============start eop完成认证后，加入shiro身份验证  by tito
		 	
			Subject subject = SecurityUtils.getSubject(); 

			UsernamePasswordToken token = new UsernamePasswordToken(username, password); 

			try{ 

				subject.login(token); 

			}catch (AuthenticationException e){ 
	
				this.logger.error(e.getMessage(), e);
			}
	
		   //=============end eop完成认证后，加入shiro身份验证
	
			return JsonResultUtil.getSuccessJson("登陆成功");
			 
		} catch (Throwable exception) {
			this.logger.error(exception.getMessage(), exception);
			exception.getStackTrace();
			return JsonResultUtil.getErrorJson(exception.getMessage());
		}
	 
	}
	
	/**
	 * 管理员退出
	 * @return 退出状态
	 */
	@ResponseBody
	@RequestMapping(value="/logout")
	public JsonResult logout(){
		try {
			HttpSession sessonContext = ThreadContextHolder.getSession();
			sessonContext.removeAttribute(UserConext.CURRENT_ADMINUSER_KEY);
			
			//=============start eop完成退出后，shiro也退出  by tito
			Subject subject = SecurityUtils.getSubject(); 
			try {
				subject.logout();
			} catch (AuthenticationException e) {
				this.logger.error(e.getMessage(), e);
			}
			//=============end  eop完成退出后，shiro也退出
			return JsonResultUtil.getSuccessJson("成功注销");
		} catch (Exception e) {
			this.logger.error("管理员注销失败"+e.getMessage());
			return JsonResultUtil.getErrorJson("注销失败");
		}
	 
	}
	
	
}
