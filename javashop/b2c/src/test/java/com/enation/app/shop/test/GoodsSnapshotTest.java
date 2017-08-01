package com.enation.app.shop.test;

import java.util.HashMap;
import java.util.Map;

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
import com.enation.framework.util.DateUtil;

import net.sf.json.JSONObject;

public class GoodsSnapshotTest extends SpringTestSupport {
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	/*
	 * 会员登陆，购买商品 ，下单，后台修改商品信息，查看快照
	 * @throws Exception
	 */
	@Test
	public void goodsSnapshotTest() throws Exception{
	       
        //会员登陆
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
		 		
		 			
		 		String sql = "select max(snapshot_id) from es_goods_snapshot ";
		 		int snapshot_id = daoSupport.queryForInt(sql, null);
		//获取快照商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/goods/product-snapshot-list.do")
				 .param("goodsid", "267")
				 .param("snapshotid",""+snapshot_id)
				 .param("product_id", "344")
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
}
