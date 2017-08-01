package com.enation.app.shop.core.other.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.order.service.IOrderManager;

/**
 * 后台首页显示项
 * @author kingapex
 * 2010-10-12上午10:18:15
 * @author Kanon  2016-2-29;6.0版本改造
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/indexItem")
public class ShopIndexItemController  {
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IGoodsManager goodsManager;
	
	/**
	 * 统计订单状态
	 * @param orderss订单状态,Map
	 * @return 订单统计页
	 */
	@RequestMapping(value="/order")
	public ModelAndView order(){
		
		ModelAndView view =new ModelAndView();
		view.setViewName("/shop/admin/index/order");
		view.addObject("orderss", orderManager.censusState());
		return view;
	}
	
	
	/**
	 * 商品统计信息
	 * @param goodsss 商品统计信息
	 * @return 商品统计页
	 */
	@RequestMapping(value="/goods")
	public ModelAndView goods(){
		ModelAndView view= new ModelAndView();
		view.setViewName("/shop/admin/index/goods");
		view.addObject("goodsss", goodsManager.census());
		return view;
	}
}
