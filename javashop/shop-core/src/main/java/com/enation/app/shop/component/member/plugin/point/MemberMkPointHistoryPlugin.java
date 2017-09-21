package com.enation.app.shop.component.member.plugin.point;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.plugin.IMemberTabShowEvent;
import com.enation.app.shop.core.member.service.IPointHistoryManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 会员消费积分历史插件
 * @author lzf<br/>
 * 2012-4-1下午10:34:27<br/>
 */
@Component
public class MemberMkPointHistoryPlugin extends AutoRegisterPlugin implements
		IMemberTabShowEvent {
	
	@Autowired
	private IPointHistoryManager pointHistoryManager;

	@Override
	public boolean canBeExecute(Member member) {
		return true;
	}

	@Override
	public int getOrder() {
		return 13;
	}

	@Override
	public String getTabName(Member member) {
		return "消费积分";
	}

	@Override
	public String onShowMemberDetailHtml(Member member) {
		List listPointHistory = pointHistoryManager.listPointHistory(member.getMember_id(),1);
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("listPointHistory",listPointHistory);
		freeMarkerPaser.setPageName("point_history");		
		return freeMarkerPaser.proessPageContent();
	}

}
