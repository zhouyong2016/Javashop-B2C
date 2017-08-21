package com.enation.app.shop.core.order.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 订单状态
 * 
 * @author apexking
 * @author Kanon 修改订单流程 2016-6-5
 * 
 */
public abstract class OrderStatus {

	/**
	 * -------------------------------------------------------------
	 * 							订单状态
	 * -------------------------------------------------------------
	 */
	public static final int ORDER_NOT_PAY = 0;	//新建订单，货到付款需确认
	
	public static final int ORDER_CONFIRM = 1;	// 已确认
	
	public static final int ORDER_PAY=2;		//已支付
	
	public static final int ORDER_SHIP = 3; 	// 已发货

	public static final int ORDER_ROG = 4; 		// 已收货
	
	public static final int ORDER_COMPLETE =5;	// 已完成
	
	public static final int ORDER_CANCELLATION = 6; // 订单取消（货到付款审核未通过、新建订单取消、订单发货前取消）
	
	public static final int ORDER_MAINTENANCE =7;	// 交易成功已申请售后申请
	
	
	/**
	 * -------------------------------------------------------------
	 * 							付款状态
	 * -------------------------------------------------------------
	 */
	public static final int PAY_NO= 0;   //未付款
	public static final int PAY_PARTIAL_PAYED =1 ;//部分付款
	public static final int PAY_YES= 2; //已全部支付
	


	/**
	 * -------------------------------------------------------------
	 * 							货运状态
	 * -------------------------------------------------------------
	 */	
	
	public static final int SHIP_NO= 0;  //	0未发货
	public static final int SHIP_YES= 1;//	1已发货
	public static final int SHIP_ROG= 2;// 2已收货	

	
	
	
	
