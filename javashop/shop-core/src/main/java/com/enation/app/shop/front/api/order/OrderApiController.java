package com.enation.app.shop.front.api.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.component.receipt.Receipt;
import com.enation.app.shop.component.receipt.service.IReceiptManager;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.app.shop.core.member.service.IMemberReceiptManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.model.support.OrderPrice;
import com.enation.app.shop.core.order.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.IExpressManager;
import com.enation.app.shop.core.order.service.IOrderFlowManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.other.service.IActivityManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单api
 * @author kingapex 
 * 2013-7-24下午9:27:47
 * @version v2.0,2016年2月20日 Sylow 版本改造
 * @since v6.0
 */
@Controller
@RequestMapping("/api/shop/order")
public class OrderApiController {
	@Autowired
	private IOrderFlowManager orderFlowManager;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IMemberAddressManager memberAddressManager;
	@Autowired
	private IExpressManager expressManager;
	@Autowired
	private ICartManager cartManager;
	@Autowired
	private CartPluginBundle cartPluginBundle; 
	@Autowired
	private IPaymentManager paymentManager;
	@Autowired
	private IRegionsManager regionsManager;
	@Autowired
	private IMemberReceiptManager memberReceiptManager;
	
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 促销活动管理接口 
	 * add_by DMRain 2016-7-12
	 */
	@Autowired
	private IActivityManager activityManager;
	
