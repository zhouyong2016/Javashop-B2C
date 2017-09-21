package com.enation.app.shop.component.shortmsg.plugin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.ShortMsg;
import com.enation.app.base.core.plugin.shortmsg.IShortMessageEvent;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.order.model.SellBackStatus;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 订单申请插件
 * 包含取消订单申请，退货申请，退款申请，新建退款单（未处理）待结算,待发货,待结算
 * @author Kanon 2016-6-16
 * @version 1.0
 * @author Kanon 2016-7-14 添加判断是否为b2c 如果不为b2c则不显示订单提示信息，增加权限判断
 */
@Component
public class OrderApplyShortMsgPlugin extends AutoRegisterPlugin implements IShortMessageEvent{
	
	@Autowired
	private IDaoSupport daoSupport;
	@Autowired
	private IPermissionManager permissionManager;

	@Override
	public List<ShortMsg> getMessage() {
		List<ShortMsg> msgList  = new ArrayList<ShortMsg>();
		//判断是否为b2c 程序
		if(EopSetting.PRODUCT.equals("b2c")){
			
			boolean haveDepotAdmin = this.permissionManager.checkHaveAuth( PermissionConfig.getAuthId("depot_admin")  ); //库存管理 权限
			boolean haveFinance = this.permissionManager.checkHaveAuth( PermissionConfig.getAuthId("finance")  ); 	//财务 权限
			boolean haveOrder = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("order"));	 //订单管理员权限
			boolean haveCustomerService = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("customer_service"));//客服权限
			
			
			ShortMsg msg=new ShortMsg();
			//判断是否有订单权限或客服权限
			if(haveOrder||haveCustomerService){
				
				//取消订单
				msg=getCancelOrder();
				if(msg!=null){
					msgList.add(msg);
				}
				//退货申请
				msg=getSellBackApply();
				if(msg!=null){
					msgList.add(msg);
				}
				//退款申请
				msg=getRefundApply();
				if(msg!=null){
					msgList.add(msg);
				}
			}
			
			
			//判断是否有财务权限
			if(haveFinance){
				//退款
				msg=getRefundList();
				if(msg!=null){
					msgList.add(msg);
				}
				//待结算
				msg=getPendingSettlement();
				if(msg!=null){
					msgList.add(msg);
				}
			}
			
			//判断是否有库管权限
			if(haveDepotAdmin){
				//待发货
				msg=getToBeShipped();
				if(msg!=null){
					msgList.add(msg);
				}
//				
//				//待收货
//				msg=getReceiptOfGoods();
//				if(msg!=null){
//					msgList.add(msg);
//				}
			}
			ShortMsg fail = this.getFailMessage();
			if(fail!=null){
				msgList.add(fail);
			}
		}
		return msgList;
	}
	/**
	 * 待收货
	 * @return
	 */
	private ShortMsg getReceiptOfGoods(){
		String sql="select count(0) from es_order where is_cancel=0 and status=?";
		int count = this.daoSupport.queryForInt(sql,OrderStatus.ORDER_SHIP);
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order/not-rog-order.do");
			msg.setContent("有"+count +"个待收货订单需要完成");
			msg.setTitle("待收货订单");
			msg.setTarget("ajax");
			return msg;
		}
		return null;
	}
	/**
	 * 待结算
	 * @return
	 */
	private ShortMsg getPendingSettlement(){
		StringBuffer sql=new StringBuffer("select count(0) from es_order where ( ( payment_type!='cod' and  status="+OrderStatus.ORDER_CONFIRM +") ");//非货到付款的，未付款状态的可以结算
		sql.append(" or ( payment_type='cod' and   status="+OrderStatus.ORDER_ROG+"  ) )");//货到付款的要发货或收货后才能结算
		int count = this.daoSupport.queryForInt(sql.toString());
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order/not-pay-order.do");
			msg.setContent("有"+count +"个待结算订单需要完成");
			msg.setTitle("待结算订单");
			msg.setTarget("ajax");
			return msg;
		}
		return null;
	}
	/**
	 * 待发货
	 * @return
	 */
	private ShortMsg getToBeShipped(){
		
		StringBuffer sql=new StringBuffer("select count(0) from es_order where is_cancel=0 and ( ( payment_type!='cod'  and  status="+OrderStatus.ORDER_PAY +") ");//非货到付款的，要已结算才能发货
		sql.append(" or ( payment_type='cod' and  status="+OrderStatus.ORDER_CONFIRM +")) ");//货到付款的，已确认就可以发货
		int count = this.daoSupport.queryForInt(sql.toString());
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order/not-ship-order.do");
			msg.setContent("有"+count +"个待发货订单需要完成");
			msg.setTitle("待发货订单");
			msg.setTarget("ajax");
			return msg;
		}
		return null;
	}

	/**
	 * 取消订单申请
	 * @return
	 */
	private ShortMsg getCancelOrder(){
		String sql = "select count(0) from es_order where is_cancel=1";
		int count = this.daoSupport.queryForInt(sql);
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order/cancel-application-list.do");
			msg.setContent("有"+count +"个取消订单申请需要完成");
			msg.setTitle("取消订单申请");
			msg.setTarget("ajax");
			return msg;
		}
		return null;
	}

	/**
	 * 退款申请
	 * @return
	 */
	private ShortMsg getRefundApply(){
		String sql="";
		if(EopSetting.PRODUCT.equals("b2b2c")){
			sql= "select count(0) from es_sellback_list where type=? AND tradestatus=? AND store_id=1 ";
		}else{
			sql= "select count(0) from es_sellback_list where type=? AND tradestatus=? ";
		} 
		Integer count = this.daoSupport.queryForInt(sql,1,SellBackStatus.wait.getValue());
		if(count!=null && count.intValue()>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/sell-back/refund-list.do");
			msg.setTitle("退款申请");
			msg.setContent("有"+count +"个退款申请需要完成");
			msg.setTarget("ajax");
			return msg;
		}
		return null;
	} 

	/**
	 * 退货申请
	 * @return
	 */
	private ShortMsg getSellBackApply(){
		String sql= "select count(0) from es_sellback_list where type=? AND tradestatus=?";
		Integer count = this.daoSupport.queryForInt(sql,2,SellBackStatus.apply.getValue());
		if(count!=null && count.intValue()>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order-report/returned-list.do?status=0&type=2");
			msg.setContent("有"+count +"个退货申请需要完成");
			msg.setTitle("退货申请");
			msg.setTarget("ajax");
			return msg;
		}
		return null;
	} 

	/**
	 * 退款单（未处理）
	 * @return
	 */
	private ShortMsg getRefundList(){
		String sql="select count(0) from es_refund where status=?";
		int count = this.daoSupport.queryForInt(sql,SellBackStatus.apply.getValue());
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order-report/refund-list.do?state="+SellBackStatus.apply.getValue());
			msg.setContent("有"+count +"个退款单需要完成");
			msg.setTitle("待处理退款单");
			msg.setTarget("ajax");
			return msg;
		}
		return null;
	}
	
	/**
	 * 获取退款失败的消息
	 */
	private ShortMsg getFailMessage(){
		String sql = "select count(0) from es_refund where status=3 ";
		int count = this.daoSupport.queryForInt(sql);
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order-report/refund-list.do?state=3");
			msg.setTitle("退款失败订单");
			msg.setTarget("ajax");
			msg.setContent("有"+(count)+"个退款失败的订单");
			return msg;
		}
		return null;
	}
}
