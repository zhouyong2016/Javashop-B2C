package com.enation.app.base.core.plugin.user;

/**
 * 管理员删除事件
 * @author kingapex
 *
 */
public interface IAdminUserDeleteEvent {
	
	
	/**
	 * 管理员删除事件
	 * @param userid
	 */
	public void onDelete(int userid);
	
}
