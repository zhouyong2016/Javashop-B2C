package com.enation.app.shop.component.shortmsg.plugin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.ShortMsg;
import com.enation.app.base.core.plugin.shortmsg.IShortMessageEvent;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.component.orderreturns.service.ReturnsOrderStatus;
import com.enation.app.shop.core.goods.model.DepotUser;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 网店短消息提醒插件
 * @author kingapex
 *
 */
@Component
public class ShopShortMsgPlugin extends AutoRegisterPlugin implements IShortMessageEvent{
	
	@Autowired
	private IPermissionManager permissionManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	
	@Override
	public List<ShortMsg> getMessage() {
		if(EopSetting.PRODUCT.equals("b2c")){
			List<ShortMsg> msgList  = new ArrayList<ShortMsg>();
			AdminUser adminUser  = UserConext.getCurrentAdminUser();
			
			boolean haveDepotAdmin = this.permissionManager.checkHaveAuth( PermissionConfig.getAuthId("depot_admin")  ); //库存管理 权限
			boolean haveFinance = this.permissionManager.checkHaveAuth( PermissionConfig.getAuthId("finance")  ); 	//财务 权限
			boolean haveOrder = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("order"));	 //订单管理员权限
			boolean haveCustomerService = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("customer_service"));//客服权限
			
			//查找退换货任务
			if(haveOrder||haveCustomerService){
				ShortMsg msg= this.getReturnsOrderMessage();
				if(msg!=null){
					msgList.add(msg);
				}
			}
			
			//查找咨询评论任务,查找货到付款订单确认任务
			if(haveCustomerService){
				ShortMsg discuss= this.getDiscussMessage();
				ShortMsg ask= this.getAskMessage();
				ShortMsg confirm = this.getOrderConfirm();
				
				if(discuss!=null){
					msgList.add(discuss);
				}
				if(ask!=null){
					msgList.add(ask);
				}
				
				if(confirm!=null){
					//msgList.add(confirm);
				}
			}
			
			
			//查找发货任务
//			if(haveDepotAdmin){
//				ShortMsg msg= this.getShipMessage(adminUser);
//				if(msg!=null)
//				msgList.add(msg);
//			}
			
//			//查找待确认付款任务消息 
//			if(haveFinance){
//				ShortMsg msg= this.getPayConfirmMessage();
//				if(msg!=null)
//				msgList.add(msg);
//			}
			
			return msgList;
		}
		return null;
	}

	/**
	 * 获取订单确认任务
	 * @return
	 */
	private ShortMsg getOrderConfirm(){
		String sql = "select count(0) from es_order where status=? and   payment_type = 'cod'";
		int count = this.daoSupport.queryForInt(sql, OrderStatus.ORDER_NOT_PAY);
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order/list-by-ship.do?state="+ OrderStatus.ORDER_NOT_PAY+"&shipping_id=2");
			msg.setTitle("有"+count +"个订单需要完成确认订单任务");
			msg.setTarget("ajax");
			return msg;
		}
		return null;
	}
	
	/**
	 * 获取待确认付款任务消息
	 * @return
	 */
	private ShortMsg getPayConfirmMessage(){
		String sql  ="select count(0) from es_order where  disabled=0 and status=? and payment_type != 'cod'  ";		
		int count = this.daoSupport.queryForInt(sql, OrderStatus.ORDER_NOT_PAY);
		String sql_1 = "select count(0) from es_order where disabled=0 and status=? and payment_type = 'cod' ";	
		int count_1 =this.daoSupport.queryForInt(sql_1,OrderStatus.ORDER_ROG);
		if((count+count_1)>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order/not-pay-order.do");
			msg.setTitle("待结算订单");
			msg.setTarget("ajax");
			msg.setContent("有"+(count+count_1) +"个新订单需要您结算");
			return msg;
		}
		return null;
	}
	
	/**
	 * 获取发货任务消息 
	 * @return
	 */
	private ShortMsg getShipMessage(AdminUser adminUser){
		int count=0;
		if(adminUser.getFounder()==0){
			DepotUser depotUser  = (DepotUser)adminUser;
			String sql  ="select count(0) from es_order where status=? and depotid=? and disabled=0";		
			count = this.daoSupport.queryForInt(sql, OrderStatus.SHIP_NO,depotUser.getDepotid());
		}else{
			String sql  ="select count(0) from es_order where (status=? or (payment_type='cod' and status=0)) and disabled=0 ";		
			count = this.daoSupport.queryForInt(sql, OrderStatus.ORDER_PAY);
		}
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/order/not-ship-order.do");
			msg.setTitle("待发货订单");
			msg.setTarget("ajax");
			msg.setContent("有"+count +"个订单需要您发货");
			return msg;
		}
		return null;
	}
	
	/**
	 * 获取退换货消息
	 * @return
	 */
	private ShortMsg getReturnsOrderMessage(){
		String sql  ="select count(0) from es_returns_order where state =? ";		
		int count = this.daoSupport.queryForInt(sql, ReturnsOrderStatus.APPLY_SUB);
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/return-order/returns-apply-list.do?state="+ ReturnsOrderStatus.APPLY_SUB);
			msg.setTitle("退换货申请单");
			msg.setTarget("ajax");
			msg.setContent("有"+(count)+"个退换货申请单");
			return msg;
		}
		return null;
	}
	
	
	/**
	 * 获取评论
	 * @return
	 */
	private ShortMsg getDiscussMessage(){
		String sql  ="select count(0) from es_member_comment where type=1 and status=0 ";		
		int count = this.daoSupport.queryForInt(sql);
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/comments/list.do?type=1");
			msg.setTitle("评论列表");
			msg.setTarget("ajax");
			msg.setContent("有"+(count)+"个评论需要您处理。");
			return msg;
		}
		return null;
	}
	
	/**
	 * 获取咨询
	 * @return
	 */
	private ShortMsg getAskMessage(){
		String sql  ="select count(0) from es_member_comment where type=2 and status=0 ";		
		int count = this.daoSupport.queryForInt(sql);
		
		if(count>0){
			ShortMsg msg  = new ShortMsg();
			msg.setUrl("/shop/admin/comments/list.do?type=2");
			msg.setTitle("咨询列表");
			msg.setTarget("ajax");
			msg.setContent("有"+(count)+"个咨询需要您处理。");
			return msg;
		}
		return null;
	}
	
	
		
}
