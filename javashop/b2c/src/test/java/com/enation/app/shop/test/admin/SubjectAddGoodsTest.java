package com.enation.app.shop.test.admin;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.StringUtil;

/** 
 * 专题管理--专题设计，添加商品   单元测试类
 * @author  LYH
 * @version 1.0
 * @since 6.2
 * @date 2016年12月12日 下午10:23:25  
 */

public class SubjectAddGoodsTest extends SpringTestSupport{
	
	@Autowired
	private IDaoSupport daosupport;
	
	
	/**
	 * 登陆--添加专题--专题设计，添加商品
	 * @throws Exception 
	 */
	@Test
	public void subjectAddTest() throws Exception{
		//管理员登陆
		this.login();
		
		//模拟图片上传
        MockMultipartFile file = new MockMultipartFile("file", "face.jpg","multipart/form-data","some pic".getBytes());
		
        //添加专题
        mockMvc.perform(
			 MockMvcRequestBuilders.fileUpload("/core/admin/subject/save-add.do?ajax=yes").file(file)
			 .param("title", "添加专题管理")
			 .param("sort", "1")
			 .param("is_display", "0")
			 .session(session)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
        
        //专题设计，添加商品
        mockMvc.perform(
   			 MockMvcRequestBuilders.post("/core/admin/subject/save-add-goods.do")
   			 .param("g_temp_id", "")
   			 .param("g_temp_id", "")
   			 .param("g_temp_id", "")
   			 .param("goods_ids", "283")
   			 .param("goods_ids", "284")
   			 .param("goods_ids", "285")
   			 .param("subject_id",StringUtil.toString(this.daosupport.getLastId("es_subject")))
   			 .session(session)
   			 )
   		 	.andDo(MockMvcResultHandlers.print())  
   		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
	}
	/**
	 * 登陆--添加专题--专题设计，添加商品--删除商品
	 * @throws Exception 
	 */
	@Test
	public void subjectAddDeleteTest() throws Exception{
		//管理员登陆
		this.login();
		
		//模拟图片上传
        MockMultipartFile file = new MockMultipartFile("file", "face.jpg","multipart/form-data","some pic".getBytes());
		
        //添加专题
        mockMvc.perform(
			 MockMvcRequestBuilders.fileUpload("/core/admin/subject/save-add.do?ajax=yes").file(file)
			 .param("title", "添加专题管理")
			 .param("sort", "1")
			 .param("is_display", "0")
			 .session(session)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
        
        //专题设计，添加商品
        mockMvc.perform(
   			 MockMvcRequestBuilders.post("/core/admin/subject/save-add-goods.do")
   			.param("g_temp_id", "")
  			 .param("g_temp_id", "")
  			 .param("g_temp_id", "")
  			 .param("g_temp_id", "")
  			 .param("g_temp_id", "")
  			 .param("goods_ids", "282")
  			 .param("goods_ids", "283")
  			 .param("goods_ids", "281")
  			 .param("goods_ids", "284")
  			 .param("goods_ids", "285")
  			 .param("subject_id", StringUtil.toString(this.daosupport.getLastId("es_subject")))
  			 .session(session)
   			 .session(session)
   			 )
   		 	.andDo(MockMvcResultHandlers.print())  
   		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
        
        //删除所添加商品
        mockMvc.perform(
   			 MockMvcRequestBuilders.post("/core/admin/subject/delete-goods.do")
   			 .param("subject_id",StringUtil.toString(this.daosupport.getLastId("es_subject")))
   			 .param("index", "0")
   			 .session(session)
   			 )
   		 	.andDo(MockMvcResultHandlers.print())  
   		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
	}
	/**
	 * 登陆--添加专题--专题设计，添加商品--编辑商品
	 * @throws Exception 
	 */
	@Test
	public void subjectAddEditTest() throws Exception{
		//管理员登陆
		this.login();
		
		//模拟图片上传
        MockMultipartFile file = new MockMultipartFile("file", "face.jpg","multipart/form-data","some pic".getBytes());
		
        //添加专题
        mockMvc.perform(
			 MockMvcRequestBuilders.fileUpload("/core/admin/subject/save-add.do?ajax=yes").file(file)
			 .param("title", "添加专题管理")
			 .param("sort", "1")
			 .param("is_display", "0")
			 .session(session)
			 )
		 	.andDo(MockMvcResultHandlers.print())  
		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
        
        //专题设计，添加商品
        mockMvc.perform(
   			 MockMvcRequestBuilders.post("/core/admin/subject/save-add-goods.do")
   			 .param("g_temp_id", "")
  			 .param("g_temp_id", "")
  			 .param("g_temp_id", "")
  			 .param("g_temp_id", "")
  			 .param("g_temp_id", "")
  			 .param("goods_ids", "282")
  			 .param("goods_ids", "283")
  			 .param("goods_ids", "281")
  			 .param("goods_ids", "284")
  			 .param("goods_ids", "285")
  			 .param("subject_id", StringUtil.toString(this.daosupport.getLastId("es_subject")))
   			 .session(session)
   			 )
   		 	.andDo(MockMvcResultHandlers.print())  
   		 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
        
        //编辑所添加商品
        mockMvc.perform(
   			 MockMvcRequestBuilders.post("/core/admin/subject/save-edit-goods.do")
   			 .param("g_temp_id", "")
   			 .param("g_temp_id", "")
   			 .param("g_temp_id", "")
   			 .param("g_temp_id", "")
   			 .param("g_temp_id", "")
   			 .param("goods_ids", "276")
   			 .param("goods_ids", "280")
   			 .param("goods_ids", "277")
   			 .param("goods_ids", "278")
   			 .param("goods_ids", "279")
   			 .param("index", "0")
   			 .param("subject_id", StringUtil.toString(this.daosupport.getLastId("es_subject")))
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


