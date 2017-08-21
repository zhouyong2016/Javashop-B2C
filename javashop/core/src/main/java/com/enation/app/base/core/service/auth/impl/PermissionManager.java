package com.enation.app.base.core.service.auth.impl;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.model.Role;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.database.IDaoSupport;

/**
 * 权限管理
 * @author kingapex
 * 2010-11-4下午12:50:09
 * @version 2.0 6.0升级改造 wangxin
 */
@Service("permissionManager")
public class PermissionManager implements IPermissionManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/**
	 * 为某个用户赋予某些角色<br>
	 * 会清除此用户的前角色，重新赋予
	 * @param userid  用户id
	 * @param roleids 角色id数组
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IPermissionManager#giveRolesToUser(int, int[])
	 */
	@Override
	public void giveRolesToUser(int userid, int[] roleids) {
		this.daoSupport.execute("delete from es_user_role where userid=?", userid);
		if(roleids==null) return ;
		for(int roleid:roleids){
			this.daoSupport.execute("insert into es_user_role(roleid,userid)values(?,?)", roleid,userid);
		}
	}
	
	/**
	 * 读取某用户的权限点
	 * @param userid 用户id
	 * @param acttype 权限类型
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IPermissionManager#getUesrAct(int, java.lang.String)
	 */
	@Override
	public List<AuthAction> getUesrAct(int userid, String acttype) {
		
		//查询权限表acttype符合条记录
		String sql ="select * from es_auth_action where type=? ";
		//并且 权限id在用户的角色权限范围内
		sql+=" and actid in(select authid from  es_role_auth  where roleid in ";
		//查询用户的角色列表
		sql+=" (select roleid from es_user_role where userid=?)";
		sql+=" )";
	 
		return this.daoSupport.queryForList(sql,AuthAction.class,acttype,userid);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IPermissionManager#getUesrAct(int)
	 */
	@Override
	public List<AuthAction> getUesrAct(int userid){
		//查询权限表acttype符合条记录
		String sql ="select * from es_auth_action  where ";
		//并且 权限id在用户的角色权限范围内
		sql+="   actid in(select authid from  es_role_auth where roleid in ";
		//查询用户的角色列表
		sql+=" (select roleid from es_user_role  where userid=?)";
		sql+=" )";
	 
		return this.daoSupport.queryForList(sql,AuthAction.class,userid);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IPermissionManager#checkHaveAuth(int)
	 */
	@Override
	public boolean checkHaveAuth( int actid) {
		
		HttpSession  sessonContext = ThreadContextHolder
				.getSession();			
		AdminUser user  =   (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
		if(user.getFounder()==1) return true;
		List<AuthAction> authList  = user.getAuthList();
		for(AuthAction auth :authList){
			if(auth.getActid() == actid){//权限列表中有此权限返回真 
				return true;
			}
		}
		
		return false;
	}

	
	/**
	 * 读取某用户的角色集合
	 * @param userid
	 * @return 此用户的角色集合
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IPermissionManager#getUserRoles(int)
	 */
	@Override
	public List<Role> getUserRoles(int userid) {
		return this.daoSupport.queryForList("select roleid from  es_user_role where userid=?", Role.class,userid);
	}
	
	
	/**
	 * 删除某用户的所有角色
	 * @param userid 要删除角色的用户
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IPermissionManager#cleanUserRoles(int)
	 */
	@Override
	public void cleanUserRoles(int userid){
		this.daoSupport.execute("delete from es_user_role where userid=?", userid);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IPermissionManager#checkHaveRole(int, int)
	 */
	@Override
	public boolean checkHaveRole(int userid,int roleid){
		int count = this.daoSupport.queryForInt("select count(0) from  es_user_role where userid=? and roleid=?", userid,roleid);
		return count>0; 
	}

	@Override
	public boolean checkHaveAdmin() { 
		HttpSession sessonContext = ThreadContextHolder.getSession();			
		AdminUser user  =   (AdminUser) sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
		//是否是超级管理员
		if(user.getFounder()==1){
			return true; 	
		}
		
		return false;
	}


}
