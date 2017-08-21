package com.enation.app.base.core.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.plugin.setting.SettingPluginBundle;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.base.core.service.SettingRuntimeException;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;

/**
 * 系统设置业务类
 * @author apexking
 * @version 2.0   2016-2-20  6.0 升级改造  wangxin
 */
@Service("settingDbService")
public class SettingService implements ISettingService  {
	@Autowired
	private SettingPluginBundle settingPluginBundle;
	
	@Autowired
	private IDaoSupport daoSupport;
 
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISettingService#add(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="向系统设置中添加一项名为${name}系统设置")
	public void add(String groupname, String name, String value) {
		Map<String,String> params = new HashMap<String, String>();
    	params.put(name, value);
    	JSONObject jsonObject = JSONObject.fromObject( params);
    	this.daoSupport.execute("insert into es_settings(cfg_value,cfg_group)values(?,?)",jsonObject.toString(),groupname);
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISettingService#save(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="保存名为${groupname}这项系统设置")
	public void save(String groupname, String name, String value) {
		Map<String,String> params = new HashMap<String, String>();
    	params.put(name, value);
    	JSONObject jsonObject = JSONObject.fromObject( params);
    	this.daoSupport.execute("update es_settings set cfg_value=? where cfg_group=?",jsonObject.toString(),groupname);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISettingService#delete(java.lang.String)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="删除名为${groupname}这项系统设置")
	public void delete(String groupname) {
		this.daoSupport.execute("delete from es_settings where cfg_group=?", groupname);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.setting.service.ISettingService#save()
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="更新所有系统设置")
	public void save( Map<String,Map<String,String>> settings) throws SettingRuntimeException {

		Iterator<String> settingkeyItor = settings.keySet().iterator();
		 
		while ( settingkeyItor.hasNext() ) {
			
			String settingKey = settingkeyItor.next();
			JSONObject jsonObject = JSONObject.fromObject( settings.get(settingKey) );
			
			this.daoSupport.execute("update es_settings set cfg_value=? where cfg_group=?",jsonObject.toString(),settingKey);
			
		}
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISettingService#save(java.lang.String, java.util.Map)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="更新名为${groupname}设置")
	public void save(String groupname, Map<String, String> settings) {
		 
		JSONObject jsonObject = JSONObject.fromObject(settings );
		this.daoSupport.execute("update es_settings set cfg_value=? where cfg_group=?",jsonObject.toString(),groupname);
		
	}

	/* (non-Javadoc)
	 * @see com.enation.app.setting.service.ISettingService#getSetting()
	 */
	@Override
	public Map<String,Map<String ,String>>  getSetting() {
		String sql = "select * from es_settings";
		List<Map<String, String>> list = this.daoSupport.queryForList(sql);
		Map<String,Map<String,String>> cfg = new HashMap();
		
		for (Map<String,String> map : list) {
			String setting_value = map.get("cfg_value");
			if(StringUtil.isEmpty(setting_value)){
				cfg.put( map.get("cfg_group"), new HashMap<String, String>());
			}else{
				JSONObject jsonObject = JSONObject.fromObject( setting_value );  
				Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
				cfg.put( map.get("cfg_group"), itemMap);
			}
			
		}
		
		return cfg;
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

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISettingService#getSetting(java.lang.String)
	 */
	@Override
	public Map<String, String> getSetting(String group) {
		Map<String,Map<String ,String>> settings  = this.getSetting();
		if(settings==null) return null;
		Map<String ,String> setting = settings.get(group);		
		if(setting== null )return null;
		return setting;
	}

	
    public static void main(String[] args){
    	String setting_value = "{\"thumbnail_pic_height\":\"40\",\"thumbnail_pic_width\":\"50\"}" ;
    	JSONObject jsonObject = JSONObject.fromObject( setting_value );  
		Map map1 = (Map)jsonObject.toBean(jsonObject, Map.class);
}



 

}
