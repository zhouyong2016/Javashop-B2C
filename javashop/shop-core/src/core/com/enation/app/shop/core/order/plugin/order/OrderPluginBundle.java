package com.enation.app.shop.core.order.plugin.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.PluginTab;
import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.DeliveryItem;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.RefundLog;
import com.enation.app.shop.core.order.model.SellBackGoodsList;
import com.enation.app.shop.core.order.model.SellBack;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.model.support.OrderPrice;
import com.enation.app.shop.core.order.plugin.cart.ICountPriceEvent;
import com.enation.app.shop.core.order.service.impl.IBeforeOrderItemSaveEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.database.Page;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;


/**
 * 订单插件桩
 * @author kingapex
 *
 */
@Service("orderPluginBundle")
public class OrderPluginBundle extends AutoRegisterPluginsBundle {

	@Override
	public String getName() {
		
		return "订单插件桩";
	}
	/**
	 * 订单退货后事件
	 * @return
	 */
	public void afterReturnOrder(List<SellBackGoodsList> sellBackGoodsList,Order order){
		List<IPlugin> plugins = this.getPlugins();
		 //如果插件集合不为空
		if (plugins != null) {  
			for (IPlugin plugin : plugins) {
				//如果实现了 该事件
				if(plugin instanceof IAfterOrderSellBackEvent ){
					IAfterOrderSellBackEvent event = (IAfterOrderSellBackEvent) plugin;
					event.returnOrder(sellBackGoodsList, order); 
				}
			} 
		} 
	}
	
	/**
	 * 订单创建前事件
	 * @author xulipeng	 2016年10月21日 增加注解
	 * @param order
	 * @param itemList
	 * @param sessionid
	 */
	public void onBeforeCreate(Order order,List<CartItem>   itemList,String sessionid){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IBeforeOrderCreateEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onBeforeCreate 开始...");
						}
						IBeforeOrderCreateEvent event = (IBeforeOrderCreateEvent) plugin;
						event.onBeforeOrderCreate(order, itemList,sessionid);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onBeforeCreate 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[Before创建]事件错误", e);
			throw e;
		}
	}
	
	/**
	 * 订单创建后事件
	 * @author xulipeng	 2016年10月21日 增加注解
	 * @param order
	 * @param itemList
	 * @param sessionid
	 */
	public void onAfterCreate(Order order,List<CartItem>   itemList,String sessionid){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IAfterOrderCreateEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAfterCreate 开始...");
						}
						IAfterOrderCreateEvent event = (IAfterOrderCreateEvent) plugin;
						event.onAfterOrderCreate(order, itemList,sessionid);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onAfterCreate 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[After创建]事件错误", e);
			throw e;
		}
	}
	
	/**
	 * 过滤订单事件
	 * @author xulipeng  2016年10月21日 增加注解
	 * @param orderid
	 * @param itemList
	 */
	public void onFilter(Integer orderid,List<OrderItem> itemList){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderItemFilter) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onFilter 开始...");
						}
						IOrderItemFilter event = (IOrderItemFilter) plugin;
						event.filter(orderid,itemList);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onFilter 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[filter]事件错误", e);
			throw e;
		}
	}	
	
	
	
	
	/**
	 * 激发订单发货事件
	 * @param orderid
	 * @param itemList
	 */
	public void onShip(Delivery delivery,List<DeliveryItem> itemList){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderShipEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onShip 开始...");
						}
						IOrderShipEvent event = (IOrderShipEvent) plugin;
						event.ship(delivery, itemList);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onShip 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[ship]事件错误", e);
			throw e;
		}
	}	
	

	/**
	 * 激发订单项发货事件
	 * @param deliveryItem
	 * @param allocationItem
	 */
	public void onItemShip(Order order,DeliveryItem deliveryItem){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderShipEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemShip 开始...");
						}
						IOrderShipEvent event = (IOrderShipEvent) plugin;
						
						event.itemShip(order,deliveryItem);
						
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemShip 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[onItemShip]事件错误", e);
			throw e;
		}
	}	
	

	/**
	 * 订单确认收款 退款结算事件
	 * @param 订单id
	 * @author Chopper
	 * 2015年10月28日15:05:00
	 */
	public void confirm(Integer orderid,double price){
		try{
			List<IPlugin> plugins = this.getPlugins();
			//plugins 如果不为空
			if (plugins != null) {
				for (IPlugin plugin : plugins) { 
					if (plugin instanceof IConfirmReceiptEvent) {
						//如果 级别为debug 不输出这些日志
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemShip 开始...");
						}
						IConfirmReceiptEvent event = (IConfirmReceiptEvent) plugin;
						 event.confirm(orderid,price);
						//如果 级别为debug 不输出这些日志
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemShip 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[onItemShip]事件错误", e);
			throw e;
		}
	}	
	
	
	/**
	 * 订单退货
	 * @param delivery
	 * @param itemList
	 */
	public void onReturned(Delivery delivery,List<DeliveryItem> itemList){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderReturnsEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onReturned 开始...");
						}
						IOrderReturnsEvent event = (IOrderReturnsEvent) plugin;
						event.returned(delivery, itemList);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onReturned 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[returned]事件错误", e);
			
			throw e;
		}
	}	
	
	
	/**
	 * 激发订单删除事件
	 * @param orderId
	 */
	public void onDelete(Integer[] orderId){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderDeleteEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onDelete 开始...");
						}
						IOrderDeleteEvent event = (IOrderDeleteEvent) plugin;
						event.delete(orderId);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onDelete 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[delete]事件错误", e);
			throw e;
		}
	}
	
	/**
	 * 获取某个订单项的各库房的库存情况
	 * @param item
	 * @return
	 */
