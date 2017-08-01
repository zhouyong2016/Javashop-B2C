package com.enation.app.shop.test.admin;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.StringUtil;
/**
 * 
 * 添加优惠券、修改优惠券
 * @author yinchao
 * @version v1.0
 * @since v6.0
 * 2016年11月22日 下午3:18:30 
 *
 */
@Rollback(true)
public class BonusTestError extends SpringTestSupport{
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/**
	 * 登录后台，添加优惠券
	 * @throws Exception
	 */
	@Test
	public void testAddBouns() throws Exception{
		
		//管理员登录
		this.login();
		
		//清空优惠券表
		this.daoSupport.execute("TRUNCATE table es_bonus_type");
		
		//按用户发放优惠券
		mockMvc.perform(
			MockMvcRequestBuilders.post("/shop/admin/bonus-type/save-add.do")
			.param("min_amount","")
			.param("min_goods_amount", "50.00")
			.param("recognition", "20161123")
			.param("sendTimeEnd", "2016-12-23")
			.param("sendTimeStart", "2016-11-23")
			.param("send_type","0")
			.param("type_money", "10")
			.param("type_name", "测试按用户发放优惠券")
			.param("useTimeEnd", "2016-12-23")
			.param("useTimeStart", "2016-11-23")
			.session(session)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  
	}
	
	/**
	 * 对发放的优惠券，进行修改
	 * @throws Exception
	 */
	@Test
	public void testEditBouns() throws Exception{
		
		//管理员登录
		this.login();
		
		//共用添加优惠券
		this.testAddBouns();
		
		//修改按用户发放的优惠券
		mockMvc.perform(
				MockMvcRequestBuilders.post("/shop/admin/bonus-type/save-edit.do")
				.param("min_goods_amount", "60.00")
				.param("recognition", "20161125")
				.param("sendTimeEnd", "2016-12-23")
				.param("sendTimeStart", "2016-11-23")
				.param("send_type", "0")
				.param("type_id","1")
				.param("type_money", "20.00")
				.param("type_name", "修改测试优惠券")
				.param("useTimeEnd", "2017-01-01")
				.param("useTimeStart", "2016-11-23")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
	
	}
	/**
	 * 对发放的优惠券进行删除
	 * @throws Exception
	 */
	@Test
	public void testDeleteBouns() throws Exception{
		
		//管理员登录
		this.login();
		
		//共用添加优惠券
		this.testAddBouns();
		
		//获取最后一个bonusid
		int bonusid=this.daoSupport.getLastId("es_member_bonus");
		
		//获取最后一个typeid
		int typeid=this.daoSupport.getLastId("es_bonus_type");
		
		//删除优惠券
		mockMvc.perform(
				MockMvcRequestBuilders.post("/shop/admin/bonus-type/delete.do")
				.param("ajax", "yes")
				.param("type_id", StringUtil.toString(typeid))
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
	}
	
	/**
	 * 共用的登录方法
	 * @throws Exception
	 */
	private void login() throws Exception{
		
		//--------------登录----------------------
		//执行验证码请求
		mockMvc.perform(MockMvcRequestBuilders.get("/api/validcode/create.do?vtype=admin"));
		
		//管理员登录api测试
		mockMvc.perform(
				MockMvcRequestBuilders.post("/core/admin/admin-user/login.do")
				.param("username", "admin")
				.param("password", "admin")
				.param("valid_code", "1111")
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1));
	}
}
