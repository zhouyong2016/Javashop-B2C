package com.enation.app.shop.component.shopsetting;

import com.enation.app.base.core.plugin.setting.IOnSettingInputShow;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 
 * 暂时先不上线
 * 网店设置插件
 * 
 * @author kingapex
 *
 */

public class ShopSettingPlugin extends AutoRegisterPlugin implements
		IOnSettingInputShow {
 

	@Override
	public String onShow() {
		
		return "ShopSetting";
	}

	@Override
	public String getSettingGroupName() {
		
		return "shop";
	}

 

	@Override
	public String getTabName() {
	 
		return "网店参数";
	}

	@Override
	public int getOrder() {
	 
		return 4;
	}

}
