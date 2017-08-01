package com.enation.app.shop.core.member.plugin;


/**
 * 用户更新密码事件
 * @author kingapex
 *2012-5-17下午2:40:08
 */
public interface IMemberUpdatePasswordEvent {
	
	/**
	 * 更新密码事件
	 * @param password 密码，未加密码过的
	 * @param memberid
	 */
	public void updatePassword(String password,int memberid);
	
	
}
