package com.enation.app.shop.core.order.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.DeliveryItem;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.model.RefundLog;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.framework.database.ObjectNotFoundException;

/**
 * 订单流程管理<br>
 * 负责订单后台管理中的：支付、退款、发货、退货、完成、作废操作<br>
 * 总体关系为：
 * <li>
 * 未支付不可以退款，支付后才能退款<br>
 * 未支付可以支付，支付完成后可以再支付
 * </li>
 * 
 * <li>
 * 未发货不可以退货，发货后可以退货<br>
 * 未发货可以发货，发货完成后不可以再发货
 * </li>
 * 
 *<li>
 * 订单标记为完成状态后不可以进行其它操作<br>
 * 订单标记为作废状态后不可以进行其它操作
 * </li>
 * @author kingapex
 * 2010-4-8上午09:07:00
 * @see com.enation.test.shop.order.OrderFlowTest
 */
public interface IOrderFlowManager {
	/**
	 * 创建订单 计算如下业务逻辑：</br> <li>为订单创建唯一的sn(订单号)</li> <li>
	 * 根据sessionid读取购物车计算订商品价格及商品重量，填充以下信息:</br> goods_amount
	 * 商品总额,shipping_amount 配送费用,order_amount 订单总额,weight商品重量,商品数量：goods_num</li>
	 * <li>根据shipping_id(配送方式id)、regionid(收货地区id)及is_protect(是否保价) 计算
	 * protect_price</li> <li>根据payment_id(支付方式id)计算paymoney(支付费用)</li> <li>
	 * 读取当前买家是否为会员或匿名购买并填充member_id字段</li> <li>计算获得积分和消费积分</li>
	 * 
	 * @param order
	 *            订单实体:<br/>
	 *            <li>shipping_id(配送方式id):需要填充用户选择的配送方式id</li> <li>
	 *            regionid(收货地区id)</li> <li>是否保价is_protect</li>
	 *            shipping_area(配送地区):需要填充以下格式数据：北京-北京市-昌平区 </li>
	 * 
	 *            <li>
	 *            payment_id(支付方式id):需要填充为用户选择的支付方式</li>
	 * 
	 *            <li>填充以下收货信息：</br> ship_name(收货人姓名)</br> ship_addr(收货地址)</br>
	 *            ship_zip(收货人邮编)</br> ship_email(收货人邮箱 ) ship_mobile( 收货人手机)
	 *            ship_tel (收货人电话 ) ship_day (送货日期 ) ship_time ( 送货时间 )
	 * 
	 *            </li> <li>remark为买家附言</li>
	 *            
	 * @param cartItemList 2015-08-20新增by kingapex
	 *        购物车列表<br>
	 *        以前是通过sessionid获取，现改为通过接口传递参数方式<br>
	 *        目的是解决比如在多店中需要创建子订单时需要传递某个店铺的购物列表，而不是全部的购物列表
	 *        
	 * @param sessionid
	 *            会员的sessionid
	 *            
	 *            
	 * @throws IllegalStateException 会员尚未登录,不能兑换赠品!   
	 *         
	 * @return 创建的新订单实体，已经赋予order id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Order add(Order order,List<CartItem> cartItemList, String sessionid);
	
	/**
	 * 为某订单付款<br/>
	 * 对订单状态有如下影响：<br>
	 * 		<li>如果全额付款订单状态为<b>已付款</b>，付款状态为已付款</li>
	 * 		<li>如果部分付款订单状态为<b>已付款</b>，付款状态为部分已付款</li>
	 * 如果订单为非匿名购买且支付方式(paymentLog.pay_method)为“预存款支付”则影响到会员预存款：<br>
	 * 订单预存款为当前预存款<b>减</b>去支付费用
	 * @param paymentLog 付款日志对象<br/>
	 * @throws IllegalArgumentException 下列情形之一抛出此异常:
	 * <li>paymentLog为null</li> 
	 * <li>order_id(订单id)为null</li> 
	 * <li>money(付款金额)为null</li> 
	 * @throws ObjectNotFoundException 如果要支付的订单不存在
	 * @throws IllegalStateException 如果订单支付状态为已支付
	 * @throws RuntimeException  当使用预存款支付，且用户预存款不够支付金额时
	 * @see com.enation.app.shop.core.order.service.javashop.service.support.OrderStatus
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean pay(Integer paymentId,Integer orderId,double payMoney,String userName) ;

	/**
	 * 发货
	 * @param delivery 货运单对象</br>
	 * 如果不指定物流费用和保价费用，则其默认值为0
	 * @param itemList 本次发货的明细
	 * @throws ObjectNotFoundException 如果要发货的订单不存在
	 * @throws IllegalStateException 如果订单发货状态为已发货
	 * @throws IllegalArgumentException 下列情形之一抛出此异常:<br>
	 * <li>delivery为null</li> 
	 *  <li>delivery 对象的 order_id(订单id)为null</li> 
	 * <li>itemList为null 或为空</li> 
	 * <li>发货明细列表中的DeliveryItem对象下列属情有一个为空则抛出异常
	 *  goods_id, product_id、num
	 *  </li>
	 * @see com.enation.app.shop.core.order.service.javashop.service.support.OrderStatus
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void shipping(Delivery delivery ,List<DeliveryItem> itemList);
	
	
	
	
	
	/**
	 * 完成订单:
	 * 标记一个订单为完成状态
	 * @param  orderId 订单id
	 * @throws IllegalArgumentException 当orderId为null时
	 */
	 @Transactional(propagation = Propagation.REQUIRED)
	public void complete(Integer orderId);
	
	
	
	
	
	/**
	 * 作废订单 
	 * 标记一个订单为作废状态
	 * @param  orderId 订单id
	 * @throws IllegalArgumentException 当orderId为null时
	 */
	 @Transactional(propagation = Propagation.REQUIRED)
	public void cancel(Integer orderId,String cancel_reason);
	 
	 /**
	 * 订单确认(付款方式为货到付款 支付方式id为2)
	 * 标记一个订单为订单已确认
	 * @param  orderId 订单id
	 * @throws IllegalArgumentException 当orderId为null时
	 */
	 @Transactional(propagation = Propagation.REQUIRED)
	public void confirmOrder(Integer orderId);
	
	
	/**
	 * 读取某订单未发货的货物(商品)列表
	 * @param orderId 订单id
	 * @return 此订单未发货的货物(商品)列表
	 */
	public List<OrderItem> listNotShipGoodsItem(Integer orderId);
	
	
 
	
	/**
	 * 读取某订单发货的商品明细列表
	 * @param orderId 订单id
	 * @return 此订单发货商品货物的列表
	 */
	public List<OrderItem> listShipGoodsItem(Integer orderId);	
	
 
	
	/**
	 * 确认付款
	 * @param orderId 订单标识
	 * @param paymentId 订单付款ID 2 为货到付款
	 */
	public Order payConfirm(int orderId);
	
	/**
	 * 确认收货
	 * @param orderId 订单标识
	 * @param op_id 操作员标识
	 * @param op_name 操作员名称
	 * @param sign_name 签收人
	 * @param sign_time 签收时间
	 */
	public void rogConfirm(int orderId,Integer op_id,String op_name,String sign_name,Long sign_time);
	
	/**
	 * 订单还原
	 * @param sn 订单号
	 */
	public void restore(String sn);
	/**
	 * 货到付款自动添加收款记录
	 */
	public void addCodPaymentLog(Order order);
}
