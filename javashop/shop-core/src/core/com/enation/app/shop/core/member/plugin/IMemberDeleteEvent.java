package com.enation.app.shop.core.member.plugin;

import com.enation.app.base.core.model.Member;

/**
 * 会员删除事件
 * @author zh
 * @version 1.0
 * @since v61
 * 2016年09月23号上午 9点10分
 */
public interface IMemberDeleteEvent {
	/**
	 * 会员删除
	 * @param member 会员信息
	 */
	public void delteMember(Member member);


}
