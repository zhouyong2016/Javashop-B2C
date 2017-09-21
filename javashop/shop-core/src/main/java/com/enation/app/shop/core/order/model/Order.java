package com.enation.app.shop.core.order.model;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.order.model.support.OrderPrice;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.framework.database.DynamicField;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

/**
 * 订单实体
 * @author kingapex
 *2010-4-6上午11:11:27
 */
public class Order extends DynamicField implements java.io.Serializable,PayEnable {

	private Integer order_id;	//订单Id

	private String sn;			//订单编号

	private Integer member_id;	//会员Id

	private Integer status;		//订单状态

	private Integer pay_status;	//付款状态

	private Integer ship_status;	//货运状态
	
	//状态显示字串
	private String shipStatus;	//货运状态字符串显示
	private String payStatus;	//付款状态字符串显示
	private String orderStatus;	//订单状态字符串显示
	
	
	//收货地区id三级省市的最后一级
	private Integer regionid; 	//地区Id
	private Integer shipping_id;//配送方式

	private String shipping_type;	//配送方式名称

	private String shipping_area;	//配送地址

	private String goods;

	private Long create_time;	//创建时间

	private String ship_name;	//收货人

	private String ship_addr;	//收货地址

	private String ship_zip;		//邮政编码

	private String ship_email;	//收获人邮箱

	private String ship_mobile;	//收货人手机号

	private String ship_tel;		//收货人电话

	private String ship_day;		//收货时间（工作日／周六日）

	private String ship_time;	//发货时间

	private Integer is_protect;

	private Double protect_price;

	private Double goods_amount;	//商品价格

	private Double shipping_amount;	//配送费用
	private Double discount; //优惠金额
	private Double order_amount;		//订单价格

	private Double weight;	//订单重量
	
	private Double paymoney;	//付款金额

	private String remark;	//订单备注
	
	private Integer disabled;	//关闭
	
	/**
	 * 付款方式Id
	 */
	private Integer payment_id;		
	
	/**
	 * 付款方式名称
	 */
	private String payment_name;	
	
	/**
	 * 付款方式
	 */
	private String payment_type;	
	
	/**
	 * 付款账号
	 */
	private String payment_account;	
	
	
	private Integer goods_num;	//商品数量
	private int gainedpoint;	//赠送的积分
	private int consumepoint;
 
	private Integer depotid; //发货仓库id
	
	private String cancel_reason;	//取消订单的原因
	
	private int sale_cmpl;
	private Long sale_cmpl_time;
	
	private Integer ship_provinceid; //收货地址省Id
	private Integer ship_cityid;		//收货地址市Id
	private Integer ship_regionid;	//收货地址区Id
	private Integer ship_townid;	//收货地址街道Id
	/**
	 * 签收时间
	 */
	private Long signing_time;		
	private String the_sign;		//签收人姓名
	
	private Long allocation_time;
	
	private String admin_remark;
	private Integer address_id;//地址id
	
	
	//订单的价格，非数据库字段
	private OrderPrice orderprice;
	
	//会员地址，非数据库字段
	private MemberAddress memberAddress;
	
	
	//订单项列表，非数据库字段
	private List<OrderItem> orderItemList;
	
	//2013年0808新增订单还需要支付多少钱的字段-kingapex
	private Double need_pay_money;
	private String ship_no; //发货单
	
	//2015-05-21新增货物列表json字段-by kingapex
	private String items_json; 
	
	private Integer logi_id;		//物流公司Id
	
	private String logi_name;	//物流公司名称
	
	private Integer is_online;	//是否在线支付，1为在线支付，0其它支付方式。-xulipeng
	
	/**
	 * 促销活动赠品ID 0：代表没有赠品
	 * add by DMRain 2016-1-15
	 */
	private Integer gift_id;
	
	/**
	 * 促销活动优惠券ID 0：代表没有优惠券
	 * add by DMRain 2016-1-20
	 */
	private Integer bonus_id;
	
