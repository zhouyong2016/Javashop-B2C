package com.enation.app.shop.component.goodscore.plugin.goodswarning;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import com.enation.app.base.core.service.ISettingService;
import com.enation.app.base.core.service.solution.IInstaller;
import com.enation.framework.database.IDaoSupport;

/**
 * 
 * (系统设置-库存预警安装器) 
 * @author zjp
 * @version v1.0
 * @since v6.1
 * 2016年12月7日 下午2:17:10
 */
@Component
public class WarningSettingInstaller implements IInstaller{
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private ISettingService settingService;
	
	@Override
	public void install(String productId, Node fragment) {
		if(!"inventory".equals(productId)){
			return ;
		}
		daoSupport.execute("insert into es_settings (cfg_group) values ( '"+WarningSetting.setting_key+"')");
		
		Map settings = settingService.getSetting();
		Map systemSetting = new HashMap();
		systemSetting.put("inventory_warning_count", "5");
		settings.put(WarningSetting.setting_key, systemSetting);		 
		settingService.save(settings); //保存系统设置
		
	}

}
