package com.enation.app.shop.core.order.service.impl;

import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.goods.model.DepotUser;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.model.OrderLog;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.SellBackGoodsList;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.app.shop.core.order.service.IOrderFlowManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.IRefundManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.annotation.Log;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.database.StringMapper;
import com.enation.framework.log.LogType;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单管理
 * 
 * @author kingapex 2010-4-6上午11:16:01
 */
@Service("orderManager")
public class OrderManager implements IOrderManager {

	@Autowired
	private ICartManager cartManager;
	@Autowired
	private IDlyTypeManager dlyTypeManager;
	@Autowired
	private IPaymentManager paymentManager;
	@Autowired
	private OrderPluginBundle orderPluginBundle;
	@Autowired
	private IPermissionManager permissionManager;
	@Autowired
	private IGoodsManager goodsManager;
	@Autowired
	private IRegionsManager regionsManager;
	@Autowired
	private IDaoSupport daoSupport;

	@Autowired
	private IRefundManager refundManager;

	@Autowired
	private IMemberManager memberManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#savePrice(double,
	 * int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.ORDER,detail="修改订单ID为${orderid}的订单总额")
	public void savePrice(double price, int orderid) {
		Order order = this.get(orderid);
		
		//获取优惠金额
		double discount = order.getDiscount();
		
		//应付金额 = 订单金额 - 优惠券金额
		double needPayMoney = CurrencyUtil.sub(price, discount);
		
		//是否使用优惠券
		if(discount == 0){
			this.daoSupport.execute("update es_order set order_amount=?,need_pay_money=? where order_id=?", price, price,
					orderid);
		}else{
			this.daoSupport.execute("update es_order set order_amount=?,need_pay_money=? where order_id=?", price, needPayMoney,
					orderid);
		}
		//如果当前运行的程序为b2b2c项目  修改子订单的金额时，同时改变父订单的金额  addBy -LYH  2017.8.16
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			Integer parentId=this.daoSupport.queryForInt("select parent_id from es_order where order_id =?", orderid);
			List<Order> childOrderList=this.getChildOrders(parentId);
			Double needpaymoney=0d;
			Double order_amount=0d;
			for (Order childOrder : childOrderList) {
				needpaymoney += childOrder.getNeed_pay_money();
				order_amount += childOrder.getOrder_amount();
			}
			String psql="update es_order set need_pay_money=? , order_amount=? where order_id =?";
			this.daoSupport.execute(psql, needpaymoney, order_amount,parentId);
		}
		// 修改收款单价格
		String sql = "update es_payment_logs set money=? where order_id=?";
		this.daoSupport.execute(sql, price, orderid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#saveShipmoney(
	 * double, int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.ORDER,detail="修改订单ID为${orderid}的配送费用")
	public double saveShipmoney(double shipmoney, int orderid) {
		Order order = this.get(orderid);
		double currshipamount = order.getShipping_amount();
		// double discount= amount-price;
		double shortship = CurrencyUtil.sub(shipmoney, currshipamount);
//		double discount = CurrencyUtil.sub(currshipamount, shipmoney);
		// 2014-9-18 配送费用修改 @author LiFenLong
//		this.daoSupport.execute(
//				"update es_order set order_amount=order_amount+?,need_pay_money=need_pay_money+?,shipping_amount=?,discount=discount+? where order_id=?",
//				shortship, shortship, shipmoney, discount, orderid);
		this.daoSupport.execute(
				"update es_order set order_amount=order_amount+?,need_pay_money=need_pay_money+?,shipping_amount=? where order_id=?",
				shortship, shortship, shipmoney,  orderid);
		// 2014-9-12 LiFenLong 修改配送金额同时修改收款单
		this.daoSupport.execute("update es_payment_logs set money=money+? where order_id=?", shortship, orderid);
		return this.get(orderid).getShipping_amount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#log(java.lang.
	 * Integer, java.lang.String, java.lang.Integer, java.lang.String)
	 */
	@Override
	public void addLog(Integer order_id, String message, String op_name) {
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		orderLog.setOp_id(0);
		orderLog.setOp_name(op_name);
		orderLog.setOp_time(com.enation.framework.util.DateUtil.getDateline());
		orderLog.setOrder_id(order_id);
		this.daoSupport.insert("es_order_log", orderLog);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#log(java.lang.
	 * Integer, java.lang.String)
	 */
	@Override
	public void addLog(Integer order_id, String message) {

		// 获取当前后台管理员
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		if (adminUser == null) {
			orderLog.setOp_id(0);
			orderLog.setOp_name("系统检测");
		} else {
			orderLog.setOp_id(0);
			orderLog.setOp_name(adminUser.getUsername());
		}
		orderLog.setOp_time(DateUtil.getDateline());
		orderLog.setOrder_id(order_id);
		this.daoSupport.insert("es_order_log", orderLog);
	}

	/**
	 * 记录会员操作订单日志
	 * 
	 * @author Kanon
	 * @param order_id
	 *            订单Id
	 * @param message
	 *            日志信息
	 */
	public void frontAddLog(Integer order_id, String message) {
		// 获取当前前台会员
		Member member = UserConext.getCurrentMember();
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		orderLog.setOp_id(member.getMember_id());
		orderLog.setOp_name(member.getName());
		orderLog.setOp_time(DateUtil.getDateline());
		orderLog.setOrder_id(order_id);
		this.daoSupport.insert("es_order_log", orderLog);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listbyshipid(int,
	 * int, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page listbyshipid(int pageNo, int pageSize, int status, int shipping_id, String sort, String order) {
		order = " ORDER BY " + sort + " " + order;
		String sql = "select * from es_order where disabled=0 and status=" + status + " and shipping_id= "
				+ shipping_id;
		sql += " order by " + order;
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize, Order.class);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listConfirmPay(int,
	 * int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page listConfirmPay(int pageNo, int pageSize, String sort, String order) {
		order = " order_id";
		String sql = "select * from es_order where disabled=0 and ((status = " + OrderStatus.ORDER_SHIP
				+ " and payment_type = 'cod') or status= " + OrderStatus.ORDER_PAY + "  )";
		sql += " order by " + order;
		// System.out.println(sql);
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize, Order.class);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#get(java.lang.
	 * Integer)
	 */
	@Override
	public Order get(Integer orderId) {
		String sql = "select * from es_order where order_id=?";
		Order order = (Order) this.daoSupport.queryForObject(sql, Order.class, orderId);
		return order;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#get(java.lang.
	 * String)
	 */
	@Override
	public Order get(String ordersn) {
		String sql = "select * from es_order where sn='" + ordersn + "'";
		Order order = (Order) this.daoSupport.queryForObject(sql, Order.class);
		return order;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listGoodsItems(java
	 * .lang.Integer)
	 */
	@Override
	public List<OrderItem> listGoodsItems(Integer orderId) {
		String sql = "select * from es_order_items";
		sql += " where order_id = ? ";
		List<OrderItem> itemList = this.daoSupport.queryForList(sql, OrderItem.class, orderId);
		this.orderPluginBundle.onFilter(orderId, itemList);
		return itemList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#
	 * listGoodsItemsByItemId(java.lang.Integer)
	 */
	@Override
	public List listGoodsItemsByItemId(Integer itemId) {
		String sql = "select i.*,g.is_pack from es_order_items";
		sql += " i LEFT JOIN es_goods g on i.goods_id = g.goods_id where i.item_id = ?";
		List itemList = this.daoSupport.queryForList(sql, itemId);

		// 获取订单货物规格信息
		getAddr(itemList);
		return itemList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#getOrderItem(int)
	 */
	@Override
	public List getOrderItem(int order_id) {
		String sql = "select o.*,g.name,g.is_pack from es_goods g INNER JOIN es_order_items o on o.goods_id=g.goods_id where o.order_id = ? ";
		List<Map> items = this.daoSupport.queryForList(sql, order_id);
		for (Map item : items) {
			Object obj = item.get("addon");
			if (obj == null) {
				obj = "";
			}
			String addon = obj.toString();
			if (!StringUtil.isEmpty(addon)) {

				List<Map<String, Object>> specList = JsonUtil.toList(addon);

				FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
				freeMarkerPaser.setClz(this.getClass());
				freeMarkerPaser.putData("specList", specList);
				freeMarkerPaser.setPageName("order_item_spec");
				String html = freeMarkerPaser.proessPageContent();

				item.put("other", html);
			}
		}
		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listLogs(java.lang.
	 * Integer)
	 */
	@Override
	public List listLogs(Integer orderId) {
		String sql = "select * from es_order_log where order_id=?";
		return this.daoSupport.queryForList(sql, orderId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#clean(java.lang.
	 * Integer[])
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.ORDER,detail="彻底删除某些订单")
	public void clean(Integer[] orderId) {
		String ids = StringUtil.arrayToString(orderId, ",");
		String sql = "delete from es_order where order_id in (" + ids + ")";
		this.daoSupport.execute(sql);

		sql = "delete from es_order_items where order_id in (" + ids + ")";
		this.daoSupport.execute(sql);

		sql = "delete from es_order_log where order_id in (" + ids + ")";
		this.daoSupport.execute(sql);

		sql = "delete from es_payment_logs where order_id in (" + ids + ")";
		this.daoSupport.execute(sql);

		sql = "delete from es_delivery_item" + " where delivery_id in (select delivery_id from "
				+ "es_delivery where order_id in (" + ids + "))";
		this.daoSupport.execute(sql);

		sql = "delete from es_delivery where order_id in (" + ids + ")";
		this.daoSupport.execute(sql);

		// add from Chopper date 2015-01-21 删除订单同时删除退货单
		sql = "delete from es_sellback_list where orderid in(" + ids + ")";
		this.daoSupport.execute(sql);

		/**
		 * ------------------- 激发订单的删除事件 -------------------
		 */
		this.orderPluginBundle.onDelete(orderId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#delete(java.lang.
	 * Integer[])
	 */
	@Override
	@Log(type=LogType.ORDER,detail="删除订单，放入回收站")
	public boolean delete(Integer[] orderId) {
		return exec(orderId, 1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#revert(java.lang.
	 * Integer[])
	 */
	@Override
	@Log(type=LogType.ORDER,detail="订单还原")
	public void revert(Integer[] orderId) {
		exec(orderId, 0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listOrderByMemberId
	 * (int)
	 */
	@Override
	public List listOrderByMemberId(int member_id) {
		String sql = "select * from es_order where member_id = ? order by create_time desc";
		List list = this.daoSupport.queryForList(sql, Order.class, member_id);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#mapOrderByMemberId(
	 * int)
	 */
	@Override
	public Map mapOrderByMemberId(int memberId) {
		Integer buyTimes = this.daoSupport.queryForInt("select count(0) from es_order where member_id = ?", memberId);
		Double buyAmount =  this.daoSupport.queryForDouble("select sum(paymoney) from es_order where member_id = ?",   memberId);
		Map map = new HashMap();
		map.put("buyTimes", buyTimes);
		map.put("buyAmount", buyAmount);
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#edit(com.enation.
	 * app.shop.core.order.model.Order)
	 */
	@Override
	@Log(type="edit",detail="订单号为${order.sn}，添加特殊处理")
	public void edit(Order order) {
		this.daoSupport.update("es_order", order, "order_id = " + order.getOrder_id());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listAdjItem(java.
	 * lang.Integer)
	 */
	@Override
	public List<Map> listAdjItem(Integer orderid) {
		String sql = "select * from es_order_items where order_id=? and addon!=''";
		return this.daoSupport.queryForList(sql, orderid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#censusState()
	 */
	@Override
	public Map censusState() {

		// 构造一个返回值Map，并将其初始化：各种订单状态的值皆为0
		Map<String, Integer> stateMap = new HashMap<String, Integer>(7);
		String[] states = { "cancel_ship", "cancel_pay", "pay", "ship", "complete", "allocation_yes" };
		for (String s : states) {
			stateMap.put(s, 0);
		}

		// 分组查询、统计订单状态
		String sql = "select count(0) num,status  from es_order" + " where disabled = 0 group by status";
		List<Map<String, Integer>> list = this.daoSupport.queryForList(sql, new RowMapper() {

			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				Map<String, Integer> map = new HashMap<String, Integer>();
				map.put("status", rs.getInt("status"));
				map.put("num", rs.getInt("num"));
				return map;
			}
		});
		//
		// // 将list转为map
		for (Map<String, Integer> state : list) {
			stateMap.put(this.getStateString(state.get("status")), state.get("num"));
		}

		sql = "select count(0) num  from es_order where disabled = 0  and status=0 ";
		int count = this.daoSupport.queryForInt(sql);
		stateMap.put("wait", 0);

		sql = "select count(0) num  from es_order where disabled = 0  ";
		sql += " and ( ( payment_type!='cod' and  status=" + OrderStatus.ORDER_NOT_PAY + ") ";// 非货到付款的，未付款状态的可以结算
		sql += " or ( payment_type!='cod' and  status=" + OrderStatus.PAY_PARTIAL_PAYED + ") ";
		sql += " or ( payment_type='cod' and  (status=" + OrderStatus.ORDER_SHIP + " or status=" + OrderStatus.ORDER_ROG
				+ " )  ) )";// 货到付款的要发货或收货后才能结算
		count = this.daoSupport.queryForInt(sql);
		stateMap.put("not_pay", count);

		sql = "select count(0) from es_order where is_cancel=0 and  disabled=0  and ( ( payment_type != 'cod' and status = "
				+ OrderStatus.ORDER_PAY + ") or ( payment_type='cod' and  status = " + OrderStatus.ORDER_CONFIRM + "))";// 待发货订单数量
		count = this.daoSupport.queryForInt(sql);
		stateMap.put("allocation_yes", count);

		return stateMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#export(java.util.
	 * Date, java.util.Date)
	 */
	@Override
	public String export(Date start, Date end) {
		String sql = "select * from es_order where disabled=0 ";
		if (start != null) {
			sql += " and create_time>" + start.getTime();
		}

		if (end != null) {
			sql += "  and create_timecreate_time<" + end.getTime();
		}

		List<Order> orderList = this.daoSupport.queryForList(sql, Order.class);

		// 使用excel导出流量报表
		ExcelUtil excelUtil = new ExcelUtil();

		// 流量报表excel模板在类包中，转为流给excelutil
		InputStream in = FileUtil.getResourceAsStream("com/enation/app/shop/core/service/impl/order.xls");

		excelUtil.openModal(in);
		int i = 1;
		for (Order order : orderList) {

			excelUtil.writeStringToCell(i, 0, order.getSn()); // 订单号
			excelUtil.writeStringToCell(i, 1,
					DateUtil.toString(new Date(order.getCreate_time()), "yyyy-MM-dd HH:mm:ss")); // 下单时间
			excelUtil.writeStringToCell(i, 2, order.getOrderStatus()); // 订单状态
			excelUtil.writeStringToCell(i, 3, "" + order.getOrder_amount()); // 订单总额
			excelUtil.writeStringToCell(i, 4, order.getShip_name()); // 收货人
			excelUtil.writeStringToCell(i, 5, order.getPayStatus()); // 付款状态
			excelUtil.writeStringToCell(i, 6, order.getShipStatus()); // 发货状态
			excelUtil.writeStringToCell(i, 7, order.getShipping_type()); // 配送方式
			excelUtil.writeStringToCell(i, 8, order.getPayment_name()); // 支付方式
			i++;
		}
		// String target= EopSetting.IMG_SERVER_PATH;
		// saas 版导出目录用户上下文目录access文件夹
		String filename = "/order";
		String static_server_path = SystemSetting.getStatic_server_path();
		File file = new File(static_server_path + filename);
		if (!file.exists())
			file.mkdirs();

		filename = filename + "/order" + com.enation.framework.util.DateUtil.getDateline() + ".xls";
		excelUtil.writeToFile(static_server_path + filename);
		String static_server_domain = SystemSetting.getStatic_server_domain();

		return static_server_domain + filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#getMemberOrderNum(
	 * int, int)
	 */
	public int getMemberOrderNum(int member_id, int payStatus) {
		return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_order WHERE member_id=? AND pay_status=?",
				member_id, payStatus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#getItemsByOrderid(
	 * java.lang.Integer)
	 */
	@Override
	public List<Map> getItemsByOrderid(Integer order_id) {
		String sql = "select * from es_order_items where order_id=?";
		return this.daoSupport.queryForList(sql, order_id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#refuseReturn(java.
	 * lang.String)
	 */
	@Override
	public void refuseReturn(String orderSn) {
		this.daoSupport.execute("update es_order set state = -5 where sn = ?", orderSn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#updateOrderPrice(
	 * double, int)
	 */
	@Override
	public void updateOrderPrice(double price, int orderid) {
		this.daoSupport.execute(
				"update es_order set order_amount = order_amount-?,goods_amount = goods_amount- ? where order_id = ?",
				price, price, orderid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#queryLogiNameById(
	 * java.lang.Integer)
	 */
	@Override
	public String queryLogiNameById(Integer logi_id) {
		return   this.daoSupport.queryForString("select name from es_logi_company where id=?",   logi_id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#searchForGuest(int,
	 * int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page searchForGuest(int pageNo, int pageSize, String ship_name, String ship_tel) {
		String sql = "select * from es_order where ship_name=? AND (ship_mobile=? OR ship_tel=?) and member_id is null ORDER BY order_id DESC";
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, Order.class, ship_name, ship_tel,
				ship_tel);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listByStatus(int,
	 * int, int, int)
	 */
	@Override
	public Page listByStatus(int pageNo, int pageSize, int status, int memberid) {
		String filedname = "status";
		if (status == 0) {
			// 等待付款的订单 按付款状态查询
			filedname = " status!=" + OrderStatus.ORDER_CANCELLATION + " AND pay_status";
		}
		String sql = "select * from es_order where " + filedname + "=? AND member_id=? ORDER BY order_id DESC";
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, Order.class, status, memberid);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listByStatus(int,
	 * int, int)
	 */
	@Override
	public Page listByStatus(int pageNo, int pageSize, int memberid) {
		String sql = "";
		if (EopSetting.PRODUCT.equals("b2c")) {
			sql = "select e.sn,e.status,e.pay_status,e.create_time from es_order e where member_id=? ORDER BY order_id DESC";
		} else {
			sql = "select e.sn,e.status,e.pay_status,e.create_time from es_order e where member_id=? and parent_id is not null ORDER BY order_id DESC";
		}

		Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, Order.class, memberid);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listByStatus(int,
	 * int)
	 */
	@Override
	public List<Order> listByStatus(int status, int memberid) {
		String filedname = "status";
		if (status == 0) {
			// 等待付款的订单 按付款状态查询
			filedname = " status!=" + OrderStatus.ORDER_CANCELLATION + " AND pay_status";
		}
		String sql = "select * from es_order where " + filedname + "=? AND member_id=? ORDER BY order_id DESC";

		return this.daoSupport.queryForList(sql, status, memberid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#getMemberOrderNum(
	 * int)
	 */
	@Override
	public int getMemberOrderNum(int member_id) {
		return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_order WHERE member_id=?", member_id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#search(int,
	 * int, int, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, int, java.lang.Integer)
	 */
	@Override
	public Page search(int pageNO, int pageSize, int disabled, String sn, String logi_no, String uname,
			String ship_name, int status, Integer paystatus) {

		StringBuffer sql = new StringBuffer("select * from es_order where disabled=?  ");
		if (status != -100) {
			if (status == -99) {
				/*
				 * 查询未处理订单
				 */
				sql.append(" and ((payment_type='cod' and status=0 )  or (payment_type!='cod' and status=1 )) ");
			} else
				sql.append(" and status = " + status + " ");

		}
		if (paystatus != null && paystatus != -100) {
			sql.append(" and pay_status = " + paystatus + " ");
		}

		if (!StringUtil.isEmpty(sn)) {
			sql.append(" and sn = '" + sn + "' ");
		}
		if (!StringUtil.isEmpty(uname)) {
			sql.append(" and member_id  in ( SELECT  member_id FROM es_member where disabled!=1 and uname = '" + uname
					+ "' )  ");
		}
		if (!StringUtil.isEmpty(ship_name)) {
			sql.append(" and  ship_name = '" + ship_name + "' ");
		}
		if (!StringUtil.isEmpty(logi_no)) {
			sql.append(" and order_id in (SELECT order_id FROM es_delivery where logi_no = '" + logi_no + "') ");
		}
		sql.append(" order by create_time desc ");
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNO, pageSize, Order.class, disabled);
		return page;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#search(int,
	 * int, int, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, int)
	 */
	@Override
	public Page search(int pageNO, int pageSize, int disabled, String sn, String logi_no, String uname,
			String ship_name, int status) {
		return search(pageNO, pageSize, disabled, sn, logi_no, uname, ship_name, status, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#getNext(java.lang.
	 * String, java.lang.Integer, java.lang.Integer, int, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Order getNext(String next, Integer orderId, Integer status, int disabled, String sn, String logi_no,
			String uname, String ship_name) {
		StringBuffer sql = new StringBuffer("select * from es_order where  1=1  ");

		StringBuffer depotsql = new StringBuffer("  ");
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		if (adminUser.getFounder() != 1) { // 非超级管理员加过滤条件
			boolean isShiper = permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_admin")); // 检测是否是发货员

			boolean haveOrder = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("order"));// 订单管理员权限
			if (isShiper && !haveOrder) {
				DepotUser depotUser = (DepotUser) adminUser;
				int depotid = depotUser.getDepotid();
				depotsql.append(" and depotid=" + depotid + "  ");
			}
		}

		StringBuilder sbsql = new StringBuilder("  ");
		if (status != null && status != -100) {
			sbsql.append(" and status = " + status + " ");
		}
		// if (!StringUtil.isEmpty(sn)) {
		// sbsql.append(" and sn = '" + sn.trim() + "' ");
		// }
		if (!StringUtil.isEmpty(uname) && !uname.equals("undefined")) {
			sbsql.append(" and member_id  in ( SELECT  member_id FROM es_member where disabled!=1 and uname = '" + uname
					+ "' )  ");
		}
		if (!StringUtil.isEmpty(ship_name)) {
			sbsql.append("  and  ship_name = '" + ship_name.trim() + "'  ");
		}
		if (!StringUtil.isEmpty(logi_no) && !logi_no.equals("undefined")) {
			sbsql.append("  and order_id in (SELECT order_id FROM es_delivery where logi_no = '" + logi_no + "')  ");
		}
		if (next.equals("previous")) {
			sql.append("  and order_id IN (SELECT CASE WHEN SIGN(order_id - " + orderId
					+ ") < 0 THEN MAX(order_id)  END AS order_id FROM es_order WHERE order_id <> " + orderId
					+ depotsql.toString() + " and disabled=? " + sbsql.toString() + " GROUP BY SIGN(order_id - "
					+ orderId + ") ORDER BY SIGN(order_id - " + orderId + "))   ");
			// TODO MAX 及SIGN 函数经试验均可在mysql及oracle中通过，但mssql未验证
		} else if (next.equals("next")) {
			sql.append("  and  order_id in (SELECT CASE WHEN SIGN(order_id - " + orderId
					+ ") > 0 THEN MIN(order_id) END AS order_id FROM es_order WHERE order_id <> " + orderId
					+ depotsql.toString() + " and disabled=? " + sbsql.toString() + " GROUP BY SIGN(order_id - "
					+ orderId + ") ORDER BY SIGN(order_id - " + orderId + "))   ");
		} else {
			return null;
		}
		sql.append(" order by create_time desc ");
		//// System.out.println(sql);
		Order order = (Order) this.daoSupport.queryForObject(sql.toString(), Order.class, disabled);
		return order;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#delItem(java.lang.
	 * Integer, java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean delItem(Integer itemid, Integer itemnum) {// 删除订单货物
		OrderItem item = this.getOrderItem(itemid);
		Order order = this.get(item.getOrder_id());
		boolean flag = false;
		int paymentid = order.getPayment_id();
		int status = order.getStatus();
		if ((paymentid == 1 || paymentid == 3 || paymentid == 4 || paymentid == 5)
				&& (status == 0 || status == 1 || status == 2 || status == 3 || status == 4)) {
			flag = true;
		}
		if ((paymentid == 2) && (status == 0 || status == 9 || status == 3 || status == 4)) {
			flag = true;
		}
		if (flag) {
			try {
				if (itemnum.intValue() <= item.getNum().intValue()) {
					Goods goods = goodsManager.getGoods(item.getGoods_id());
					double order_amount = order.getOrder_amount();
					double itemprice = item.getPrice().doubleValue() * itemnum.intValue();
					double leftprice = CurrencyUtil.sub(order_amount, itemprice);
					int difpoint = (int) Math.floor(leftprice);
					Double[] dlyprice = this.dlyTypeManager.countPrice(order.getShipping_id(),
							order.getWeight() - (goods.getWeight().doubleValue() * itemnum.intValue()), leftprice,
							order.getShip_regionid().toString());
					double sumdlyprice = dlyprice[0];
					this.daoSupport.execute(
							"update es_order set goods_amount = goods_amount- ?,shipping_amount = ?,order_amount =  ?,weight =  weight - ?,gainedpoint =  ? where order_id = ?",
							itemprice, sumdlyprice, leftprice, (goods.getWeight().doubleValue() * itemnum.intValue()),
							difpoint, order.getOrder_id());
					this.daoSupport.execute("update es_freeze_point set mp =?,point =?  where orderid = ? and type = ?",
							difpoint, difpoint, order.getOrder_id(), "buygoods");
					if (itemnum.intValue() == item.getNum().intValue()) {
						this.daoSupport.execute("delete from order_items where item_id = ?", itemid);
					} else {
						this.daoSupport.execute("update es_order_items set num = num - ? where item_id = ?",
								itemnum.intValue(), itemid);
					}

				} else {
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}
		return flag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#saveAddrDetail(java
	 * .lang.String, int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.ORDER,detail="修改订单ID为${orderid}的详细配送地址")
	public boolean saveAddrDetail(String addr, int orderid) {
		if (addr == null || StringUtil.isEmpty(addr)) {
			return false;
		} else {
			this.daoSupport.execute("update es_order set ship_addr=?  where order_id=?", addr, orderid);
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#saveShipInfo(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, int)
	 */
	@Override
	public boolean saveShipInfo(String remark, String ship_day, String ship_name, String ship_tel, String ship_mobile,
			String ship_zip, int orderid) {
		Order order = this.get(orderid);
		try {
			if (ship_day != null && !StringUtil.isEmpty(ship_day)) {
				this.daoSupport.execute("update es_order set ship_day=?  where order_id=?", ship_day, orderid);
				if (remark != null && !StringUtil.isEmpty(remark) && !remark.equals("undefined")) {
					StringBuilder sb = new StringBuilder("");
					sb.append("【配送时间：");
					sb.append(remark.trim());
					sb.append("】");
					this.daoSupport.execute(
							"update es_order set remark= concat(remark,'" + sb.toString() + "')   where order_id=?",
							orderid);
				}
				return true;
			}
			if (ship_name != null && !StringUtil.isEmpty(ship_name)) {
				this.daoSupport.execute("update es_order set ship_name=?  where order_id=?", ship_name, orderid);
				return true;
			}
			if (ship_tel != null && !StringUtil.isEmpty(ship_tel)) {
				this.daoSupport.execute("update es_order set ship_tel=?  where order_id=?", ship_tel, orderid);
				return true;
			}
			if (ship_mobile != null && !StringUtil.isEmpty(ship_mobile)) {
				this.daoSupport.execute("update es_order set ship_mobile=?  where order_id=?", ship_mobile, orderid);
				return true;
			}
			if (ship_zip != null && !StringUtil.isEmpty(ship_zip)) {
				this.daoSupport.execute("update es_order set ship_zip=?  where order_id=?", ship_zip, orderid);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#updatePayMethod(
	 * int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void updatePayMethod(int orderid, int payid, String paytype, String payname) {

		this.daoSupport.execute("update es_order set payment_id=?,payment_type=?,payment_name=? where order_id=?",
				payid, paytype, payname, orderid);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.javashop.core.service.IOrderManager#checkProInOrder(int)
	 */
	@Override
	public boolean checkProInOrder(int productid) {
		String sql = "select count(0) from es_order_items where product_id=?";
		return this.daoSupport.queryForInt(sql, productid) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.javashop.core.service.IOrderManager#checkGoodsInOrder(int)
	 */
	@Override
	public boolean checkGoodsInOrder(int goodsid) {
		String sql = "select count(0) from es_order_items oi inner join es_order o ON o.order_id=oi.order_id WHERE oi.goods_id=? AND o.status!=?";
		return this.daoSupport.queryForInt(sql, goodsid, OrderStatus.ORDER_CANCELLATION) > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listByOrderIds(java
	 * .lang.Integer[], java.lang.String)
	 */
	@Override
	public List listByOrderIds(Integer[] orderids, String order) {
		try {
			StringBuffer sql = new StringBuffer("select * from es_order where disabled=0 ");

			if (orderids != null && orderids.length > 0)
				sql.append(" and order_id in (" + StringUtil.arrayToString(orderids, ",") + ")");

			if (StringUtil.isEmpty(order)) {
				order = "create_time desc";
			}
			sql.append(" order by  " + order);
			return this.daoSupport.queryForList(sql.toString(), Order.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#list(int, int,
	 * int, java.lang.String)
	 */
	@Override
	public Page list(int pageNo, int pageSize, int disabled, String order) {

		StringBuffer sql = new StringBuffer("select * from es_order where disabled=? ");

		AdminUser adminUser = UserConext.getCurrentAdminUser();
		if (adminUser.getFounder() != 1) { // 非超级管理员加过滤条件
			boolean isShiper = permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_ship")); // 检测是否是发货员
			boolean haveOrder = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("order"));// 订单管理员权限
			if (isShiper && !haveOrder) {
				DepotUser depotUser = (DepotUser) adminUser;
				int depotid = depotUser.getDepotid();
				sql.append(" and depotid=" + depotid);
			}
		}

		order = StringUtil.isEmpty(order) ? "order_id desc" : order;
		sql.append(" order by " + order);
		Page webpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, disabled);
		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#orderStatusNum(java
	 * .lang.Integer)
	 */
	@Override
	public Integer orderStatusNum(Integer status) {
		Member member = UserConext.getCurrentMember();
		String sql = "select count(0) from es_order where status =? and member_id=?";
		return this.daoSupport.queryForInt(sql, status, member.getMember_id());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#list(int, int,
	 * int, int, java.lang.String)
	 */
	@Override
	public Page list(int pageNo, int pageSize, int status, int depotid, String order) {
		order = StringUtil.isEmpty(order) ? "order_id desc" : order;
		String sql = "select * from es_order where disabled=0 and status=" + status;
		if (depotid > 0) {
			sql += " and depotid=" + depotid;
		}
		sql += " order by " + order;
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize, Order.class);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#listOrder(java.util
	 * .Map, int, int, java.lang.String, java.lang.String)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public Page listOrder(Map map, int page, int pageSize, String other, String order) {
		// this.cancelOrder();
		String sql = createTempSql(map, other, order)+ " order by create_time desc";
		Page webPage = this.daoSupport.queryForPage(sql, page, pageSize);
		orderPluginBundle.filterOrderPage(webPage);
		return webPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#saveDepot(int,
	 * int)
	 */
	@Override
	@Log(type=LogType.ORDER,detail="修改订单ID为${orderid}所在库存")
	public void saveDepot(int orderid, int depotid) {
		this.orderPluginBundle.onOrderChangeDepot(this.get(orderid), depotid, this.listGoodsItems(orderid));
		this.daoSupport.execute("update es_order set depotid=?  where order_id=?", depotid, orderid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#savePayType(int,
	 * int)
	 */
	@Override
	@Log(type=LogType.ORDER,detail="修改订单ID为${orderid}的支付方式")
	public void savePayType(int orderid, int paytypeid) {
		PayCfg cfg = this.paymentManager.get(paytypeid);
		String typename = cfg.getName();
		String paytype = cfg.getType();
		this.daoSupport.execute("update es_order set payment_id=?,payment_name=?,payment_type=? where order_id=?",
				paytypeid, typename, paytype, orderid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#saveShipType(int,
	 * int)
	 */
	@Override
	@Log(type=LogType.ORDER,detail="修改订单ID为${orderid}的配送方式")
	public void saveShipType(int orderid, int shiptypeid) {
		String typename = this.dlyTypeManager.getDlyTypeById(shiptypeid).getName();
		this.daoSupport.execute("update es_order set shipping_id=?,shipping_type=? where order_id=?", shiptypeid,
				typename, orderid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#add(com.enation.app
	 * .shop.core.order.model.Order)
	 */
	@Override
	public void add(Order order) {
		this.daoSupport.insert("es_order", order);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#saveAddr(int,
	 * int, int, int, java.lang.String)
	 */
	@Override
	@Log(type=LogType.ORDER,detail="修改订单ID为${orderId}的配送地区")
	public void saveAddr(int orderId, int ship_provinceid, int ship_cityid, int ship_regionid, String Attr) {
		this.daoSupport.execute(
				"update es_order set ship_provinceid=?,ship_cityid=?,ship_regionid=?,ship_townid= null,shipping_area=? where order_id=?",
				ship_provinceid, ship_cityid, ship_regionid, Attr, orderId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#getOrderGoodsNum(
	 * int)
	 */
	@Override
	public Integer getOrderGoodsNum(int order_id) {
		String sql = "select count(0) from es_order_items where order_id =?";
		return this.daoSupport.queryForInt(sql, order_id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#getOrderItemDetail(
	 * int)
	 */
	@Override
	public List getOrderItemDetail(int item_id) {
		String sql = "SELECT c.*,g.mktprice from es_order_item_child c INNER JOIN es_goods g ON g.goods_id=c.goodsid where itemid=? ORDER BY c.goodsid";
		return this.daoSupport.queryForList(sql, item_id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#getOrderByMemberid(
	 * java.lang.String, java.lang.Integer)
	 */
	@Override
	public boolean getOrderByMemberid(String sn, Integer memberid) {
		boolean flag = false;
		String sql = "select count(0) from es_order where member_id=? and sn=?";
		Integer num = this.daoSupport.queryForInt(sql, memberid, sn);
		if (num == 1) {
			flag = true;
		}
		return flag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#checkCod(java.lang.
	 * String)
	 */
	@Override
	public int checkCod(String regionList) {
		int result = 0;
		String[] ary = regionList.split(",");
		for (int i = 0; i < ary.length; i++) {
			Regions reg = this.regionsManager.get(Integer.parseInt(ary[i]));
			if (reg.getCod() == 0) {
				result += 1;
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#getRecordList(java.
	 * lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page getRecordList(Integer goods_id, Integer pageNo, Integer pageSize) {

		// 获取交易记录
		String sql = "select * from  es_transaction_record where goods_id=? order by record_id";
		List list = daoSupport.queryForListPage(sql, pageNo, pageSize, goods_id);

		// 获取总交易量
		sql="select count(*) from  es_transaction_record where goods_id=?";

		Integer count = daoSupport.queryForInt(sql, goods_id);
		Page page = new Page(pageNo, count, pageSize, list);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#
	 * getCancelApplicationList(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page getCancelApplicationList(Integer pageNo, Integer pageSize) {
		String sql = "select * from es_order where is_cancel=1 order by order_id desc ";
		return this.daoSupport.queryForPage(sql, pageNo, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#
	 * authCancelApplication(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void authCancelApplication(Integer order_id, Integer status) {
		Order order = this.get(order_id);
		if (order.getStatus() == OrderStatus.ORDER_SHIP) {
			this.daoSupport.execute("update es_order set is_cancel=0 where order_id=?", order_id);
			throw new RuntimeException("订单已发货无法取消订单！");
		}
		// 如果审核通过
		if (status == 1) {

			IOrderFlowManager flowManager = SpringContextHolder.getBean("orderFlowManager");
			flowManager.cancel(order_id, order.getCancel_reason());

			// 取消订单如果为已付款订单创建退款单
			if (order.getPay_status() == OrderStatus.PAY_YES) {
				// 创建退款单
				Refund refund = new Refund();
				refund.setCreate_time(DateUtil.getDateline());
				refund.setSn(DateUtil.toString(DateUtil.getDateline(), "yyMMddhhmmss"));
				refund.setRefund_money(order.getOrder_amount());
				refund.setRefund_way(order.getPayment_name());
				refund.setMember_id(order.getMember_id());
				refund.setMember_name(memberManager.get(order.getMember_id()).getUname());
				refund.setOrder_id(order_id);
				refund.setStatus(0);
				refundManager.addRefund(refund);
			}

			// 执行取消订单事件
//			this.orderPluginBundle.onCanel(order);
		} else {
			this.addLog(order_id, "拒绝取消订单申请");
		}
		this.daoSupport.execute("update es_order set is_cancel=0 where order_id=?", order_id);
	}
    //后台取消订单直接取消
	@Override
	@Log(type=LogType.ORDER,detail="取消ID为${order_id}的订单")
	public void addCancelApplicationAdmin(Integer order_id,String reason) {
		this.daoSupport.execute("UPDATE es_order SET status=6,cancel_reason=? WHERE order_id=?",reason, order_id);
		//触发插件桩
		this.orderPluginBundle.onCanel(this.get(order_id));
		this.addLog(order_id, "申请取消订单", "管理员:"+UserConext.getCurrentAdminUser().getUsername());
	}

	/**
	 * 
	 * @param orderId
	 * @param disabled
	 * @return
	 */
	private boolean exec(Integer[] orderId, int disabled) {
		if (cheack(orderId)) {
			String ids = StringUtil.arrayToString(orderId, ",");
			String sql = "update es_order set disabled = ? where order_id in (" + ids + ")";
			this.daoSupport.execute(sql, disabled);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param orderId
	 * @return
	 */
	private boolean cheack(Integer[] orderId) {
		boolean i = true;
		for (int j = 0; j < orderId.length; j++) {
			if (this.daoSupport.queryForInt("select status from es_order where order_id=?",
					orderId[j]) != OrderStatus.ORDER_CANCELLATION) {
				i = false;
			}
		}
		return i;
	}

	/**
	 * 根据订单状态值获取状态字串，如果状态值不在范围内反回null。
	 * 
	 * @param state
	 * @return
	 */
	private String getStateString(Integer state) {
		String str = null;
		switch (state.intValue()) {
		case -2:
			str = "cancel_ship";
			break;
		case -1:
			str = "cancel_pay";
			break;
		case 1:
			str = "pay";
			break;
		case 2:
			str = "ship";
			break;
		case 4:
			str = "allocation_yes";
			break;
		case 5:
			str = "complete";
			break;
		default:
			str = null;
			break;
		}
		return str;
	}

	/**
	 * 获取订单中商品的总价格
	 * 
	 * @param sessionid
	 * @return
	 */
	private double getOrderTotal(String sessionid) {
		List goodsItemList = cartManager.listGoods(sessionid);
		double orderTotal = 0d;
		if (goodsItemList != null && goodsItemList.size() > 0) {
			for (int i = 0; i < goodsItemList.size(); i++) {
				CartItem cartItem = (CartItem) goodsItemList.get(i);
				orderTotal += cartItem.getCoupPrice() * cartItem.getNum();
			}
		}
		return orderTotal;
	}

	/**
	 * 
	 * @param itemid
	 * @return
	 */
	private OrderItem getOrderItem(Integer itemid) {
		return (OrderItem) this.daoSupport.queryForObject("select * from es_order_items where item_id = ?",
				OrderItem.class, itemid);
	}

	/**
	 * 检查订单是否过期，若已过期，将其状态置为取消 添加人：DMRain 2015-12-08
	 */
	// private void cancelOrder(){
	// String sql = "select * from es_order where status = 0 and pay_status = 0
	// and ship_status = 2 and payment_type != 'cod'";
	// List<Order> list = this.daoSupport.queryForList(sql, Order.class);
	//
	// if(list != null){
	// for(Order order : list){
	// long createTime = order.getCreate_time();
	// long nowTime = DateUtil.getDateline();
	// if((nowTime - createTime) > 259200){
	// Integer order_id = order.getOrder_id();
	// this.daoSupport.execute("update es_order set status = 8,cancel_reason =
	// '订单过期，系统自动将其取消' where order_id = ?", order_id);
	//
	// //响应订单取消事件 add by DMRain 2016-4-22
	// this.orderPluginBundle.onCanel(this.get(order_id));
	//
	// //添加订单日志
	// this.addLog(order_id, "订单过期，系统自动将其取消");
	// }
	// }
	// }
	// }

	/**
	 * 
	 * @param map
	 * @param other
	 * @param order
	 * @return
	 */
	private String createTempSql(Map map, String other, String order) {

		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String orderstate = (String) map.get("order_state");// 订单状态
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		Integer status = (Integer) map.get("status");
		String sn = (String) map.get("sn");
		String ship_name = (String) map.get("ship_name");
		Integer paystatus = (Integer) map.get("paystatus");
		Integer shipstatus = (Integer) map.get("shipstatus");
		Integer shipping_type = (Integer) map.get("shipping_type");
		Integer payment_id = (Integer) map.get("payment_id");
		Integer depotid = (Integer) map.get("depotid");
		String complete = (String) map.get("complete");

		StringBuffer sql = new StringBuffer();
		sql.append(
				"select o.*, m.uname from es_order o left join es_member m on o.member_id = m.member_id where o.disabled=0");

		if (stype != null && keyword != null) {
			if (stype == 0) {
				sql.append(" and (o.sn like '%" + keyword + "%'");
				sql.append(" or o.ship_name like '%" + keyword + "%')");
			}
		}

		if (status != null && status != 99) {
			sql.append(" and o.status=" + status);
		} /*
			 * else{ sql.append(" and o.status!=8"); }
			 */

		if (sn != null && !StringUtil.isEmpty(sn)) {
			sql.append(" and o.sn like '%" + sn + "%'");
		}

		if (ship_name != null && !StringUtil.isEmpty(ship_name)) {
			sql.append(" and o.ship_name like '" + ship_name + "'");
		}

		if (paystatus != null) {
			sql.append(" and o.pay_status=" + paystatus);
		}

		if (shipstatus != null) {
			sql.append(" and o.ship_status=" + shipstatus);
		}

		if (shipping_type != null) {
			sql.append(" and o.shipping_id=" + shipping_type);
		}

		if (payment_id != null) {
			sql.append(" and o.payment_id=" + payment_id);
		}

		if (depotid != null && depotid > 0) {
			sql.append(" and o.depotid=" + depotid);
		}

		if (start_time != null && !StringUtil.isEmpty(start_time)) {
			long stime = com.enation.framework.util.DateUtil.getDateline(start_time + " 00:00:00",
					"yyyy-MM-dd HH:mm:ss");
			sql.append(" and o.create_time>" + stime);
		}
		if (end_time != null && !StringUtil.isEmpty(end_time)) {
			long etime = com.enation.framework.util.DateUtil.getDateline(end_time + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and o.create_time<" + etime);
		}
		if (!StringUtil.isEmpty(orderstate)) {
			if (orderstate.equals("wait_ship")) { // 对待发货的处理
				sql.append(" and is_cancel=0 and ( ( payment_type!='cod'  and  status=" + OrderStatus.ORDER_PAY + ") ");// 非货到付款的，要已结算才能发货
				sql.append(" or ( payment_type='cod' and  status=" + OrderStatus.ORDER_CONFIRM + ")) ");// 货到付款的，已确认就可以发货
			} else if (orderstate.equals("wait_pay")) {
				sql.append(" and ( ( payment_type!='cod' and  status=" + OrderStatus.ORDER_CONFIRM + ") ");// 非货到付款的，未付款状态的可以结算
				sql.append(" or ( payment_type='cod' and   status=" + OrderStatus.ORDER_ROG + "  ) )");// 货到付款的要发货或收货后才能结算
			} else if (orderstate.equals("wait_rog")) {
				sql.append(" and o.status=" + OrderStatus.ORDER_SHIP);
			} else {
				sql.append(" and o.status=" + orderstate);
			}

		}

		if (!StringUtil.isEmpty(complete)) {
			sql.append(" and o.status=" + OrderStatus.ORDER_COMPLETE);
		}
		if(!StringUtil.isEmpty(order)){
			sql.append(" ORDER BY o." + other + " " + order);
		}

		// System.out.println(sql.toString());
		return sql.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.order.service.IOrderManager#getOrderByProductId
	 * (java.lang.Integer)
	 */
	@Override
	public String getOrderByProductId(Integer productid) {
		String sql = "select * from es_order_items where product_id=?";
		return this.daoSupport.queryForMap(sql, productid).get("order_id").toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.enation.app.shop.core.order.service.IOrderManager#
	 * getItemsByOrderidAndGoodsList(int, java.util.List)
	 */
	@Override
	public List<Map> getItemsByOrderidAndGoodsList(int orderid, List<SellBackGoodsList> goodsList) {
		// jdbctemplate 条件语句 where x in (?)这么传参有问题
		String sql = "select * from es_order_items where order_id=? and goods_id in (";
		int i = 0;
		StringBuffer goods_ids = new StringBuffer();
		for (SellBackGoodsList sellBackGoodsList : goodsList) {
			goods_ids.append(sellBackGoodsList.getGoods_id());
			i++;
			if (i != goodsList.size()) {
				goods_ids.append(",");
			}
		}
		sql += goods_ids + ")";
		return this.daoSupport.queryForList(sql, orderid);
	}

	/**
	 * 获取订单货物规格信息
	 * 
	 * @param itemList
	 * @return
	 */
	private List getAddr(List<Map> itemList) {
		for (Map item : itemList) {
			if (item.get("addon") != null) {
				String addon = item.get("addon").toString();
				if (!StringUtil.isEmpty(addon)) {
					JSONArray specArray = JSONArray.fromObject(addon);
					List<Map> specList = (List) JSONArray.toCollection(specArray, Map.class);
					FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
					freeMarkerPaser.setClz(this.getClass());
					freeMarkerPaser.putData("specList", specList);
					freeMarkerPaser.setPageName("order_item_spec");
					String html = freeMarkerPaser.proessPageContent();
					item.put("other", html);
				}
			}

		}
		return itemList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderManager#cancel(java.lang.Integer, java.lang.String)
	 */
	@Override
	public void cancel(Integer order_id, String reason) {
		Order order = this.get(order_id);
		if(order == null){
			throw new RuntimeException("对不起，此订单不存在！");
		}
		if(order.getStatus() == null || order.getStatus().intValue()>OrderStatus.ORDER_PAY){
			throw new RuntimeException("对不起，此订单不能取消！");
		}
		
		Member member = UserConext.getCurrentMember();
		if(order.getMember_id().intValue() !=member.getMember_id().intValue()){
			throw new RuntimeException("对不起，您没有权限进行此项操作！");
		}
		order.setStatus(OrderStatus.ORDER_CANCELLATION);
		order.setCancel_reason(reason);
		this.edit(order);
		
		
		//记录日志
		if(member==null){
			this.addLog(order.getOrder_id(), "取消订单", member.getUname());
		}else{
			this.addLog(order.getOrder_id(), "取消订单", member.getUname());
		}

		this.orderPluginBundle.onCanel(order);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IOrderManager#saveAddrAndTown(int, int, int, int, int, java.lang.String)
	 */
	@Override
	public void saveAddrAndTown(int orderId, int ship_provinceid, int ship_cityid, int ship_regionid, int town_id,
			String Attr) {
		this.daoSupport.execute(
				"update es_order set ship_provinceid=?,ship_cityid=?,ship_regionid=?,ship_townid=?,shipping_area=? where order_id=?",
				ship_provinceid, ship_cityid, ship_regionid,town_id, Attr, orderId);
		
	}

	@Override
	public List<Order> getChildOrders(Integer orderid) {
		String sql = "select * from es_order where parent_id = ?";
		
		return daoSupport.queryForList(sql,Order.class,orderid);
	}



}
