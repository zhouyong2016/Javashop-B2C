package com.enation.app.shop.core.order.plugin.promotion;

import java.util.List;

import javax.xml.ws.ServiceMode;

import org.springframework.stereotype.Service;

import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 优惠规则插件桩
 * @author kingapex
 *2010-4-15下午03:50:35
 */
@Service("promotionPluginBundle")
public class PromotionPluginBundle extends AutoRegisterPluginsBundle {

	
	public String getName() {
		 
		return "优惠规则插件桩";
	}
	
	public List getPluginList(){
		
		return this.getPlugins();
	}
	

}
