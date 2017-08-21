package com.enation.app.base.core.service.auth.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.plugin.user.AdminUserPluginBundle;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.annotation.Log;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

/**
 * 管理员管理实现
 * 
 * @author kingapex 2010-11-5下午06:49:02
 */
@Service("adminUserManager")
public class AdminUserManagerImpl  implements	IAdminUserManager {

	@Autowired
	private IDaoSupport  daoSupport;

	@Autowired
	private AdminUserPluginBundle adminUserPluginBundle;

	@Autowired
	private IPermissionManager permissionManager;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#clean()
	 */
	@Override
	@Log(type=LogType.SETTING,detail="清除本站点所有管理员信息")
	public void clean() {
		this.daoSupport.execute("truncate table es_adminuser");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#add(com.enation.eop.resource.model.AdminUser)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="当前站点添加了一个名为${adminUser.username}的管理员")
	public Integer add(AdminUser adminUser) {
		adminUser.setPassword(StringUtil.md5(adminUser.getPassword()));
		// 添加管理员
		this.daoSupport.insert("es_adminuser", adminUser);
		int userid = this.daoSupport.getLastId("es_adminuser");

		// 给用户赋予角色
		permissionManager.giveRolesToUser(userid, adminUser.getRoleids());
		this.adminUserPluginBundle.onAdd(userid);
		return userid;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#add(int, int, com.enation.eop.resource.model.AdminUser)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="ID为${siteid}的站点，添加了一个名为${adminUser.username}管理员")
	public Integer add(int userid, int siteid, AdminUser adminUser) {
		adminUser.setState(1);
		this.daoSupport.insert("es_adminuser", adminUser);
		return this.daoSupport.getLastId("es_adminuser");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#checkLast()
	 */
	@Override
	public int checkLast() {
		int count = this.daoSupport.queryForInt("select count(0) from es_adminuser");
		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#delete(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="删除ID为${id}的管理员")
	public void delete(Integer id) {
		// 如果只有一个管理员，则抛出异常
		if (this.checkLast() == 1) {
			throw new RuntimeException("必须最少保留一个管理员");
		}

		// 清除用户角色
		permissionManager.cleanUserRoles(id);

		// 删除用户基本信息
		this.daoSupport.execute("delete from es_adminuser where userid=?", id);
		this.adminUserPluginBundle.onDelete(id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#edit(com.enation.eop.resource.model.AdminUser)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="修改了名为${adminUser.username}的管理员信息")
	public void edit(AdminUser adminUser) {
		// 给用户赋予角色
		permissionManager.giveRolesToUser(adminUser.getUserid(), adminUser.getRoleids());

		// 修改用户基本信息
		if (!StringUtil.isEmpty(adminUser.getPassword()))
			adminUser.setPassword(StringUtil.md5(adminUser.getPassword()));
		int userId = adminUser.getUserid();
		adminUser.setUserid(null); // 不设置为空，SQLServer更新出错
		this.daoSupport.update("es_adminuser", adminUser, "userid=" + userId);
		this.adminUserPluginBundle.onEdit(userId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#get(java.lang.Integer)
	 */
	@Override
	public AdminUser get(Integer id) {
		return this.daoSupport.queryForObject("select * from es_adminuser where userid=?", AdminUser.class, id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#list()
	 */
	@Override
	public List list() {
		return this.daoSupport.queryForList("select * from es_adminuser order by dateline", AdminUser.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#list(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Map> list(Integer userid, Integer siteid) {
		String sql = "select * from es_adminuser_" + userid + "_" + siteid;
		return this.daoSupport.queryForList(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#listByRoleId(int)
	 */
	@Override
	public List<AdminUser> listByRoleId(int roleid) {
		String sql = "select u.* from es_adminuser  u , es_user_role  ur where ur.userid=u.userid and ur.roleid=?";
		return this.daoSupport.queryForList(sql, AdminUser.class, roleid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#login(java.lang.String, java.lang.String)
	 */
	@Override
	public int login(String username, String password) {
		return this.loginBySys(username, StringUtil.md5(password));
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#loginBySys(java.lang.String, java.lang.String)
	 */
	@Override
	public int loginBySys(String username, String password) {
		String sql = "select * from es_adminuser where username=?";
		List<AdminUser> userList = this.daoSupport.queryForList(sql, AdminUser.class, username);
		if (userList == null || userList.size() == 0) {
			throw new RuntimeException("此用户不存在");
		}
		AdminUser user = userList.get(0);

		if (!password.equals(user.getPassword())) {
			throw new RuntimeException("密码错误");
		}

		if (user.getState() == 0) {
			throw new RuntimeException("此用户已经被禁用");
		}



		// 读取此用户的权限点，并设置给当前用户
		List<AuthAction> authList = this.permissionManager.getUesrAct(user.getUserid());
		user.setAuthList(authList);


		// 记录session信息
		HttpSession sessonContext = ThreadContextHolder.getSession();
		sessonContext.setAttribute(UserConext.CURRENT_ADMINUSER_KEY, user);
		this.adminUserPluginBundle.onLogin(user);
		return user.getUserid();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAdminUserManager#is_exist(java.lang.String)
	 */
	@Override
	public boolean is_exist(AdminUser eopUserAdmin) {
		boolean flag = false;
		String sql="select count(0) from es_adminuser where username = ?";
		List list=new ArrayList();
		list.add(eopUserAdmin.getUsername());
		if(eopUserAdmin.getUserid() != null){
			sql += " and userid != ?";
			list.add(eopUserAdmin.getUserid());
		}
		int i =  this.daoSupport.queryForInt(sql, list.toArray());
		if(i!=0){
			flag=true;
		}
		return flag;
	}


}
