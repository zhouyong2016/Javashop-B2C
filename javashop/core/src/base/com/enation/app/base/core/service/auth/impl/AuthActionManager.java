package com.enation.app.base.core.service.auth.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

/**
 * 权限点管理
 * 
 * @author kingapex 2010-10-24下午10:38:33
 * @version wangxin 6.0升级改造 2016-2-23
 */
@Service("authActionManager")
public class AuthActionManager   implements IAuthActionManager {


	@Autowired
	private IDaoSupport daoSupport;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAuthActionManager#add(com.enation.app.base.core.model.AuthAction)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="添加一个名为${act.name}的权限点")
	public int add(AuthAction act) {
		daoSupport.insert("es_auth_action", act);
		return daoSupport.getLastId("es_auth_action");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAuthActionManager#delete(int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="删除ID为${actid}的权限点")
	public void delete(int actid) {
		// 删除角色权限表中对应的数据
		daoSupport.execute("delete from es_role_auth where authid=?",	actid);
		// 删除权限基本数据
		daoSupport.execute("delete from es_auth_action where actid=?", actid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAuthActionManager#edit(com.enation.app.base.core.model.AuthAction)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="修改了名为${act.name}的权限点信息")
	public void edit(AuthAction act) {
		daoSupport.update("es_auth_action", act, "actid=" + act.getActid());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAuthActionManager#list()
	 */
	@Override
	public List<AuthAction> list() {
		return daoSupport.queryForList("select * from es_auth_action where actid!=0", AuthAction.class);
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAuthActionManager#get(int)
	 */
	@Override
	public AuthAction get(int authid) {
		// return
		// daoSupport.queryForObject("select * from auth_action where actid=?",
		// AuthAction.class, authid);
		// 修改此方法，解决log中的大量报错
		List<AuthAction> list = daoSupport.queryForList("select * from es_auth_action where actid=?", AuthAction.class, authid);
		AuthAction result = null;
		if (list.size() > 0)
			result = list.get(0);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAuthActionManager#addMenu(int, java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.SETTING,detail="给ID为${actid}权限点添加新菜单")
	public void addMenu(int actid, Integer[] menuidAr) {
		if (menuidAr == null)
			return;

		AuthAction authAction = this.get(actid);
		if (authAction == null)
			return;
		String menuStr = authAction.getObjvalue();
		if (StringUtil.isEmpty(menuStr)) {
			menuStr = StringUtil.arrayToString(menuidAr, ",");
			authAction.setObjvalue(menuStr);
		} else {
			String[] oldMenuAr = StringUtils.split(menuStr, ",");// menuStr.split(",");
			oldMenuAr = merge(menuidAr, oldMenuAr);
			menuStr = StringUtil.arrayToString(oldMenuAr, ",");
			authAction.setObjvalue(menuStr);
		}
		this.edit(authAction);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.auth.IAuthActionManager#deleteMenu(int, java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.SETTING,detail="删除ID为${actid}权限点的菜单")
	public void deleteMenu(int actid, Integer[] menuidAr) {
		if (menuidAr == null)
			return;
		AuthAction authAction = this.get(actid);
		if (authAction == null)
			return;

		String menuStr = authAction.getObjvalue();
		if (StringUtil.isEmpty(menuStr)) {
			return;
		}

		String[] oldMenuAr = StringUtils.split(menuStr, ",");
		menuStr.split(",");
		oldMenuAr = delete(menuidAr, oldMenuAr);
		menuStr = StringUtil.arrayToString(oldMenuAr, ",");
		authAction.setObjvalue(menuStr);
		this.edit(authAction);
	}

	/**
	 * 将ar1合并进ar2中
	 * 
	 * @param ar1
	 * @param ar2
	 * @return
	 */
	private static String[] merge(Integer[] ar1, String[] ar2) {

		List<String> newList = new ArrayList<String>();
		for (String num : ar2) {
			newList.add(num);
		}

		boolean flag = false;
		for (Integer num1 : ar1) {
			flag = false;

			for (String num2 : ar2) {
				if (num1.intValue() == Integer.valueOf(num2)) {
					flag = true;
					break;
				}
			}

			if (!flag) {// 原数组不存在这个数添加进来
				newList.add(String.valueOf(num1));
			}
		}

		return (String[]) newList.toArray(new String[newList.size()]);
	}

	/**
	 * 从ar2中删除a1
	 * 
	 * @param ar1
	 * @param ar2
	 * @return
	 */
	public static String[] delete(Integer[] ar1, String[] ar2) {
		List<String> newList = new ArrayList<String>();
		boolean flag = false;
		for (String num2 : ar2) {
			flag = false;
			for (Integer num1 : ar1) {
				if (num1.intValue() == Integer.valueOf(num2)) {
					flag = true;
					break;
				}
			}

			if (!flag) {
				newList.add(num2);
			}
		}

		return (String[]) newList.toArray(new String[newList.size()]);
	}

	/**
	 * 检测权限点是否重复
	 */
	@Override
	public Integer checkMenu(AuthAction auth) {
		List list=new ArrayList();
		String sql="select count(*) from es_auth_action where name = ?";
		list.add(auth.getName().replaceAll(" ", ""));
		if(auth.getActid() != null){
			sql += " and actid != ?";
			list.add(auth.getActid());
		}
		return this.daoSupport.queryForInt(sql, list.toArray());
	}

}
