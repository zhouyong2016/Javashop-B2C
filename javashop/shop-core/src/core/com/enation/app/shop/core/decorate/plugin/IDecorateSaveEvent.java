package com.enation.app.shop.core.decorate.plugin;

/** 
 * 装修保存事件
 * @author    jianghongyan 
 * @version   1.0.0,2016年7月8日
 * @since     v6.1
 */

public interface IDecorateSaveEvent {

	/**
	 * 装修保存激发事件
	 * @param type 保存的类型 详见DecorateTypeEnum
	 * @param id 保存对象(floor,showcase,subject)的id
	 */
	public void onSave(String type,Integer id);
}
