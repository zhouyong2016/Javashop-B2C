package com.enation.app.shop.core.member.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.member.model.MessageBg;
import com.enation.framework.database.Page;

/**
 * 
 * 后台站内消息
 * @author fk
 * @version v1.0
 * @since v6.1
 * 2016年12月5日 下午1:29:27
 */
public interface IMessageBgManager {

	
	/**
	 * 管理员添加消息
	 * @param messageBg
	 * @return 0：添加失败，1：添加成功
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public void add(MessageBg messageBg) throws Exception;

	/**
	 * 在后台显示所有的历史消息
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page getAllMessage(int page, int pageSize);
}
