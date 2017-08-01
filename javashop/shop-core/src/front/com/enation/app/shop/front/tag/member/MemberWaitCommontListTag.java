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
import com.enation.app.shop.core.member.service.IMemberOrderItemManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 会员中心等待评论商品列表     
 * @author  whj
 * @version v1.1,2015-12-17 whj
 */
@Component
@Scope("prototype")
public class MemberWaitCommontListTag extends BaseFreeMarkerTag{
	
	@Autowired
	private IMemberCommentManager memberCommentManager;
	
	@Autowired
	private IMemberOrderItemManager memberOrderItemManager;
	
	/**
	 *  @会员中心等待评论商品列表
	 *  @param member 当前登录会员
	 *  @param page 等待评论商品分页列表
	 *  @param goodsnum 每页展示商品数量
	 */

	@Override
	protected Object exec(Map params) throws TemplateModelException {
	
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberWaitCommontListTag]");
		}
		Integer pageSize = (Integer)params.get("goodsnum");
		//判断如果不传递每页展示商品数量，默认每页10个商品
		if(pageSize == null){
			 pageSize = 10;
		}

		Map result = new HashMap();
		
		Page goodsPage = memberOrderItemManager.getGoodsList(member.getMember_id(), 0, Integer.valueOf(page), pageSize);
		
		List waitcommentsList = (List) goodsPage.getResult();
		waitcommentsList = waitcommentsList == null ? new ArrayList() : waitcommentsList;
		
		result.put("goodsPage", goodsPage);
		result.put("totalCount", goodsPage.getTotalCount());
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("waitcommentsList", waitcommentsList);
		return result;
	}


}
