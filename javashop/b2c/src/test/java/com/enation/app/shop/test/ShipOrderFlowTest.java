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
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;

/**
 * 指定地区配送费用订单流程
 * @author yinchao
 * @version v1.0
 * @since v6.2
 * 2016年12月9日 上午11:47:03 
 */
@Rollback(true)
public class ShipOrderFlowTest extends SpringTestSupport{
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IDlyTypeManager dlyTypeManager;
	
	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	@Autowired
	private IOrderManager orderManager;
	
	/**
	 * 登录后台，设置指定地区配送费用
	 * @throws Exception
	 */
	@Test 
	public void testAdminLogin() throws Exception{
		
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
		
		//设置指定地区配送费用
		//设置原有的配送方式，添加指定地区操作
		mockMvc.perform(
				MockMvcRequestBuilders.post("/shop/admin/dly-type/save-add.do")
				.param("areacount", "1")
				.param("config", "")
				.param("areas1","密云县,三河")
				.param("continueprice","")
				.param("continueprice", "10")
				.param("firstprice","")
				.param("firstprice", "10")
				.param("continueprice1", "0")
				.param("firstprice1","5")
				.param("continueunit", "1000")
				.param("firstunit", "1000")
				.param("corp_id", "1")
				.param("defAreaFee","1")
				.param("detail", "测试用，请在后台修改")
				.param("disabled", "0")
				.param("has_cod", "0")
				.param("is_same", "0")
				.param("name", "圆通速递")
				.param("ordernum", "1")
				.param("totle_areas1","33,34")
				.param("totle_regions1", "451,452")
				.param("useexp", "0")
				.param("useexp","0")
				.param("useexp1","0")
				.param("type_id","1")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
				
	}
	
	/**
	 * 共用的登录方法
	 * @throws Exception
	 */
	private void newLogin() throws Exception{
		
		MemberAddress addr = this.memberAddressManager.getMemberDefault(2);
		
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
		
		//为会员添加一个指定地址，配送地区的默认地址
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/member-address/add.do")
				.param("addr","test")
				.param("city", "密云县")
				.param("city_id", "33")
				.param("mobile", "13681154558")
				.param("name","demowhj")
				.param("province", "北京")
				.param("province_id", "1")
				.param("region", "密云县")
				.param("region_id", "451")
				.param("shipAddressName", "公司")
				.param("zip", "100983")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
	}
	
	 //指定地区配送费用，订单流程
	@Test
	public void testBase() throws Exception{
		
		//共用添加指定配送费用方法
		this.testAdminLogin();
		
		//共用登录方法
		this.newLogin();
		
		MemberAddress addr = this.memberAddressManager.getMemberDefault(2);
		
		//--------------创建订单----------------------
		
		//将商品加入购物车
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/cart/add-goods.do")
				.param("action", "add-goods")
				.param("goodsid", "267")
				.param("num", "1")
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
				.param("activity_id", "0")
				.param("addressId", ""+addr.getAddr_id())
				.param("city","密云县")
				.param("city_id", "33")
				.param("needPay", "24.8")
				.param("paymentId", "0")
				.param("province", "北京")
				.param("province_id", "1")
				.param("receipt", "2")
				.param("region","密云县")
				.param("region_id","451")
				.param("shipAddr","test")
				.param("shipDay", "任意时间")
				.param("shipDay", "任意日期")
				.param("shipMobile","15373167766")
				.param("shipName","test")
				.param("shipZip","101505")
				.param("typeId","1")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				
				//返回正确的结果
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1))
				
				//订单价格29.8
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.order.order_amount").value(29.8))
				
				//商品价格为90,
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.order.goods_amount").value(19.8))
					
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
				.param("paymoney", "29.8")
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
	
	
	
}
