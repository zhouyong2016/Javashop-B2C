package com.enation.app.shop.core.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.SellBackStatus;
import com.enation.app.shop.core.order.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IRefundManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.annotation.Log;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.DateUtil;

/**
 * 退款单管理类
 * @author Kanon
 *
 */
@Service("refundManager")
public class RefundManager implements IRefundManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private OrderPluginBundle orderPluginBundle;
	
	@Autowired
	private ISellBackManager sellBackManager;
	
	@Autowired
	private IOrderManager orderManager;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#addRefund(com.enation.app.shop.core.order.model.Refund)
	 */
	@Override
	public void addRefund(Refund refund) {
		daoSupport.insert("es_refund", refund);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#editRefund(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Log(type=LogType.ORDER,detail="退款单ID为${id}的退款单已退款")
	public void editRefund(Integer id, Integer status,Double refund_money) {
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		/**
		 * 修改退款单状态，修改退款单金额
		 */
		daoSupport.execute("update es_refund set status=?,refund_user=?,refund_time=?,refund_money=? where id=?", status,adminUser.getUsername(),DateUtil.getDateline(),refund_money,id);
		
		/**
		 * 修改售后申请状态
		 */
		Refund refund=this.getRefund(id);
		daoSupport.execute("update es_sellback_list set tradestatus=?,alltotal_pay=? where id=?",SellBackStatus.refund.getValue(),refund_money,refund.getSellback_id());
		
		/**
		 * 记录订单日志
		 */
		orderManager.addLog(refund.getOrder_id(), "已退款，金额："+refund_money);
		
		/**
		 * 记录维权订单日志
		 */
		sellBackManager.saveLog(refund.getSellback_id(), "已退款，金额："+refund_money);
		
		/**
		 * 激发退款事件 add by jianghongyan 
		 * 激发退款结算后时间
		 */
		Order order=orderManager.get(refund.getOrder_id());
		orderPluginBundle.onRefund(order, refund);//
		//orderPluginBundle.afterReturnOrderConfirm(refund.getOrder_id(), -refund_money);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#getRefund(java.lang.Integer)
	 */
	@Override
	public Refund getRefund(Integer id) {
		return (Refund) daoSupport.queryForObject("select * from es_refund where id=?", Refund.class, id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#refundList(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page refundList(String state,Integer page, Integer pageSize) {
		StringBuffer sql=new StringBuffer();
		sql.append("select r.*,sl.alltotal_pay as alltotal_pay from es_refund r left join es_sellback_list sl on r.sellback_id = sl.id ");
		if(!state.equals("") && state != null && !state.equals("-1")){
			sql.append(" where status=" +state );
		}
		sql.append(" order by r.create_time desc");
		return daoSupport.queryForPage(sql.toString(),page, pageSize);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IRefundManager#getRefundBySellbackId(java.lang.Integer)
	 */
	@Override
	public Refund getRefundBySellbackId(Integer id) {
		
		return (Refund) daoSupport.queryForObject("select * from es_refund where sellback_id=?", Refund.class, id);
	}

}
