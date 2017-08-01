package com.enation.app.shop.test;

import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;

/** 
 * 会员退货申请单元测试类
 * @author LYH
 * @version V1.0
 * @since  V6.2
 *2016年11月26日 下午17:21:39 
 */
public class OrderMemberReturnTest extends SpringTestSupport {

	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	
	/*
	 * 退货申请:先创建一个完整的订单，然后会员进行退货申请
	 * @throws Exception
	 */
	@Test
	public void orderReturnTest() throws Exception{
		
		this.memberlogin();
		MemberAddress addr = this.memberAddressManager.getMemberDefault(1);
		
		//--------------创建订单----------------------
		
		//加入一个商品到购物车
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/cart/add-goods.do")
				 .param("action", "add-goods")
				 .param("goodsid", "267")
				 .param("num", "2")
				 .param("showCartData", "0")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  
		
	 	

		//创建订单
		 MvcResult result =mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/order/create.do")
				 .param("activity_id", "0")
				 .param("addressId",""+ addr.getAddr_id())
				 .param("city", "海淀区")
				 .param("city_id", "35")
				 .param("needPay", "29.8")
				 .param("paymentId", "0")
				 .param("province", "北京")
				 .param("province_id", "1")
				 .param("receiptContent", "办公用品")
				 .param("receiptType", "1")
				 .param("region", "海淀区")
				 .param("region_id", "453")
				 .param("shipAddr", "test")
				 .param("shipDay", "任意时间")
				 .param("shipDay", "任意日期")
				 .param("shipMobile", "15373167766")
				 .param("shipName", "test")
				 .param("shipZip", "100093")
				 .param("typeId", "1")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	
			 	//返回正确结果
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) )
			 	
			 	//订单价格为49.6,
			 	.andExpect( MockMvcResultMatchers.jsonPath("$.data.order.order_amount").value(49.6))
			 	
				//商品格为49.6,
			 	.andExpect( MockMvcResultMatchers.jsonPath("$.data.order.goods_amount").value(39.6))
			 	
			 	//运费是10
			 	.andExpect( MockMvcResultMatchers.jsonPath("$.data.order.shipping_amount").value(10.0))
			 	
			 	//订单状态为0
			 	.andExpect( MockMvcResultMatchers.jsonPath("$.data.order.status").value(0))
			 	
			 	.andReturn() ;  
		 
		 		//由返回结果中查出orderid
		 		String resultStr= 	result.getResponse().getContentAsString();
		 		JSONObject orderResult = JSONObject.fromObject(resultStr);
		 		Map orderData =(Map) orderResult.get("data");
		 		Map orderMap  = (Map)orderData.get("order");
		 		Integer orderid = (Integer)orderMap.get("order_id");
		 		
		 		//根据orderid获取 paymentid
		 		int paymentid = this.daoSupport.queryForInt("select payment_id from es_payment_logs where order_id=?", orderid);
		 
		//管理员登陆
		this.adminlogin();
 
		//--------------支付----------------------
		//支付此订单
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/payment/pay.do")
				 .param("orderId", ""+orderid)
				 .param("payment_id",""+ paymentid)
				 .param("paymoney", "49.6")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  		
		
		
		//断言订单为已支付状态
		Order order  = this.orderManager.get(orderid);
		Assert.assertEquals(OrderStatus.ORDER_PAY, order.getStatus().intValue()); 
		
		
		//--------------发货----------------------
		//填写快递单号
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/order-print/save-ship-no.do")
				 .param("order_id", ""+orderid)
				 .param("expressno","11111111101")
				 .param("logi_name", "圆通快递")
				 .param("logi_id", "1")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  		
		
		//发货
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/order-print/ship.do")
				 .param("order_id", ""+orderid)
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  	
		
		//断言发货状态
		order  = this.orderManager.get(orderid);
		Assert.assertEquals(OrderStatus.ORDER_SHIP, order.getStatus().intValue()); 
		Assert.assertEquals(OrderStatus.SHIP_YES, order.getShip_status().intValue()); 
		
		//会员登陆
		this.memberlogin();
		
		//会员确认收货
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/order/rog-confirm.do")
				 .param("orderId", ""+orderid)
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		
		
		//断言发货状态
		order  = this.orderManager.get(orderid);
		Assert.assertEquals(OrderStatus.ORDER_COMPLETE, order.getStatus().intValue()); 
		Assert.assertEquals(OrderStatus.SHIP_ROG, order.getShip_status().intValue()); 
		
		
		//会员对已完成订单进行退货申请
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/sell-back/return-goods.do")
				 .param("orderid", ""+orderid)
				 .param("ship_status", "1")
				 .param("goodsId", "267")
				 .param("item_id", "9")
				 .param("productId", "359")
				 .param("price", "39.6")
				 .param("payNum", "2")
				 .param("goodsNum", "2")
				 .param("refund_way", "快钱")
				 .param("return_account", "33")
				 .param("apply_alltotal", "99.2")
				 .param("reason", "商品质量有问题")
				 .param("remark", "很糟糕的商品")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
	}
	
   /*
    *公用的会员登陆方法 
    *@throws Exception
    */
	private void memberlogin() throws Exception{

		//--------------登录----------------------
		//执行验证码请求		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=memberlogin"));
			
		//登陆api测试
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/member/login.do")
				 .param("username", "kingapex")
				 .param("password", "wangfeng")
				 .param("validcode", "1111")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  
		
		//为会员添加一个默认地址
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/member-address/add.do")
				 .param("addr", "测试地址")
				 .param("city", "海淀区")
				 .param("city_id", "35")
				 .param("def_addr", "1")
				 .param("mobile", "15373167788")
				 .param("name", "王峰")
				 .param("province", "北京")
				 .param("province_id", "1")
				 .param("region", "海淀区")
				 .param("region_id", "453")
				 .param("shipAddressName", "家里")
				 .param("zip", "100093")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  
				
	}
	   
   /*
    *公用的后台登陆方法 
    *@throws Exception
    */
	
	private void adminlogin() throws Exception{

		//执行验证码请求		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=admin"));
		
		//管理员登陆api测试
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/core/admin/admin-user/login.do")
				 .param("username", "admin")
				 .param("password", "admin")
				 .param("valid_code", "1111")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  
	}
 }
