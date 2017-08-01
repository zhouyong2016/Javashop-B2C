package com.enation.app.shop.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;

/** 
* 会员中心我的收藏单元测试类
* @author LYH
* @version V1.0
* @since  V6.2
*2016年11月8日 下午7:36:09 
*/
public class MemberFavoriteTest extends SpringTestSupport{
    
	@Autowired
	private IDaoSupport daoSupport;
	
	/**
	 * 测试不登陆进行收藏商品，没有权限
	 * @throws Exception
	 */
	@Test
	public void memberFavoriteNologionTest() throws Exception{
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/collect/addCollect.do")
				 .param("goods_id", "22")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
		         .andDo(MockMvcResultHandlers.print())  
	 	         .andExpect( MockMvcResultMatchers.content().bytes( "ajax 401 没有访问权限".getBytes() ));
	}
	
	/**
	 * 收藏测试:登录-添加收藏-删除此商品的收藏
	 * @throws Exception
	 */
	@Test
	public void memberFavoriteLogionTest() throws Exception{
		//进行登录
		this.login();
		
		//登录后收藏商品
		mockMvc.perform(
				 MockMvcRequestBuilders.get("/api/shop/collect/add-collect.do")
				 .param("goods_id","11")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
		         .andDo(MockMvcResultHandlers.print())  
		         .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  
		
 		//根据goods_id获取此商品的 favorite_id
		int favorite_id = this.daoSupport.queryForInt("select favorite_id from es_favorite where goods_id=?", 11);

 		//收藏商品后删除此商品收藏
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/favorite/delete.do")
				 .param("favorite_id", ""+favorite_id)
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
		         .andDo(MockMvcResultHandlers.print())  
		         .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  
		
	}

	/**
	 * 登陆后直接删除收藏
	 * @throws Exception
	 */
	@Test
	public void memberCancelFavoriteLoginTest() throws Exception{
		//进行登录
		this.login();
		
		//登录后删除收藏
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/favorite/delete.do")
				 .param("favorite_id", "23")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
		         .andDo(MockMvcResultHandlers.print())  
		         .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  
		
	}
	/**
	 * 未登陆直接删除收藏是没有权限的
	 * @throws Exception
	 */
	@Test
	public void memberCancelFavoriteNotloginTest() throws Exception{
		
		//登录后删除收藏
		mockMvc.perform(
				 MockMvcRequestBuilders.post("/api/shop/favorite/delete.do")
				 .param("favorite_id", "23")
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				 )
		         .andDo(MockMvcResultHandlers.print())  
		         .andExpect( MockMvcResultMatchers.content().bytes( "ajax 401 没有访问权限".getBytes() ));
		
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
 