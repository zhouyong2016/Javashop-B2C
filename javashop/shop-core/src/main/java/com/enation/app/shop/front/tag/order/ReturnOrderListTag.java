package com.enation.app.shop.front.tag.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 退换货申请列表
 * @author whj
 *2014-02-20下午19:48:00
 */
@Component
@Scope("prototype")
public class  ReturnOrderListTag extends BaseFreeMarkerTag{
	        
	
	private IReturnsOrderManager returnsOrderManager;
	private IOrderManager orderManager;
	private IGoodsManager goodsManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		 HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		 String page  = request.getParameter("page");
		 page = (page == null || page.equals("")) ? "1" : page;
		
		 int pageSize = 10;
		 Page returnOrderPage = returnsOrderManager.pageReturnOrder(Integer.valueOf(page), pageSize);
		 Long totalCount = returnOrderPage.getTotalCount();
		 Map result = new HashMap();
		 List returnOrderList = (List)returnOrderPage.getResult();
		 returnOrderList = returnOrderList == null ? new ArrayList() : returnOrderList;
		 result.put("totalCount", totalCount);
		 result.put("pageSize", pageSize);
		 result.put("page", page);
		 result.put("returnOrderList", returnOrderList);
		 return result;
	}

	public IReturnsOrderManager getReturnsOrderManager() {
		return returnsOrderManager;
	}

	public void setReturnsOrderManager(IReturnsOrderManager returnsOrderManager) {
		this.returnsOrderManager = returnsOrderManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

}
