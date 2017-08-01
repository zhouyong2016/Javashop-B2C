package com.enation.app.shop.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;

/**
 * 
 * 会员中心站内消息(管理员)单元测试
 * @author fk
 * @version v1.0
 * @since v6.1
 * 2016年12月5日 下午12:58:11
 */
@Transactional(propagation=Propagation.NOT_SUPPORTED)
public class MessageBgTest extends SpringTestSupport {

	@Autowired
	private IDaoSupport daoSupport;

	/**
	 * 测试消息发送
	 * @throws Exception
	 */
	@Test
	public void messageBgAddLoginTest() throws Exception{
		//进行登录
		this.login();

		//登录-添加地址
		mockMvc.perform(
				MockMvcRequestBuilders.post("/shop/admin/message/save-message.do")
				.param("msg_title", "单元测试消息标题")
				.param("msg_content", "单元测试消息内容")
				.param("send_type", "1")
				.param("member_ids", "1")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.session(session)
				)
				.andDo(MockMvcResultHandlers.print())  
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );  
		
		Thread.sleep(3000);//会员消息为异步添加，所以可能数据还未添加就开始以下操作,所以先睡会
		//会员登录
		this.memberLogin();
		
		int msgId = daoSupport.queryForInt("select max(msg_id) from es_message_front where member_id=1");
		
		//消息标记为已读
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/messageFront/have-read.do")
				.param("messageids", msgId+"")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.session(session)
				)
				.andDo(MockMvcResultHandlers.print())  
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );
		
		
		//登录-放入回收站
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/messageFront/in-recycle.do")
				.param("messageids",msgId+"")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.session(session)
				)
				.andDo(MockMvcResultHandlers.print())  
				.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );
		
		
		//消息删除
		mockMvc.perform(
				MockMvcRequestBuilders.post("/api/shop/messageFront/msg-delete.do")
				.param("messageids",msgId+"")
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
	
	/**
	 * 共用的登陆方法
	 * @throws Exception
	 */
	private void memberLogin() throws Exception{
		
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
