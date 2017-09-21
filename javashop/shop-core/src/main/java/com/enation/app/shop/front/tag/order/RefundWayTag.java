package com.enation.app.shop.front.tag.order;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 
 * 获取是否需要愿支付方式退款信息
 * @author zjp
 * @version v6.5.0
 * @since v6.5.1
 * 2017年7月7日 上午11:16:59
 */
@Component
@Scope("prototype")
public class RefundWayTag extends BaseFreeMarkerTag {

	@Autowired
	private IOrderManager orderManager ;
	@Autowired
	private IPaymentManager paymentManager;

	/**
	 * 
	 * 获取是否需要原支付方式退款
	 * 必须传递orderid
	 * @param orderid,订单id，int型
	 * @return 订单详细 ，Order型
	 * {@link Order}
	 */
	@Override
	public Object exec(Map args) throws TemplateModelException {

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String orderid  = request.getParameter("order_id");
		/** 获取订单信息 */
		Order order = this.orderManager.get(StringUtil.toInt(orderid,false));
		/** 根据订单支付方式获取支付方式详细信息 */
		PayCfg payCfg=null;
		if(order != null){
			payCfg = this.paymentManager.get(order.getPayment_type());
		}
		if(payCfg != null){
			return payCfg.getIs_retrace();
		}
		return 0;


	}

}
