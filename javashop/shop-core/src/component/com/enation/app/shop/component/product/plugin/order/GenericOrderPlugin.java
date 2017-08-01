package com.enation.app.shop.component.product.plugin.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.goods.model.Depot;
import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.DeliveryItem;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.goods.model.StoreLog;
import com.enation.app.shop.core.order.plugin.order.IOrderCanelEvent;
import com.enation.app.shop.core.order.plugin.order.IOrderChangeDepotEvent;
import com.enation.app.shop.core.order.plugin.order.IOrderItemSaveEvent;
import com.enation.app.shop.core.order.plugin.order.IOrderShipEvent;
import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.goods.service.IProductStoreManager;
import com.enation.app.shop.core.goods.service.IStoreLogManager;
import com.enation.app.shop.core.goods.service.StoreLogType;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 普通商品订单插件
 * @author kingapex
 *
 */

@Component
public class GenericOrderPlugin extends AutoRegisterPlugin implements IOrderShipEvent,IOrderCanelEvent,IOrderItemSaveEvent,IOrderChangeDepotEvent {
 
	@Autowired
	private IProductStoreManager productStoreManager;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IStoreLogManager storeLogManager;
	
	@Autowired
	private IDepotManager depotManager;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.plugin.order.IOrderItemSaveEvent#onItemSave(com.enation.app.shop.core.order.model.Order, com.enation.app.shop.core.order.model.OrderItem)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void onItemSave(Order order,OrderItem item) {
		
		
		 Integer order_id = order.getOrder_id();
		 String ordersn = order.getSn();
		 int depotid = order.getDepotid();
		//更新商品可用库存
		this.productStoreManager.decreaseEnable(item.getGoods_id(), item.getProduct_id(), depotid, item.getNum());
		
		//记录库存日志
		StoreLog storeLog = new StoreLog();
		storeLog.setDateline(  com.enation.framework.util.DateUtil.getDateline());
		storeLog.setDepot_type(0);
		storeLog.setDepotid(depotid);
		storeLog.setGoodsid(item.getGoods_id());
		storeLog.setGoodsname( item.getName() );
		storeLog.setNum(0);
		storeLog.setEnable_store(0-item.getNum());
		storeLog.setOp_type(StoreLogType.create_order.getType());  //记录为创建订单减少可用库存
		storeLog.setProductid(item.getProduct_id());
		storeLog.setUserid(0);
		storeLog.setRemark("创建订单["+ordersn+"]，减少可用库存");
		storeLog.setUsername("系统");
		this.storeLogManager.add(storeLog);
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.plugin.order.IOrderShipEvent#itemShip(com.enation.app.shop.core.order.model.Order, com.enation.app.shop.core.order.model.DeliveryItem)
	 */
	@Override
	public void itemShip(Order order,DeliveryItem deliveryItem) {
	 
		int depotid  = order.getDepotid(); //发货仓库
		int num = deliveryItem.getNum(); //发货量
		int goodsid  = deliveryItem.getGoods_id(); //商品id
		int productid  = deliveryItem.getProduct_id();  //货品id
		String name = deliveryItem.getName();
		
		//记录库存日志
		StoreLog storeLog = new StoreLog();
		storeLog.setDateline( DateUtil.getDateline());
		storeLog.setDepot_type(0);
		storeLog.setDepotid(depotid);
		storeLog.setGoodsid(goodsid);
		storeLog.setGoodsname(name);
		storeLog.setNum(0-deliveryItem.getNum());
		storeLog.setOp_type(StoreLogType.ship.getType());   
		storeLog.setProductid(productid);
		storeLog.setUserid(0);
		storeLog.setUsername("系统");
		storeLog.setRemark("订单["+order.getSn()+"]发货，减少库存");
		this.storeLogManager.add(storeLog);
		
		//更新库存 
		this.productStoreManager.decreaseStroe(goodsid, productid, depotid, num);
	}


	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.plugin.order.IOrderCanelEvent#canel(com.enation.app.shop.core.order.model.Order)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void canel(Order order) {
 
