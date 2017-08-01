package com.enation.app.shop.test;

import java.util.Map;

import org.junit.Assert;

import net.sf.json.JSONObject;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.app.shop.core.other.service.IActivityManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;


/**
 * 赠送优惠券促销活动
 * @author yinchao
 * @version v1.0
 * @since v6.0
 * 2016年12月1日 下午5:09:03 
 */
@Rollback(true)
public class ActivityBounsOrderFlowTest extends SpringTestSupport{
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IActivityManager activityManager;
	
	/**
	 * 登录后台,添加促销活动
	 * @throws Exception
	 */
	@Test
	public void testActivity() throws Exception{
		
		
		this.daoSupport.execute("TRUNCATE table es_activity");
		
		this.daoSupport.execute("TRUNCATE table es_bonus_type");
		//添加优惠券
		mockMvc.perform(
				MockMvcRequestBuilders.post("/shop/admin/bonus-type/save-add.do")
				.param("min_goods_amount", "60.00")
				.param("recognition", "123")
				.param("sendTimeEnd", "2017-01-01")
				.param("sendTimeStart", "2016-12-01")
				.param("send_type", "0")
				.param("type_money", "5.00")
				.param("type_name", "促销优惠券")
				.param("useTimeEnd", "2017-12-31")
				.param("useTimeStart", "2016-12-01")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
		
		//添加满设置金额赠送优惠券的促销活动
		mockMvc.perform(MockMvcRequestBuilders.post("/shop/admin/activity/save-add.do")
				.param("activity_name", "赠送优惠券促销活动")
				.param("activity_type", "1")
				.param("bonus_id", "1")
				.param("description","测试赠送优惠券促销活动")
				.param("endTime", "2017-12-31 17:32:27")
				.param("full_money","50.00")
				.param("gift_id", "0")
				.param("goods_ids", "272")
				.param("is_send_bonus","1")
				.param("range_type", "2")
				.param("startTime", "2016-12-01 17:32:25")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
		
		//--------------管理员登录----------------------
		//执行验证码请求
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=admin"));
		
		//管理员登录api测试
		mockMvc.perform(MockMvcRequestBuilders.post("/core/admin/admin-user/login.do")
				.param("username", "admin")
				.param("password","admin")
				.param("valid_code", "1111")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
				
	}
	
		//添加满设置金额送优惠券的促销活动
		@Test
		public void testBase() throws Exception{
			
			//共用添加促销活动方法
			this.testActivity();
			
			//共用登录方法
			this.newLogin();
			
			MemberAddress addr = this.memberAddressManager.getMemberDefault(2);
			
			//--------------创建订单----------------------
			
			//将商品加入购物车
			mockMvc.perform(MockMvcRequestBuilders.post("/api/shop/cart/add-goods.do")
					.param("action", "add-goods")
					.param("activity_id","1")
					.param("goodsid","272")
					.param("num", "2")
					.param("showCartData", "0")	
					.session(session)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					)
					.andDo(MockMvcResultHandlers.print())
					.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
			
			//创建订单
			MvcResult result = mockMvc.perform(
					MockMvcRequestBuilders.post("/api/shop/order/create.do")
					 .param("activity_id", "1")
					 .param("addressId",""+ addr.getAddr_id())
					 .param("bonus_id", "1")
					 .param("city", "海淀区")
					 .param("city_id", "35")
					 .param("needPay", "100")
					 .param("paymentId", "0")
					 .param("province", "北京")
					 .param("province_id", "1")
					 .param("receipt", "2")
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
					 .session(session)
					 .contentType(MediaType.APPLICATION_JSON)
					 .accept(MediaType.APPLICATION_JSON)
					 )
					 .andDo(MockMvcResultHandlers.print())
					 
					 //返回正确结果
					.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1))
			
					//订单价格100,
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.order.order_amount").value(100.0))
				
					//商品价格为90,
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.order.goods_amount").value(90.0))
					
					//运费是10,
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.order.shipping_amount").value(10.0))
					
					//订单状态为0
					.andExpect(MockMvcResultMatchers.jsonPath("$.data.order.status").value(0))
					
					.andReturn();
			
					//由返回结果中查出orderid
					String resultStr = result.getResponse().getContentAsString();
					JSONObject orderResult = JSONObject.fromObject(resultStr);
					Map orderData = (Map) orderResult.get("data");
					Map orderMap = (Map) orderData.get("order");
					Integer orderid = (Integer) orderMap.get("order_id");
					
					//根据orderid获取paymentid
					int paymentid = this.daoSupport.queryForInt("select payment_id from es_payment_logs where order_id=?", orderid);
					 
					
					
		//--------------管理员登录----------------------	
		//执行验证码请求
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=admin"));	
		
		//管理员登录api测试
		mockMvc.perform(
				MockMvcRequestBuilders.post("/core/admin/admin-user/login.do")
				.param("username","admin")
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
				.param("orderId", ""+orderid)
				.param("payment_id", ""+paymentid)
				.param("paymoney", "100")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
		
		//断言订单已支付状态
		Order order = this.orderManager.get(orderid);
		Assert.assertEquals(OrderStatus.ORDER_PAY, order.getStatus().intValue());
		
		
		//登录
		this.newLogin();
		
		//会员确认收货
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/order/rog-confirm.do")
				.param("orderId",""+orderid)
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
	 *登录
	 *@throws Exception
	 */
	private void newLogin() throws Exception{
		
		//--------------登录----------------------
		//执行验证码请求
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=memberlogin"));
		
		//登录api测试
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/member/login.do")
				.param("username", "demowhj")
				.param("password","111111")
				.param("validcode", "1111")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
		
		//为会员添加一个默认地址
		mockMvc.perform(MockMvcRequestBuilders.post("/api/shop/member-address/add.do")
				.param("addr", "测试地址")
				 .param("city", "海淀区")
				 .param("city_id", "35")
				 .param("def_addr", "1")
				 .param("mobile", "13204566553")
				 .param("name", "demowhj")
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
				 .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
	}


}
