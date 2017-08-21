package com.enation.app.base.core.service.impl.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.SiteMenu;
import com.enation.app.base.core.service.ISiteMenuManager;
import com.enation.framework.cache.AbstractCacheProxy;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;

@Service("siteMenuManager")
public class SiteMenuCacheProxy extends AbstractCacheProxy<List<SiteMenu>> implements ISiteMenuManager {
	public  static final String MENU_LIST_CACHE_KEY = "siteMenuList";
	
	@Autowired
	private ISiteMenuManager siteMenuManager;
	@Autowired
	public SiteMenuCacheProxy(ISiteMenuManager siteMenuDbManager) {
		this.siteMenuManager = siteMenuDbManager;
		 
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#add(com.enation.app.base.core.model.SiteMenu)
	 */
	@Override
	public void add(SiteMenu siteMenu) {
		this.siteMenuManager.add(siteMenu);
		this.cleanCache();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#delete(java.lang.Integer)
	 */
	@Override
	public void delete(Integer id) {
		this.siteMenuManager.delete(id);
		this.cleanCache();
	 
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#edit(com.enation.app.base.core.model.SiteMenu)
	 */
	@Override
	public void edit(SiteMenu siteMenu) {
		this.siteMenuManager.edit(siteMenu);
		this.cleanCache();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#get(java.lang.Integer)
	 */
	@Override
	public SiteMenu get(Integer menuid) {
		return this.siteMenuManager.get(menuid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#list(java.lang.Integer)
	 */
	@Override
	public List<SiteMenu> list(Integer parentid) {
		ICache cache=CacheFactory.getCache(MENU_LIST_CACHE_KEY);
		List<SiteMenu> menuList  =  (List<SiteMenu>) cache.get( MENU_LIST_CACHE_KEY);
		
		
		if(menuList== null ){
			menuList = this.siteMenuManager.list(parentid);
			cache.put( MENU_LIST_CACHE_KEY,menuList);
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load sitemenu from database");
			}
		}else{
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load sitemenu from cache");
			}
		}
		
		return menuList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#updateSort(java.lang.Integer[], java.lang.Integer[])
	 */
	@Override
	public void updateSort(Integer[] menuid, Integer[] sort) {
		this.siteMenuManager.updateSort(menuid, sort);
		this.cleanCache();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISiteMenuManager#get(java.lang.String)
	 */
	@Override
	public SiteMenu get(String name) {
		return siteMenuManager.get(name);
	}
	
	private void cleanCache(){
		ICache cache=CacheFactory.getCache(MENU_LIST_CACHE_KEY);
		if(cache!=null){
			cache.remove( MENU_LIST_CACHE_KEY);
		}
	}
}
