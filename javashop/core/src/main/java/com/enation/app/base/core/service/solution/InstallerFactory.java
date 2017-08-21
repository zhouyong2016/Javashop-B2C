package com.enation.app.base.core.service.solution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 根据类型返回合适的Installer,此文件必须工作于spring 容器下
 * @author kingapex
 * 2010-1-20下午07:20:34
 */
@Service
public class InstallerFactory {
	
	public static final String TYPE_APP ="apps";
	public static final String TYPE_MENU ="menus";
	public static final String TYPE_ADMINTHEME ="adminThemes";
	public static final String TYPE_THEME ="themes";
	public static final String TYPE_URL ="urls";
	public static final String TYPE_WIDGET ="widgets";
	public static final String TYPE_INDEX_ITEM ="indexitems";
	public static final String TYPE_COMPONENT="components";
	public static final String TYPE_SITE="site";
	
	@Autowired
	private IInstaller menuInstaller;
	
	@Autowired
	private IInstaller adminThemeInstaller;
	
	@Autowired
	private IInstaller themeInstaller;
	
	@Autowired
	private IInstaller uriInstaller;

	@Autowired
	private IInstaller appInstaller;
	
	@Autowired
	private IInstaller indexItemInstaller;
	
	@Autowired
	private IInstaller componentInstaller;
	
	@Autowired
	private IInstaller siteInstaller;
	
	
	public IInstaller getInstaller(String type){
		
		
		if(TYPE_APP.equals(type)){
			return this.appInstaller;
		}
		
		if(TYPE_MENU.equals(type)){
			return this.menuInstaller;
		}
		

		if(TYPE_ADMINTHEME.equals(type)){
			return this.adminThemeInstaller;
		}
		
		if(TYPE_THEME.equals(type)){
			return this.themeInstaller;
			
		}

		if(TYPE_URL.equals(type)){
			return this.uriInstaller;
		}

	
		
		if(TYPE_INDEX_ITEM.equals(type)){
			return this.indexItemInstaller;
		}
		
		if(TYPE_COMPONENT.equals(type)){
			return this.componentInstaller;
		}
 
		if(TYPE_SITE.equals(type)){
			return this.siteInstaller;
		}
		throw new  RuntimeException(" get Installer instance error[incorrect type param]");
	}

	 
}
