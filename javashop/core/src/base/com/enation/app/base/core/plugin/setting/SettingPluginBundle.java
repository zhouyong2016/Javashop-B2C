package com.enation.app.base.core.plugin.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.PluginTab;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;



/**
 * 系统设置插件桩
 * @author apexking
 * @version2.0 author wangxin 6.0版本升级  
 */
@Service("settingPluginBundle")
public class SettingPluginBundle extends AutoRegisterPluginsBundle {
	
	
	protected static final Log loger = LogFactory
			.getLog(SettingPluginBundle.class);


	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.plugin.AutoRegisterPluginsBundle#getName()
	 */
	@Override
	public String getName() {
		return "系统设置插件桩";
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.plugin.AutoRegisterPluginsBundle#registerPlugin(com.enation.framework.plugin.IPlugin)
	 */
	@Override
	public void registerPlugin(IPlugin plugin) {
		super.registerPlugin(plugin);
	}
	
	public List<PluginTab> onInputShow(Map<String,Map<String,String>> settings){
		 

		List<PluginTab> list = new ArrayList<PluginTab>();
		
		FreeMarkerPaser freeMarkerPaser =  FreeMarkerPaser.getInstance();
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			
			for (IPlugin plugin : plugins) {
				if(plugin instanceof IOnSettingInputShow){
					
					PluginTab tab = new PluginTab();
					
					IOnSettingInputShow event = (IOnSettingInputShow)plugin;
					String groupname = event.getSettingGroupName();
					String pageName = event.onShow();
					
					freeMarkerPaser.setClz(event.getClass());
					freeMarkerPaser.setPageName(pageName);
					freeMarkerPaser.putData(groupname, settings.get(groupname));
					
					tab.setTabTitle(event.getTabName());
					tab.setOrder(event.getOrder());
					tab.setTabHtml(freeMarkerPaser.proessPageContent());
					
					list.add(tab);
				}
			}
		}
		
		//对tablist进行排序列
		PluginTab.sort(list);
		return list;
	}

	
	
	

	

	/**
	 * 激发保存事件
	 */
	
	public void onSave(){
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if(plugin instanceof IOnSettingSaveEnvent){
					IOnSettingSaveEnvent event = (IOnSettingSaveEnvent)plugin;
					event.onSave();
				}
			}
		}
		
	}

	

 
	
}
