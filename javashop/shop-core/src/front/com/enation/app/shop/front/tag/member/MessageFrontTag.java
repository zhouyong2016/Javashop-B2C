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
import com.enation.app.shop.core.member.service.IMessageFrontManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * 会员站内消息列表标签 
 * @author fk
 * @version v1.0
 * @since v6.1
 * 2016年12月5日 下午1:02:32
 */
@Component
@Scope("prototype")
public class MessageFrontTag  extends BaseFreeMarkerTag{

	@Autowired
	private IMessageFrontManager messageFrontManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page");
		String status = request.getParameter("status");
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MessageFrontTag]");
		}
		
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 5;
		Page messagePage;
		if("1".equals(status)){//回收站列表
			
			messagePage = messageFrontManager.getRecycleMessage(Integer.valueOf(page), pageSize,member.getMember_id());
		
		}else{
			status = "0";
			messagePage = messageFrontManager.getMessagesFront(Integer.valueOf(page), pageSize,member.getMember_id());
		}
		Long totalCount = messagePage.getTotalCount();

		Map result = new HashMap();
		List messagesList = (List) messagePage.getResult();
		messagesList = messagesList == null ? new ArrayList() : messagesList;

		
		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("messagesList", messagesList);
		result.put("status", status);
		return result;
	}

}
