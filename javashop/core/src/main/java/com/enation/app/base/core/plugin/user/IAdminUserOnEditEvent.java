package com.enation.app.base.core.plugin.user;

/**
 * 管理员修改事件
 * @author kingapex
 *
 */
public interface IAdminUserOnEditEvent {
	
	/**
	 * 修改管理员时激发此事件
	 * @param userid
	 */
	public void onEdit(Integer userid);
	
}
