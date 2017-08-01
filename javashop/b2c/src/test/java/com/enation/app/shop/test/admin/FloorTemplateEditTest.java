package com.enation.app.shop.test.admin;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.test.SpringTestSupport;

/**
 * 首页楼层管理--模板编辑   单元测试类
 * @author  LYH
 * @version 1.0
 * @since 6.2
 * @date 2016年12月11日 下午4:46:26  
 */

public class FloorTemplateEditTest extends SpringTestSupport{
	
	/**
	 *模板编辑--保存品牌 
	 * @throws Exception 
	 */
	@Test
	public void templateEditTest() throws Exception{
	   
		//管理员登录
		this.login();
		//保存品牌
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/template/save-brand.do?ajax=yes")
			 .param("floor_id", "2")
			 .param("brand_ids[]", "116")
			 .param("brand_ids[]", "115")
			 .param("brand_ids[]", "114")
			 .param("brand_ids[]", "113")
			 .param("brand_ids[]", "112")
			 .param("brand_ids[]", "111")
			 .param("brand_ids[]", "110")
			 .param("brand_ids[]", "119")
			 .param("keyword", "")
			 .contentType(MediaType.APPLICATION_JSON)
			 .accept(MediaType.APPLICATION_JSON)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		
		//保存分类
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/template/save-cat.do?ajax=yes")
			 .param("floor_id", "2")
			 .param("cat_id", "56")
			 .contentType(MediaType.APPLICATION_JSON)
			 .accept(MediaType.APPLICATION_JSON)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		
		//保存广告
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/template/save-adv.do?ajax=yes")
			 .param("floor_id", "2")
			 .param("adv_ids[]", "23")
			 .param("adv_ids[]", "25")
			 .param("adv_ids[]", "4")
			 .param("position", "only")
			 .param("keyword", "")
			 .contentType(MediaType.APPLICATION_JSON)
			 .accept(MediaType.APPLICATION_JSON)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		
		//保存批量商品
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/template/save-batch-goods.do?ajax=yes")
			 .param("floor_id", "6")
			 .param("goods_ids[]", "272")
			 .param("goods_ids[]", "270")
			 .param("goods_ids[]", "268")
			 .param("goods_ids[]", "124")
			 .param("keyword", "")
			 .contentType(MediaType.APPLICATION_JSON)
			 .accept(MediaType.APPLICATION_JSON)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		
	    //保存导航分类
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/template/save-guid-cat.do?ajax=yes")
			 .param("floor_id", "2")
			 .param("cat_id", "88")
			 .contentType(MediaType.APPLICATION_JSON)
			 .accept(MediaType.APPLICATION_JSON)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		 
		//保存单个商品
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/template/save-each-goods.do?ajax=ye")
			 .param("floor_id", "12")
			 .param("index", "0")
			 .param("search_name", "哎呦喂和田红枣")
			 .param("cat_id", "1")
			 .param("new_goods_id", "267")
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

