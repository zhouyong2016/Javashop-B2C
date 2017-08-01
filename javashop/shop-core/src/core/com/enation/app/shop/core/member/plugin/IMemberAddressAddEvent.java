package com.enation.app.shop.core.member.plugin;

import com.enation.app.shop.core.member.model.MemberAddress;

/**
 * 会员地址添加事件
 * @author kingapex
 *2013-7-8下午5:55:51
 */
public interface IMemberAddressAddEvent {
	public void addressAdd(MemberAddress address);
}
