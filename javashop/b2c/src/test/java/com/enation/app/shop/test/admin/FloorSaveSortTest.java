package com.enation.app.shop.test.admin;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.test.SpringTestSupport;

/** 
 * 楼层管理-保存排序  单元测试类
 * @author LYH
 * @version V1.0
 * @since  V6.2
 *2016年12月6日 下午7:22:39 
 */
public class FloorSaveSortTest extends SpringTestSupport {
	
	/**
	 * 登陆后保存排序
	 * @throws Exception
	 */
	@Test
	public void floorSaveSortTest() throws Exception{
		
		//管理员登陆
		this.login();
		
		//保存排序(直接保存排序或者修改排序之后保存排序)
		mockMvc.perform(
			 MockMvcRequestBuilders.post("/core/admin/floor/save-sort.do")
			 .param("floor_sorts", "2")
			 .param("floor_ids", "1")
			 .param("floor_sorts", "2")
			 .param("floor_ids", "2")
			 .param("floor_sorts", "2")
			 .param("floor_ids", "3")
			 .param("floor_sorts", "2")
			 .param("floor_ids", "4")
			 .param("floor_sorts", "2")
			 .param("floor_ids", "5")
			 .param("floor_sorts", "2")
			 .param("floor_ids", "6")
			 .param("floor_sorts", "2")
			 .param("floor_ids", "7")
			 .param("floor_sorts", "2")
			 .param("floor_ids", "9")
			 .param("floor_sorts", "2")
			 .param("floor_ids", "11")
			 .param("floor_sorts", "2")
			 .param("floor_ids", "12")
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