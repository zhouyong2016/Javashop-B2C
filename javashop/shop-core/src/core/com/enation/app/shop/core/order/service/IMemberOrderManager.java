package com.enation.app.shop.core.order.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.Delivery;
import com.enation.framework.database.Page;

/**
 * 用户中心-我的订单
 * @author lzf<br/>
 * 2010-3-15 上午10:21:45<br/>
 * version 1.0<br/>
 */
public interface IMemberOrderManager {
	
	/**
	 * 订单列表
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page pageOrders(int pageNo, int pageSize);
	
	/**
	 * 按订单状态或商品关键词查询订单
	 * @param pageNo
	 * @param pageSize
	 * @param status
	 * @param keyword
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public Page pageOrders(int pageNo, int pageSize,String status, String keyword);
	
	/**
	 * 查看一个用户已购买的商品列表
	 * @param pageNo
	 * @param pageSize
	 * @param keyword
	 * @return
	 */
	public Page pageGoods(int pageNo, int pageSize,String keyword);
	/**
	 * 获取订单的货运情况
	 * @param order_id 订单Id
	 * @return
	 */
	public Delivery getOrderDelivery(int order_id);
	
	/**
	 * 读取订单货物（商品）
	 * @param order_id
	 * @return
	 */
	public List listGoodsItems(int order_id);
	
	
	/**
	 * 读取订单赠品列表
	 * @param orderid
	 * @return
	 */
	public List listGiftItems(int orderid);
	
	/***
	 * 判断当前会员是否购买过此商品
	 * @param goodsid
	 * @return 如果当前用户未登录，返回假
	 */
	public boolean isBuy(int goodsid);
	/**
	 * 获取未评论订单
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public Page pageCommentOrders(int pageNo, int pageSize);
}
