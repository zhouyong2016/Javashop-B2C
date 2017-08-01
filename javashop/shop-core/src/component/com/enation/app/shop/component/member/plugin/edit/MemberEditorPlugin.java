package com.enation.app.shop.component.member.plugin.edit;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.member.plugin.IMemberTabShowEvent;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.IAjaxExecuteEnable;

@Component
public class MemberEditorPlugin extends AutoRegisterPlugin implements IMemberTabShowEvent,IAjaxExecuteEnable  {

	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IMemberLvManager memberLvManager;
	/**
	 *异步请求走这里
	 */
	@Override
	public String execute() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String action=request.getParameter("action");
		//待王峰稍后完善
		return null;
	}
	
	
	/**
	 * 首先显示面板内容及异步的脚本
	 */
	@Override
	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		List lvlist = this.memberLvManager.list();

		Integer is_lvmember = 0;
		// 判定是否是b2b2c
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			// 如果为0，则隐藏会员等级相关信息
			is_lvmember = 0;
		} else {
			is_lvmember = 1;
		}
		
		freeMarkerPaser.putData("is_lvmember", is_lvmember);
		freeMarkerPaser.putData("memberid",member.getMember_id());
		freeMarkerPaser.putData("lvlist",lvlist);
		freeMarkerPaser.setPageName("editor");
		
		return freeMarkerPaser.proessPageContent();
	}

	
	
	@Override
	public String getTabName(Member member) {
		
		return "编辑会员";
	}

	@Override
	public int getOrder() {
		
		return 3;
	}

	@Override
	public boolean canBeExecute(Member member) {
		
		return true;
	}
}
