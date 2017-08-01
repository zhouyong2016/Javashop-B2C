package com.enation.app.shop.test.admin;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.StringUtil;

/** 
 * 后台增/删/改商品-单元测试类
 * @author LYH
 * @version V1.0
 * @since  V6.0
 *2016年11月20日 下午7:22:39 
 */
public class GoodsAdminTest extends SpringTestSupport{
	
	
	@Autowired
	private IDaoSupport daosupport;
	/**
	 * 登陆后添加商品
	 * @throws Exception
	 */
	@Test
	public void goodsAddTest() throws Exception{
		
		//进行登录
		this.login();
		
		//添加商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/goods/save-add.do")
				 .param("isedit", "yes")
				 .param("cat_id", "6")
				 .param("type_id", "1")
				 .param("name", "测试商品11")
				 .param("sn", "2015609090")
				 .param("market_enable", "1")
				 .param("mktprice", "220")
				 .param("point", "0")
				 .param("file", "")
				 .param("picnames", "http://localhost:8080/b2c/statics/attachment//goods/2016/12/6/22//51054878.jpg")
				 .param("haveSpec", "0")
				 .param("price", "100")
				 .param("cost", "90.00")
				 .param("weight", "10")
				 .param("unit", "")
				 .param("brand_id", "")
				 .param("propnames", "产地")
				 .param("propvalues","北京")
				 .param("propnames", "包装")
				 .param("propnames", "杯装")
				 .param("groupnames", "规格参数")
				 .param("paramnums", "9")
				 .param("paramnames", "规格")
				 .param("paramvalues", "")
				 .param("paramnames", "重量")
				 .param("paramvalues", "")
				 .param("paramnames", "产品标准号")
				 .param("paramvalues", "")
				 .param("paramnames", "生产日期")
				 .param("paramvalues", "")
				 .param("paramnames", "保质期")
				 .param("paramvalues", "")
				 .param("paramnames", "储存方法")
				 .param("paramvalues", "")
				 .param("paramnames", "配料")
				 .param("paramvalues", "")
				 .param("paramnames", "产地")
				 .param("paramvalues", "")
				 .param("paramnames", "厂家")
				 .param("paramvalues", "")
				 .param("page_title", "测试")
				 .param("meta_keywords", "测试商品进行测试")
				 .param("page_title", "")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
	}
	/**
	 * 登陆后编辑商品
	 * @throws Exception
	 */
	@Test
	public void goodsEditTest() throws Exception{
		
		//进行登录
		this.login();
		
	   //编辑商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/goods/save-edit.do")
				 .param("isedit", "yes")
				 .param("goods_id", "285")
				 .param("cat_id", "111")
				 .param("type_id", "53")
				 .param("name", "潮胖子男士毛衣")
				 .param("sn", "201511243123")
				 .param("market_enable", "1")
				 .param("mktprice", "22")
				 .param("point", "0")
				 .param("file", "picnames")
				 .param("picnames", "http://localhost:8080/b2c/statics/attachment//gift/2016/11/29/17//11113497.jpg")
				 .param("haveSpec", "0")
				 .param("price", "300")
				 .param("lvid", "1")
				 .param("lvPrice", "")
				 .param("lvid", "2")
				 .param("lvPrice", "")
				 .param("lvid", "3")
				 .param("lvPrice", "")
				 .param("lvid", "4")
				 .param("lvPrice", "")
				 .param("cost", "0.00")
				 .param("weight", "2")
				 .param("unit", "")
				 .param("brand_id", "")
				 .param("page_title", "测试")
				 .param("meta_keywords", "测试商品进行测试")
				 .param("page_title", "1111111")
				 .param("info", "")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
	}	
	/**
	 * 登陆后添加商品-编辑商品
	 * @throws Exception
	 */
	@Test
	public void goodsAddEditTest() throws Exception{
		
		//进行登录
		this.login();
		
		//添加商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/goods/save-add.do")
				 .param("isedit", "yes")
				 .param("cat_id", "6")
				 .param("type_id", "1")
				 .param("name", "测试商品11")
				 .param("sn", "2015609090")
				 .param("market_enable", "1")
				 .param("mktprice", "220")
				 .param("point", "0")
				 .param("file", "")
				 .param("picnames", "http://localhost:8080/b2c/statics/attachment//goods/2016/12/6/22//51054878.jpg")
				 .param("haveSpec", "0")
				 .param("price", "100")
				 .param("cost", "90.00")
				 .param("weight", "10")
				 .param("unit", "")
				 .param("brand_id", "")
				 .param("propnames", "产地")
				 .param("propvalues","北京")
				 .param("propnames", "包装")
				 .param("propnames", "杯装")
				 .param("groupnames", "规格参数")
				 .param("paramnums", "9")
				 .param("paramnames", "规格")
				 .param("paramvalues", "")
				 .param("paramnames", "重量")
				 .param("paramvalues", "")
				 .param("paramnames", "产品标准号")
				 .param("paramvalues", "")
				 .param("paramnames", "生产日期")
				 .param("paramvalues", "")
				 .param("paramnames", "保质期")
				 .param("paramvalues", "")
				 .param("paramnames", "储存方法")
				 .param("paramvalues", "")
				 .param("paramnames", "配料")
				 .param("paramvalues", "")
				 .param("paramnames", "产地")
				 .param("paramvalues", "")
				 .param("paramnames", "厂家")
				 .param("paramvalues", "")
				 .param("page_title", "测试")
				 .param("meta_keywords", "测试商品进行测试")
				 .param("page_title", "")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		int goodsid=this.daosupport.getLastId("es_goods");
		//编辑商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/goods/save-edit.do")
				 .param("isedit", "yes")
				 .param("goods_id", goodsid+"")
				 .param("cat_id", "6")
				 .param("type_id", "1")
				 .param("name", "测试商品11")
				 .param("sn", "201623276767")
				 .param("market_enable", "1")
				 .param("mktprice", "440")
				 .param("point", "0")
				 .param("file", "")
				 .param("picnames", "http://localhost:8080/b2c/statics/attachment//goods/2016/12/6/22//51054878.jpg")
				 .param("haveSpec", "0")
				 .param("price", "100")
				 .param("cost", "90.00")
				 .param("weight", "10")
				 .param("unit", "")
				 .param("brand_id", "")
				 .param("propnames", "产地")
				 .param("propvalues","北京")
				 .param("propnames", "包装")
				 .param("propnames", "杯装")
				 .param("groupnames", "规格参数")
				 .param("paramnums", "9")
				 .param("paramnames", "规格")
				 .param("paramvalues", "")
				 .param("paramnames", "重量")
				 .param("paramvalues", "")
				 .param("paramnames", "产品标准号")
				 .param("paramvalues", "")
				 .param("paramnames", "生产日期")
				 .param("paramvalues", "")
				 .param("paramnames", "保质期")
				 .param("paramvalues", "")
				 .param("paramnames", "储存方法")
				 .param("paramvalues", "")
				 .param("paramnames", "配料")
				 .param("paramvalues", "")
				 .param("paramnames", "产地")
				 .param("paramvalues", "")
				 .param("paramnames", "厂家")
				 .param("paramvalues", "")
				 .param("page_title", "测试")
				 .param("meta_keywords", "测试商品进行测试")
				 .param("page_title", "")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );
		
		
	}
	
	
	/**
	 * 登陆后刪除商品
	 * @throws Exception
	 */
	@Test
	public void goodsDeleteTest() throws Exception{
		
		//进行登录
		this.login();
		
	   //删除商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/goods/delete.do")
				 .param("goods_id", "9")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
	}	
	
	/**
	 * 登陆后添加商品-删除商品
	 * @throws Exception
	 */
	@Test
	public void goodsAddDeleteTest() throws Exception{
		
		//进行登录
		this.login();
		
		//添加商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/goods/save-add.do")
				 .param("isedit", "yes")
				 .param("cat_id", "6")
				 .param("type_id", "1")
				 .param("name", "测试商品11")
				 .param("sn", "2015609090")
				 .param("market_enable", "1")
				 .param("mktprice", "220")
				 .param("point", "0")
				 .param("file", "")
				 .param("picnames", "http://localhost:8080/b2c/statics/attachment//goods/2016/12/6/22//51054878.jpg")
				 .param("haveSpec", "0")
				 .param("price", "100")
				 .param("cost", "90.00")
				 .param("weight", "10")
				 .param("unit", "")
				 .param("brand_id", "")
				 .param("propnames", "产地")
				 .param("propvalues","北京")
				 .param("propnames", "包装")
				 .param("propnames", "杯装")
				 .param("groupnames", "规格参数")
				 .param("paramnums", "9")
				 .param("paramnames", "规格")
				 .param("paramvalues", "")
				 .param("paramnames", "重量")
				 .param("paramvalues", "")
				 .param("paramnames", "产品标准号")
				 .param("paramvalues", "")
				 .param("paramnames", "生产日期")
				 .param("paramvalues", "")
				 .param("paramnames", "保质期")
				 .param("paramvalues", "")
				 .param("paramnames", "储存方法")
				 .param("paramvalues", "")
				 .param("paramnames", "配料")
				 .param("paramvalues", "")
				 .param("paramnames", "产地")
				 .param("paramvalues", "")
				 .param("paramnames", "厂家")
				 .param("paramvalues", "")
				 .param("page_title", "测试")
				 .param("meta_keywords", "测试商品进行测试")
				 .param("page_title", "")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		//删除商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/goods/delete.do")
				 .param("goods_id", StringUtil.toString(this.daosupport.getLastId("es_goods")))
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
	}
	
	/**
	 * 登陆后添加商品-编辑商品-删除商品
	 * @throws Exception
	 */
	@Test
	public void goodsAddEditDeleteTest() throws Exception{
		
		//进行登录
		this.login();
		
		//添加商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/goods/save-add.do")
				 .param("isedit", "yes")
				 .param("cat_id", "6")
				 .param("type_id", "1")
				 .param("name", "测试商品11")
				 .param("sn", "2015609090")
				 .param("market_enable", "1")
				 .param("mktprice", "220")
				 .param("point", "0")
				 .param("file", "")
				 .param("picnames", "http://localhost:8080/b2c/statics/attachment//goods/2016/12/6/22//51054878.jpg")
				 .param("haveSpec", "0")
				 .param("price", "100")
				 .param("cost", "90.00")
				 .param("weight", "10")
				 .param("unit", "")
				 .param("brand_id", "")
				 .param("propnames", "产地")
				 .param("propvalues","北京")
				 .param("propnames", "包装")
				 .param("propnames", "杯装")
				 .param("groupnames", "规格参数")
				 .param("paramnums", "9")
				 .param("paramnames", "规格")
				 .param("paramvalues", "")
				 .param("paramnames", "重量")
				 .param("paramvalues", "")
				 .param("paramnames", "产品标准号")
				 .param("paramvalues", "")
				 .param("paramnames", "生产日期")
				 .param("paramvalues", "")
				 .param("paramnames", "保质期")
				 .param("paramvalues", "")
				 .param("paramnames", "储存方法")
				 .param("paramvalues", "")
				 .param("paramnames", "配料")
				 .param("paramvalues", "")
				 .param("paramnames", "产地")
				 .param("paramvalues", "")
				 .param("paramnames", "厂家")
				 .param("paramvalues", "")
				 .param("page_title", "测试")
				 .param("meta_keywords", "测试商品进行测试")
				 .param("page_title", "")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		int goodsid=this.daosupport.getLastId("es_goods");
		//编辑商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/goods/save-edit.do")
				 .param("isedit", "yes")
				 .param("goods_id", goodsid+"")
				 .param("cat_id", "6")
				 .param("type_id", "1")
				 .param("name", "测试商品11")
				 .param("sn", "201623276767")
				 .param("market_enable", "1")
				 .param("mktprice", "440")
				 .param("point", "0")
				 .param("file", "")
				 .param("picnames", "http://localhost:8080/b2c/statics/attachment//goods/2016/12/6/22//51054878.jpg")
				 .param("haveSpec", "0")
				 .param("price", "100")
				 .param("cost", "90.00")
				 .param("weight", "10")
				 .param("unit", "")
				 .param("brand_id", "")
				 .param("propnames", "产地")
				 .param("propvalues","北京")
				 .param("propnames", "包装")
				 .param("propnames", "杯装")
				 .param("groupnames", "规格参数")
				 .param("paramnums", "9")
				 .param("paramnames", "规格")
				 .param("paramvalues", "")
				 .param("paramnames", "重量")
				 .param("paramvalues", "")
				 .param("paramnames", "产品标准号")
				 .param("paramvalues", "")
				 .param("paramnames", "生产日期")
				 .param("paramvalues", "")
				 .param("paramnames", "保质期")
				 .param("paramvalues", "")
				 .param("paramnames", "储存方法")
				 .param("paramvalues", "")
				 .param("paramnames", "配料")
				 .param("paramvalues", "")
				 .param("paramnames", "产地")
				 .param("paramvalues", "")
				 .param("paramnames", "厂家")
				 .param("paramvalues", "")
				 .param("page_title", "测试")
				 .param("meta_keywords", "测试商品进行测试")
				 .param("page_title", "")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );
		//删除商品
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/shop/admin/goods/delete.do")
				 .param("goods_id", goodsid+"")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		
	}
	
	/**
	 * 共用的登陆方法
	 * @throws Exception
	 */
	private void login() throws Exception{

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
