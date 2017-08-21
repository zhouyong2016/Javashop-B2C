package com.enation.app.shop.core.order.service;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.DeliveryItem;
import com.enation.app.shop.core.order.model.PaymentDetail;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.RefundLog;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.database.Page;

/**
 * 订单单据管理接口
 * @author kingapex
 *2010-4-11下午01:01:29
 */
public interface IOrderReportManager {
	
	/**
	 * 分页读取收款单<br>
	 * <li>对应表payment_logs</li>
	 * <li>读取字段type值为<b>1</b>的记录</li>
	 * <li>其中字段order_id和order表关联</li>
	 * <li>其中字段member_id和member表关联</li>
	 * 
	 * @param pageNo 当前页码 
	 * @param pageSize 每页分页数量
	 * 
	 * @param order 排序项<br>
	 * 接受如下格式参数：
	 * order_id desc 或 pay_user asc
	 * 默认为 payment_id desc
	 * @return  收款单分页列表，列表中的实体为： PaymentLog实体,<br> 
	 * 其member_name为非数据库字段，用于显示会员用户名<br>
	 * 如果非会员购买此属性为空
	 */
	public Page listPayment(Map map,int pageNo,int pageSize,String order);
	
	/**
	 * 读取发货单分页列表
	 * <li>对应表delivery</li>
	 * <li>读取字段type值为<b>1</b>的记录</li>
	 * <li>其中字段order_id和order表关联</li>
	 * <li>其中字段member_id和member表关联</li>
	 * 
	 * @param pageNo 当前页码 
	 * @param pageSize 每页分页数量
	 * 
	 *  @param order 排序项<br>
	 * 接受如下格式参数：
	 * order_id desc 或 ship_name asc
	 * 默认为 delivery_id desc
	 * 
	 * @return 发货单分页列表,列表中的实体为：Delivery<br> 
	 * 其member_name为非数据库字段，用于显示会员用户名<br>
	 * 如果非会员购买此属性为空
	 */
	public Page listShipping(int pageNo,int pageSize,String order);
	
	
	
	
	/**
	 * 读取退货单分页列表
	 * <li>对应表delivery</li>
	 * <li>读取字段type值为<b>2</b>的记录</li>
	 * <li>其中字段order_id和order表关联</li>
	 * <li>其中字段member_id和member表关联</li>
	 * 
	 * @param pageNo 当前页码 
	 * @param pageSize 每页分页数量
	 * 
	 *  @param order 排序项<br>
	 * 接受如下格式参数：
	 * order_id desc 或 ship_name asc
	 * 默认为 delivery_id desc
	 * 
	 * @return 发货单分页列表,列表中的实体为：Delivery<br> 
	 * 其member_name为非数据库字段，用于显示会员用户名<br>
	 * 如果非会员购买此属性为空
	 */
	public Page listReturned(int pageNo,int pageSize,String order,String type);	
	
	
	
	
	
	/**
	 * 读取收款单 
	 * @param paymentId 收款单id
	 * @return 收款单实体
	 * @throws ObjectNotFoundException 如果此单据不存在时
	 */
	public PaymentLog getPayment(Integer paymentId);
	
	
	
	/**
	 * 读取发货单/退货单详细
	 * @param deliveryId 发货单/退货单id 
	 * @return  发货单/退货单实体
	 * @throws ObjectNotFoundException 如果此单据不存在时
	 */
	public Delivery getDelivery(Integer deliveryId);
	
	/**
	 * 读取货运信息
	 * @param orderId 订单号 
	 * @return
	 */
	public List<Delivery> getDeliveryList(int orderId);
	
	
	
	/**
	 * 读取读取发货单/退货单明细
	 * @param deliveryId 发货单/退货单id 
	 * @return 发货/退货明细列表
	 */
	public List<DeliveryItem> listDeliveryItem(Integer deliveryId);
	
	
	
	/**
	 * 读取某个订单的付款单
	 * @param order_id
	 * @return
	 */
	public List<PaymentLog> listPayLogs(Integer order_id);
	

	
	/**
	 * 读取某个订单的退款单
	 * @param order_id
	 * @return
	 */
	public List<RefundLog> listRefundLogs(Integer order_id);
	
	/**
	 * 发退货日志
	 * @param order_id
	 * @param type, 1发货;2退货
	 * @return
	 */
	public List listDelivery(Integer order_id, Integer type);
	
	/**
	 * 获取某订单的已付金额
	 * @param paymentId
	 * @return 订单已付金额
	 * @author LiFenLong
	 */
	public double getPayMoney(Integer paymentId);
	/**
	 * 添加某订单详细支付单
	 * @author LiFenLong
	 */
	public void addPayMentDetail(PaymentDetail paymentdetail);
	/**
	 * 获取订单下的收款单Id
	 * @author LiFenLong
	 */
	public Integer getPaymentLogId(Integer orderId);
	/**
	 * 读取某个订单的收款详细单
	 * @param paymentId
	 * @return 订单详细表
	 */
	public List<PaymentDetail> listPayMentDetail(Integer paymentId);
	
}