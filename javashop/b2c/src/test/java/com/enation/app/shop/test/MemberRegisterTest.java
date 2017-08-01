package com.enation.app.shop.test;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.test.SpringTestSupport;

/** 
* 会员注册单元测试类
* @author LYH
* @version V1.0
* @since  V6.2
*2016年11月6日 下午6:19:38 
*/
public class MemberRegisterTest extends SpringTestSupport {

	//会员邮箱注册
	@Test
	public void registerEmailTest() throws Exception{

		//执行验证码请求		
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=memberreg"));
		
		//注册api正确测试
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/member/register.do")
				 .param("username", "kobe1")
				 .param("email", "999@qq.com")
				 .param("password", "111111")
				 .param("reg_password", "111111")
				 .param("validcode", "1111")
				 .param("license", "agree")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
		
		//注册api错误测试
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/member/register.do")
				 .param("username", "kobe")
				 .param("email", "122@qq.com")
				 .param("password", "111111")
				 .param("reg_password", "111111")
				 .param("validcode", "1111")
				 .param("license", "agree")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) ); 
	}
}
 