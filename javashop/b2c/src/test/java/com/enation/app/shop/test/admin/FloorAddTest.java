package com.enation.app.shop.test.admin;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.test.SpringTestSupport;

/** 
 * 楼层管理-添加楼层  单元测试类
 * @author LYH
 * @version V1.0
 * @since  V6.2
 *2016年12月10日 下午22:09:39 
 */
public class FloorAddTest extends SpringTestSupport{
	
	/**
	 * 管理员登陆后直接添加父楼层
	 * @throws Exception
	 */
	@Test
	public void floorParentAddTest() throws Exception{
		
		//管理员登陆
		this.login();
		
		//模拟图片上传
        MockMultipartFile file = new MockMultipartFile("file", "face.jpg","multipart/form-data","some pic".getBytes());
		
        //添加楼层
        mockMvc.perform(
			 MockMvcRequestBuilders.fileUpload("/core/admin/floor/save-add.do?ajax=yes").file(file)
			 .param("title", "添加父楼层测试")
			 .param("page_id", "1")
			 .param("style", "new_storey")
			 .param("is_display", "0")
			 .param("sort", "1")
			 .session(session)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
	}
	/**
	 * 管理员登陆后台直接添加子楼层
	 * @throws Exception
	 */
	@Test
	public void fllorChildAddTest() throws Exception{
		//管理员登陆
		this.login();
		
		//模拟图片上传
        MockMultipartFile file = new MockMultipartFile("file", "face.jpg","multipart/form-data","some pic".getBytes());
		
        //添加楼层
        mockMvc.perform(
			 MockMvcRequestBuilders.fileUpload("/core/admin/floor/save-add.do?ajax=yes").file(file)
			 .param("title", "添加子楼层测试")
			 .param("page_id", "1")
			 .param("style", "style1")
			 .param("parent_id", "2")
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
