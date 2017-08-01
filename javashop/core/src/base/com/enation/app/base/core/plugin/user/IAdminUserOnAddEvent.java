package com.enation.app.base.core.plugin.user;


/**
 * 管理员添加事件
 * @author kingapex
 *
 */
public interface IAdminUserOnAddEvent {

	/**
	 * 添加时激发此事件
	 * @param userid
	 */
	public void onAdd(Integer userid);
	
}
