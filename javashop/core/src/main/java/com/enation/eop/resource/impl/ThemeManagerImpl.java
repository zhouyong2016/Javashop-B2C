package com.enation.eop.resource.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.ISettingService;
import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.Theme;
import com.enation.framework.database.IDaoSupport;


/**
 * 前台主题管理
 * @author kingapex
 * @version v2.0
 * 2016年2月17日下午9:20:38
 * @since v6.0
 */
@Service("themeManager")
public class ThemeManagerImpl  implements IThemeManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private  ISettingService settingService;
	
	public void clean() {
		daoSupport.execute("truncate table  es_theme");
	}

	public Theme getTheme(Integer themeid) {
		return daoSupport.queryForObject("select * from es_theme where id=?", Theme.class, themeid);
	}

	public List<Theme> list() {
			return daoSupport.queryForList("select * from es_theme where siteid = 0", Theme.class);
		 
	}

	/*
	 * 取得主站的theme列表 (non-Javadoc)
	 * 
	 * @see com.enation.eop.core.resource.IThemeManager#getMainThemeList()
	 */
	public List<Theme> list(int siteid) {
		return daoSupport.queryForList("select * from es_theme where siteid = 0", Theme.class);
	}

	public void addBlank(Theme theme) {
		try {

			daoSupport.insert("es_theme", theme);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("创建主题出错");
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer add(Theme theme, boolean isCommon) {
		try {
			//4.0开始不再copy目录 
		//	this.copy(theme, isCommon);
			daoSupport.insert("es_theme", theme);
			return daoSupport.getLastId("es_theme");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("安装主题出错");
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void changetheme(int themeid) {
		 Theme theme = this.getTheme(themeid);
			Map map =  this.settingService.getSetting(EopSite.SITE_SETTING_KEY);
			map.put("themeid",""+ themeid);
			map.put("themepath", theme.getPath());
			this.settingService.save(EopSite.SITE_SETTING_KEY, map);
			EopSite.reload();
		 
	}

 
	
 
}
