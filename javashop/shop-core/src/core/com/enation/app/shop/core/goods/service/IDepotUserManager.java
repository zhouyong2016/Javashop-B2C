package com.enation.app.shop.core.goods.service;

import java.util.List;

import com.enation.app.shop.core.goods.model.DepotUser;


/**
 * 库管管理接口
 * @author fenlongli
 *
 */
public interface IDepotUserManager {
	
	
	/**
	 * 添加一个库管员
	 * @param depotUser
	 */
	public void add(DepotUser depotUser);
	
	
	
	/**
	 * 修改一个库管员
	 * @param depotUser
	 */
	public void edit(DepotUser depotUser);
	
	
	/**
	 * 删除一个库管员	
	 * @param userid
	 */
	public void delete(int userid);
	
	
	/**
	 * 获取一个库管员
	 * @param userid
	 * @return
	 */
	public DepotUser get(int userid);
	
	
	/**
	 * 读取某个库房的所有库管员
	 * @param depotid
	 * @return
	 */
	public List<DepotUser> listByDepotId(int depotid);
	
	
	/**
	 * 读取某个角色的所有库管员
	 * @param roleid
	 * @return
	 */
	public List<DepotUser> listByRoleId(int roleid);
	
	/**
	 * 读取某个仓库的某个角色管理员
	 * @param roleid
	 * @param depotid
	 * @return
	 */
	public List<DepotUser> listByRoleId(int depotid,int roleid);
	
}
