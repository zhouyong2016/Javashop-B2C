package com.enation.app.shop.core.member.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.model.MessageFront;
import com.enation.app.shop.core.member.service.IMessageFrontManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;

/**
 * 前台站内消息
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2016年12月5日 下午1:07:01
 */
@Service
public class MessageFrontManager  implements IMessageFrontManager{

	@Autowired
	private IDaoSupport daoSupport;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMessageFrontManager#getMessagesFront(java.lang.Integer, int, java.lang.Integer)
	 */
	@Override
	public Page getMessagesFront(Integer pageNo, Integer pageSize, Integer memberId) {
		String sql = "select * from es_message_front where member_id=? and is_delete=1 order by msg_id desc ";
		return daoSupport.queryForPage(sql, pageNo, pageSize, memberId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMessageFrontManager#getMessageNoReadCount()
	 */
	@Override
	public int getMessageNoReadCount() {
		Member member  =UserConext.getCurrentMember();
		if(member==null){
			return 0;
		}
		String sql = "select count(0) from es_message_front where member_id =? and is_read =0 and is_delete =1 ";
		return daoSupport.queryForInt(sql.toString(), member.getMember_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMessageFrontManager#editMessageInRecycle(java.lang.String)
	 */
	@Override
	public void editMessageInRecycle(String messageIds) {
		Map map = new HashMap<String, Object>();
		map.put("is_delete", 0);
		daoSupport.update("es_message_front", map , " msg_id in ("+messageIds+")");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMessageFrontManager#editMessageHaveRead(java.lang.String)
	 */
	@Override
	public void editMessageHaveRead(String messageIds) {
		Map map = new HashMap<String, Object>();
		map.put("is_read", 1);
		daoSupport.update("es_message_front", map , " msg_id in ("+messageIds+")");
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMessageFrontManager#getMessageDetail(java.lang.Integer)
	 */
	@Override
	public Map getMessageDetail(Integer messageId) {
		String sql = "select * from es_message_front where msg_id= ?";
		Map map = daoSupport.queryForMap(sql, messageId);
		return map;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMessageFrontManager#getRecycleMessage(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page getRecycleMessage(Integer page, Integer pageSize, Integer memberId) {
		
		String sql = "select * from es_message_front where member_id=? and is_delete=0 order by msg_id desc ";
		
		return daoSupport.queryForPage(sql, page, pageSize, memberId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMessageFrontManager#deleteMessages(java.lang.String)
	 */
	@Override
	public void deleteMessages(String messageIds) {
		
		String sql = "delete from es_message_front where msg_id in ("+messageIds+")";
		
		daoSupport.execute(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMessageFrontManager#getCountByIds(java.lang.String)
	 */
	@Override
	public Integer getCountByIds(String messageIds) {
		Member member  =UserConext.getCurrentMember();
		if(member==null){
			return 0;
		}
		String sql = "select count(0) from es_message_front where  member_id =? and msg_id in ("+messageIds+")";
		
		return daoSupport.queryForInt(sql.toString(), member.getMember_id());
	}
}
