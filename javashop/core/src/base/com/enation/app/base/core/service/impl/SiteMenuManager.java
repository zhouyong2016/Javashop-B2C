package com.enation.app.base.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.SiteMenu;
import com.enation.app.base.core.service.ISiteMenuManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;

/**
 * 后台导航栏管理实现类
 * @author DMRain 2016年2月20日
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Service("siteMenuDbManager")
public class SiteMenuManager implements ISiteMenuManager {

	@Autowired
	private IDaoSupport daoSupport;
	
	protected  Logger logger = Logger.getLogger(getClass());
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#add(com.enation.app.base.core.model.SiteMenu)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="新添加了一个菜单名为${siteMenu.name}导航栏")
	public void add(SiteMenu siteMenu) {
		this.daoSupport.insert("es_site_menu", siteMenu);
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#delete(java.lang.Integer)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.SETTING,detail="删除了一个菜单导航栏ID为${id}的导航栏")
	public void delete(Integer id) {
		String sql  ="delete from es_site_menu where parentid = ?";
		this.daoSupport.execute(sql, id);
		sql = "delete from  es_site_menu where menuid = ?";
		this.daoSupport.execute(sql, id);
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#edit(com.enation.app.base.core.model.SiteMenu)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="修改菜单名为${siteMenu.name}导航栏信息")
	public void edit(SiteMenu siteMenu) {
		this.daoSupport.update("es_site_menu", siteMenu,"menuid="+siteMenu.getMenuid());
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#list(java.lang.Integer)
	 */
	@Override
	public List<SiteMenu> list(Integer parentid) {
		String sql  ="select * from es_site_menu order by parentid,sort desc,menuid desc";
		List<SiteMenu> menuList  = this.daoSupport.queryForList(sql, SiteMenu.class);
		List<SiteMenu> topMenuList  = new ArrayList<SiteMenu>();
		if(this.logger.isDebugEnabled()){
			this.logger.debug("查找"+parentid+"的子...");
		}
		for(SiteMenu menu :menuList){
			if(menu.getParentid().compareTo(parentid)==0){
				if(this.logger.isDebugEnabled()){
					this.logger.debug("发现子["+menu.getName()+"-"+menu.getMenuid()+"]");
				}
				List<SiteMenu> children = this.getChildren(menuList, menu.getMenuid());
				
				int i = this.daoSupport.queryForInt("select count(0) from es_site_menu where parentid="+menu.getMenuid());
				if(i!=0){
					menu.setState("closed");
				}
				menu.setChildren(children);
				topMenuList.add(menu);
			}
		}
		
		return topMenuList;
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#updateSort(java.lang.Integer[], java.lang.Integer[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="更新菜单导航栏排序")
	public void updateSort(Integer[] menuid, Integer[] sort) {
		
		if(menuid==null || sort == null )  throw new  IllegalArgumentException("menuid or sort is NULL");
		if(menuid.length != sort.length )  throw new  IllegalArgumentException("menuid or sort length not same");
		for(int i=0, len=menuid.length;i<len;i++){
			this.daoSupport.execute("update es_site_menu set sort = ? where menuid = ?",sort[i],menuid[i]);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#get(java.lang.Integer)
	 */
	@Override
	public SiteMenu get(Integer menuid) {	 
		return this.daoSupport.queryForObject("select * from es_site_menu where menuid=?", SiteMenu.class, menuid);
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#get(java.lang.String)
	 */
	@Override
	public SiteMenu get(String name) {
		return this.daoSupport.queryForObject("select * from es_site_menu where name = ?", SiteMenu.class, name);
	}
	
	private List<SiteMenu> getChildren(List<SiteMenu> menuList ,Integer parentid){
		if(this.logger.isDebugEnabled()){
			this.logger.debug("查找["+parentid+"]的子");
		}
		List<SiteMenu> children =new ArrayList<SiteMenu>();
		for(SiteMenu menu :menuList){
			if(menu.getParentid().compareTo(parentid)==0){
				if(this.logger.isDebugEnabled()){
					this.logger.debug(menu.getName()+"-"+menu.getMenuid()+"是子");
				}
			 	menu.setChildren(this.getChildren(menuList, menu.getMenuid()));
				children.add(menu);
			}
		}
		return children;
	}
}
