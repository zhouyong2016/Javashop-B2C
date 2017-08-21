package com.enation.app.base.core.service.auth;

import java.util.List;

import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.model.Role;


/**
 * 权限管理接口
 * @author kingapex
 * 2010-10-24下午12:49:08
 */
public interface IPermissionManager {
	
	
	
	/**
	 * 为某个用户赋予某些角色<br>
	 * 会清除此用户的前角色，重新赋予
	 * @param userid  用户id
	 * @param roleids 角色id数组
	 */
	public void giveRolesToUser(int userid,int[] roleids);
	
	
	
	
	/**
	 * 读取某用户的角色集合
	 * @param userid
	 * @return 此用户的角色集合
	 */
	public List<Role> getUserRoles(int userid);
	
	
	
	/**
	 * 读取某用户的权限点
	 * @param userid 用户id
	 * @param acttype 权限类型
	 * @return
	 */
	public List<AuthAction> getUesrAct(int userid,String acttype);
	 
	
	/**
	 * 读取某用户的权限点列表
	 * @param userid
	 * @return
	 */
	public List<AuthAction> getUesrAct(int userid);
	 
	
	
	
	/**
	 * 检测当前登录用户是否拥有某权限
	 * @param userid
	 * @param actid
	 * @return
	 */
	public boolean checkHaveAuth(int actid);
	
	
	
	/**
	 * 删除某用户的所有角色
	 * @param userid 要删除角色的用户
	 */
	public void cleanUserRoles(int userid);
	
	/**
	 * 检测用户是否某种角色
	 * @param userid
	 */
	public boolean checkHaveRole(int userid,int roleid); 


	/**
	 * 检测是否拥有超级权限
	 * @return
	 */
	public boolean checkHaveAdmin();
	
	
}
