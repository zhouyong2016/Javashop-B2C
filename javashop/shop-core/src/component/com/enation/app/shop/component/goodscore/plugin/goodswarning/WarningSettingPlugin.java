package com.enation.app.shop.component.goodscore.plugin.goodswarning;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.setting.IOnSettingInputShow;
import com.enation.app.base.core.plugin.setting.IOnSettingSaveEnvent;
import com.enation.app.shop.component.ordercore.plugin.setting.OrderSetting;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 
 * (系统设置-库存预警设置插件) 
 * @author zjp
 * @version v1.0
 * @since v6.1
 * 2016年12月7日 上午10:17:54
 */
@Component
public class WarningSettingPlugin extends AutoRegisterPlugin implements IOnSettingInputShow,IOnSettingSaveEnvent{

	@Override
	public void onSave() {
		WarningSetting.load();
	}

	@Override
	public String onShow() {
		return "inventory";
	}

	@Override
	public String getSettingGroupName() {

		return WarningSetting.setting_key;
	}

	@Override
	public String getTabName() {
		return "库存预警设置";
	}

	@Override
	public int getOrder() {
		
		return 7;
	}
	
}
