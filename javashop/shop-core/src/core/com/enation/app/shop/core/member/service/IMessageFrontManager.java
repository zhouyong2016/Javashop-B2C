package com.enation.app.shop.core.member.service;

import java.util.Map;

import com.enation.framework.database.Page;

/**
 * 会员站内消息
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2016年12月5日 下午1:08:38
 */
public interface IMessageFrontManager {

	/**
	 * 获取站内发送的消息列表
	 * @param page
	 * @param pageSize
	 * @param memberId 会员标号
	 * @return
	 */
	public Page getMessagesFront(Integer page, Integer pageSize, Integer memberId);
	
	/**
	 * 获取未读的站内消息数量
	 * @return
	 */
	public int getMessageNoReadCount();

	/**
	 * 将消息放入回收站
	 * @param messageIds 消息标号集合如"1,2,3"或者一条消息标识如"1"
	 */
	public void editMessageInRecycle(String messageIds);
	
	/**
	 * 更新消息为已读
	 * @param messageIds 消息标号集合如"1,2,3"或者一条消息标识如"1"
	 */
	public void editMessageHaveRead(String messageIds);

	/**
	 * 获取消息详情
	 * @param messageId
	 * @return
	 */
	public Map getMessageDetail(Integer messageId);

	/**
	 * 获取回收站消息列表
	 * @param page
	 * @param pageSize
	 * @param memberId
	 * @return
	 */
	public Page getRecycleMessage(Integer page, Integer pageSize,Integer memberId);

	/**
	 * 删除消息
	 * @param messageIds
	 */
	public void deleteMessages(String messageIds);

	/**
	 * 查询此范围内的消息个数
	 * @param messageIds
	 * @return
	 */
	public Integer getCountByIds(String messageIds);

}
