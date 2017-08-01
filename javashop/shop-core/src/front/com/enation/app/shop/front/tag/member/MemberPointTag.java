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
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.member.service.IMemberPointManger;
import com.enation.app.shop.core.member.service.IPointHistoryManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 会员我的积分
 * @author whj
 *2014-02-17下午15:13:00
 */
@Component
@Scope("prototype")
public class MemberPointTag extends BaseFreeMarkerTag{

	@Autowired
	private IPointHistoryManager pointHistoryManager;
	
	@Autowired
	private IMemberLvManager memberLvManager;
	
	@Autowired
	private IMemberManager memberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		Map data = new HashMap();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();		 
		String action = request.getParameter("action");
		
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberPointTag]");
		}
		int memberid=member.getMember_id();
		member = this.memberManager.get(memberid);//获取当前最新的member对象
		
		if(member==null){
			member = this.memberManager.getDisabled(memberid);
		}
		if(action == null || action.equals("")){
			
			data.put("member",member);
			MemberLv memberLv =null;
			MemberLv nextLv =null;
			//当前等级
			try {
				memberLv= memberLvManager.get(member.getLv_id());
				data.put("memberLv",memberLv);
				//下一等级
				nextLv= memberLvManager.getNextLv(member.getPoint());
			} catch (NullPointerException e) {
				// TODO: handle exception
				throw new TemplateModelException("查询不到您的会员信息");
			}
			if(nextLv!=null){
				data.put("nextLv",nextLv);
			}else{
				data.put("nextLv",memberLv);
			}
			
		}else if(action.equals("list")){
			
		 
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 20;
			Page pointHistoryPage = pointHistoryManager.pagePointHistory(Integer
					.valueOf(page), pageSize);
			List pointHistoryList = (List) pointHistoryPage.getResult();
			pointHistoryList = pointHistoryList == null ? new ArrayList()
					: pointHistoryList;
	
			data.put("totalCount", pointHistoryPage.getTotalCount());
			data.put("pageSize", pageSize);
			data.put("pageNo", page);
			data.put("pointHistoryList", pointHistoryList);
			
			
			
		}else if(action.equals("freeze")){
			//冻结明细
		
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 20;
			Page pointHistoryPage = pointHistoryManager.pagePointFreeze(Integer
					.valueOf(page), pageSize);
			List pointFreezeList = (List) pointHistoryPage.getResult();
			pointFreezeList = pointFreezeList == null ? new ArrayList()
					: pointFreezeList;
	
			data.put("totalCount", pointHistoryPage.getTotalCount());
			data.put("pageSize", pageSize);
			data.put("pageNo", page);
			data.put("pointFreezeList", pointFreezeList);
			data.put("point", member.getPoint());
			
		}
		ThreadContextHolder.getSession().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
		return data;
	}

}
