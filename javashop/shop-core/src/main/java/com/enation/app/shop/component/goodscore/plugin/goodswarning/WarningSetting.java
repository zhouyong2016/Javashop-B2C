package com.enation.app.shop.component.goodscore.plugin.goodswarning;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.ISettingService;
import com.enation.framework.context.spring.SpringContextHolder;
/**
 * 
 * (库存预警设置) 
 * @author zjp
 * @version v1.0
 * @since v6.1
 * 2016年12月7日 上午10:47:07
 */
@Component
public class WarningSetting {
	//系统设置中的分组
	public static final String setting_key="inventory"; 
	private static Integer inventory_warning_count; //预警库存数 
	
	//订单设置的默认初始化
	static{
		inventory_warning_count=5;			
	}
	
	/**
	 * 加载系统设置-库存预警设置
	 * 由数据库中加载
	 */
	public static void load(){
		
		ISettingService settingService= SpringContextHolder.getBean("settingService");
		Map<String,String> settings = settingService.getSetting(setting_key);
		
		inventory_warning_count =Integer.parseInt(settings.get("inventory_warning_count").toString());
		
	}


	public static Integer getInventory_warning_count() {
		return inventory_warning_count;
	}




	public static void setInventory_warning_count(Integer inventory_warning_count) {
		WarningSetting.inventory_warning_count = inventory_warning_count;
	}




	public static String getSettingKey() {
		return setting_key;
	}
	
	
}
