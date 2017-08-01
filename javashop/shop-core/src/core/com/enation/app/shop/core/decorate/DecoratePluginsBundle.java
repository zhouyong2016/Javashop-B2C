package com.enation.app.shop.core.decorate;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enation.app.shop.core.decorate.plugin.IDecorateSaveEvent;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 
 * 装修插件桩
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Service("decoratePluginsBundle")
public class DecoratePluginsBundle extends AutoRegisterPluginsBundle{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "装修插件桩";
	}
	/**
	 * 激发装修保存事件
	 * @param type 保存的类型 详见DecorateTypeEnum
	 * @param id 保存对象(floor,showcase,subject)的id
	 */
	public void onSave(String type,Integer id){
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			
			for (IPlugin plugin : plugins) {
					if(plugin instanceof IDecorateSaveEvent){
						IDecorateSaveEvent event = (IDecorateSaveEvent)plugin;
						event.onSave(type,id);
					}
			}
		}
	}
}
