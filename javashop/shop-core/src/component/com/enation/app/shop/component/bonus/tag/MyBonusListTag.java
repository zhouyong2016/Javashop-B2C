package com.enation.app.shop.component.bonus.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 个人中心—我的所有优惠券列表（包含可用 和 不可用）
 * @author xulipeng
 * @version v1.0
 * @since v6.2.1
 * 2017年01月03日17:43:39
 */
@Component
@Scope("prototype")
public class MyBonusListTag extends BaseFreeMarkerTag {

	@Autowired
	private IBonusManager bonusManager;
	
	/**
	 * @param	is_usable 1为可用，0为不可用，2为全部  默认为2
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		Member member = UserConext.getCurrentMember();
		if(member ==null){
			return ("未登录，不能使用此api");
		}
		
		Map result = new HashMap();
		
		Integer is_usable = (Integer) params.get("is_usable");
		if(is_usable==null){
			is_usable=2;
		}
		
		//第几页
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		
		//每页多少条
		String pageSize = request.getParameter("pageSize");
		pageSize = (pageSize == null || pageSize.equals("")) ? "10" : pageSize;
		
		Page webpage  = bonusManager.getMemberBonusList(member.getMember_id(),Integer.parseInt(page),Integer.parseInt(pageSize),is_usable);
		
		Long totalCount = webpage.getTotalCount();
		List<Map> bonusList = (List) webpage.getResult();
		bonusList = bonusList == null ? new ArrayList() : bonusList;
		
		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("bonusList", bonusList);
		
		return  result;
	}

}
