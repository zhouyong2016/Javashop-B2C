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
import com.enation.app.shop.core.member.model.MemberOrderItem;
import com.enation.app.shop.core.member.service.IMemberOrderItemManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.service.IMemberOrderManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 
 * (会员未评论订单标签) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2017年1月5日 上午12:13:05
 */
@Component
@Scope("prototype")
public class MemberWaitCommontOrderListTag extends BaseFreeMarkerTag{
	@Autowired
	private IMemberOrderManager memberOrderManager;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IMemberOrderItemManager memberOrderItemManager;
	
	@SuppressWarnings("unchecked")
	@Override
	public Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();

		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[MemberOrderListTag]");
		}
		Map result = new HashMap();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 10;
		Page ordersPage = memberOrderManager.pageCommentOrders(Integer.valueOf(page), pageSize);	
		Long totalCount = ordersPage.getTotalCount();
		List ordersList = (List) ordersPage.getResult();
		ordersList = ordersList == null ? new ArrayList() : ordersList;
		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("ordersList", ordersList);
		Map<String,Object> orderstatusMap=OrderStatus.getOrderStatusMap();
		for (String orderStatus: orderstatusMap.keySet()) {
			result.put(orderStatus, orderstatusMap.get(orderStatus));
		}
		
		return result;
	}

	

}
