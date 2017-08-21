package com.enation.app.shop.component.gallery.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.setting.IOnSettingInputShow;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 图片设置插件
 * @author kingapex
 *2012-3-5下午5:08:46
 */
@Component
public class ImageSettingPlugin extends AutoRegisterPlugin implements
		IOnSettingInputShow {


	@Override
	public String onShow() {
		 
		return "setting";
	}

	@Override
	public String getSettingGroupName() {
		return "photo";
	}

	@Override
	public String getTabName() {
		 
		return "图片设置";
	}

	@Override
	public int getOrder() {
	 
		return 10;
	}

 
 
}