	/**
	 * 创建订单，需要购物车中有商品
	 * @param address_id:收货地址id.int型，必填项
	 * @param payment_id:支付方式id，int型，必填项
	 * @param shipping_id:配送方式id，int型，必填项
	 * @param shipDay：配送时间，String型 ，可选项
	 * @param shipTime，String型 ，可选项
	 * @param remark，String型 ，可选项
	 * 
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 * 
	 */
	@ResponseBody
	@RequestMapping(value="/create", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult create(){
		try{
			Order order  = this.createOrder();
			return JsonResultUtil.getObjectJson(order, "order");
		}catch(RuntimeException e){
			//e.printStackTrace();
			this.logger.error("创建订单出错", e);
			return JsonResultUtil.getErrorJson("创建订单出错:" + e.getMessage());
		}
	}
	
	/**
	 * 取消订单
	 * @param sn:订单序列号.String型，必填项
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 */
	@ResponseBody
	@RequestMapping(value="/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult cancel(Integer order_id,String reason) {
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				return JsonResultUtil.getErrorJson("取消订单失败：登录超时");
			} else {
				//校验权限
				Order order = orderManager.get(order_id);
				if(order==null || !order.getMember_id().equals(member.getMember_id())){
					return JsonResultUtil.getErrorJson("您没有操作权限");
				}
				this.orderManager.cancel(order_id, reason);
				return JsonResultUtil.getSuccessJson("取消订单成功");
			}
		} catch (RuntimeException re) {
			return JsonResultUtil.getErrorJson("取消订单失败：" + re.getMessage());
		}
	}
	
	/**
	 * 确认收货
	 * @param orderId:订单id.String型，必填项
	 * 
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 */
	@ResponseBody
	@RequestMapping(value="/rog-confirm", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult rogConfirm() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try {
			String orderId = request.getParameter("orderId");
			Member member = UserConext.getCurrentMember();
			//会员不为空
			if(member==null){
				return JsonResultUtil.getErrorJson("订单操作失败：登录超时");
			} else {
				this.orderFlowManager.rogConfirm(Integer.parseInt(orderId), member.getMember_id(), member.getUname(), member.getUname(),DateUtil.getDateline());
				return JsonResultUtil.getSuccessJson("确认收货成功！");
				
			}
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("发送错误：" + e.getMessage());
		}
	}
	
	/**
	 * 快递查询
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/order-kuaidi", produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView orderKuaidi(){
		ModelAndView view = new ModelAndView();

		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String logino = request.getParameter("logino");//物流号
			String code = request.getParameter("code");//物流公司代码
			if(logino==null || logino.length()<5){
				Map result = new HashMap();
				result.put("status", "-1");
				Map kuaidiResult = new HashMap();
				kuaidiResult.put("message", "请输入正确的快递单号！\n\r");
				view.addObject("kuaidiResult", kuaidiResult);
				//return JsonResultUtil.getErrorJson("请输入正确的运单号");
			} else {
				if(code == null || code.equals("")){
					code = "yuantong";
				}
				
				Map kuaidiResult = this.expressManager.getDefPlatform(code, logino);
				view.addObject("kuaidiResult", kuaidiResult);
			}
			
		} catch (Exception e) {
			this.logger.error("查询货运状态", e);
		}
		if(EopSetting.PRODUCT.equals("b2c")){
			view.setViewName("/themes/kaben/member/order_kuaidi");
		}else{
			view.setViewName("/themes/b2b2cv4/member/order_kuaidi");
		}
		return view;
	}
	
	/**
	 * 检查订单填写的收货地区是否支持货到付款
	 * add by DMRain 2016-3-15
	 * @return result 0:支持，不为0则代表不支持
	 */
	@ResponseBody
	@RequestMapping(value="/check-regions-cod", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult checkRegionsCod(String regionList){
		int result = this.orderManager.checkCod(regionList);
		
		if(result == 0){
			return JsonResultUtil.getSuccessJson("保存成功！");
		}else{
			return JsonResultUtil.getErrorJson("您填写的收货地区不支持货到付款！");
		}
		
	}
	
	/**
	 * 检查订单中的商品参加的促销活动信息是否有效
	 * @author DMRain
	 * @date 2016-7-12
	 * @param activity_id 促销活动id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/check-activity",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult checkActivity(Integer activity_id) {
		int result = this.activityManager.checkActivity(activity_id);
		if (result == 0) {
			return JsonResultUtil.getSuccessJson("促销活动有效");
		} else {
			return JsonResultUtil.getErrorJson("无效的促销活动");						
		}
	}
	
	/**************以下非api，不用书写文档**************/
	/**
	 * 创建订单-需要request信息中 带以下参数
	 * @param address_id:收货地址id.int型，必填项
	 * @param payment_id:支付方式id，int型，必填项
	 * @param shipping_id:配送方式id，int型，必填项
	 * @param shipDay：配送时间，String型 ，可选项
	 * @param shipTime，String型 ，可选项
	 * @param remark，String型 ，可选项
	 * @return
	 */
	private Order createOrder(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		
		Integer shippingId = StringUtil.toInt(request.getParameter("typeId"),null);
 		if(shippingId==null ){
 			throw new RuntimeException("配送方式不能为空");
 		}
		
		Integer paymentId = StringUtil.toInt(request.getParameter("paymentId"),0);
//		修改为提交订单后，选择支付方式付款，修改人：xulipeng
// 		if(paymentId==0 ){
// 			throw new RuntimeException("支付方式不能为空");
// 		}
		
		Order order = new Order() ;
		order.setShipping_id(shippingId); //配送方式
		order.setPayment_id(paymentId);//支付方式
		String payType="";    
		if(paymentId.intValue()==0){
			order.setIs_online(1);
			payType="online";
		}else{
			payType = this.paymentManager.get(paymentId).getType();
		}
		
		Integer addressId = StringUtil.toInt(request.getParameter("addressId"), false);
		
		MemberAddress address = new MemberAddress();
		address = this.createAddress();	
		if ("cod".equals(payType)) {
			// 如果用户选择的收货地区不支持货到付款(对省、市、区三级都要做判断)
			if (regionsManager.get(address.getProvince_id()).getCod() != 1) {
				throw new RuntimeException("您选择的收货地址不支持货到付款！");
			}

			if (regionsManager.get(address.getCity_id()).getCod() != 1) {
				throw new RuntimeException("您选择的收货地址不支持货到付款！");
			}

			if (regionsManager.get(address.getRegion_id()).getCod() != 1) {
				throw new RuntimeException("您选择的收货地址不支持货到付款！");
			}
		}
		order.setShip_provinceid(address.getProvince_id());
		order.setShip_cityid(address.getCity_id());
		order.setShip_regionid(address.getRegion_id());
		if(address.getTown_id()!=null&&!address.getTown_id().equals(-1)){
			order.setShip_townid(address.getTown_id());
		}
		order.setShip_addr(address.getAddr());
		order.setShip_mobile(address.getMobile());
		order.setShip_tel(address.getTel());
		order.setShip_zip(address.getZip());
		if(address.getTown_id()!=null&&!address.getTown_id().equals(-1)){
			order.setShipping_area(address.getProvince()+"-"+ address.getCity()+"-"+ address.getRegion()+"-"+address.getTown());
		}else{
			order.setShipping_area(address.getProvince()+"-"+ address.getCity()+"-"+ address.getRegion());
		}
		order.setShip_name(address.getName());
		order.setRegionid(address.getRegion_id());
		
		//新的逻辑：只要选中了“保存地址”，就会新增一条收货地址，即使数据完全没有修改
	 	if ("yes".equals(request.getParameter("saveAddress"))) {
	 		Member member = UserConext.getCurrentMember();
			if (member != null) {
					address.setAddr_id(null);
					addressId= this.memberAddressManager.addAddress(address);
			}
		}
 	 	address.setAddr_id(addressId);
	 	order.setMemberAddress(address);
		order.setShip_day(request.getParameter("shipDay"));
		order.setShip_time(request.getParameter("shipTime"));
		order.setRemark(request.getParameter("remark"));
		order.setAddress_id(address.getAddr_id());//保存本订单的会员id

		/**发票*/

		Integer receipt = Integer.parseInt(request.getParameter("receipt").toString());
		order.setReceipt(receipt);
		/**判断是否需要发票*/
		if(receipt==1){
			String receipt_title = request.getParameter("receiptTitle");
			String receipt_content = request.getParameter("receiptContent");
			String receipt_duty = request.getParameter("receiptDuty");
			Integer receipt_type = Integer.parseInt(request.getParameter("receiptType"));
			order.setReceipt_content(receipt_content);
			order.setReceipt_title(receipt_title);
			order.setReceipt_duty(receipt_duty);
			order.setReceipt_type(receipt_type);
		}

		String sessionid =request.getSession().getId();
		List<CartItem> itemList  = this.cartManager.selectListGoods(sessionid);
		if(itemList==null||itemList.size()==0){
			throw new RuntimeException("购物车不能为空");
		}
		OrderPrice orderPrice =   this.cartManager.countPrice(itemList, shippingId,address.getRegion_id()+"");
	
		//激发价格计算事件
		orderPrice  = this.cartPluginBundle.coutPrice(orderPrice);
		order.setOrderprice(orderPrice);
		
		Integer activity_id = StringUtil.toInt(request.getParameter("activity_id"),0);
		
		//如果促销活动id不等于0
		if (activity_id != 0) {
			
			//如果当前促销活动有效
			if (this.activityManager.checkActivity(activity_id) == 0) {
				//以下为当有促销活动时，提交订单，下列信息要set进订单信息中 
				//add by DMRain 2016-6-8
				String gift_id = request.getParameter("gift_id");
				String bonus_id = request.getParameter("bonus_id");
				String act_discount = request.getParameter("act_discount");
				String activity_point = request.getParameter("activity_point");
				String freeShip = request.getParameter("freeShip");
				
				//如果促销活动赠送的赠品id不为空
				if (gift_id != null) {
					order.setGift_id(Integer.parseInt(gift_id));
				}
				
				//如果促销活动赠送的优惠券id不为空
				if (bonus_id != null) {
					order.setBonus_id(Integer.parseInt(bonus_id));
				}
				
				//如果促销活动减免的现金金额不为空
				if (act_discount != null) {
					order.setAct_discount(Double.parseDouble(act_discount));
					order.setNeed_pay_money(order.getNeed_pay_money() - Double.parseDouble(act_discount));
				}
				
				//如果促销活动赠送的积分不为空
				if (activity_point != null) {
					order.setActivity_point(Integer.parseInt(activity_point));
				}
				
				//如果促销活动拥有免邮费的优惠条件
				if (freeShip != null) {
					order.setNeed_pay_money(order.getNeed_pay_money() - order.getShipping_amount());
					order.setOrder_amount(order.getOrder_amount() - order.getShipping_amount());
					order.setShipping_amount(0.00);
				}
			} 
		}
		
		return	this.orderFlowManager.add(order,itemList,sessionid);
		
	}
	
	/**
	 * 创建地址-需要request中 带地址参数
	 * @param shipName 收货人 【必填】
	 * @param shipTel 电话 【选填】
	 * @param shipMobile 手机 【选填】
	 * @param province_id 省份id 【必填】
	 * @param city_id 城市id 【必填】
	 * @param region_id 地区id 【必填
	 * @param province -- 【必填】
	 * @param city -- 【必填】
	 * @param region -- 【必填
	 * @param shipAddr 收获地址
	 * @param shipZip -- 【必填】
	 * @return
	 */
	private MemberAddress createAddress(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		MemberAddress address = new MemberAddress();
 

		String name = request.getParameter("shipName");
		address.setName(name);

		String tel = request.getParameter("shipTel");
		address.setTel(tel);

		String mobile = request.getParameter("shipMobile");
		address.setMobile(mobile);

		String province_id = request.getParameter("province_id");
		if(province_id!=null){
			address.setProvince_id(Integer.valueOf(province_id));
		}

		String city_id = request.getParameter("city_id");
		if(city_id!=null){
			address.setCity_id(Integer.valueOf(city_id));
		}

		String region_id = request.getParameter("region_id");
		if(region_id!=null){
			address.setRegion_id(Integer.valueOf(region_id));
		}
		String town_id = request.getParameter("town_id");
		if(town_id!=null&&!town_id.equals("")){
			address.setTown_id(Integer.valueOf(town_id));
		}

		String province = request.getParameter("province");
		address.setProvince(province);

		String city = request.getParameter("city");
		address.setCity(city);

		String region = request.getParameter("region");
		address.setRegion(region);
		
		String town = request.getParameter("town");
		if(town!=null && !"".equals(town)){
			address.setTown(town);
		}

		String addr = request.getParameter("shipAddr");
		address.setAddr(addr);

		String zip = request.getParameter("shipZip");
		address.setZip(zip);
	
		return address;
	}
	/**
	 * @param title 抬头 【必填】
	 * @param content 类别 【选填】
	 */
	private Receipt createReceipt(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		Receipt receipt = new Receipt();
		
		String title = request.getParameter("titlee");
		receipt.setTitle(title);

		String content = request.getParameter("contente");
		receipt.setContent(content);

		return receipt;
	}
}
