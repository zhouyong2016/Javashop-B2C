package com.enation.app.shop.core.member.service;

import java.util.List;

import com.enation.app.shop.core.member.model.MemberOrderItem;
import com.enation.framework.database.Page;

public interface IMemberOrderItemManager {

	/**
	 * 添加记录
	 * @param memberOrderItem
	 */
	public void add(MemberOrderItem memberOrderItem);
	
	/**
	 * 根据条件查询个数
	 * @param member_id
	 * @param goods_id
	 * @return
	 */
	public int count(int member_id, int goods_id,int commented);
	
	/**
	 * 根据条件查询个数
	 * @param member_id
	 * @param goods_id
	 * @return
	 */
	public int count(int member_id, int goods_id);
	
	/**
	 * 查询一条记录
	 * @param member_id
	 * @param goods_id
	 * @param commented
	 * @return
	 */
	public MemberOrderItem get(int member_id, int goods_id,int commented);
	
	/**
	 * 更新记录
	 * @param memberOrderItem
	 */
	public void update(MemberOrderItem memberOrderItem);
	
	/**
	 * 获取一个会员的待评论或已经评论过的商品列表
	 * @param member_id
	 * @param commented
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page getGoodsList(int member_id,int commented, int pageNo, int pageSize);
	/**
	 * 根据订单id和货品id查询一条记录
	 * @param member_id
	 * @param goods_id
	 * @param commented
	 * @return
	 */
	public MemberOrderItem getMemberOrderItem(int order_id, int product_id,int commented);
	/**
	 * 根据订单id和货品id查询多条记录
	 * @param member_id
	 * @param goods_id
	 * @param commented
	 * @return
	 */
	public List<MemberOrderItem> getMemberOrderItemList(int order_id, int product_id,int commented);
	/**
	 * 更新评论记录
	 * @param memberOrderItem
	 */
	public void updateComment(MemberOrderItem memberOrderItem);
}