	/**
	 * 获取订单状态值 map
	 * key为状态变量名字串
	 * value为状态值
	 * @return
	 */
	public static Map<String,Object> getOrderStatusMap(){
		Map<String,Object> map = new HashMap<String, Object>(17);
		
		//订单状态
		map.put("ORDER_NOT_PAY",OrderStatus.ORDER_NOT_PAY) ;
		map.put("ORDER_CONFIRM",OrderStatus.ORDER_CONFIRM) ;
		map.put("ORDER_PAY",OrderStatus.ORDER_PAY) ;
		map.put("ORDER_SHIP",OrderStatus.ORDER_SHIP) ;
		map.put("ORDER_ROG",OrderStatus.ORDER_ROG) ;
		map.put("ORDER_COMPLETE",OrderStatus.ORDER_COMPLETE) ;
		map.put("ORDER_CANCELLATION",OrderStatus.ORDER_CANCELLATION) ;
		map.put("ORDER_MAINTENANCE", OrderStatus.ORDER_MAINTENANCE);
		//付款状态
		map.put("PAY_NO", PAY_NO);
		map.put("PAY_YES", PAY_YES);
		map.put("PAY_PARTIAL_PAYED", PAY_PARTIAL_PAYED);
		
		
		//货运状态  
		map.put("SHIP_NO", SHIP_NO);
		map.put("SHIP_YES", SHIP_YES);
		map.put("SHIP_PARTIAL_CANCEL", SHIP_YES);
		map.put("SHIP_ROG", SHIP_ROG);
		
		
		
		return map;
	}
	/**
	 * 获取订单状态
	 * @return
	 */
	public static List getOrderStatus(){
			
		Map<String,Object> orderStatus = new HashMap<String, Object>(17);
		
		//订单状态
		orderStatus.put(""+OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));				//新订单,待确认
		orderStatus.put(""+OrderStatus.ORDER_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CONFIRM));				// 确认订单
		orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));						// 已支付
		orderStatus.put(""+OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));						// 已发货
		orderStatus.put(""+OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));						// 已收货
		orderStatus.put(""+OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE)); 			// 已完成
		orderStatus.put(""+OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));		// 作废
		orderStatus.put(""+OrderStatus.ORDER_MAINTENANCE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_MAINTENANCE));		// 交易成功已申请退货申请
		
		List list = new ArrayList();  
	    Iterator it = orderStatus.keySet().iterator();  
	    while (it.hasNext()) {  
	    	String key = it.next().toString();  
	    	Map map=new HashMap();
	    	map.put("key", key);
	    	map.put("value", orderStatus.get(key));
	    	list.add(map);
	    }  
	       
		return list;
	}
	
	/**
	 * 获取付款状态
	 * @return
	 */
	public static List getPayStatus(){
		Map<String,Object> map = new HashMap<String, Object>(6);
		
		//付款状态
		map.put(""+OrderStatus.PAY_NO, OrderStatus.getPayStatusText(OrderStatus.PAY_NO));
		map.put(""+OrderStatus.PAY_PARTIAL_PAYED, OrderStatus.getPayStatusText(OrderStatus.PAY_PARTIAL_PAYED));
		map.put(""+OrderStatus.PAY_YES, OrderStatus.getPayStatusText(OrderStatus.PAY_YES));
		
		List list = new ArrayList();  
	    Iterator it = map.keySet().iterator();  
	    while (it.hasNext()) {  
	    	String key = it.next().toString();  
	    	Map status=new HashMap();
	    	status.put("key", key);
	    	status.put("value", map.get(key));
	    	list.add(status);
	    }  
	       
		return list;
		
	}
	
	/**
	 * 获取发货状态
	 * @return
	 */
	public static List getShipStatus(){
		Map<String,Object> map = new HashMap<String, Object>(10);
		
		//货运状态  
		map.put(""+OrderStatus.SHIP_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_NO));
		map.put(""+OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
		map.put(""+OrderStatus.SHIP_ROG, OrderStatus.getShipStatusText(OrderStatus.SHIP_ROG));
		

		List list = new ArrayList();  
	    Iterator it = map.keySet().iterator();  
	    while (it.hasNext()) {  
	    	String key = it.next().toString();  
	    	Map status=new HashMap();
	    	status.put("key", key);
	    	status.put("value", map.get(key));
	    	list.add(status);
	    }  
	       
		return list;
	}
	
	/**
	 * 获取订单状态 
	 * @param status
	 * @return
	 */
	public static String getOrderStatusText(int status){
		String text = "";
		
		switch (status) {
			
		case ORDER_NOT_PAY:
			text="新订单,待确认";
			break;
			
		case ORDER_CONFIRM:
			text = "已确认";
			break;
		case ORDER_PAY:
			text = "已付款";
			break;

		case ORDER_SHIP:
			text = "已发货";
			break;
			
		case ORDER_COMPLETE:
			text = "已完成";
			break;
			
		case ORDER_ROG:
			text = "已收货";
			break;
			
		case ORDER_CANCELLATION:
			text = "已取消";
			break;
		case ORDER_MAINTENANCE:
			text= "交易完成申请售后";
			break;
		default:
			text = "错误状态";
			break;
		}
		return text;
                  
	}
	
	
	/**
	 * 获取付款状态 
	 * @param status
	 * @return
	 */
	public static String getPayStatusText(int status){
		String text = "";
		
		switch (status) {
		case PAY_NO:
			text = "未付款";
			break;
		case PAY_YES:
			text = "已付款";
			break;
		case PAY_PARTIAL_PAYED:
			text = "部分付款";
			break;
		default:
			text = "错误状态";
			break;
		}
		return text;
	}	

	

	
	/**
	 * 获取货运状态
	 * @param status
	 * @return
	 */
	public static String getShipStatusText(int status){
		String text = "";
		
		switch (status) {

		case SHIP_NO:
			text = "未发货";
			break;
			
		case SHIP_YES:
			text = "已发货";
			break;

		case SHIP_ROG:
			text = " 已收货";
			break;		
		default:
			text = "错误状态";
			break;
		}
                     
		return text;
                  
	}
	
	
}