/*	public String getAllocationHtml(OrderItem item){
		String  html = null;
		try{
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderAllocationItemEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " getAllocationHtml 开始...");
						}
						IOrderAllocationItemEvent event = (IOrderAllocationItemEvent) plugin;
						if( event.canBeExecute(item.getCat_id()) ){
							html = ((IOrderAllocationItemEvent) plugin).getAllocationStoreHtml(item);
						} 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " getAllocationHtml 结束.");
						}
					} 
				}
			}
			
			return html;
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[getAllocationHtml]事件错误", e);
			
			throw e;
		}
	} 
	*/
 
	/**
	 * 订单付款事件
	 * @author xulipeng  2016年10月21日  增加注解
	 * @param order
	 * @param isOnline
	 */
	public void onPay(Order order ,boolean isOnline){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderPayEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " pay 开始...");
						}
						    IOrderPayEvent event = (IOrderPayEvent) plugin;
						   event.pay(order, isOnline);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " pay 结束.");
						}
					} 
				}
			} 
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[pay]事件错误", e);
			throw e;
		}		
	}
	
	/**
	 * 执行取消订单事件
	 * @param order 订单
	 */
	public void onCanel(Order order){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderCanelEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " canel 开始...");
						}
						IOrderCanelEvent event = (IOrderCanelEvent) plugin;
					    event.canel(order);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " canel 结束.");
						}
					} 
				}
			} 
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[canel]事件错误", e);
			throw e;
		}		
	}
	
	/**
	 * 订单还原事件
	 * @author xulipeng  2016年10月21日  增加注解
	 * @param order
	 */
	public void onRestore(Order order){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderRestoreEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " canel 开始...");
						}
						IOrderRestoreEvent event = (IOrderRestoreEvent) plugin;
						   event.restore(order);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " canel 结束.");
						}
					} 
				}
			} 
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[canel]事件错误", e);
			throw e;
		}		
	}
	
	/**
	 * 订单确认付款事件
	 * @author xulipeng  2016年10月21日  增加注解
	 * @param order
	 */
	public void onConfirmPay(Order order){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderConfirmPayEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " confirmPay 开始...");
						}
						IOrderConfirmPayEvent event = (IOrderConfirmPayEvent) plugin;
						   event.confirmPay(order);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " confirmPay 结束.");
						}
					} 
				}
			}
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[confirmPay]事件错误", e);
			throw e;
		}		
	}
	
	/**
	 * 确认收货
	 * @param order
	 */
	public void onRogconfirm(Order order) {
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderRogconfirmEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " confirmPay 开始...");
						}
						IOrderRogconfirmEvent event = (IOrderRogconfirmEvent) plugin;
						   event.rogConfirm(order);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " confirmPay 结束.");
						}
					} 
				}
			}
			
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用订单插件[confirmPay]事件错误", e);
			throw e;
		}	
	}
	
	/**
	 * 保存订单商品详细事件
	 * @author xulipeng  2016年10月21日  增加注解
	 * @param order
	 * @param item
	 */
	public void onItemSave(Order order,OrderItem item){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderItemSaveEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemSave 开始...");
						}
						IOrderItemSaveEvent event = (IOrderItemSaveEvent) plugin;
						 event.onItemSave(order,item);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemSave 结束.");
						}
					} 
				}
			}
			
		}catch(RuntimeException  e){
			this.loger.error("调用订单插件[onItemSave]事件错误", e);
			throw e;
		}	
	}
	
	/**
	 * 保存与修改发货仓库事件
	 * @author xulipeng  2016年10月21日  增加注解
	 * @param order
	 * @param newdepotid
	 * @param itemList
	 */
	public void onOrderChangeDepot(Order order,int newdepotid,List<OrderItem> itemList){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderChangeDepotEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemSave 开始...");
						}
						IOrderChangeDepotEvent event = (IOrderChangeDepotEvent) plugin;
						 event.chaneDepot(order,newdepotid,itemList);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + " onItemSave 结束.");
						}
					} 
				}
			}
			
		}catch(RuntimeException  e){
			 
			this.loger.error("调用订单插件[onItemSave]事件错误", e);
			throw e;
		}	
	}
	
	/**
	 * 订单统计—下单金额统计 tab名称事件
	 * @author xulipeng  2016年10月21日   增加注解
	 * @return
	 */
	public Map<Integer,String> getStatisTabList(){
		List<IPlugin> plugins = this.getPlugins();
		Map<Integer,String> tabMap = new TreeMap<Integer, String>();
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if(plugin instanceof IOrderStatisTabShowEvent ){
					
					IOrderStatisTabShowEvent event  = (IOrderStatisTabShowEvent)plugin;
					String name = event.getTabName();
					tabMap.put( event.getOrder(), name);
				}
			}
		}
		return tabMap;
	}
	
	/**
	 * 订单统计—下单金额统计 tab内容事件
	 * @author xulipeng  2016年10月21日   增加注解
	 * @return
	 */
	public Map<Integer,String>   getStatisDetailHtml(Map map) {
		
		Map<Integer,String> htmlMap = new TreeMap<Integer,String>();
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				
				if (plugin instanceof IOrderStatisDetailHtmlEvent) {
					IOrderStatisDetailHtmlEvent event = (IOrderStatisDetailHtmlEvent) plugin;
					freeMarkerPaser.setClz(event.getClass());
					
					if(plugin instanceof IOrderStatisTabShowEvent){
						IOrderStatisTabShowEvent tabEvent =  (IOrderStatisTabShowEvent)plugin;
						String html =event.onShowOrderDetailHtml(map);
						htmlMap.put(tabEvent.getOrder(), html);
					}
				}
			}
		}
		return htmlMap;
	}
	
	
	/**
	 * 激发订单价格计算事件<br>
	 * 此方法以前在cartPluginBundle中，现转移至orderPlugin
	 * @param orderpice
	 * @param map
	 * @return
	 */
	public OrderPrice coutPrice(OrderPrice orderpice){
		try{
			List<IPlugin> plugins = this.getPlugins();
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICountPriceEvent ) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.countPrice开始...");
						}
						ICountPriceEvent event = (ICountPriceEvent) plugin;
						orderpice =event.countPrice(orderpice);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.countPrice结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		 
 		return orderpice;
	}
	
	/**
	 * 激发退款事件
	 * @param order
	 * @param refund
	 */
	public void onRefund(Order order, Refund refund) {
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderRefundEvent ) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "order.onRefund开始...");
						}
						IOrderRefundEvent event = (IOrderRefundEvent) plugin;
						event.onRefund(order,refund);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " order.onRefund结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
		
			
		}catch(Exception e){
			e.printStackTrace();
			 
		}
	}
	/**
	 * 激发订单分页数据过滤事件
	 * @param 订单分页数据
	 */
	public void filterOrderPage(Page webPage) {
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IOrderPageListEvent ) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "order.onRefund开始...");
						}
						IOrderPageListEvent event = (IOrderPageListEvent) plugin;
						event.onfilterPage(webPage);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " order.onRefund结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存订单商品项前 事件
	 * @author xulipeng	2016年10月21日  增加注解
	 * @param orderItem
	 * @param cartItem
	 */
	public void beforeItemSave(OrderItem orderItem, CartItem cartItem) {
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IBeforeOrderItemSaveEvent ) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "order.onRefund开始...");
						}
						IBeforeOrderItemSaveEvent event = (IBeforeOrderItemSaveEvent) plugin;
						event.beforeItemSave(orderItem,cartItem);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " order.onRefund结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取各个插件的html
	 * 
	 */
	public List<PluginTab>   getDetailHtml(Order order) {
		List<PluginTab> list = new ArrayList<PluginTab>();
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("order",order);
		List<IPlugin> plugins = this.getPlugins();
		
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
			
				
				if (plugin instanceof IShowOrderDetailHtmlEvent) {
					IShowOrderDetailHtmlEvent event = (IShowOrderDetailHtmlEvent) plugin;
					freeMarkerPaser.setClz(event.getClass());
					if(plugin instanceof IOrderTabShowEvent){
						
						PluginTab tab = new PluginTab();
						IOrderTabShowEvent tabEvent =  (IOrderTabShowEvent)plugin;
						
						/**
						 * 如果插件返回不执行，则跳过此插件
						 */
						if(!tabEvent.canBeExecute(order)){
							continue;
						}
						String tabTitle = tabEvent.getTabName(order);
						tab.setTabTitle(tabTitle);
						tab.setOrder(tabEvent.getOrder());
						tab.setTabHtml(event.onShowOrderDetailHtml(order));
						list.add(tab);
					}
				}
			}
		}
		//对tablist进行排序列
		PluginTab.sort(list);
		return list;
	}
	 
	
}


