package com.enation.eop.resource.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.ISettingService;
import com.enation.eop.resource.IAdminThemeManager;
import com.enation.eop.resource.model.AdminTheme;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.database.IDaoSupport;

/**
 * 后台主题管理
 * 
 * @author kingapex 2010-5-9下午07:46:18
 */
@Service("adminThemeManager")
public class AdminThemeManagerImpl  implements IAdminThemeManager {

	@Autowired
	private  ISettingService settingService;
	
	@Autowired
	private IDaoSupport  daoSupport;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer add(AdminTheme theme, boolean isCommon) {
		try {
			// this.copy(theme,isCommon);
			this.daoSupport.insert("es_admintheme", theme);
			return this.daoSupport.getLastId("es_admintheme");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("安装后台主题出错");
		}
	}

	public void clean() {
		this.daoSupport.execute("truncate table es_admintheme");
	}
 

	public AdminTheme get(Integer themeid) {
		List<AdminTheme> list = this.daoSupport.queryForList("select * from es_admintheme where id=?", AdminTheme.class, themeid);
		if (list == null || list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IAdminThemeManager#changeTheme(int)
	 */
	@Override
	public void changeTheme(int themeid) {
		Map map =  this.settingService.getSetting(EopSite.SITE_SETTING_KEY);
		map.put("adminthemeid",""+ themeid);
		this.settingService.save(EopSite.SITE_SETTING_KEY, map);
		EopSite.reload();
	}
	
	
	public List<AdminTheme> list() {
		return this.daoSupport.queryForList("select * from es_admintheme ", AdminTheme.class);
	}

 
	
	
}
