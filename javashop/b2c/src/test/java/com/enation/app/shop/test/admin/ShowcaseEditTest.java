package com.enation.app.shop.test.admin;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.test.SpringTestSupport;

/** 
 * 橱窗管理--橱窗编辑  单元测试类
 * @author  LYH
 * @version 1.0
 * @since 6.2
 * @date 2016年12月11日 下午5:52:38  
 */

public class ShowcaseEditTest extends SpringTestSupport {
	
	/**
	 * 登陆-编辑橱窗
	 * @throws Exception
	 */
	@Test
	public void showcaseEditTest() throws Exception{
		
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
		
    	//编辑橱窗
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/showcase/save-edit.do")
			 .param("id", "1")
			 .param("title", "橱窗编辑测试")
			 .param("sort", "2")
			 .param("is_display", "1")
			 .param("flag", "index_top")
			 .param("content", "11,12,10,9")
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