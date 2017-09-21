package com.enation.app.shop.core.decorate.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.decorate.service.ISettingManager;
import com.enation.framework.database.IDaoSupport;


/**
 * 获取系统设置相对应参数设置
 * @author liao
 * @since v621
 * @version 1.0
 * 2017年5月31日
 */
@Service("settingManager")
public class SettingManager implements ISettingManager{

	@Autowired
	private IDaoSupport daoSupport; 
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.decorate.service.ISettingManager#getSettingPoint()
	 */
	@Override
	public Map getSettingPoint() {
		String sql = "select cfg_value from es_settings where cfg_group='point'";
		return this.daoSupport.queryForMap(sql);
	}

}
