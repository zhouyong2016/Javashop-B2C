package com.enation.app.shop.component.member.plugin.comments;


import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.plugin.IMemberTabShowEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 他的评论
 * @author lzf<br/>
 * 2012-4-5下午05:15:19<br/>
 */
@Component
public class MemberDiscussPlugin extends AutoRegisterPlugin implements
		IMemberTabShowEvent {
	

	@Override
	public boolean canBeExecute(Member member) {
		return true;
	}

	@Override
	public int getOrder() {
		return 19;
	}

	@Override
	public String getTabName(Member member) {
		return "他的评论";
	}
	/**
	 * @param member 会员
	 * listComments 会员评论列表
	 */
	@Override
	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("member_id",member.getMember_id());
		freeMarkerPaser.putData("type", 1);
		freeMarkerPaser.setPageName("discuss_comments");	
		return freeMarkerPaser.proessPageContent();
	}

}
