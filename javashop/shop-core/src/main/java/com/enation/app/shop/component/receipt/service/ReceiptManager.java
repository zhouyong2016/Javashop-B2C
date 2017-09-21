package com.enation.app.shop.component.receipt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.receipt.Receipt;
import com.enation.framework.database.IDaoSupport;


/**
 * 发票管理
 * @author kingapex
 *2012-2-6下午10:25:55
 */
@Component
public class ReceiptManager   implements IReceiptManager {
	@Autowired
	private IDaoSupport daoSupport; 
	
	
	@Transactional(propagation = Propagation.REQUIRED)  
	public void add(Receipt invoice) {
		invoice.setAdd_time( System.currentTimeMillis() );
		this.daoSupport.insert("es_receipt", invoice);
		//this.daoSupport.execute("update order set apply_invoice=1 where order_id=?", invoice.getOrder_id());//改写订单表索取发票字段
	}

	@Override
	public Receipt getById(Integer id) {
 
		List list= this.daoSupport.queryForList("select * from es_receipt where id = ? ",Receipt.class,id);
		if(list.size()==0){
			return null;
		}else{
			return (Receipt)list.get(0);
		}
	}
	
	@Override
	public Receipt getByOrderid(Integer orderid) {
 
		List list= this.daoSupport.queryForList("select * from es_receipt where order_id = ? ",Receipt.class,orderid);
		if(list.size()==0){
			return null;
		}else{
			return (Receipt)list.get(0);
		}
	}


}
