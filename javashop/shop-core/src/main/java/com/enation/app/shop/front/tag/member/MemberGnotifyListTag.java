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
import com.enation.app.shop.core.goods.service.IGnotifyManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;



/**
 * 会员缺货登记列表标签
 * @author whj
 *2014-02-26 上午10：18
 */
@Component
@Scope("prototype")
public class MemberGnotifyListTag extends BaseFreeMarkerTag{

	@Autowired
	private IGnotifyManager gnotifyManager;

	@Override
	public Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberPointTag]");
		}
		
		Map result = new HashMap();

		String page = request.getParameter("page");
		page = page == null ? "1" : page;
		int pageSize = 20;
		Page gnotifyPage = gnotifyManager.pageGnotify(
				Integer.valueOf(page), pageSize);
		Long totalCount = gnotifyPage.getTotalCount();
		Long pageCount = gnotifyPage.getTotalPageCount();
		List gnotifyList = (List) gnotifyPage.getResult();
		gnotifyList = gnotifyList == null ? new ArrayList() : gnotifyList;
		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
	 
		result.put("gnotifyList", gnotifyList);
		result.put("pageCount", pageCount);
		return result;
	}

	
}	
