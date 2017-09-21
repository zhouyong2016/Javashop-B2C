package com.enation.app.shop.core.order.service;

import com.enation.app.shop.core.order.model.Refund;
import com.enation.framework.database.Page;

/**
 * 退款单管理类
 * @author Kanon
 * 2016-6-12
 */
public interface IRefundManager {

	
	/**
	 * 添加退款单
	 * @param refund 退款单
	 */
	public void addRefund(Refund refund);
	
	/**
	 * 
	 * 修改退款单状态
	 * @param id 退款单id
	 * @param status 退款单状态
	 * @param refund_money 退款金额
	 * @param username 操作人
	 * @return
	 */
	public String editRefund(Integer id,Integer status,Double refund_money,String username);
	
	/**
	 * 获取退款单
	 * @param id
	 */
	public Refund getRefund(Integer id);

	/**
	 * 退款单列表
	 * @param page 分页数量
	 * @param pageSize 分页每页显示数量
	 * @return 退款单列表
	 */
	public Page refundList(String state,Integer page,Integer pageSize);
	
	/**
	 * 根据退款的id查询退款单
	 * @param id
	 * @return
	 */
	public Refund getRefundBySellbackId(Integer id);
	/**
	 * 获取退款单
	 * @param id
	 */
	public Refund getRefundByOrderId(Integer orderId);
	/**
	 * 手动修改退款单状态
	 * @param id	退款单id
	 * @param refund_money	退款金额
	 * @param username		用户信息
	 */	
	public void manualRefundStatus(Integer id,String username);
	
	/**
	 * 添加退款发起时间
	* @param id	退款单id
	 * @param txn_time	发起退款时间
	 */
	public void addRefundTxntime(Integer id,String txn_time);
	
}
