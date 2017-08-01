package com.enation.app.shop.front.tag.order;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IOrderReportManager;
import com.enation.app.shop.core.order.service.IPromotionManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 游客订单查询标签
 * @author lina
 *	2013-12-21
 */
@Component
@Scope("prototype")
public class SearchOrderTag extends BaseFreeMarkerTag {
	private IOrderManager orderManager;
	private IOrderReportManager orderReportManager;
	private IPromotionManager promotionManager;
	/**
	 * 游客订单查询标签
	 * @param ship_name 必填  收货人姓名
	 * @param ship_tel 必填 收货人电话
	 * @param action 必填值为search
	 * return Map订单信息：
	 * ordersPage 订单分页列表 ：ordersPage.result值为List类型 {@link Order}
	 * totalCount 订单总数
	 * ship_name 收货人姓名
	 * ship_tel 收货人电话
	 * pageSize 分页大小
	 * page 当前页
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object exec(Map args) throws TemplateModelException {
		Map data = new HashMap();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String action = request.getParameter("action");
		if (action == null || action.equals("")) {

		}else if (action.equals("search")) {
			String ship_name = request.getParameter("ship_name");
			String ship_tel = request.getParameter("ship_tel");
			if(StringUtil.isEmpty(ship_name)){
				throw new TemplateModelException("请输入收货人姓名!");
			}
			if(StringUtil.isEmpty(ship_tel)){
				throw new TemplateModelException("请输入手机或固定号码!");
			}
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 10;
			Page ordersPage = orderManager.searchForGuest(Integer.parseInt(page), pageSize, ship_name, ship_tel);
			data.put("ordersPage",ordersPage);//订单分页列表
			data.put("totalCount",ordersPage.getTotalCount());//订单总数
			data.put("ship_name",ship_name);//收货人姓名
			data.put("ship_tel",ship_tel);//收货人电话
			data.put("pageSize", pageSize);
			data.put("page", page);
			data.put("status",OrderStatus.getOrderStatusMap());//订单状态
		} 
		
		return data;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}

	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}
	
}