	/**
	 * 促销活动优惠金额
	 * add by DMRain 2016-6-8
	 */
	private Double act_discount;
	/**
	 * 是否申请取消订单
	 * add by kanon 2016-6-13
	 */
	private Integer is_cancel;	//1:申请取消订单
	
	/**
	 * 促销活动赠送的积分
	 * add by DMRain 2016-7-22
	 */
	private Integer activity_point;
	
	//非数据库字段，订单商品列表
	private List<Map<String, Object>> itemList;
	//非数据库字段，会员名称
	private String uname;
	//非数据库字段  订单所需总积分 add by jianghongyan
	private Integer order_exchange_point;
	
	private Integer is_comment; //1：订单有商品需要评论
	
	
	
	/**
	 * 增加发票字段
	 */
	/** 是否需要发票 */
	private Integer receipt; // 1 需要 2 不需要
	/** 发票抬头 */
	private String receipt_title;
	/** 发票类型 */
	private Integer receipt_type;  // 1 个人 2 单位
	/** 发票税号 */
	private String receipt_duty;
	/** 发票内容 */
	private String receipt_content;
	/**
	 * 获取订单积分
	 */
	@NotDbField
	public Integer getOrder_exchange_point() {
		Integer sum=0;
		List list=this.getItemList();
		if(list!=null){
			for (Object object : this.getItemList()) {
				OrderItem orderItem=(OrderItem) object;
				Integer point=orderItem.getExchange_point();
				if(point!=null && point!=0){
					Integer itemNum=orderItem.getNum();
					sum+=point*itemNum;
				}
			}
		}
		return sum;
	}



	public void setOrder_exchange_point(Integer order_exchange_point) {
		this.order_exchange_point = order_exchange_point;
	}



	/**
	 * 订单价格设置<br>
	 * 根据订单价格设置此订单的商品价、运费、重量、优惠金额等
	 * @param orderPrice
	 */
	public void setOrderprice(OrderPrice orderPrice) {
		this.setGoods_amount( orderPrice.getGoodsPrice());
		this.setWeight(orderPrice.getWeight());		
		this.setOrder_amount(orderPrice.getOrderPrice());
		this.setProtect_price(0d);
		this.setShipping_amount(orderPrice.getShippingPrice());
		this.setGainedpoint(orderPrice.getPoint());
		this.setDiscount(orderPrice.getDiscountPrice());
		this.setNeed_pay_money(orderPrice.getNeedPayMoney());
		this.setGift_id(orderPrice.getGift_id());	//添加赠品ID add by DMRain 2016-1-15
		this.setBonus_id(orderPrice.getBonus_id());	//添加优惠券ID add by DMRain 2016-1-20
		this.setAct_discount(orderPrice.getActDiscount()); //添加促销活动优惠的现金金额 add by DMRain 2016-6-20
		this.setActivity_point(orderPrice.getActivity_point());	//添加促销活动赠送的积分 add by DMRain 2016-7-22
		this.orderprice = orderPrice;
	}
	
	
	
	public Long getAllocation_time() {
		return allocation_time;
	}

	public void setAllocation_time(Long allocation_time) {
		this.allocation_time = allocation_time;
	}

	public Integer getGoods_num() {
		return goods_num;
	}

