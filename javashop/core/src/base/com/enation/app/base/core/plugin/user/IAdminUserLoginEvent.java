package com.enation.app.base.core.plugin.user;

import com.enation.eop.resource.model.AdminUser;


/**
 * 管理员登录事件
 * @author kingapex
 *
 */
public interface IAdminUserLoginEvent {
	
	public void onLogin(AdminUser user);
	
}
