package com.enation.app.shop.component.orderreturns.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.OrderLog;
import com.enation.app.shop.core.order.model.ReturnsOrder;
import com.enation.framework.database.Page;

/**
 * 退货管理
 * @author kingapex;modify by dalbe 
 *
 */
public interface IReturnsOrderManager {
	
	
	/**
	 * 添加一个退货信息
	 * 并更新订单货物表状态
	 * @param returnsOrder
	 */
	public void add(ReturnsOrder returnsOrder,int orderid,int state,int[] goodids);
	
	/**
	 * 根据订单sn号查退换货表中的货物号字符串
	 * @param orderSn
	 * @return
	 */
	public String getSnByOrderSn(String orderSn);
	
	/**
	 * 根据id获取退换信息
	 * @param id
	 */
	public ReturnsOrder get(Integer id);
	
	/**
	 * 根据订单编号得到退换信息
	 * @param orderid
	 * @return
	 */
	public ReturnsOrder getByOrderSn(String ordersn);
	
	
	/**
	 * 读取当前会员的退货申请
	 */
	public List<ReturnsOrder> listMemberOrder();
	
	
	/**
	 * 读取所有的退货信息
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public  Page listAll(int pageNo,int pageSize);
	
	/**
	 * 更新状态s
	 * @param id
	 * @param state
	 */
	public void updateState(Integer id,int state);
	
	/**
	 * 更新货物项中的换货信息
	 */
	public void updateItemChange(String change_goods_name,int change_goods_id,int itemId);
	

	
	/**
	 * 同意退货时，订单货物状态改变
	 */
	public void updateOrderItemsState(Integer itemsId,int state);
	
	public void updateItemsState(Integer orderid,int nowstate,int prestate);
	
	/**
	 * 根据ID 查询价格总和
	 */
	public Double queryItemPrice(Integer orderid,Integer state);
	
	/**
	 * 拒绝退货申请
	 * @param id
	 * @param reson
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void refuse(Integer return_id,String refuse_reason,int return_state);
	
	/**
	 * 退换货申请
	 * @param pageNo 当前页面号
	 * @param pageSize 每页显示行数
	 * @return
	 */
	public Page pageReturnOrder(int pageNo, int pageSize);
	
	/**
	 * 根据订单id和之前状态修改货物表状态
	 * @param newStatus 新的状态
	 * @param prevStatus 之前的状态
	 * @param orderid 订单ID
	 */
	public void updateItemStatusByOrderidAndStatus(int newStatus,int prevStatus,int orderid);
	
	/**
	 * 根据退货表id查询订单表id
	 * @param returnorderid
	 * @return
	 */
	public int queryOrderidByReturnorderid(int returnorderid);
	
	/**
	 * 根据id更改价格
	 * @param itemid
	 * @param price
	 */
	public void updatePriceByItemid(int itemid,double price);
	
	/**
	 * 列出所有某一状态的退换货申请
	 * @param pageNo
	 * @param pageSize
	 * @param state
	 * @return
	 */
	public Page listAll(int pageNo, int pageSize,int state); 
	
	public Integer getOrderidByReturnid(Integer returnorderid);
}
