package com.enation.app.shop.core.member.plugin;

import com.enation.app.shop.core.member.model.MemberComment;

public interface IMemberCommentEvent {
	public void onMemberComment(MemberComment comment);
}
