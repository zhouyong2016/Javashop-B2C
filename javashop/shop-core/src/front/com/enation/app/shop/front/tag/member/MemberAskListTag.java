package com.enation.app.shop.front.tag.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.service.IMemberCommentManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 会员我的咨询列表标签
 * @author whj
 *2014-02-18下午15:13:00
 */
@Component
@Scope("prototype")
public class MemberAskListTag extends BaseFreeMarkerTag{

	@Autowired
	private IMemberCommentManager memberCommentManager;
	/**
	 *  member 当前登录会员
	 *  commentsList 我的咨询列表
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page");
		
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberAskListTag]");
		}
		
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 5;
		Page commentsPage = memberCommentManager.getMemberComments(Integer.valueOf(page), pageSize,2,member.getMember_id());
		Long totalCount = commentsPage.getTotalCount();

		Map result = new HashMap();
		List commentsList = (List) commentsPage.getResult();
		commentsList = commentsList == null ? new ArrayList() : commentsList;

		
		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("commentsList", commentsList);
		return result;
	}
	
}
