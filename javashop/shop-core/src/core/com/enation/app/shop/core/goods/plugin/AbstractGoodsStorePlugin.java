package com.enation.app.shop.core.goods.plugin;

import java.util.Map;

import com.enation.framework.plugin.AutoRegisterPlugin;


/**
 * 商品库存插件基类
 * @author kingapex
 *
 */
public abstract class AbstractGoodsStorePlugin extends AutoRegisterPlugin{
		
	/**
	 * 获取库存管理页面html
	 * @param goods
	 * @return
	 */
	public abstract String getStoreHtml(Map goods);
	
	
	/**
	 * 获取进货html
	 * @param goods
	 * @return
	 */
	public abstract String getStockHtml(Map goods);
	

	/**
	 * 获取出货html
	 * @param goods
	 * @return
	 */
	public abstract String getShipHtml(Map goods);
	
	
	
	
	/**
	 * 响应保存库存事件
	 * @param goods
	 * @return
	 */
	/*public abstract void onStoreSave(Map goods);*/
	
	
	/**
	 * 响应进货事件
	 * @param goods
	 */
	/*public abstract void onStockSave(Map goods);
	*/
	
	/**
	 * 响应出货事件
	 * @param goods
	 */
	public abstract void onShipSave(Map goods);
	
	/**
	 * 定义此插件是否被执行
	 * @param goods
	 * @return
	 */
	public abstract boolean canBeExecute(Map goods);
	
	
	/**
	 * 获取报警设置页面
	 * @param goods
	 * @return
	 */
	public abstract  String getWarnNumHtml(Map goods);
	
	/**
	 * 保存报警设置页面
	 * @param goods
	 */
	public  abstract void onWarnSave(Map goods);
	
	/**
	 * 获取预警库存管理页面html
	 * @param goods
	 * @return
	 */
	public abstract String getWarningStoreHtml(Map goods);
}