		int orderid  = order.getOrder_id();
		List<OrderItem > itemList   = this.orderManager.listGoodsItems(orderid);
		for (OrderItem orderItem : itemList) {
			int goodsid  = orderItem.getGoods_id();
			int productid  = orderItem.getProduct_id();
			int num = orderItem.getNum();
			int depotid = order.getDepotid();
			String name = orderItem.getName();
			
			//记录库存日志
			StoreLog storeLog = new StoreLog();
			storeLog.setDateline( DateUtil.getDateline());
			storeLog.setDepot_type(0);
			storeLog.setDepotid(depotid);
			storeLog.setGoodsid(goodsid);
			storeLog.setGoodsname(name);
			storeLog.setNum(0);
			storeLog.setEnable_store(num);
			storeLog.setRemark("取消订单["+order.getSn()+"],增加可用库存");
			storeLog.setOp_type(StoreLogType.cancel_order.getType());  
			storeLog.setProductid(productid);
			storeLog.setUserid(0);
			storeLog.setUsername("系统");
			this.storeLogManager.add(storeLog);
			
			this.productStoreManager.increaseEnable(goodsid, productid, depotid, num);
			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.plugin.order.IOrderChangeDepotEvent#chaneDepot(com.enation.app.shop.core.order.model.Order, int, java.util.List)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void chaneDepot(Order order, int newdepotid, List<OrderItem> itemList) {
		Depot depot = this.depotManager.get(newdepotid);
		for (OrderItem item : itemList) {
			int goodsid  = item.getGoods_id();
			int num = item.getNum();
			int store = this.productStoreManager.getEnableStroe(goodsid, newdepotid);
			if(store<num){ 
				
				throw new RuntimeException("["+item.getName()+"]库存不足，请求库存["+num+"]在["+depot.getName()+"]中不足，可用库存为["+store+"]");
			}
			int  olddepotid = order.getDepotid();
			this.productStoreManager.decreaseEnable(goodsid, item.getProduct_id(), newdepotid, num); //减少新库可用库存
			this.productStoreManager.increaseEnable(goodsid,  item.getProduct_id(), olddepotid, num);//增加原仓库可用库存
			
			int userid=0;
			String username = "系统";
			
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			if(adminUser!=null){
				userid= adminUser.getUserid();
				username= adminUser.getUsername();
			}
			
			
			//记录库存日志
			//原仓库
			StoreLog storeLog = new StoreLog();
			storeLog.setDateline( DateUtil.getDateline());
			storeLog.setDepot_type(0);
			storeLog.setDepotid(olddepotid);
			storeLog.setGoodsid(goodsid);
			storeLog.setGoodsname(item.getName());
			storeLog.setNum(0);
			storeLog.setEnable_store(num);
			storeLog.setRemark("订单["+order.getSn()+"]仓库修改为["+depot.getName()+"],增加可用库存");
			storeLog.setOp_type(StoreLogType.change_depot.getType());  
			storeLog.setProductid(item.getProduct_id());
			storeLog.setUserid(userid);
			storeLog.setUsername(username);
			this.storeLogManager.add(storeLog);
			
			//新仓库
			storeLog.setDepotid(newdepotid);
			storeLog.setEnable_store(0-num);
			storeLog.setRemark("订单["+order.getSn()+"]仓库修改为["+depot.getName()+"],减少可用库存");
			this.storeLogManager.add(storeLog);
			
			
			
		}
	 
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.plugin.order.IOrderShipEvent#ship(com.enation.app.shop.core.order.model.Delivery, java.util.List)
	 */
	@Override
	public void ship(Delivery delivery, List<DeliveryItem> itemList) {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.plugin.order.IOrderShipEvent#canBeExecute(int)
	 */
	@Override
	public boolean canBeExecute(int catid) {
			return true;
	}

	
}
