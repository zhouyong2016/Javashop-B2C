package com.enation.app.shop.front.tag.order;

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
 * 会员订单列表标签
 * @author whj
 *2014-02-17下午15:13:00
 */
@Component
@Scope("prototype")
public class MemberOrderListTag extends BaseFreeMarkerTag{
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
		String status = request.getParameter("status");
		String keyword = request.getParameter("keyword");
		
		Page ordersPage = memberOrderManager.pageOrders(Integer.valueOf(page), pageSize,status,keyword);
		Long totalCount = ordersPage.getTotalCount();
		List ordersList = (List) ordersPage.getResult();
		for (Object object : ordersList) {
			Order order = (Order) object;
			Integer orderid = order.getOrder_id();
			List<OrderItem> itemList  =orderManager.listGoodsItems(orderid);
			//遍历订单项，查询没有评论过的商品列表
			List<OrderItem> orderItemList = new ArrayList<OrderItem>();
			for(int i=0;i<itemList.size();i++){
				Integer order_id = itemList.get(i).getOrder_id();
				Integer product_id =itemList.get(i).getProduct_id();
				MemberOrderItem memberOrderItem = memberOrderItemManager.getMemberOrderItem(order_id, product_id, 0);
				if(memberOrderItem!=null){
					orderItemList.add(itemList.get(i));
				}
			}
			if(orderItemList.size()>0){
				order.setIs_comment(1);
			}
		}
		ordersList = ordersList == null ? new ArrayList() : ordersList;
		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("ordersList", ordersList);

		//Author LiFenLong
		Map<String,Object> orderstatusMap=OrderStatus.getOrderStatusMap();
		for (String orderStatus: orderstatusMap.keySet()) {
			result.put(orderStatus, orderstatusMap.get(orderStatus));
		}
		
		if(status!=null){
			result.put("status",Integer.valueOf( status) );
		}
		
		return result;
	}

	

}
