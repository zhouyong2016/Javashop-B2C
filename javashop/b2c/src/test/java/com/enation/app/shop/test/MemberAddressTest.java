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
 * 会员中心收货地址管理单元测试类
 * @author LYH
 * @version V1.0
 * @since  V6.2
 *2016年11月20日 下午6:56:39 
 */
public class MemberAddressTest extends SpringTestSupport {

	@Autowired
	private IDaoSupport daoSupport;

	/**
	 * 测试不登陆时进行操作，是没有权限的
	 * @throws Exception
	 */
	@Test
	public void memberAddressNotloginTest() throws Exception{

		//未登录无权限进行添加地址
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/member-address/add.do")
				.param("def_addr", "1")
				.param("name", "kill")
				.param("mobile", "15030658275")
				.param("tel", "06356877789")
				.param("province", "北京")
				.param("province_id", "1")
				.param("city", "海淀区")
				.param("city_id", "35")
				.param("region", "海淀区")
				.param("region_id", "4")
				.param("town", "海淀区")
				.param("town_id", "45")
				.param("addr", "111111")
				.param("zip", "230654")
				.param("shipAddressName", "家里")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())  
				.andExpect( MockMvcResultMatchers.content().bytes( "ajax 401 没有访问权限".getBytes() ));

		//未登录无权限进行修改
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/member-address/edit.do")
				.param("addr_id", "33")
				.param("def_addr", "0")
				.param("name", "koko")
				.param("mobile", "15030658275")
				.param("tel", "06356877789")
				.param("province", "北京")
				.param("province_id", "1")
				.param("city", "海淀区")
				.param("city_id", "35")
				.param("region", "海淀区")
				.param("region_id", "4")
				.param("town", "海淀区")
				.param("town_id", "45")
				.param("addr", "111111")
				.param("zip", "230654")
				.param("shipAddressName", "家里")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())  
				.andExpect( MockMvcResultMatchers.content().bytes( "ajax 401 没有访问权限".getBytes() )); 

		//未登录无权限进行删除
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/member-address/delete.do")
				.param("addr_id", "22")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())  
				.andExpect( MockMvcResultMatchers.content().bytes( "ajax 401 没有访问权限".getBytes() )); 

	}

	/**
	 * 登录-添加地址-修改地址-删除地址
	 * @throws Exception
	 */
	@Test
	public void memberAddressLoginTest() throws Exception{
		//进行登录
		this.login();

		//登录-添加地址
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/member-address/add.do")
				.param("def_addr", "1")
				.param("name", "kill")
				.param("mobile", "15030658275")
				.param("tel", "06356877789")
				.param("province", "北京")
				.param("province_id", "1")
				.param("city", "海淀区")
				.param("city_id", "35")
				.param("region", "海淀区")
				.param("region_id", "4")
				.param("town", "海淀区")
				.param("town_id", "45")
				.param("addr", "111111")
				.param("zip", "230654")
				.param("shipAddressName", "家里")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())  
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  

		//添加地址为默认地址,根据默认地址的唯一性def_addr=1,获取地址的addr_id
		int addr_id = this.daoSupport.queryForInt("select addr_id from es_member_address where def_addr=?", 1); 

		//登录-添加地址-修改地址
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/member-address/edit.do")
				.param("addr_id", ""+addr_id)
				.param("def_addr", "0")
				.param("name", "koko")
				.param("mobile", "15030658275")
				.param("tel", "06356877789")
				.param("province", "北京")
				.param("province_id", "1")
				.param("city", "海淀区")
				.param("city_id", "35")
				.param("region", "海淀区")
				.param("region_id", "4")
				.param("town", "海淀区")
				.param("town_id", "45")
				.param("addr", "111111")
				.param("zip", "230654")
				.param("shipAddressName", "家里")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())  
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  

		//登录-添加地址-修改地址-删除地址
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/member-address/delete.do")
				.param("addr_id", ""+addr_id)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
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
