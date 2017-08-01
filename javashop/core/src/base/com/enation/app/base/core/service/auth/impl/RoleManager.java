package com.enation.app.base.core.service.auth.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.model.Role;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

/**
 * 角色管理
 * @author kingapex
 * 2010-10-24下午11:08:12
 */
@Service("roleManager")
public class RoleManager implements IRoleManager {
	@Autowired
	private IDaoSupport daoSupport;

	/**
	 * 添加一个角色
	 * @param role 角色实体
	 * @param acts 此角色的权限集合
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IRoleManager#add(com.enation.app.base.core.model.Role, int[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="角色管理中添加一个名为${role.rolename}的角色")
	public void add(Role role, int[] authids) {
		//添加角色并
		this.daoSupport.insert("es_role", role);

		//不赋予权限则直接返回
		if(authids==null) return ;

		//获取角色id
		int roleid =  this.daoSupport.getLastId("es_role");


		//为这个角色 赋予权限点，写入角色权限对照表
		for(int authid:authids){
			this.daoSupport.execute("insert into es_role_auth(roleid,authid)values(?,?)", roleid,authid);
		}

	}

	/**
	 * 删除某角色
	 * @param roleid
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IRoleManager#delete(int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="删除ID为${rolename}的角色")
	public void delete(int roleid) {

		//删除用户角色
		this.daoSupport.execute("delete from es_user_role where roleid=?", roleid);

		//删除角色权限
		this.daoSupport.execute("delete from es_role_auth where roleid =?", roleid);

		//删除角色 
		this.daoSupport.execute("delete from es_role where roleid =?", roleid);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IRoleManager#edit(com.enation.app.base.core.model.Role, int[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="修改了名为${role.rolename}的角色信息")
	public void edit(Role role, int[] authids) {
		//校验参数 
		if(role.getRoleid()==0) throw new IllegalArgumentException("编辑角色时id不可为空");
		if(StringUtil.isEmpty( role.getRolename())) throw new IllegalArgumentException("编辑角色时名称不可为空");

		if (authids != null && authids.length > 0) {
			//清除角色的权限
			this.daoSupport.execute("delete from es_role_auth where roleid=?", role.getRoleid());
			//为这个角色 赋予权限点，写入角色权限对照表
			for (int authid : authids) {
				this.daoSupport.execute("insert into es_role_auth(roleid,authid)values(?,?)", role.getRoleid(), authid);
			}
		}
		//更新角色基本信息
		this.daoSupport.update("es_role", role, "roleid="+role.getRoleid());
	}

	/**
	 * 读取所有角色列表 
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IRoleManager#list()
	 */
	@Override
	public List<Role> list() {

		return this.daoSupport.queryForList("select * from es_role", Role.class);

	}


	/**
	 * 读取某个角色信息，同时读取此角色权限
	 * @param roleid
	 * @return 权限id存于role.actids数组中
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IRoleManager#get(int)
	 */
	@Override
	public Role get(int roleid){
		String sql ="select * from es_auth_action where actid in(select authid from es_role_auth where roleid =?)";
		List  actlist = this.daoSupport.queryForList(sql,AuthAction.class, roleid);
		Role role = this.daoSupport.queryForObject("select * from es_role where roleid=?", Role.class, roleid);

		if(actlist!=null){
			int[] actids = new int[ actlist.size()];
			for(int i=0, len=actlist.size();i<len;i++){
				actids[i] =( (AuthAction)actlist.get(i)).getActid();
			}
			role.setActids(actids);
		}
		return  role;
	}


	/**
	 * 检测是否存在相同的角色名称
	 * @param role 角色
	 * @return Integer   
	 */
	@Override
	public Integer checkRoleName(Role role) {
		List list=new ArrayList();
		String sql="select count(*) from es_role where rolename=?";
		list.add(role.getRolename().replace(" ", ""));
		if(role.getRoleid() != null){
			sql += " and roleid != ?";
			list.add(role.getRoleid());
		}
		return this.daoSupport.queryForInt(sql, list.toArray());
	}



}
