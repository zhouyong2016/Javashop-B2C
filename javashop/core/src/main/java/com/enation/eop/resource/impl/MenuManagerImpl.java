package com.enation.eop.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.Menu;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

/**
 * 菜单管理
 * 
 * @author kingapex 2010-5-10下午02:00:10
 */
@Service("menuManager")
public class MenuManagerImpl   implements IMenuManager {
	
	@Autowired
	private IDaoSupport  daoSupport;
	
	@Autowired
	private IPermissionManager permissionManager;
	
	@Autowired
	private IAuthActionManager authActionManager;

	
	private String showall;
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void clean() {
		this.daoSupport.execute("truncate table es_menu");
	}

	public List<Menu> getMenuList() {
		return this.daoSupport.queryForList("select * from es_menu where deleteflag = '0' order by sorder asc", Menu.class);
	}
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="添加一个名为${menu.title}的菜单项")
	public Integer add(Menu menu) {
		if (menu.getTitle() == null)
			throw new IllegalArgumentException("title argument is null");
		if (menu.getPid() == null)
			throw new IllegalArgumentException("pid argument is null");
		if (menu.getSorder() == null)
			throw new IllegalArgumentException("sorder argument is null");
		menu.setDeleteflag(0);
		menu.setMenutype(2);
		this.daoSupport.insert("es_menu", menu);
		return this.daoSupport.getLastId("es_menu");
	}

	public List<Menu> getMenuTree(Integer menuid) {
		if (menuid == null)
			throw new IllegalArgumentException("menuid argument is null");
		List<Menu> menuList = this.getMenuList();
		List<Menu> topMenuList = new ArrayList<Menu>();
		for (Menu menu : menuList) {
			if (menu.getPid().compareTo(menuid) == 0) {
				List<Menu> children = this.getChildren(menuList, menu.getId());
				menu.setChildren(children);
				menu.setState("closed");
				topMenuList.add(menu);
			}
		}
		return topMenuList;
	}

	/**
	 * 在一个集合中查找子
	 * 
	 * @param menuList
	 *            所有菜单集合
	 * @param parentid
	 *            父id
	 * @return 找到的子集合
	 */
	private List<Menu> getChildren(List<Menu> menuList, Integer parentid) {
		List<Menu> children = new ArrayList<Menu>();
		for (Menu menu : menuList) {
			if (menu.getPid().compareTo(parentid) == 0) {
				menu.setChildren(this.getChildren(menuList, menu.getId()));
				children.add(menu);
			}
		}
		return children;
	}

	public Menu get(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("ids argument is null");
		String sql = "select * from es_menu where id=?";
		return (Menu)this.daoSupport.queryForObject(sql, Menu.class, id);
	}

	public Menu get(String title) {
		String sql = "select * from es_menu where title=?";
		List<Menu> menuList = this.daoSupport.queryForList(sql, Menu.class,	title);

		if (menuList.isEmpty())
			return null;
		return menuList.get(0);
	}
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="修改了名为${menu.title}的菜单项信息")
	public void edit(Menu menu) {
		if (menu.getId() == null)
			throw new IllegalArgumentException("id argument is null");
		if (menu.getTitle() == null||StringUtil.isEmpty(menu.getTitle()))
			throw new IllegalArgumentException("名称不能为空");
		if (menu.getPid() == null)
			throw new IllegalArgumentException("上级菜单不能为空");
		
		if(this.getPidList(menu.getId()).size() == 0){
			if (menu.getUrl() == null||StringUtil.isEmpty(menu.getUrl()))
				throw new IllegalArgumentException("url不能为空");
		}
		
		if (menu.getSorder() == null)
			throw new IllegalArgumentException("sorder不能为空");
		menu.setDeleteflag(0);
		menu.setMenutype(2);
		this.daoSupport.update("es_menu", menu, "id=" + menu.getId());
	}

	
	private List<Menu> getPidList(int pid){
		return this.daoSupport.queryForList("select * from es_menu where pid = "+ pid);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="更新菜单排序")
	public void updateSort(Integer[] ids, Integer[] sorts) {
		if (ids == null)
			throw new IllegalArgumentException("ids argument is null");
		if (sorts == null)
			throw new IllegalArgumentException("sorts argument is null");
		if (sorts.length != ids.length)
			throw new IllegalArgumentException("ids's length and sorts's length not same");
		for (int i = 0; i < ids.length; i++) {
			String sql = "update es_menu set sorder=? where id=?";
			this.daoSupport.execute(sql, sorts[i], ids[i]);
		}
	}
	
	@Log(type=LogType.SETTING,detail="根据ID，删除ID为${id}的菜单项")
	public void delete(Integer id) throws RuntimeException {
		if (id == null)
			throw new IllegalArgumentException("ids argument is null");
		String sql = "select count(0) from es_menu where pid=?";
		int count = this.daoSupport.queryForInt(sql, id);
		if (count > 0)
			throw new RuntimeException("菜单" + id + "存在子类别,不能直接删除，请先删除其子类别。");
		sql = "delete from es_menu where id=?";
		this.daoSupport.execute(sql, id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="根据title，删除名为${title}的一个菜单")
	public void delete(String title) {
		String sql = "delete from es_menu where title=?";
		this.daoSupport.execute(sql, title);
	}
	

	@Override
	@Log(type=LogType.SETTING,detail="移动ID为${menuid}的菜单到ID为${targetid}的父菜单下")
	public void move(int menuid, int targetid, String type) {
		
		Menu menu = this.get(menuid);
		Menu target = this.get(targetid);
		
		int parentid = menu.getPid();
		int targetpid = target.getPid();
		
		//移入
		if("inner".equals(type)){
			
			this.daoSupport.execute("update es_menu set pid=? where id=?", targetid,menu.getId());
			List<Integer> sorderList  = (List<Integer>)this.daoSupport.queryForList("select max(sorder) sorder from es_menu where pid=?",new IntegerMapper(), targetid);//找到最大的排序
			int sorder=1;
			if(!sorderList.isEmpty()){
				sorder = sorderList.get(0)+1;
				
			}
			this.daoSupport.execute("update es_menu set sorder=? where id=?", sorder,menuid);
			
		}
		
		//排序
		if("prev".equals(type) || "next".equals(type)){ //移动次序，但有可能切换了父菜单
			
			if(parentid!=targetpid){//切换了父菜单
				this.daoSupport.execute("update es_menu set pid=? where id=?", targetpid,menu.getId());
			}
			
			if("prev".equals(type) ){
				
				//更新目标菜单所有上面的菜单排序-1		
				String sql  ="update es_menu set sorder=sorder-1 where pid=? and sorder<=? and id!=?";
				this.daoSupport.execute(sql,targetpid,target.getSorder(),target.getId());
				
				//直接更新这个菜单的排序为目录菜单排序-1
				sql ="update es_menu set sorder=? where id=?";
				this.daoSupport.execute(sql, target.getSorder()-1,menu.getId());
				
			}
			
			if("next".equals(type) ){
				
				//更新目标菜单所有上面的菜单排序-1		
				String sql  ="update es_menu set sorder=sorder+1 where pid=? and sorder>=? and id!=?";
				this.daoSupport.execute(sql,targetpid,target.getSorder(),target.getId());
				
				//更新这个菜单的排序为目录菜单排序-1
				sql ="update es_menu set sorder=? where id=?";
				this.daoSupport.execute(sql, target.getSorder()+1,menu.getId());
				
			}
			
		}
		
	}
	 

	@Override
	public List<Menu> newMenutree(Integer menuid, AdminUser user) {
		if (menuid == null)
			throw new IllegalArgumentException("menuid argument is null");
		//获取所有菜单
		List<Menu> menuList = this.getMenuList();
		//对菜单进行筛选
		menuList=menuListByUser(menuList, user);
		//新的List
		List<Menu> topMenuList = new ArrayList<Menu>();
		for (Menu menu : menuList) {
			if (menu.getPid().compareTo(menuid) == 0) {
				List<Menu> children = this.getChildren(menuList, menu.getId());
				menu.setChildren(children);
				topMenuList.add(menu);
			}
		}
		return topMenuList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IMenuManager#getMenuByUser(com.enation.eop.resource.model.AdminUser)
	 */
	@Override
	public List<Menu> getMenuByUser(AdminUser user) {
		//获取所有菜单
		List<Menu> menuList = this.getMenuList();
		//对菜单进行筛选
		menuList=menuListByUser(menuList, user);
		return menuList;
	}
	
	private List<Menu> menuListByUser(List<Menu> menuList,AdminUser user){
		
		List<Menu> topMenuList = new ArrayList<Menu>();
		for (Menu menu:menuList) {
			List<AuthAction> authList = permissionManager.getUesrAct(user.getUserid(), "menu");
			for (AuthAction authAction:authList) {
				String arth[]= authAction.getObjvalue().split(",");
				for (int i = 0; i < arth.length; i++) {
					if(Integer.parseInt(arth[i])==menu.getId()&&choosemenu(topMenuList, menu)){
						topMenuList.add(menu);
					}
				}
			}
		}
		return topMenuList;
	}
	private boolean choosemenu(List<Menu> newmenu, Menu menu){
		boolean choose=true;
		for (Menu cmenu:newmenu) {
			int menuId=menu.getId();
			int cmenuId=cmenu.getId();
			if(menuId==cmenuId){
				choose=false;
			}
		}
		return choose;
	}

	
}