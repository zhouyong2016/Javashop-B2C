package com.enation.app.shop.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.FileUtil;

/** 
* 会员中心基本资料修改单元测试类
* @author LYH
* @version V1.0
* @since  V6.2
*2016年11月7日 下午6:56:39 
*/
public class MemberInfoUpdateTest extends SpringTestSupport{
	
	/**
	 * 测试不登陆进行修改，是没有权限的
	 * @throws Exception
	 */
	@Test
	public void memberInfoNologionTest() throws Exception{
		
		//未登录进行访问无权进行修改
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/member/save-info.do")
				 .param("file", "")
				 .param("truename", "kill")
				 .param("sex", "2")
				 .param("mybirthday", "2016-02-15")
				 .param("province", "北京")
				 .param("province_id", "1")
				 .param("city", "海淀区")
				 .param("city_id", "35")
				 .param("region", "海淀区")
				 .param("region_id", "453")
				 .param("address", "111111")
				 .param("email", "1111@qq.com")
				 .param("zip", "230654")
				 .param("tel", "06356877715")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
		         .andDo(MockMvcResultHandlers.print())  
	 	         .andExpect( MockMvcResultMatchers.content().bytes( "ajax 401 没有访问权限".getBytes() ));
	}
	
	/**
	 * 登陆后进行修改信息测试
	 * @throws Exception
	 */
	@Test
	public void memberInfoLogionTest() throws Exception{
		//进行登录
        this.login();
        
        //模拟图片上传
        MockMultipartFile file = new MockMultipartFile("file", "face.jpg","multipart/form-data","some pic".getBytes()); 
        
        
		//登录后进行修改
		mockMvc.perform(
				 MockMvcRequestBuilders.fileUpload("/api/shop/member/save-info.do").file(file)
				 .param("truename", "kill")
				 .param("sex", "2")
				 .param("mybirthday", "2016-02-15")
				 .param("province", "北京")
				 .param("province_id", "1")
				 .param("city", "海淀区")
				 .param("city_id", "35")
				 .param("region", "海淀区")
				 .param("region_id", "453")
				 .param("address", "111111")
				 .param("email", "1111@qq.com")
				 .param("zip", "230654")
				 .param("tel", "06356877715")
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
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=memberlogin"));
		
		//登陆api测试
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/member/login.do")
				 .param("username", "kingapex")
				 .param("password", "wangfeng")
				 .param("validcode", "1111")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
			 	.andDo(MockMvcResultHandlers.print())  
			 	.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  
		
		
				
	}
	
}
 