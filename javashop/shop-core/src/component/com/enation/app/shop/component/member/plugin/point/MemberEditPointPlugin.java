package com.enation.app.shop.component.member.plugin.point;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.member.model.PointHistory;
import com.enation.app.shop.core.member.plugin.IMemberTabShowEvent;
import com.enation.app.shop.core.member.service.IPointHistoryManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.IAjaxExecuteEnable;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 后台会员积分插件
 * @author lzf<br/>
 * 2012-4-5下午04:11:58<br/>
 */
@Component
public class MemberEditPointPlugin extends AutoRegisterPlugin implements
IMemberTabShowEvent, IAjaxExecuteEnable {
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IPointHistoryManager pointHistoryManager;

	public boolean canBeExecute(Member member) {
		return true;
	}

	public int getOrder() {
		return 9;
	}

	public String getTabName(Member member) {
		return "他的积分";
	}

	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("memberid",member.getMember_id());		 
		freeMarkerPaser.setPageName("point");
		return freeMarkerPaser.proessPageContent();
	}

	@Transactional(propagation = Propagation.REQUIRED) 
	public String execute() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int point = StringUtil.toInt(request.getParameter("modi_point"),true);
		int memberid  = StringUtil.toInt(request.getParameter("memberid"),true);
		Member member = this.memberManager.get(memberid);
		member.setPoint(member.getPoint() + point);
		PointHistory pointHistory = new PointHistory();
		pointHistory.setMember_id(memberid);
		pointHistory.setOperator("管理员");
		pointHistory.setPoint(point);
		pointHistory.setReason("operator_adjust");
		pointHistory.setTime(DateUtil.getDateline());
		try {
			memberManager.edit(member);
			pointHistoryManager.addPointHistory(pointHistory);
			return JsonMessageUtil.getSuccessJson("会员积分修改成功");
		} catch (Exception e) {
			this.logger.error("会员积分修改失败",e);
			return JsonMessageUtil.getErrorJson("修改失败"); 
		}
	}

}
