package com.enation.app.shop.test.admin;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.test.SpringTestSupport;

/** 
 *首页楼层管理--楼层编辑   单元测试类 
 * @author  LYH
 * @version 1.0
 * @since 6.2
 * @date 2016年12月11日 下午4:29:20  
 */

public class FloorEditTest extends SpringTestSupport {
	
	/**
	 * 登录后编辑父楼层
	 * @throws Exception 
	 */
	@Test
	public void floorParentEditTest() throws Exception{
		
		//管理员登录
		this.login();
		
		//模拟图片上传
        MockMultipartFile file = new MockMultipartFile("file", "face.jpg","multipart/form-data","some pic".getBytes());
		
        //编辑楼层
        mockMvc.perform(
			 MockMvcRequestBuilders.fileUpload("/core/admin/floor/save-edit.do?ajax=yes").file(file)
			 .param("title", "食品饮料编辑测试")
			 .param("page_id", "1")
			 .param("style", "new_storey")
			 .param("id", "1")
			 .param("parent_id", "0")
			 .param("is_display", "0")
			 .param("sort", "1")
			 .session(session)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		
	}
	/**
	 * 登录后编辑子楼层
	 * @throws Exception 
	 */
	@Test
	public void floorChildEditTest() throws Exception{
		
		//管理员登录
		this.login();
		
		//模拟图片上传
        MockMultipartFile file = new MockMultipartFile("file", "face.jpg","multipart/form-data","some pic".getBytes());
		
        //编辑楼层
        mockMvc.perform(
			 MockMvcRequestBuilders.fileUpload("/core/admin/floor/save-edit.do?ajax=yes").file(file)
			 .param("title", "食品单元测试")
			 .param("page_id", "1")
			 .param("style", "style2")
			 .param("id", "5")
			 .param("parent_id", "1")
			 .param("is_display", "0")
			 .param("sort", "1")
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