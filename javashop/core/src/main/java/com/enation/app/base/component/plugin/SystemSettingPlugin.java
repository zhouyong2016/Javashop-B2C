package com.enation.app.base.component.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.setting.IOnSettingInputShow;
import com.enation.app.base.core.plugin.setting.IOnSettingSaveEnvent;
import com.enation.eop.SystemSetting;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 系统设置插件
 * @author kingapex
 *
 */
@Component
public class SystemSettingPlugin extends AutoRegisterPlugin implements
		IOnSettingInputShow,IOnSettingSaveEnvent {

	
	@Override
	public String onShow() {
		
		return "system-setting";
	}

	
	@Override
	public void onSave() {
		SystemSetting.load();
	}
	
	
	@Override
	public String getSettingGroupName() {
		
		return SystemSetting.setting_key;
	}

	@Override
	public String getTabName() {
		
		return "系统设置";
	}

	@Override
	public int getOrder() {
		
		return 5;
	}

	

}
