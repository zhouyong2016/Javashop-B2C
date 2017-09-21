package com.enation.app.shop.core.other.service.impl;

import java.util.ArrayList;
import java.util.List;











import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.receipt.Receipt;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.other.model.ReceiptContent;
import com.enation.app.shop.core.other.service.IReceiptContentManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.Page;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

@Service("receiptContentManager")
public class ReceiptContentManager implements IReceiptContentManager{
	
	@Autowired
	private IDaoSupport daoSupport;

	@Override
	public ReceiptContent saveAdd(ReceiptContent receiptContent) {
		
		this.daoSupport.insert("es_receipt_content", receiptContent);
		
		return receiptContent;
	}

	@Override
	public void saveEdit(ReceiptContent receiptContent) {

		this.daoSupport.update("es_receipt_content", receiptContent, "contentid =" + receiptContent.getContentid());
		
	}

	@Override
	public ReceiptContent get(Integer contentid) {
		
		return this.daoSupport.queryForObject("select * from es_receipt_content where contentid=?", ReceiptContent.class, contentid);
	}

	@Override
	public Page getAllReceiptContent(Integer pageNo, Integer pageSize){

		String sql = "select receipt_content,contentid from es_receipt_content";

		return this.daoSupport.queryForPage(sql, pageNo, pageSize, null);
	}

	@Override
	public void delete(Integer contentid) {
		
		if (this.checkLast() == 1) {
			throw new RuntimeException("必须最少保留一个发票内容");
		}
		
		this.daoSupport.execute("delete from es_receipt_content where contentid=?", contentid);
		
	}
	
	@Override
	public int checkLast() {
		int count = this.daoSupport.queryForInt("select count(0) from es_receipt_content");
		return count;
	}
	
	public boolean is_exist(ReceiptContent receiptContent) {
		
		boolean flag = false;
		
		String sql="select count(0) from es_receipt_content where receipt_content = ?";
		
		int i =  this.daoSupport.queryForInt(sql, receiptContent.getReceipt_content());
		
		if(i!=0){
			flag=true;
		}
		return flag;
	}
	
	@Override
	public List<ReceiptContent> listReceiptContent() {
		List<ReceiptContent> list = this.daoSupport.queryForList(
				"SELECT * FROM es_receipt_content", ReceiptContent.class);
		return list;
	}

	@Override
	public Page getHistoryReceipt(Integer pageNo, Integer pageSize) {
		Page page = null;
		if("b2c".equals(EopSetting.PRODUCT)){
			String sql ="select o.*, m.uname from es_order o left join es_member m on o.member_id = m.member_id where o.receipt = 1 order by o.create_time desc";
			page =  this.daoSupport.queryForPage(sql, pageNo, pageSize, null);
		}else if("b2b2c".equals(EopSetting.PRODUCT)){
			String sql ="select o.*, m.uname from es_order o left join es_member m on o.member_id = m.member_id where o.receipt = 1 and o.parent_id is NOT NULL order by o.create_time desc";
			page =  this.daoSupport.queryForPage(sql, pageNo, pageSize, null);
		}
		return page;
		
		
	}

	@Override
	public Order getHistory(String order_id) {
		String sql = "select o.*, m.uname from es_order o left join es_member m on o.member_id = m.member_id where o.order_id = ?";
		
		return this.daoSupport.queryForObject(sql, Order.class, order_id);
	}

}
