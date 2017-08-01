package com.enation.app.shop.core.goods.plugin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品修改完成事件
 * 
 * @author kingapex
 * 
 */
public interface IGoodsAfterEditEvent {
	
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request);

}
