package com.enation.eop.resource;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.Menu;

/**
 * 
 * 菜单管理
 * 
 * @author lzf
 *         <p>
 *         2009-12-16 上午11:05:28
 *         </p>
 * @version 1.0
 */
public interface IMenuManager {

	/**
	 * 添加菜单项
	 * 
	 * @param menu
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer add(Menu menu);

	/**
	 * 修改一个菜单项
	 * 
	 * @param menu
	 */
	public void edit(Menu menu);

	/**
	 * 读取菜单列表
	 * 
	 * @param userid
	 * @param siteid
	 * @return
	 */
	public List<Menu> getMenuList();

	/**
	 * 获取某个菜单的详细信息
	 * 
	 * @param id
	 * @return
	 */
	public Menu get(Integer id);

	/**
	 * 根据名字获取菜单
	 * 
	 * @param title
	 * @return
	 */
	public Menu get(String title);

	/**
	 * 读取某菜单列表并形成Tree格式
	 * 
	 * @param menuid
	 *            要读取的顶级菜单id ,0为读取所有菜单
	 * @return
	 * @since 2.1.3
	 * @author kingapex
	 */
	public List<Menu> getMenuTree(Integer menuid);

	/**
	 * 根据id删除一个菜单
	 * 
	 * @param id
	 * @throws RuntimeException如果存在子菜单则抛出此异常
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer id) throws RuntimeException;

	/**
	 * 根据title删除一个菜单
	 * 
	 * @param title
	 */
	public void delete(String title);

	/**
	 * 更新菜单排序
	 * 
	 * @param ids
	 * @param sorts
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateSort(Integer[] ids, Integer[] sorts);

	/**
	 * 清除 一般用于站点安装时
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void clean();
	/**
	 * 移动某个菜单 
	 * @param menuid
	 * @param targetid
	 * @param type <br>
	 * inner:移入某个父
	 * prev:在某个之上
	 * next:在某个之后
	 */
	public void move(int menuid,int targetid,String type);
	
	/**
	 * 根据权限读取菜单列表
	 */
	public List<Menu> newMenutree(Integer menuid,AdminUser user);
	
	
	/**
	 * 根据管理员获取该管理员具有的所有菜单权限
	 * @param user
	 * @return
	 */
	public List<Menu> getMenuByUser(AdminUser user);

}