	public void setGoods_num(Integer goodsNum) {
		goods_num = goodsNum;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public String getGoods() {
		return goods;
	}

	public void setGoods(String goods) {
		this.goods = goods;
	}

	public Double getGoods_amount() {
		return goods_amount;
	}

	public void setGoods_amount(Double goods_amount) {
		this.goods_amount = goods_amount;
	}

	public Integer getIs_protect() {
		is_protect =is_protect==null?0:is_protect;
		return is_protect;
	}

	public void setIs_protect(Integer is_protect) {
		this.is_protect = is_protect;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public Double getOrder_amount() {
		
		return order_amount==null?0:order_amount;
	}

	public void setOrder_amount(Double order_amount) {
		this.order_amount = order_amount;
	}

	@PrimaryKeyField
	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	public Integer getPay_status() {
		return pay_status;
	}

	public void setPay_status(Integer pay_status) {
		this.pay_status = pay_status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getShip_addr() {
		return ship_addr;
	}

	public void setShip_addr(String ship_addr) {
		this.ship_addr = ship_addr;
	}

	public String getShip_day() {
		return ship_day;
	}

	public void setShip_day(String ship_day) {
		this.ship_day = ship_day;
	}

	public String getShip_email() {
		return ship_email;
	}

	public void setShip_email(String ship_email) {
		this.ship_email = ship_email;
	}

	public String getShip_mobile() {
		return ship_mobile;
	}

	public void setShip_mobile(String ship_mobile) {
		this.ship_mobile = ship_mobile;
	}

	public String getShip_name() {
		return ship_name;
	}

	public void setShip_name(String ship_name) {
		this.ship_name = ship_name;
	}

	public Integer getShip_status() {
		return ship_status;
	}

	public void setShip_status(Integer ship_status) {
		this.ship_status = ship_status;
	}

	public String getShip_tel() {
		return ship_tel;
	}

	public void setShip_tel(String ship_tel) {
		this.ship_tel = ship_tel;
	}

	public String getShip_time() {
		return ship_time;
	}

	public void setShip_time(String ship_time) {
		this.ship_time = ship_time;
	}

	public String getShip_zip() {
		return ship_zip;
	}

	public void setShip_zip(String ship_zip) {
		this.ship_zip = ship_zip;
	}

	public Double getShipping_amount() {
		return shipping_amount;
	}

	public void setShipping_amount(Double shipping_amount) {
		this.shipping_amount = shipping_amount;
	}

	public String getShipping_area() {
		return shipping_area;
	}

	public void setShipping_area(String shipping_area) {
		this.shipping_area = shipping_area;
	}

	public Integer getShipping_id() {
		return shipping_id;
	}

	public void setShipping_id(Integer shipping_id) {
		this.shipping_id = shipping_id;
	}

	public String getShipping_type() {
		return shipping_type;
	}

	public void setShipping_type(String shipping_type) {
		this.shipping_type = shipping_type;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getProtect_price() {
		return protect_price;
	}

	public void setProtect_price(Double protect_price) {
		this.protect_price = protect_price;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}

	public Integer getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(Integer payment_id) {
		this.payment_id = payment_id;
	}

	public String getPayment_name() {
		return payment_name;
	}

	public void setPayment_name(String payment_name) {
		this.payment_name = payment_name;
	}

	public Double getPaymoney() {
		return paymoney==null?0:paymoney;
	}

	public void setPaymoney(Double paymoney) {
		this.paymoney = paymoney;
	}

	public int getGainedpoint() {
		return gainedpoint;
	}

	public void setGainedpoint(int gainedpoint) {
		this.gainedpoint = gainedpoint;
	}

	public int getConsumepoint() {
		return consumepoint;
	}

	public void setConsumepoint(int consumepoint) {
		this.consumepoint = consumepoint;
	}

	@NotDbField
	public Integer getRegionid() {
		return regionid;
	}

	public void setRegionid(Integer regionid) {
		this.regionid = regionid;
	}

	@NotDbField
	public String getShipStatus() {
		if(ship_status==null) return null;
		shipStatus = OrderStatus.getShipStatusText(ship_status);
		return shipStatus;
	}

	public void setShipStatus(String shipStatus) {
		this.shipStatus = shipStatus;
	}
	
	@NotDbField
	public String getPayStatus() {
 
		if(pay_status==null)return null;
		this.payStatus = OrderStatus.getPayStatusText(pay_status);
		
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	@NotDbField
	public String getOrderStatus() {
		if(status==null) return null;
		orderStatus = OrderStatus.getOrderStatusText(status);
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String paymentType) {
		payment_type = paymentType;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

 

	public Integer getDepotid() {
		return depotid;
	}

	public void setDepotid(Integer depotid) {
		this.depotid = depotid;
	}

	public String getCancel_reason() {
		return cancel_reason;
	}

	public void setCancel_reason(String cancel_reason) {
		this.cancel_reason = cancel_reason;
	}

	public int getSale_cmpl() {
		return sale_cmpl;
	}

	public void setSale_cmpl(int sale_cmpl) {
		this.sale_cmpl = sale_cmpl;
	}

 
	public Integer getShip_provinceid() {
		return ship_provinceid;
	}

	public void setShip_provinceid(Integer ship_provinceid) {
		this.ship_provinceid = ship_provinceid;
	}

	public Integer getShip_cityid() {
		return ship_cityid;
	}

	public void setShip_cityid(Integer ship_cityid) {
		this.ship_cityid = ship_cityid;
	}

	public Integer getShip_regionid() {
		return ship_regionid;
	}

	public void setShip_regionid(Integer ship_regionid) {
		this.ship_regionid = ship_regionid;
	}


	public Long getSale_cmpl_time() {
		return sale_cmpl_time;
	}

	public void setSale_cmpl_time(Long sale_cmpl_time) {
		this.sale_cmpl_time = sale_cmpl_time;
	}

	public Long getSigning_time() {
		return signing_time;
	}

	public void setSigning_time(Long signing_time) {
		this.signing_time = signing_time;
	}

	public String getThe_sign() {
		return the_sign;
	}

	public void setThe_sign(String the_sign) {
		this.the_sign = the_sign;
	}

 

	public String getAdmin_remark() {
		return admin_remark;
	}

	public void setAdmin_remark(String admin_remark) {
		this.admin_remark = admin_remark;
	}

	@NotDbField
	public OrderPrice getOrderprice() {
		//如果价格为空，返回价格为空，但是不为null
		if(orderprice == null ){
			return new OrderPrice();
		}
		return orderprice;
	}

	
	
	/**
	 * 获取此订单是否是货到付款
	 * @return
	 */
	@NotDbField
	public boolean getIsCod(){
		
		if("cod".equals(this.getPayment_type())){
			return true;
		}
		return false;
		
	}
	
	
	/**
	 * 获取此订单是否是在线支付的
	 * @return
	 */
	@NotDbField
	public boolean getIsOnlinePay(){
		if(
		    !"offline".equals(this.payment_type)
		  &&!"deposit".equals(this.payment_type)
		  &&!"cod".equals(this.payment_type)
				
		  ){
			return true;
		}
		
		return false;
	}
	
	/**
	 * 获取此订单还需要支付的金额
	 * @return
	 */
	@NotDbField
	public Double getNeedPayMoney(){
		//如果为空返回空的对象，但是不返回null
		if(need_pay_money==null){
			return 0D;
		}
		return this.need_pay_money;
	}

	@NotDbField
	public MemberAddress getMemberAddress() {
		//如果为空返回空的对象，但是不返回null
		if(memberAddress==null){
			return new MemberAddress();
		}
		return memberAddress;
	}
	
	public void setMemberAddress(MemberAddress memberAddress) {
		this.memberAddress = memberAddress;
	}

	public Integer getAddress_id() {
		return address_id;
	}

	public void setAddress_id(Integer address_id) {
		this.address_id = address_id;
	}

	@NotDbField
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}

	public Double getNeed_pay_money() {
		return need_pay_money;
	}

	public void setNeed_pay_money(Double need_pay_money) {
		this.need_pay_money = need_pay_money;
	}
	
	@NotDbField
	public String getOrderType() {
		return "s";
	}

	public String getShip_no() {
		return ship_no;
	}

	public void setShip_no(String ship_no) {
		this.ship_no = ship_no;
	}

	public String getItems_json() {
		return items_json;
	}

	public void setItems_json(String items_json) {
		this.items_json = items_json;
	}

	
	@NotDbField
	public List<Map<String, Object>> getItemList() {
		if(!StringUtil.isEmpty(this.items_json)){
			JSONArray array = JSONArray.fromObject(this.items_json);
			
			JsonConfig jsonConfig = new JsonConfig();  
			jsonConfig.setRootClass(OrderItem.class);	 
			jsonConfig.setJavaPropertyFilter(new PropertyFilter() {
				
				@Override
				public boolean apply(Object arg0, String name, Object arg2) {
					 if("addon".equals(name)){
						 return true;
					 }else if("exchange".equals(name)){//add by jianghongyan 为适配积分商城 order表中exchange字段
						 OrderItem orderItem=(OrderItem)arg0;
						 if(!StringUtil.isEmpty(arg2.toString())){
							 Map<String,Object> map=(Map<String,Object>)arg2;
							 Integer exchange_point=Integer.valueOf(map.get("exchange_point").toString());
							 orderItem.setExchange_point(exchange_point);
						 }
						 return true;
					 }else if("exchange_point".equals(name)){
						 return true;
					 }else{
						 return false;
					 }
				}
			});
			itemList = (List)JSONArray.toCollection(array, jsonConfig);			
		}
		return itemList;
	}

	public Integer getLogi_id() {
		return logi_id;
	}

	public void setLogi_id(Integer logi_id) {
		this.logi_id = logi_id;
	}

	public String getLogi_name() {
		return logi_name;
	}

	public void setLogi_name(String logi_name) {
		this.logi_name = logi_name;
	}
	@NotDbField
	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public Integer getGift_id() {
		return gift_id;
	}

	public void setGift_id(Integer gift_id) {
		this.gift_id = gift_id;
	}

	public Integer getBonus_id() {
		return bonus_id;
	}

	public void setBonus_id(Integer bonus_id) {
		this.bonus_id = bonus_id;
	}

	public Double getAct_discount() {
		return act_discount;
	}

	public void setAct_discount(Double act_discount) {
		this.act_discount = act_discount;
	}

	public Integer getIs_cancel() {
		return is_cancel;
	}

	public void setIs_cancel(Integer is_cancel) {
		this.is_cancel = is_cancel;
	}

	public Integer getActivity_point() {
		return activity_point;
	}

	public void setActivity_point(Integer activity_point) {
		this.activity_point = activity_point;
	}



	public String getPayment_account() {
		return payment_account;
	}



	public void setPayment_account(String payment_account) {
		this.payment_account = payment_account;
	}



	public Integer getIs_online() {
		return is_online;
	}



	public void setIs_online(Integer is_online) {
		this.is_online = is_online;
	}



	public Integer getShip_townid() {
		return ship_townid;
	}



	public void setShip_townid(Integer ship_townid) {
		this.ship_townid = ship_townid;
	}



	public void setItemList(List<Map<String, Object>> itemList) {
		this.itemList = itemList;
	}


	@NotDbField
	public Integer getIs_comment() {
		return is_comment;
	}



	public void setIs_comment(Integer is_comment) {
		this.is_comment = is_comment;
	}
	
	
	public String getReceipt_title() {
		return receipt_title;
	}



	public void setReceipt_title(String receipt_title) {
		this.receipt_title = receipt_title;
	}



	public String getReceipt_content() {
		return receipt_content;
	}



	public void setReceipt_content(String receipt_content) {
		this.receipt_content = receipt_content;
	}


	public String getReceipt_duty() {
		return receipt_duty;
	}



	public void setReceipt_duty(String receipt_duty) {
		this.receipt_duty = receipt_duty;
	}



	public Integer getReceipt_type() {
		return receipt_type;
	}



	public void setReceipt_type(Integer receipt_type) {
		this.receipt_type = receipt_type;
	}



	public Integer getReceipt() {
		return receipt;
	}



	public void setReceipt(Integer receipt) {
		this.receipt = receipt;
	}

	
}