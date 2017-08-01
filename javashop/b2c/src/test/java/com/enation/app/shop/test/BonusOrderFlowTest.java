package com.enation.app.shop.test;

import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.StringUtil;

/**
 * 优惠券订单流程单元测试 
 * @author yinchao
 * @version v1.0
 * @since v6.0
 * 2016年11月22日 上午11:20:10 
 *
 */
@Rollback(true)
public class BonusOrderFlowTest extends SpringTestSupport{
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IBonusManager bonusManager;
	
	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	@Autowired
	private IOrderManager orderManager;

	
	 
	/**
	 * 登录后台，发布优惠券
	 * @throws Exception
	 */
	@Test
	public void testAddBonus() throws Exception{
		
		this.daoSupport.execute("TRUNCATE table es_bonus_type");
				
		//--------------优惠券列表----------------------
		//添加优惠券类型、按用户发放api测试
		mockMvc.perform(
				MockMvcRequestBuilders.post("/shop/admin/bonus-type/save-add.do")
					.param("min_goods_amount","50.00")
					.param("recognition","javashop")
					.param("sendTimeEnd","2017-01-12")
					.param("sendTimeStart","2016-12-12")
					.param("send_type","0")
					.param("type_money","10.00")
					.param("type_name","优惠10元")
					.param("useTimeEnd","2017-12-31")
					.param("useTimeStart","2016-12-12")
					.session(session)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					)
					.andDo(MockMvcResultHandlers.print())
					.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
					
					int typeid = this.daoSupport.getLastId("es_bonus_type");
						
		//按等级发放优惠券api测试
		mockMvc.perform(
				MockMvcRequestBuilders.post("/shop/admin/bonus/send-for-member-lv.do")
					.param("typeid",""+typeid)
					.param("lvid", "1")
					.session(session)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
								
					)
					.andDo(MockMvcResultHandlers.print())
					.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );
						
						
		//--------------管理员登录----------------------
		//执行验证码请求
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=admin"));
						
		//管理员登录api测试
		mockMvc.perform(
				MockMvcRequestBuilders.post("/core/admin/admin-user/login.do")
					.param("username", "admin")
					.param("password", "admin")
					.param("valid_code", "1111")
					.session(session)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					)
					.andDo(MockMvcResultHandlers.print())
					.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
	}
		
	
		/**
		 * 使用优惠券订单测试：登录、加入购物车、使用优惠券下单
		 * @throws Exception
		 */
		@Test
		public void testBase() throws Exception{
			
			this.daoSupport.execute("TRUNCATE table es_bonus_type");
			
			//共用添加优惠券方法
			this.testAddBonus();
			
			//获取最后一个bonusid
			int bonusid=this.daoSupport.getLastId("es_member_bonus");
			
			//获取最后一个typeid
			int typeid=this.daoSupport.getLastId("es_bonus_type");
			
			//共用登录方法
			this.login();
			
			MemberAddress addr = this.memberAddressManager.getMemberDefault(1);
			
			//--------------创建订单----------------------
			
			//加入一个商品到购物车api测试
			mockMvc.perform(
					MockMvcRequestBuilders.post("/api/shop/cart/add-goods.do")
					.param("action", "add-goods")
					.param("goodsid", "272")
					.param("num", "2")
					.param("showCartData", "0")
					.session(session)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					)
					.andDo(MockMvcResultHandlers.print())
					.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
			
			//使用优惠券api测试
			mockMvc.perform(
					MockMvcRequestBuilders.post("/api/shop/bonus/use-one.do")
					.param("bonusid", StringUtil.toString(bonusid))
					.param("regionid","453")
					.param("typeid",StringUtil.toString(typeid))
					.session(session)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					)
					.andDo(MockMvcResultHandlers.print())
				 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
			
			
			//创建订单api测试
			MvcResult result =mockMvc.perform(
					MockMvcRequestBuilders.post("/api/shop/order/create.do")
						.param("activity_id", "0")
						.param("addressId",""+ addr.getAddr_id())
						.param("bonusid", StringUtil.toString(bonusid))
						.param("city", "海淀区")
						.param("city_id", "35")
						.param("needPay", "90")
						.param("paymentId", "0")
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
						.param("typeId","1")
						.session(session)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						)
						.andDo(MockMvcResultHandlers.print()) 
						
						//返回正确结果
					 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) )
					 	
					 	//订单价格为100,
					 	.andExpect( MockMvcResultMatchers.jsonPath("$.data.order.order_amount").value(100.00))
					 	
						//商品格为90,
					 	.andExpect( MockMvcResultMatchers.jsonPath("$.data.order.goods_amount").value(90.00))
					 	
					 	//优惠金额10,
					 	.andExpect( MockMvcResultMatchers.jsonPath("$.data.order.discount").value(10.00))
					 	
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
				 		
				 		
				 		
		//--------------管理员登录----------------------	 
		//执行验证码请求
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=admin"));
		
		//管理员登录api测试
		mockMvc.perform(
				MockMvcRequestBuilders.post("/core/admin/admin-user/login.do")
				.param("username", "admin")
				.param("password", "admin")
				.param("valid_code", "1111")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
		
		//--------------支付----------------------
		//支付此订单
		mockMvc.perform(
				MockMvcRequestBuilders.post("/shop/admin/payment/pay.do")
				.param("orderId", ""+ orderid)
				.param("payment_id", ""+ paymentid)
				.param("paymoney","90")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
		
		//断言订单为已支付状态
		Order order = this.orderManager.get(orderid);
		Assert.assertEquals(OrderStatus.ORDER_PAY, order.getStatus().intValue());
		
		
		//--------------发货----------------------
		//填写快递单号
		mockMvc.perform( 
				MockMvcRequestBuilders.post("/shop/admin/order-print/save-ship-no.do")
				.param("order_id", ""+ orderid)
				.param("expressno", "11111111103")
				.param("logi_name", "圆通快递")
				.param("logi_id","1")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
		
		//发货
		mockMvc.perform( 
				MockMvcRequestBuilders.post("/shop/admin/order-print/ship.do")
				.param("order_id", ""+orderid)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
		
		//断言发货状态
		order = this.orderManager.get(orderid);
		Assert.assertEquals(OrderStatus.ORDER_SHIP, order.getStatus().intValue());
		Assert.assertEquals(OrderStatus.SHIP_YES, order.getShip_status().intValue());
		
		//登录
		this.login();
		
		//会员确认收货
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/order/rog-confirm.do")
				.param("orderId", ""+orderid)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
		
		
		//断言发货状态
		order = this.orderManager.get(orderid);
		Assert.assertEquals(OrderStatus.ORDER_COMPLETE, order.getStatus().intValue());
		Assert.assertEquals(OrderStatus.SHIP_ROG, order.getShip_status().intValue());
	}
	
	
	/**
	 * 共用的登录方法
	 * @throws Exception
	 */
	private void login() throws Exception{
		
		//--------------登录----------------------
		//执行验证码请求		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=memberlogin"));
		
		//登录api测试
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/member/login.do")
				 .param("username", "kingapex")
				 .param("password", "wangfeng")
				 .param("validcode", "1111")
				 .session(session)
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				  )
				 .andDo(MockMvcResultHandlers.print())  
				 .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		
		//为会员添加一个默认地址api测试
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
				 .session(session)
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)			
				 )
				 .andDo(MockMvcResultHandlers.print())  
				 .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );
	}

}
