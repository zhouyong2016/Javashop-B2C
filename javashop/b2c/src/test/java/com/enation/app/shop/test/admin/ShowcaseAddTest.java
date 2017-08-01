package com.enation.app.shop.test.admin;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.test.SpringTestSupport;

/** 
 * 橱窗管理--增加橱窗
 * @author  LYH
 * @version 1.0
 * @since 6.2
 * @date 2016年12月11日 下午5:35:52  
 */

public class ShowcaseAddTest extends SpringTestSupport {
	
	
	/**
	 * 登录--添加橱窗
	 * @throws Exception
	 */
	@Test
	public void showcaseAddTest() throws Exception{
		
	//管理员登陆
	this.login();
	
	//添加橱窗
	mockMvc.perform(
		 MockMvcRequestBuilders.post("/core/admin/showcase/save-showcase.do")
		 .param("title", "添加橱窗测试")
		 .param("sort", "1")
		 .param("is_display", "0")
		 .param("flag", "index_top")
		 .param("content", "285,284,279")
		 .contentType(MediaType.APPLICATION_JSON)
		 .accept(MediaType.APPLICATION_JSON)
		 .session(session)
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
