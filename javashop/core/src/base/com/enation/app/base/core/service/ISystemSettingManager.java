package com.enation.app.base.core.service;

import java.util.List;
import java.util.Map;

import com.enation.eop.SystemSetting;


/**
 * 系统设置管理 
 * @author kingapex
 *
 */
public interface ISystemSettingManager {
	
	/**
	 * 获取系统设置
	 * @return
	 */
	public SystemSetting getSetting();
	
	/**
	 * 获取url对照数据
	 * @return
	 */
	public List<Map> getUrlConfig();
	
}
