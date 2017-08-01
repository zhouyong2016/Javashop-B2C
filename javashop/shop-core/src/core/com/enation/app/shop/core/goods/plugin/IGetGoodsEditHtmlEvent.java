package com.enation.app.shop.core.goods.plugin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取商品修改html事件
 * 
 * @author kingapex
 * 
 */
public interface IGetGoodsEditHtmlEvent {

	/**
	 * 填充修改时商品信息
	 * 
	 * @param goods
	 *            商品基本信息，包含goods_id
	 * @param request
	 *            HttpServletRequest 对象，可通过此对象获取用户的输入数据，或向request压入对象，以便在修改页面中使用
	 */
	public String getEditHtml(Map goods, HttpServletRequest request);

}
