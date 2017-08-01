package com.enation.app.base.core.service.impl;

import java.util.Map;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.plugin.setting.SettingPluginBundle;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.base.core.service.SettingRuntimeException;
import com.enation.framework.cache.AbstractCacheProxy;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;

/**
 * 设置缓存代理类。
 * @author kingapex
 * 2010-1-15下午03:12:29
 */
@Service("settingService")
public class SettingCacheProxy implements ISettingService {

	@Autowired
	private ISettingService settingService;
	
	@Autowired
	private SettingPluginBundle settingPluginBundle;

	private static final String uKey="setting-cache";
	private ICache<Map<String,Map<String,String>>> cache;
	
	@Autowired
	public SettingCacheProxy(ISettingService settingDbService){
		this.settingService = settingDbService;
		//cache = CacheFactory.getCache("settings");
	}
	
	@Override
	public void add(String groupname, String name, String value) {
		this.settingService.add(groupname, name, value);
		Map<String,Map<String,String>> settings  = cache.get(uKey);
		cache=CacheFactory.getCache("settings");
		settings= this.settingService.getSetting();
		cache.put(uKey,settings);
		
	}


	@Override
	public void save(String groupname, String name, String value) {
		this.settingService.save(groupname, name, value);
		Map<String,Map<String,String>> settings  = cache.get(uKey);

		settings= this.settingService.getSetting();
		cache=CacheFactory.getCache("settings");
		cache.put(uKey,settings);
	
		
	}

	@Override
	@SuppressWarnings("null")
	public void delete(String groupname) {
		this.settingService.delete(groupname);
		cache=CacheFactory.getCache("settings");
		Map<String,Map<String,String>> settings  = cache.get(uKey);
		//未命中，由库中取出设置并压入缓存
		if(settings!=null || settings.size()<=0){
			settings.remove(groupname);		 
		}
		
	}

	
	public Map<String,Map<String ,String>>  getSetting() {
		 
		Map<String,Map<String,String>> settings  = null;
		//未命中，由库中取出设置并压入缓存
		if(settings==null || settings.size()<=0){
			settings= this.settingService.getSetting();
			cache=CacheFactory.getCache("settings");
			cache.put(uKey,settings);
		}
		
		return settings;
	}
  
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISettingService#save(java.util.Map)
	 */
	@Override
	public void save(Map<String,Map<String,String>> settings ) throws SettingRuntimeException {
		this.settingService.save(settings);
		cache=CacheFactory.getCache("settings");
		cache.put(uKey,  settingService.getSetting());
		settingPluginBundle.onSave();
	}
	
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISettingService#save(java.lang.String, java.util.Map)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(String groupname, Map<String, String> settings) {
		this.settingService.save(groupname, settings);
		cache=CacheFactory.getCache("settings");
		cache.put(uKey, settingService.getSetting());
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISettingService#getSetting(java.lang.String, java.lang.String)
	 */
	@Override
	public String getSetting(String group, String code) {
		Map<String,Map<String ,String>> settings  = this.getSetting();
		if(settings==null) return null;
		
		Map<String ,String> setting = settings.get(group);		
		if(setting== null )return null;
		
		return setting.get(code);
	}
	
	@Override
	public Map<String, String> getSetting(String group) {
		Map<String,Map<String ,String>> settings  = this.getSetting();
		if(settings==null) return null;
		
		Map<String ,String> setting = settings.get(group);		
		if(setting== null )return null;
		return setting;
	}

	public void setCache(ICache<Map<String, Map<String, String>>> cache) {
		cache=CacheFactory.getCache("settings");
	}


	
}
