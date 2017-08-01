package com.enation.app.shop.component.ordercore.plugin.setting;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.setting.IOnSettingInputShow;
import com.enation.app.base.core.plugin.setting.IOnSettingSaveEnvent;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 系统设置-订单设置插件
 * @author Kanon
 * 2016-6-15
 *
 */
@Component
public class OrderSettingPlugin extends AutoRegisterPlugin implements IOnSettingInputShow,IOnSettingSaveEnvent{

	@Override
	public void onSave() {

		OrderSetting.load();
	}

	@Override
	public String onShow() {

		return "order";
	}

	@Override
	public String getSettingGroupName() {

		return OrderSetting.setting_key;
	}

	@Override
	public String getTabName() {
		return "订单设置";
	}

	@Override
	public int getOrder() {

		return 6;
	}

}
