package com.enation.app.shop.core.member.plugin;
import com.enation.app.base.core.model.Member;

/**
 * 会员修改事件
 * @author xulipeng
 * @version v1.0 
 * @since v6.2.1
 */
public interface IMemberEditEvent {
	
	/**
	 * 会员修改
	 * @param member
	 */
	public void onEditMember(Member member);

}
