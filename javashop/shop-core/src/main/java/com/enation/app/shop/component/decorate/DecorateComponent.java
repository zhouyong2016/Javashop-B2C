package com.enation.app.shop.component.decorate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.ISiteMenuManager;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.app.base.core.service.impl.cache.SiteMenuCacheProxy;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;
import com.enation.framework.component.IComponent;
import com.enation.framework.component.IComponentStartAble;
import com.enation.framework.component.IComponentStopAble;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.data.IDataOperation;
/**
 * 装修组件
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Component
public class DecorateComponent implements IComponent,IComponentStartAble,IComponentStopAble{

	@Autowired
	private IDataOperation dataOperation;
	
	
	@Autowired
	private ISiteMenuManager siteMenuDbManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	public void install() {
		dataOperation.imported("file:com/enation/app/shop/component/decorate/decorate_install.xml");
	
		if("b2b2c".equals(EopSetting.PRODUCT)){
			this.installSubjectSiteMenu();
		}

	}

	public void unInstall() {
		dataOperation.imported("file:com/enation/app/shop/component/decorate/decorate_uninstall.xml");
	}
	
	
	

	
	
	public void installSubjectSiteMenu(){
		String sql="insert into es_site_menu (parentid,name,url,target,sort) values (?,?,?,?,?)";
		this.daoSupport.execute(sql, 0,"专题","subject/subject-index.html","",5);
	}
	/**
	 * 停用方法
	 */
	@Override
	public void stop() {

	}

	/**
	 * 启动方法
	 */
	@Override
	public void start() {

	}
	
}
