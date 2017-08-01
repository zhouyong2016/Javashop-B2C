package com.enation.app.base.core.service.impl;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.SmsPlatform;
import com.enation.app.base.core.plugin.sms.ISmsSendEvent;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.IPlugin;

import net.sf.json.JSONObject;

/**
 * 短信管理Manager
 * @author xulipeng	
 * @author wangxin 6.0升级改造
 */
@Service("smsManager")
@SuppressWarnings("rawtypes")
public class SmsManager  implements ISmsManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmsManager#getList()
	 */
	@Override
	public List getList() {
		List list = this.daoSupport.queryForList("select * from es_sms_platform");
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmsManager#addSmsPlatform(com.enation.app.base.core.model.SmsPlatform)
	 */
	@Override
	public void addSmsPlatform(SmsPlatform smsPlatform) {
		this.daoSupport.insert("es_sms_platform", smsPlatform);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmsManager#getSmsPlatformHtml(java.lang.String, java.lang.Integer)
	 */
	@Override
	public String getSmsPlatformHtml(String pluginid,Integer smsid) {
		
		FreeMarkerPaser fp =  FreeMarkerPaser.getInstance();
		IPlugin installPlugin = null;
		installPlugin = SpringContextHolder.getBean(pluginid);
		fp.setClz(installPlugin.getClass());
		
		Map<String,String> params = this.getConfigParams(smsid);
		fp.putData(params);
		return fp.proessPageContent();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmsManager#setParam(java.lang.Integer, java.util.Map)
	 */
	@Override
	public void setParam(Integer id, Map<String,String> param) {
		String sql ="update es_sms_platform set config=? where id=?";
		this.daoSupport.execute(sql, JSONObject.fromObject(param).toString(),id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmsManager#get(java.lang.Integer)
	 */
	@Override
	public SmsPlatform get(Integer id) {
		String sql = "select * from es_sms_platform where id=?";
		SmsPlatform platform =  (SmsPlatform) this.daoSupport.queryForObject(sql, SmsPlatform.class, id);
		return platform;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmsManager#send(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings("static-access")
	public boolean send(String phone, String content,Map param) {
		boolean flag = false;
		try {
			String sql ="select * from es_sms_platform where is_open=1";
			SmsPlatform platform =  (SmsPlatform) this.daoSupport.queryForObject(sql, SmsPlatform.class);
			
			//判断是否设置了短信网关组件 add by DMRain 2016-3-17
			if (platform != null) {
				String config = platform.getConfig();
				JSONObject jsonObject = JSONObject.fromObject( config ); 
				
				String code = "";
				if(param!=null){
					code = (String) param.get("code");
				}
				
				Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
				itemMap.put("code", code);
				
				ISmsSendEvent smsSendEvent = SpringContextHolder.getBean(platform.getCode());
				flag = smsSendEvent.onSend(phone, content, itemMap);
			} else {
				flag = false;
			}
			
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		}
		return flag;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.ISmsManager#open(java.lang.Integer)
	 */
	@Override
	public void open(Integer id) {
		this.daoSupport.execute("update es_sms_platform set is_open=0");
		this.daoSupport.execute("update es_sms_platform set is_open=1 where id=?", id);
	}
	
	
	private Map<String, String> getConfigParams(Integer id) {
		SmsPlatform platform = this.get(id);
		String config  = platform.getConfig();
		if(null == config ) return new HashMap<String,String>();
		JSONObject jsonObject = JSONObject.fromObject( config );  
		Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
		return itemMap;
	}

}
