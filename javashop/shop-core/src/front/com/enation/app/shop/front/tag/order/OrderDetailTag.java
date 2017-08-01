package com.enation.app.shop.front.tag.order;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 订单详细标签 
 * @author kingapex
 *2013-7-28上午10:25:29
 */
@Component
@Scope("prototype")
public class OrderDetailTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IOrderManager orderManager ;
	
	/**
	 * 
	 * 订单详细标签
	 * 必须传递orderid或ordersn参数
	 * @param orderid,订单id，int型
	 * @param ordersn,订单sn,String 型
	 * @return 订单详细 ，Order型
	 * {@link Order}
	 */
	@Override
	public Object exec(Map args) throws TemplateModelException {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ordersn =  request.getParameter("ordersn");
		String orderid  = request.getParameter("orderid");
		
		// add_bySylow 2016-07-08 因为有的地方是orderid有的地方是order_id 代码太乱，故改为支持两种形式
		if (orderid == null) {
			orderid  = request.getParameter("order_id");
		}
		
		Order order  =null;
		if(!StringUtil.isEmpty(orderid)){
			order=orderManager.get(Integer.valueOf(orderid));
			
		}else if(!StringUtil.isEmpty(ordersn)){
			order=	orderManager.get(ordersn);
			
		}else{
			throw new UrlNotFoundException();
		}
		
		//有可能用户直接修改url地址栏中的订单号，导致查不到订单
		if(order==null){
			throw new UrlNotFoundException();
		}
		//校验是否本会员的操作
		Member member = UserConext.getCurrentMember();
		
		if(member!=null && order.getMember_id().equals(member.getMember_id())){
			
		}else{
			throw new UrlNotFoundException();
		}
		
		return order;
	}
	
}
