package com.enation.app.shop.component.ordercore.plugin.statecheck;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.shop.component.ordercore.plugin.setting.OrderSetting;
import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.OrderLog;
import com.enation.app.shop.core.order.service.IOrderFlowManager;
import com.enation.app.shop.core.order.service.OrderPaymentType;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.SystemSetting;
import com.enation.eop.payment.Payment;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;


/**
 * 订单状态检测插件
 * @author kingapex
 *
 */
@Component
public class OrderStateCheckPlugin extends AutoRegisterPlugin implements IEveryDayExecuteEvent {
	
	@Autowired
	private IDaoSupport daoSupport;

	@Autowired
	private IOrderFlowManager orderFlowManager;
	
	@Override
	public void everyDay() {
		// 检测订单的发货状态 
		this.checkRog();
		this.checkcmpl();
		this.checkCancel();
	}
	
	/**
	 * 检测订单的完成状态 
	 */
	private void checkcmpl(){
		//TODO UNIX_TIMESTAMP为mysql的函数，无法在oracle及mssql中执行
		long unix_timestamp = DateUtil.getDateline();
		Integer time=OrderSetting.getRog_order_day()*24*60*60;
		if(SystemSetting.getTest_mode()==1){//如果测试模式开启 将时间改为60秒
			time=60;
		}
		String sql = "select order_id from es_order where ? >=  signing_time+? and  signing_time is not null and   signing_time>0  and status=? and payment_type!=?";
		//查询所有非货到付款并且订单状态为已收货的订单
		List list = this.daoSupport.queryForList(sql,unix_timestamp,time,OrderStatus.ORDER_ROG,OrderPaymentType.cod.getValue());
		
		if(list!=null&&list.size()>0){
			String orderids="";
			for (int i = 0; i < list.size(); i++) {
				String orderid=((Map)list.get(i)).get("order_id").toString();
				orderids+=Integer.parseInt(orderid);
				if(i<list.size()-1)
					orderids+=",";
				OrderLog orderLog = new OrderLog();
				orderLog.setMessage("系统检测到订单["+orderid+"]为完成状态");
				orderLog.setOp_id(0);
				orderLog.setOp_name("系统检测");
				orderLog.setOp_time(System.currentTimeMillis());
				orderLog.setOrder_id(Integer.parseInt(orderid));
				this.daoSupport.insert("es_order_log", orderLog);
			}
			//TODO UNIX_TIMESTAMP问题
			sql = "update es_order set status =?,complete_time=? where order_id in ("+orderids+")";
			this.daoSupport.execute(sql,OrderStatus.ORDER_COMPLETE,unix_timestamp);
		}
		
	}
	
	/**
	 * 检测订单的发货状态 
	 * 检测在线支付订单超时则自动收货
	 */
	private void checkRog(){
		//查询已发货 的订单
		
		long unix_timestamp = DateUtil.getDateline();
		Integer time=OrderSetting.getRog_order_day()*24*60*60;
		if(SystemSetting.getTest_mode()==1){//如果测试模式开启 将时间改为60秒
			time=60;
		}
		String sql = "select d.* from es_order o ,es_delivery d where o.order_id=d.order_id and d.create_time+?<? and (o.status=? and o.payment_type!='cod')";
		
		List<Delivery>  deliList  =  this.daoSupport.queryForList(sql,Delivery.class,time,unix_timestamp,OrderStatus.ORDER_SHIP) ;
		for(Delivery delivery : deliList){
			
			orderFlowManager.rogConfirm(delivery.getOrder_id(), 0, "系统检测", "订单超时未收货自动收货", DateUtil.getDateline());
		}
		
	}
	
	/**
	 * 检测订单取消
	 * 订单超时未操作则取消订单
	 */
	private void checkCancel(){
		Integer time=OrderSetting.getCancel_order_day()*24*60*60;
		if(SystemSetting.getTest_mode()==1){//如果测试模式开启 将时间改为60秒
			time=60;
		}
		String sql="SELECT order_id from es_order  WHERE disabled=0 AND create_time+?<? AND (status=? or status=? )";
		List<Map> list= daoSupport.queryForList(sql,time,DateUtil.getDateline(),OrderStatus.ORDER_NOT_PAY,OrderStatus.ORDER_CONFIRM);
		for (Map map:list) {
			orderFlowManager.cancel(Integer.parseInt(map.get("order_id").toString()), "订单超时未付款，自动取消");
		}
	}

}
