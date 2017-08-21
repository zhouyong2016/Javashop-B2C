package com.enation.eop.sdk.context;

import javax.servlet.http.HttpSession;

import com.enation.app.base.core.model.Member;
import com.enation.eop.resource.model.AdminUser;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 用户上下文
 * @author kingapex
 *
 */
public abstract class UserConext {
	public static final String CURRENT_MEMBER_KEY="curr_member";
	public static final String CURRENT_ADMINUSER_KEY="curr_adminuser";

	/**
	 * 获取当前登录的会员
	 * @return 如果没有登录返回null
	 */
	public static Member getCurrentMember(){

		HttpSession sessonContext = ThreadContextHolder.getSession();
		if (sessonContext != null) {
			try {
				Member member = (Member) sessonContext.getAttribute(UserConext.CURRENT_MEMBER_KEY);
				return member;
			} catch (IllegalStateException e) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 获取当前登录的管理员
	 * @return 如果没有登录返回null
	 */
	public static AdminUser getCurrentAdminUser(){

		HttpSession sessonContext = ThreadContextHolder.getSession();
		if(sessonContext!=null){
			return (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
		}
		return null;
	}
}
