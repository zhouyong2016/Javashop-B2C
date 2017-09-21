package com.enation.app.shop.core.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.service.IOrderFlowManager;
import com.enation.app.shop.core.order.service.IPaymentLogManager;
import com.enation.framework.database.IDaoSupport;


/**
 * 
 * (收退款单管理) 
 * @author zjp
 * @version v6.3
 * @since v6.3
 * 2017年6月27日 下午5:28:32
 */
@Service("paymentLogManager")
public class PaymentLogManager implements IPaymentLogManager {
	@Autowired
	private IDaoSupport daoSupport;
	@Override
	public List<PaymentLog> list(int orderid, int type, int status) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IPaymentLogManager#get(int)
	 */
	@Override
	public PaymentLog get(int orderid) {
		String sql = "select * from es_payment_logs where order_id = ?";
		return this.daoSupport.queryForObject(sql, PaymentLog.class,orderid); 
	}
	
	
}
