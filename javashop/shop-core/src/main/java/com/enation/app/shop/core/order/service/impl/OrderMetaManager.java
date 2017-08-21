package com.enation.app.shop.core.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.OrderMeta;
import com.enation.app.shop.core.order.service.IOrderMetaManager;
import com.enation.framework.database.IDaoSupport;

/**
 * 
 * @author Kanon 2016-2-26;6.0版本改造
 *
 */
@Service("orderMetaManager")
public class OrderMetaManager  implements IOrderMetaManager {
	
	@Autowired
	private IDaoSupport  daoSupport;
	@Override
	public void add(OrderMeta orderMeta) {
		this.daoSupport.insert("es_order_meta", orderMeta) ;
	}

	
	
	@Override
	public List<OrderMeta> list(int orderid) {
		 
		return this.daoSupport.queryForList("select * from es_order_meta where orderid=?", OrderMeta.class,orderid);
	}
	
	

	@Override
	public OrderMeta get(int orderid, String meta_key) {
		return this.daoSupport.queryForObject("select * from es_order_meta where orderid=? and meta_key=?", OrderMeta.class,orderid,meta_key);
	}



	@Override
	public void delete(Integer metaid) {
		// TODO Auto-generated method stub
		this.daoSupport.execute("delete from es_order_meta where metaid=?", metaid);
	}
	
	

}
