package com.enation.app.shop.core.order.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.decorate.service.ISettingManager;
import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.goods.service.impl.ProductManager;
import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.DeliveryItem;
import com.enation.app.shop.core.order.model.DlyType;
import com.enation.app.shop.core.order.model.Logi;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PaymentDetail;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.PaymentLogType;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.app.shop.core.order.service.ILogiManager;
import com.enation.app.shop.core.order.service.IOrderFlowManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IOrderReportManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.OrderPaymentType;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 订单业务流程
 * @author kignapex
 * @version v2.0,2016年2月18日 版本改造
 * @since v6.0
 */
@Service("orderFlowManager")
public class OrderFlowManager implements IOrderFlowManager {
	
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private OrderPluginBundle orderPluginBundle;
	@Autowired
	private ILogiManager logiManager;
	@Autowired
	private IOrderReportManager orderReportManager;
	@Autowired
	private ICartManager cartManager;
	@Autowired
	private IDlyTypeManager dlyTypeManager;
	@Autowired
	private IPaymentManager paymentManager;
	@Autowired
	private ProductManager productManager;
	@Autowired
	private IDaoSupport daoSupport;
	@Autowired
	private ISettingManager settingManager;
	private Logger logger = Logger.getLogger(getClass());
	
	
	
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Order add(Order order,List<CartItem> itemList, String sessionid) {
		String opname = "游客";

		if (order == null)
			throw new RuntimeException("error: order is null");

		/************************** 用户信息 ****************************/
		Member member = UserConext.getCurrentMember();
		
		// 非匿名购买
		if (member != null) {
			order.setMember_id(member.getMember_id());
			opname = member.getUname();
		}

		// 配送方式名称
		DlyType dlyType = new DlyType();
		if (dlyType != null && order.getShipping_id()!=0){
			dlyType = dlyTypeManager.getDlyTypeById(order.getShipping_id());
		}else{
			dlyType.setName("");
		}
		order.setShipping_type(dlyType.getName());
		
		
		/************ 支付方式价格及名称 ************************/
		//修改为提交订单后，选择支付方式付款，修改人：xulipeng  2016年06月03日
		//如果支付方式为 0，则为在线支付，反之则为其它支付方式，如货到付款等。
		if(order.getPayment_id().intValue()==0){
			order.setPayment_name("在线支付");
			order.setPayment_type("onlinePay");
			
		}else{
			PayCfg payCfg = this.paymentManager.get(order.getPayment_id());
			//此方法实现体为空注释掉
			//order.setPaymoney(this.paymentManager.countPayPrice(order.getOrder_id()));
			order.setPayment_name(payCfg.getName());
			order.setPayment_type(payCfg.getType());
		}

		/************ 创建订单 ************************/
		order.setCreate_time(com.enation.framework.util.DateUtil.getDateline());
		
		//获取系统设置购买商品金额与积分的比例
		Map map = settingManager.getSettingPoint();
		String cfg_value = (String) map.get("cfg_value");
		JSONObject json = JSONObject.fromObject(cfg_value);
		int buygoods_num_mp = json.getInt("buygoods_num_mp");
		int need_pay_money = (int) Math.rint(order.getNeed_pay_money());
		order.setGainedpoint(CurrencyUtil.mul(need_pay_money, buygoods_num_mp).intValue());
		
		//判断订单号是否为空
		if(StringUtil.isEmpty(order.getSn())){
			order.setSn(this.createSn());
		}
		order.setStatus(OrderStatus.ORDER_NOT_PAY);
		order.setDisabled(0);
		order.setPay_status(OrderStatus.PAY_NO);
		order.setShip_status(OrderStatus.SHIP_NO);
		order.setOrderStatus("订单已生效");
		
		//给订单添加仓库 ------仓库为默认仓库
		Integer depotId= this.daoSupport.queryForInt("select id from es_depot where choose=1");
		order.setDepotid(depotId);
		/************ 写入订单货物列表 ************************/
 
		
		/**检测商品库存  Start**/
		boolean result = true;	//用于判断购买量是否超出库存
		for(CartItem item : itemList){
			int productId = item.getProduct_id();
			Product product = productManager.get(productId);
			int enableStore = product.getEnable_store();
			int itemNum = item.getNum();
			if(itemNum > enableStore){
				result = false;
				break;
			}
		}
		if(!result){
			throw new RuntimeException("创建订单失败，您购买的商品库存不足");
		}
		/**检测商品库存  End**/
		this.orderPluginBundle.onBeforeCreate(order,itemList, sessionid);
		this.daoSupport.insert("es_order", order);

//		if (itemList.isEmpty() )
//			throw new RuntimeException("创建订单失败，购物车为空");

		Integer orderId = this.daoSupport.getLastId("es_order");

		order.setOrder_id(orderId);
		
		this.saveGoodsItem(itemList, order);


		/************ 写入订单日志 ************************/
		orderManager.addLog(orderId, "订单创建",opname);
		order.setOrder_id(orderId);
		this.orderPluginBundle.onAfterCreate(order,itemList, sessionid);
		//下单则自动改为已确认
		if(!order.getIsCod()){
			this.confirmOrder(orderId);
		}
		
		//只有b2c产品清空session
		if(EopSetting.PRODUCT.equals("b2c")){
			cartManager.clean(sessionid);
		}
	
		return order;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#pay(java.lang.Integer, java.lang.Integer, double, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.ORDER,detail="订单ID为${orderId}，${userName}确认收款，金额：${payMoney}")
	public boolean pay(Integer paymentId,Integer orderId,double payMoney,String userName) {
		
		Order order = this.orderManager.get(orderId);
		
		//add by Kanon 判断是否订单申请取消订单或者订单已经取消
		if(order.getStatus()==OrderStatus.ORDER_CANCELLATION|| order.getIs_cancel()==1){
			throw new RuntimeException("订单取消或订单申请取消订单无法收款!");
		}
		PaymentDetail paymentdetail=new PaymentDetail();
		PaymentLog payment= orderReportManager.getPayment(paymentId);
		double paidmoney=this.orderReportManager.getPayMoney(paymentId);//其它金额中已结算的金额
			if(CurrencyUtil.add(payMoney, paidmoney)>payment.getMoney()){
				//添加过多
				return false;
			}
			
			if(StringUtil.isEmpty(userName)){
				userName="系统";
			}
			//添加支付详细对象
			paymentdetail.setAdmin_user(userName);
			paymentdetail.setPay_date(DateUtil.getDateline());
			paymentdetail.setPay_money(payMoney);
			paymentdetail.setPayment_id(paymentId);
			orderReportManager.addPayMentDetail(paymentdetail);
				
			if(CurrencyUtil.add(payMoney, paidmoney)<payment.getMoney()){
				//修改订单状态为部分付款
				
				this.daoSupport.execute("update es_order set pay_status=?,paymoney=paymoney+? where order_id=?",OrderStatus.PAY_PARTIAL_PAYED,payMoney,order.getOrder_id());
				this.daoSupport.execute("update es_payment_logs set status=?,paymoney=paymoney+? where payment_id=?",OrderStatus.PAY_PARTIAL_PAYED,payMoney,payment.getPayment_id());
				return true;	
			}else{
				//修改订单状态为已付款付款
				this.daoSupport.execute("update es_payment_logs set status=? ,paymoney=paymoney+? where payment_id=?",OrderStatus.PAY_YES,payMoney,payment.getPayment_id());
				//更新订单的已付金额
				this.daoSupport.execute("update es_order set paymoney=paymoney+?,status=?,pay_status=? where order_id=?",payMoney,OrderStatus.ORDER_PAY,OrderStatus.PAY_YES,order.getOrder_id());
				
				payConfirm(orderId);
				return true;
		}
			
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#shipping(com.enation.app.shop.core.order.model.Delivery, java.util.List)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void shipping(Delivery delivery, List<DeliveryItem> itemList) {
		
		if(delivery== null) throw new  IllegalArgumentException("param delivery is NULL");
		if(itemList== null) throw new  IllegalArgumentException("param itemList is NULL");
		if(delivery.getOrder_id()== null )  throw new IllegalArgumentException("param order id is null");
		
		if(delivery.getMoney()== null ) delivery.setMoney(0D);
		if(delivery.getProtect_price()==null) delivery.setProtect_price(0D);
		
		if(logger.isDebugEnabled()){
			logger.debug("订单["+delivery.getOrder_id()+"]发货");
		}
		
		Integer orderId = delivery.getOrder_id() ;
		Order order = this.orderManager.get(orderId);
		delivery.setOrder(order);
		
		checkDisabled(order);
		if(order.getShip_status().intValue() ==  OrderStatus.SHIP_YES){
			if(logger.isDebugEnabled()){
				logger.debug("订单["+order.getSn()+"]状态已经为【已发货】，不能再对其进行发货操作");
			}			
			throw new RuntimeException("订单["+order.getSn()+"]状态已经为【已发货】，不能再对其进行发货操作");
		}
		
		if(delivery.getLogi_id()!=null && delivery.getLogi_id()!=0){
			Logi logi = this.logiManager.getLogiById(delivery.getLogi_id());
			delivery.setLogi_code(logi.getCode());
			delivery.setLogi_name(logi.getName());
		}
		
		delivery.setType(1);
		delivery.setMember_id(order.getMember_id());
		delivery.setCreate_time(DateUtil.getDateline());
		this.daoSupport.insert("es_delivery", delivery);
		Integer delivery_id = this.daoSupport.getLastId("es_delivery");//产生的货运单id
		
		int shipStatus = OrderStatus.SHIP_YES;
		
		/**
		 * -----------------
		 * 激发订单项发货事件
		 * -----------------
		 */
		for(DeliveryItem  deliverItem:itemList){
				this.orderPluginBundle.onItemShip(order,deliverItem);
		} 
		
		/**
		 * -----------------
		 * 激发发货事件
		 * -----------------
		 */
		this.orderPluginBundle.onShip(delivery, itemList);
		
		if(logger.isDebugEnabled()){
			logger.debug("更新订单["+ orderId+"]状态["+OrderStatus.ORDER_SHIP+"]，发货状态["+shipStatus+"]");
		}			
		//更新订单状态为已发货
		//2014-8-17 @author LiFenLong 增加增加发货时间
		String sql = "update es_order set status=?,ship_status=?,sale_cmpl_time=?";
		
		sql += " where order_id=?";
		this.daoSupport.execute(sql, OrderStatus.ORDER_SHIP,shipStatus,DateUtil.getDateline(),orderId);
		
		orderManager.addLog(delivery.getOrder_id(), "订单发货，物流公司："+delivery.getLogi_name()+"。物流单号："+ delivery.getLogi_no());
	}
	
	
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#complete(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void complete(Integer orderId) {
		if(orderId== null ) throw new  IllegalArgumentException("param orderId is NULL");
		this.daoSupport.execute("update es_order set status=? where order_id=?", OrderStatus.ORDER_COMPLETE,orderId);
		this.daoSupport.execute("update es_order set complete_time=? where order_id=?", DateUtil.getDateline(),orderId);
		orderManager.addLog(orderId, "订单完成");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#cancel(java.lang.Integer, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void cancel(Integer orderId,String cancel_reason) {
		if(orderId== null ) throw new  IllegalArgumentException("param orderId is NULL");
		Order order  = this.orderManager.get(orderId);
		//判断订单是否发货，如果已发货无法取消订单
		if(order.getShip_status()==OrderStatus.SHIP_NO){
			this.daoSupport.execute("update es_order set status=?,cancel_reason=? where order_id=?", OrderStatus.ORDER_CANCELLATION,cancel_reason,orderId);
			orderManager.addLog(orderId, "通过订单作废，取消原因:"+cancel_reason);
			this.orderPluginBundle.onCanel(order);
		}else{
			throw new RuntimeException("订单已发货，无法取消!");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#confirmOrder(java.lang.Integer)
	 */
	@Override
	@Log(type=LogType.ORDER,detail="订单ID为${orderId}，确认订单")
	public void confirmOrder(Integer orderId) {
		if(orderId== null ) throw new  IllegalArgumentException("param orderId is NULL");
		Order order = orderManager.get(orderId);
		Member member = this.memberManager.get(order.getMember_id());
		
		this.daoSupport.execute("update es_order set status=?  where order_id=?", OrderStatus.ORDER_CONFIRM ,orderId);
		
		
		//添加一条应收记录 20131110新增
		this.addPaymentIn(member, order);	
		orderManager.addLog(orderId, "订单确认");
		if(order.getNeedPayMoney()==0){
			this.payConfirm(order.getOrder_id());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#addCodPaymentLog(com.enation.app.shop.core.order.model.Order)
	 */
	@Override
	public void addCodPaymentLog(Order order){
		if(order.getIsCod()){
           PaymentLog paymentLog =  new PaymentLog();
			Member member=null;
			if(order.getMember_id()!=null) {
				member=memberManager.get(order.getMember_id());
			}
			if(member!=null){
				paymentLog.setMember_id(member.getMember_id());
				paymentLog.setPay_user(member.getUname());
			}else{
				paymentLog.setPay_user("匿名购买者");
			}
			paymentLog.setPay_date(DateUtil.getDateline());
			paymentLog.setMoney( order.getOrder_amount());		
			paymentLog.setOrder_sn(order.getSn());
			paymentLog.setSn(this.createSn());
			paymentLog.setPay_method("货到付款");
			paymentLog.setOrder_id(order.getOrder_id());
			paymentLog.setType(PaymentLogType.receivable.getValue());
			paymentLog.setStatus(0);
			paymentLog.setCreate_time(DateUtil.getDateline());
			this.daoSupport.insert("es_payment_logs", paymentLog);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#listNotShipGoodsItem(java.lang.Integer)
	 */
	@Override
	public List<OrderItem> listNotShipGoodsItem(Integer orderId) {
		String sql ="select oi.*,p.store,p.sn from es_order_items oi left join es_product p on oi.product_id = p.product_id";
		sql += "  where order_id=? and oi.ship_num<oi.num";
		List<OrderItem> itemList  =this.daoSupport.queryForList(sql, OrderItem.class,orderId);
		this.orderPluginBundle.onFilter(orderId, itemList);
		return itemList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#listShipGoodsItem(java.lang.Integer)
	 */
	@Override
	public List<OrderItem> listShipGoodsItem(Integer orderId) {
		String sql ="select oi.*,p.store,p.sn from es_order_items oi left join es_product p on oi.product_id = p.product_id";
		sql += "  where order_id=? and ship_num!=0";
		List<OrderItem> itemList  =this.daoSupport.queryForList(sql, OrderItem.class,orderId);
		this.orderPluginBundle.onFilter(orderId, itemList);
		return itemList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#payConfirm(int)
	 */
	@Override
	@Log(type=LogType.ORDER,detail="确认ID为${orderId}，已付款")
	public Order payConfirm(int orderId){
		Order order = this.orderManager.get(orderId);
		
 
		
		/**
		 * 计算订单的状态，如果累计支付金额等于订单金额，则为已确认付款
		 */
		int payStatus = OrderStatus.PAY_YES;// 已付款
		int orderStatus = OrderStatus.ORDER_PAY; 
		
		//判断是否已收货，如果已经收货，则更新订单状态为已完成
		if(order.getShip_status() == OrderStatus.SHIP_ROG){
			orderStatus =OrderStatus.ORDER_COMPLETE;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("更新订单状态["+orderStatus+"],支付状态["+payStatus+ "]");
		}
		
	 
		String  sql = "update es_order set status="+orderStatus+",pay_status="+payStatus+"  where order_id=?";
		this.daoSupport.execute(sql, order.getOrder_id());
		if(order.getIsCod()){
			  sql = "update es_order set status="+OrderStatus.ORDER_CONFIRM+" where order_id=?";
			  this.daoSupport.execute(sql, order.getOrder_id());
		}
		 
		 AdminUser adminUser = UserConext.getCurrentAdminUser();
		
		 String opuser = "系统";
		 if(adminUser!=null){
			 opuser  = adminUser.getUsername()+"["+adminUser.getRealname()+"]";
		 }
		 
		 sql="update es_payment_logs set status=2,pay_date=?,admin_user=? where order_id=?";//核销应收
		 this.daoSupport.execute(sql,DateUtil.getDateline(),opuser,order.getOrder_id()); 
	
		if(adminUser!=null){
			orderManager.addLog(orderId, "确认付款");
		}else{
			orderManager.addLog(orderId, "确认付款", "系统");
		}
			
			
		order.setStatus( orderStatus);
		order.setPay_status( payStatus );
		this.orderPluginBundle.onConfirmPay(order);
		
		
		return order;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#rogConfirm(int, java.lang.Integer, java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	@Log(type=LogType.ORDER,detail="订单ID为${orderId}，${sign_name}确认收货")
	public void rogConfirm(int orderId,Integer op_id,String op_name,String sign_name,Long sign_time){
		Order order = this.orderManager.get(orderId);
		this.orderPluginBundle.onRogconfirm(order);
        List<OrderItem> orderItemList=orderManager.listGoodsItems(order.getOrder_id());
		for (OrderItem orderItem : orderItemList) {
			/**
			 * 更新orderItem ship_num为统计商品下单量提供数据
			 */
			orderItem.setShip_num(orderItem.getNum());
			Map<String,Object> paramMap=new HashMap<String,Object>();
			paramMap.put("item_id", orderItem.getItem_id());
			this.daoSupport.update("es_order_items", orderItem, paramMap);
		}
		
		int orderstatus =OrderStatus.ORDER_ROG; //默认是收货状态
		
		String sql ="update es_order set status=" + orderstatus + ",ship_status=" + OrderStatus.SHIP_ROG + ",the_sign='"+ sign_name + "',signing_time=" + sign_time+ " , sale_cmpl=1  where order_id=" + orderId;
		//判断订单如果是货到付款的订单确认收货。需要确认付款的操作
		if(order.getPayment_type().equals(OrderPaymentType.cod.getValue())){
			
			//判断订单收款操作成功与否
			Integer paymentId = this.orderReportManager.getPaymentLogId(orderId);
			if(this.pay(paymentId, orderId, order.getNeed_pay_money(), order.getUname())){
				orderPluginBundle.confirm(orderId,orderManager.get(orderId).getGoods_amount());
			}else{
				throw new RuntimeException("订单收款操作出现问题");
			}
			//更改订单状态为已完成
			orderstatus= OrderStatus.ORDER_COMPLETE;
			sql="update es_order set status=" + orderstatus + ",ship_status=" + OrderStatus.SHIP_ROG + ",the_sign='"+ sign_name + "',signing_time=" + sign_time+ ",complete_time="+ sign_time+" ,sale_cmpl=1  where order_id=" + orderId;
			
		}else{
			orderstatus= OrderStatus.ORDER_COMPLETE;
			sql="update es_order set status=" + orderstatus + ",ship_status=" + OrderStatus.SHIP_ROG + ",the_sign='"+ sign_name + "',signing_time=" + sign_time+ ",complete_time="+ sign_time+ " ,sale_cmpl=1  where order_id=" + orderId;
		}
		
		//sale_cmpl=1 销售生效
		this.daoSupport.execute(sql);
		//写入订单日志表
		orderManager.addLog(orderId, "确认收货",op_name);
		if(order.getMember_id()!=null){
			//写入会员购买商品表
			List<Map> itemList = this.orderManager.getItemsByOrderid(orderId);
			for (Map map : itemList) {
				this.daoSupport.execute(
								"INSERT INTO es_member_order_item(member_id,goods_id,order_id,item_id,commented,comment_time,product_id) VALUES(?,?,?,?,0,0,?)",
								order.getMember_id(), 
								map.get("goods_id").toString(), 
								map.get("order_id").toString(), 
								map.get("item_id").toString(),
								map.get("product_id").toString()); //添加product_id用于区分相同商品不同规格的情况

			} 
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderFlowManager#restore(java.lang.String)
	 */
	@Override
	public void restore(String sn) {
		// TODO Auto-generated method stub
		Order order = orderManager.get(sn);
		if(order == null){
			throw new RuntimeException("对不起，此订单不存在！");
		}
		if(order.getStatus() == null || order.getStatus().intValue() != 8){
			throw new RuntimeException("对不起，此订单不能还原！");
		}
		
		Member member = UserConext.getCurrentMember();
		
		if(order.getMember_id().intValue() != member.getMember_id().intValue()){
			throw new RuntimeException("对不起，您没有权限进行此项操作！");
		}
		order.setStatus(0);
		order.setCancel_reason("");
		orderManager.edit(order);
		
		this.orderPluginBundle.onRestore(order);
	}
	
	
 	
	/**
	 * 处理商品发货项
	 * @param orderId
	 * @param delivery_id
	 * @param itemList
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private int processGoodsShipItem(Integer orderId,Integer delivery_id, List<DeliveryItem> itemList){
		
		//此订单的相关货品
		List<Product> productList  =  listProductbyOrderId(orderId );
		List<OrderItem> orderItemList = this.orderManager.listGoodsItems(orderId);
	//	this.fillAdjItem(orderId, itemList);
		int shipStatus = OrderStatus.SHIP_YES;
		
		for(DeliveryItem item: itemList){
			
			if(item.getGoods_id() == null) throw new IllegalArgumentException(item.getName()+" goods id is  NULL");
			if(item.getProduct_id() == null) throw new IllegalArgumentException(item.getName()+" product id is  NULL");
			if(item.getNum()== null) throw new IllegalArgumentException(item.getName()+" num id is  NULL");
			
			if(logger.isDebugEnabled()){
				logger.debug("检测item["+item.getName()+"]发货数量是否合法");
			}			
			//检查发货数量是否合法，并得到这项的发货状态
			int itemStatus = this.checkGoodsShipNum(orderItemList, item);
			
			//全部为发货完成则订单的发货状态为发货完成
			shipStatus=( shipStatus== OrderStatus.SHIP_YES && itemStatus==OrderStatus.SHIP_YES)?OrderStatus.SHIP_YES:itemStatus;
			
			
			if(logger.isDebugEnabled()){
				logger.debug("检测item["+item.getName()+"]库存");
			}	
			item.setDelivery_id(delivery_id);
			//记录发货明细
			this.daoSupport.insert("es_delivery_item", item);
	 
			//更新发货量
			this.daoSupport.execute("update es_order_items set ship_num=ship_num+? where order_id=? and product_id=?", item.getNum(),orderId,item.getProduct_id());
			
			
			//更新库存量
			this.daoSupport.execute("update es_goods set store=store-? where goods_id=?", item.getNum(),item.getGoods_id());
			this.daoSupport.execute("update es_product set store=store-? where product_id=?", item.getNum(),item.getProduct_id());
			this.daoSupport.execute("update es_product_store set store=store-? where goodsid=? and productid=? and depotid=?",item.getNum(),item.getGoods_id(),item.getProduct_id(),item.getDepotId());
			
			if(this.logger.isDebugEnabled()){
				this.logger.debug("更新"+ item.getName()+"["+ item.getProduct_id()+","+item.getGoods_id()+"-["+item.getNum()+"]");
			} 
		}
		
		return shipStatus;
	}
	
	/**
	 * 添加应收记录
	 * @param member
	 * @param order
	 */
	private void addPaymentIn(Member member,Order order){
		 
        PaymentLog paymentLog =  new PaymentLog();
		 
		if(member!=null){
			paymentLog.setMember_id(member.getMember_id());
			paymentLog.setPay_user(member.getUname());
		}else{
			paymentLog.setPay_user("匿名购买者");
		}
		paymentLog.setMoney( order.getNeed_pay_money());		
		paymentLog.setOrder_sn(order.getSn());
		paymentLog.setSn("");
		paymentLog.setPay_method(order.getPayment_name());
		paymentLog.setOrder_id(order.getOrder_id());
		paymentLog.setType(PaymentLogType.receivable.getValue()); //应收
		paymentLog.setStatus(0);
		paymentLog.setCreate_time(DateUtil.getDateline());
		AdminUser adminUser  = UserConext.getCurrentAdminUser();
		if(adminUser!=null){
			paymentLog.setAdmin_user(adminUser.getRealname()+"["+adminUser.getUsername()+"]");
		}else if(member!=null){
			paymentLog.setAdmin_user(member.getName());
		}
		
		this.daoSupport.insert("es_payment_logs", paymentLog);
	}
	
	/**
	 * 检查发货量是否合法，并且计发货状态
	 * @param orderItemList 购买的订单货物信息
	 * @param item 某一个发货项
	 * @return  
	 */
	private int checkGoodsShipNum(List<OrderItem> orderItemList ,DeliveryItem item){
		
		int status =OrderStatus.SHIP_NO;
		for(OrderItem orderItem:orderItemList){
			
			if( orderItem.getItem_id().compareTo(item.getOrder_itemid())==0){
				
				Integer num = orderItem.getNum(); //总购买量
				Integer shipNum = orderItem.getShip_num();//已经发货量
				if(num<item.getNum() + shipNum){ //总购买量小于总发货量
					if(logger.isDebugEnabled()){
						logger.debug("商品["+item.getName()+"]本次发货量["+item.getNum() +"]超出用户购买量["+num+"],此商品已经发货["+shipNum+"]");
					}						
					throw new RuntimeException("商品["+item.getName()+"]本次发货量["+item.getNum() +"]超出用户购买量["+num+"],此商品已经发货["+shipNum+"]");
				}
				if(num.compareTo(item.getNum() + shipNum)==0){ //总购买量等于总发货量
					status= OrderStatus.SHIP_YES;
				}
				
			}
		}
		return status;
		
	}
	
	/**
	 * 读取某个订单的货品
	 * @param orderId
	 * @return
	 */
	private List<Product> listProductbyOrderId(Integer orderId){
		String sql = "select * from es_product"
		+ " where product_id in (select product_id from "
		+ " es_order_items where order_id=?)";
		List<Product> productList =  this.daoSupport.queryForList(sql, Product.class, orderId);
		return productList;
	}
	
	/**
	 * 检查订单状态是否在可操作状态
	 * @param order
	 * @throws IllegalStateException 如果订单是完成或作废状态
	 */
	private void checkDisabled(Order order){
		if(order.getStatus().intValue() ==  OrderStatus.ORDER_COMPLETE || order.getStatus().intValue() ==  OrderStatus.ORDER_CANCELLATION)
			throw new IllegalStateException("订单已经完成或作废，不能完成操作");
	}


	/**
	 * 创建订单号（日期+两位随机数）
	 */
	private String createSn(){
		boolean isHave = true;  //数据库中是否存在该订单
		String sn = "";			//订单号
		
		//如果存在当前订单
		while(isHave) {
			StringBuffer  snSb = new StringBuffer(DateUtil.getDateline()+"") ;
			snSb.append(new Random().nextInt(100-10)+10);
			String sql = "SELECT count(order_id) FROM es_order WHERE sn = '" + snSb.toString() + "'";
			int count = this.daoSupport.queryForInt(sql);
			if(count == 0) {
				sn = snSb.toString();
				isHave = false;
			}
		}
		return sn;
	}
	
	/**
	 * 保存商品订单项
	 * 
	 * @param itemList
	 * @param order_id
	 */
	private void saveGoodsItem(List<CartItem> itemList, Order order) {
		
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		
		Integer order_id = order.getOrder_id();
		for (int i = 0; i < itemList.size(); i++) {

			OrderItem orderItem = new OrderItem();

			CartItem cartItem = (CartItem) itemList.get(i);
			orderItem.setPrice(cartItem.getCoupPrice());
			orderItem.setName(cartItem.getName());
			orderItem.setNum(cartItem.getNum());
			
			orderItem.setGoods_id(cartItem.getGoods_id());
			orderItem.setShip_num(0);
			orderItem.setProduct_id(cartItem.getProduct_id());
			orderItem.setOrder_id(order_id);
			orderItem.setGainedpoint(cartItem.getPoint());
			orderItem.setAddon(cartItem.getAddon());
			
			//3.0新增的三个字段
			orderItem.setSn(cartItem.getSn());
			orderItem.setImage(cartItem.getImage_default());
			orderItem.setCat_id(cartItem.getCatid());
			orderItem.setGoods_type(cartItem.getGoods_type());
			orderItem.setUnit(cartItem.getUnit());
			
			orderItem.setSnapshot_id(cartItem.getSnapshot_id());//添加快照
			/**
			 * add by jianghongyan 为适配积分商城添加
			 */
			this.orderPluginBundle.beforeItemSave(orderItem,cartItem);
			this.daoSupport.insert("es_order_items", orderItem);
			int itemid = this.daoSupport.getLastId("es_order_items");
			orderItem.setItem_id(itemid);
			orderItemList.add(orderItem);
			this.orderPluginBundle.onItemSave(order,orderItem);
		}
		
		String itemsJson  = JSONArray.fromObject(orderItemList).toString();
		this.daoSupport.execute("update es_order set items_json=? where order_id=?", itemsJson,order_id);
		
	}
}
