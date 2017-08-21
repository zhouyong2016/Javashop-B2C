package com.enation.app.shop.core.member.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.member.model.MemberOrderItem;
import com.enation.app.shop.core.member.service.IMemberOrderItemManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
/**
 * @author wangxin  2016-2-17 6.0升级 改造
 */

@Service("memberOrderItemManager")
public class MemberOrderItemManager implements IMemberOrderItemManager {
	@Autowired
	private IDaoSupport daoSupport;
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberOrderItemManager#add(com.enation.app.shop.core.member.model.MemberOrderItem)
	 */
	@Override
	public void add(MemberOrderItem memberOrderItem) {
		this.daoSupport.execute("INSERT INTO es_member_order_item(member_id,goods_id,order_id,item_id,commented,comment_time) VALUES(?,?,?,?,0,0)",
				memberOrderItem.getMember_id(),memberOrderItem.getGoods_id(),memberOrderItem.getOrder_id(),memberOrderItem.getItem_id());		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberOrderItemManager#count(int, int, int)
	 */
	@Override
	public int count(int member_id, int goods_id,int commented) {
		return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_member_order_item WHERE member_id=? AND goods_id=? AND commented=?", member_id, goods_id,commented);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberOrderItemManager#get(int, int, int)
	 */
	@Override
	public MemberOrderItem get(int member_id, int goods_id, int commented) {
		List list = this.daoSupport.queryForList("SELECT * FROM es_member_order_item WHERE member_id=? AND goods_id=? AND commented=?", MemberOrderItem.class, 
				member_id,goods_id, commented);
		if(list!=null && list.size()>0)
			return (MemberOrderItem)list.get(0);
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberOrderItemManager#count(int, int)
	 */
	@Override
	public int count(int member_id, int goods_id){
		return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_member_order_item WHERE member_id=? AND goods_id=?", member_id, goods_id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberOrderItemManager#update(com.enation.app.shop.core.member.model.MemberOrderItem)
	 */
	@Override
	public void update(MemberOrderItem memberOrderItem) {
//		this.daoSupport.execute("UPDATE es_member_order_item SET member_id=?,goods_id=?,order_id=?,item_id=?,commented=?,comment_time=? WHERE id=?", 
//				memberOrderItem.getMember_id(),memberOrderItem.getGoods_id(),memberOrderItem.getOrder_id(),memberOrderItem.getItem_id(),
//				memberOrderItem.getCommented(),memberOrderItem.getComment_time(),memberOrderItem.getId());
		this.daoSupport.update("es_member_order_item", memberOrderItem, "id=" + memberOrderItem.getId());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberOrderItemManager#getGoodsList(int, int, int, int)
	 */
	public Page getGoodsList(int member_id,int commented, int pageNo, int pageSize){
		String sql = "SELECT g.*,m.order_id,m.product_id FROM es_member_order_item m LEFT JOIN es_goods g ON m.goods_id=g.goods_id WHERE m.member_id=? AND m.commented=? ORDER BY m.id DESC";
		return this.daoSupport.queryForPage(sql, pageNo, pageSize, member_id, commented);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberOrderItemManager#getMemberOrderItem(int, int, int)
	 */
	@Override
	public MemberOrderItem getMemberOrderItem(int order_id, int product_id, int commented) {
		List list = this.daoSupport.queryForList("SELECT * FROM es_member_order_item WHERE order_id=? AND product_id=? AND commented=?", MemberOrderItem.class, 
				order_id,product_id, commented);
		if(list!=null && list.size()>0){
			return (MemberOrderItem)list.get(0);
		}		
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberOrderItemManager#getMemberOrderItemList(int, int, int)
	 */
	@Override
	public List<MemberOrderItem> getMemberOrderItemList(int order_id, int product_id, int commented) {
		List list = this.daoSupport.queryForList("SELECT * FROM es_member_order_item WHERE order_id=? AND product_id=? AND commented=?", MemberOrderItem.class, 
				order_id,product_id, commented);
		if(list!=null ){
			return list;
		}		
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberOrderItemManager#updateComment(com.enation.app.shop.core.member.model.MemberOrderItem)
	 */
	@Override
	public void updateComment(MemberOrderItem memberOrderItem) {
		this.daoSupport.update("es_member_order_item", memberOrderItem, " order_id = " + memberOrderItem.getOrder_id()+" and product_id = "+memberOrderItem.getProduct_id());
	}
}
