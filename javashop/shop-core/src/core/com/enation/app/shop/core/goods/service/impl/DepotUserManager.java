package com.enation.app.shop.core.goods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.enation.app.shop.core.goods.model.DepotUser;
import com.enation.app.shop.core.goods.service.IDepotUserManager;
import com.enation.framework.database.IDaoSupport;

public class DepotUserManager  implements
		IDepotUserManager {
	
	
	@Autowired
	private IDaoSupport daoSupport;
	
	
	@Override
	public void add(DepotUser depotUser) {
		this.daoSupport.execute("insert into es_depot_user(userid,depotid)values(?,?)", depotUser.getUserid(),depotUser.getDepotid());
	}

	@Override
	public void edit(DepotUser depotUser) {
		this.daoSupport.execute("update es_depot_user set roomid=? where userid=?", depotUser.getDepotid(),depotUser.getUserid());
	}

	@Override
	public void delete(int userid) {
		this.daoSupport.execute("delete from es_depot_user where userid=?", userid);
	}

	@Override
	public DepotUser get(int userid) {
		String sql = "select * from es_depot_user where userid=?";
		return this.daoSupport.queryForObject(sql, DepotUser.class, userid);
	}

	@Override
	public List<DepotUser> listByDepotId(int depotid) {
		String sql = "select * from es_depot_user where depotid=?";
		return this.daoSupport.queryForList(sql, DepotUser.class, depotid);
	}
	
	public List<DepotUser> listByRoleId(int roleid){
		
		String sql  ="select u.*,du.depotid from es_adminuser  u ,es_user_role  ur, es_depot_user  du  where ur.userid=u.userid and du.userid = u.userid and ur.roleid=?";
		return this.daoSupport.queryForList(sql, DepotUser.class, roleid);
	}
	
	public List<DepotUser> listByRoleId(int depotid,int roleid){
		String sql  ="select u.*,du.depotid from es_adminuser u es_user_role  ur,es_depot_user  du  where ur.userid=u.userid and du.userid = u.userid and  du.depotid=? and ur.roleid=?";
		return this.daoSupport.queryForList(sql, DepotUser.class,depotid, roleid);
	}
	
}
