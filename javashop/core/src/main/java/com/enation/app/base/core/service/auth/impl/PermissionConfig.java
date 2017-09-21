package com.enation.app.base.core.service.auth.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

public  class PermissionConfig {
	private static Map<String,Integer> authMap= new HashMap<String,Integer>();
	static{ 
 
		try{
			InputStream in  = FileUtil.getResourceAsStream("auth.properties");
			Properties props = new Properties();
			props.load(in);
				
			int super_admin= StringUtil.toInt(props.getProperty("auth.super_admin"), true);
			int goods = StringUtil.toInt(props.getProperty("auth.goods"), true);
			int order = StringUtil.toInt(props.getProperty("auth.order"), true);
			int depot_admin = StringUtil.toInt(props.getProperty("auth.depot_admin"), true);
			int finance = StringUtil.toInt(props.getProperty("auth.finance"), true);
			int customer_service = StringUtil.toInt(props.getProperty("auth.customer_service"), true);
			int depot_ship = StringUtil.toInt(props.getProperty("auth.depot_ship"), true);
	
			authMap.put("super_admin", super_admin); //超级管理员
			authMap.put("goods", goods); //商品管理权限
			authMap.put("order", order); //订单管理权限
			authMap.put("depot_admin", depot_admin); //库管
			authMap.put("finance", finance);//财务
			authMap.put("customer_service", customer_service); //客服权限
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static int getAuthId(String type){
		return authMap.get(type);
	}
	
}
