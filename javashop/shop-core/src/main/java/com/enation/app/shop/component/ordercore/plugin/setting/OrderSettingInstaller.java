package com.enation.app.shop.component.ordercore.plugin.setting;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import com.enation.app.base.core.service.ISettingService;
import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.framework.database.IDaoSupport;

/**
 * 系统设置-订单设置安装器
 * @author Kanon
 * 2016-6-15
 */
@Component
public class OrderSettingInstaller implements IInstaller{
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private ISettingService settingService;
	
	@Override
	public void install(String productId, Node fragment) {
		if(!"order".equals(productId)){
			return ;
		}
		daoSupport.execute("insert into es_settings (cfg_group) values ( '"+OrderSetting.setting_key+"')");
		
		Map settings = settingService.getSetting();
		Map systemSetting = new HashMap();
		systemSetting.put("cancel_order_day", "2");
		systemSetting.put("rog_order_day", "14");
		systemSetting.put("comment_order_day", "14");
		systemSetting.put("cancel_sellback_day", "14");
		settings.put(OrderSetting.setting_key, systemSetting);		 
		settingService.save(settings); //保存系统设置
		
	}

}
