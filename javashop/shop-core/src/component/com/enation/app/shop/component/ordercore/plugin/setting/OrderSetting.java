package com.enation.app.shop.component.ordercore.plugin.setting;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.ISettingService;
import com.enation.framework.context.spring.SpringContextHolder;

/***
 * 订单设置
 * 设置订单常用设置
 * @author Kanon
 * @version 1.0
 * @since 6.1 
 * 2016-6-15
 */
@Component
public class OrderSetting {
	
	//系统设置中的分组
	public static final String setting_key="order"; 
	private static Integer cancel_order_day; //取消订单天数 
	private static Integer rog_order_day;	//确定收货天数
	private static Integer comment_order_day; //自动评价订单天数
	private static Integer cancel_sellback_day; //自动取消退货
	
	
	//订单设置的默认初始化
		static{
			cancel_order_day=2;
			rog_order_day=14;
			comment_order_day=14;
			cancel_sellback_day=14;
		}
	
	/**
	 * 加载系统设置-订单设置
	 * 由数据库中加载
	 */
	public static void load(){
		
		ISettingService settingService= SpringContextHolder.getBean("settingService");
		Map<String,String> settings = settingService.getSetting(setting_key);
		if(settings==null){
			return ;
		}
		cancel_order_day =Integer.parseInt(settings.get("cancel_order_day").toString());
		
		rog_order_day =Integer.parseInt(settings.get("rog_order_day").toString());
		
		comment_order_day =Integer.parseInt(settings.get("comment_order_day").toString());
		
		cancel_sellback_day =Integer.parseInt(settings.get("cancel_sellback_day").toString());
	}

	public static Integer getCancel_order_day() {
		return cancel_order_day;
	}

	public static void setCancel_order_day(Integer cancel_order_day) {
		OrderSetting.cancel_order_day = cancel_order_day;
	}

	public static Integer getRog_order_day() {
		return rog_order_day;
	}

	public static void setRog_order_day(Integer rog_order_day) {
		OrderSetting.rog_order_day = rog_order_day;
	}

	public static Integer getComment_order_day() {
		return comment_order_day;
	}

	public static void setComment_order_day(Integer comment_order_day) {
		OrderSetting.comment_order_day = comment_order_day;
	}

	public static Integer getCancel_sellback_day() {
		return cancel_sellback_day;
	}

	public static void setCancel_sellback_day(Integer cancel_sellback_day) {
		OrderSetting.cancel_sellback_day = cancel_sellback_day;
	}
	
}
