package com.enation.app.shop.test.admin;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.test.SpringTestSupport;

/** 
 * 楼层管理-楼层是否显示  单元测试类
 * @author LYH
 * @version V1.0
 * @since  V6.2
 *2016年12月7日 下午7:22:39 
 */
public class FloorIsdisplayTest extends SpringTestSupport {

	/**
	 * 登陆后直接修改父楼层-改为不显示状态
	 * @throws Exception
	 */
	@Test
	public void floorParentNoneTest() throws Exception{
       //管理员登录
		this.login();
	   
	   //父楼层不显示测试
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/floor/save-display.do")
			 .param("id", "2")
			 .param("is_display", "1")
			 .contentType(MediaType.APPLICATION_JSON)
			 .accept(MediaType.APPLICATION_JSON)
			 .session(session)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		
	}
	/**
	 * 登陆后直接修改父楼层-改为显示状态
	 * @throws Exception
	 */
	@Test
	public void floorParentDisplayTest() throws Exception{
       //管理员登录
		this.login();
	   
	   //父楼层不显示测试
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/floor/save-display.do")
			 .param("id", "4")
			 .param("is_display", "0")
			 .contentType(MediaType.APPLICATION_JSON)
			 .accept(MediaType.APPLICATION_JSON)
			 .session(session)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );
	}
	
	/**
	 * 登陆后直接修改子楼层-改为不显示状态
	 * @throws Exception
	 */
	@Test
	public void floorChildNoneTest() throws Exception{
       //管理员登录
		this.login();
	   
	   //父楼层显示条件下，子楼层可以不显示
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/floor/save-display.do")
			 .param("id", "11")
			 .param("is_display", "1")
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