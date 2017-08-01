package com.enation.app.shop.component.member.plugin.base;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.plugin.IMemberTabShowEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 会员基本信息插件
 * @author kingapex
 *2012-4-1上午11:45:35
 */

@Component
public class MemberDetailBasePlugin extends AutoRegisterPlugin implements IMemberTabShowEvent {
	/**
	 * @param member 会员
	 */
	@Override
	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("member",member);
		freeMarkerPaser.setPageName("base");
		return freeMarkerPaser.proessPageContent();
	 
	}

	@Override
	public String getTabName(Member member) {
	 
		return "基本信息";
	}

	@Override
	public int getOrder() {
		 
		return 1;
	}

	@Override
	public boolean canBeExecute(Member member) {
		 
		return false;
	}

 

}
