package com.enation.app.shop.component.member.plugin.remark;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.member.plugin.IMemberTabShowEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.plugin.IAjaxExecuteEnable;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员备注插件
 * @author kingapex
 *2012-4-1下午5:12:29
 */
@Component
public class MemberRemarkPlugin extends AutoRegisterPlugin implements IMemberTabShowEvent,IAjaxExecuteEnable{

	@Autowired
	private IMemberManager memberManager;
	/**
	 * 响应异步请求
	 */
	@Override
	public String execute() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String modify_memo = request.getParameter("modify_memo");
		int memberid  = StringUtil.toInt(request.getParameter("memberid"),true);
		Member member = this.memberManager.get(memberid);
		member.setRemark(modify_memo);
		try {
			memberManager.edit(member);
			return JsonMessageUtil.getSuccessJson("会员备注修改成功");
		} catch (Exception e) {
			 this.logger.error("修改会员备注",e);
			return JsonMessageUtil.getErrorJson("会员备注修改失败"); 
		}
	}

	
	
	
	/**
	 * 定义tab的名称
	 */
	@Override
	public String getTabName(Member member) {
		
		return "会员备注";
	}

	
	/**
	 * 定义tab显次序为7
	 */
	@Override
	public int getOrder() {
		
		return 27;
	}

	
	/**
	 * 在这里，我们可以根据会员的情况定义是否执行此插件
	 */
	@Override
	public boolean canBeExecute(Member member) {
		
		return true;
	}

	
	/**
	 * 响应会员详细信息显示事件，先显示出一个容器，然后在点击相应的tab页时加载会员备注。
	 * 这样做的目的是为了避免在显示会员详细信息时一次性多过的查询数据库（通过tab页切换时再加载一次方式）。
	 * 
	 */
	@Override
	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.putData("memberid",member.getMember_id());		 //为页面put变量 
		freeMarkerPaser.setPageName("remark");//解析此类同级目录中的remark.html
		return freeMarkerPaser.proessPageContent();//返回上述页面的内容作为tab页的内容

	}
}
