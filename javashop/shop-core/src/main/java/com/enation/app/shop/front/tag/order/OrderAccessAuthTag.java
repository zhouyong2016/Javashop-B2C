package com.enation.app.shop.front.tag.order;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
 * 订单访问权限判断标签
 * @author DMRain
 * @date 2016-8-24
 */
@Component
public class OrderAccessAuthTag extends BaseFreeMarkerTag {

	@Autowired
	private IOrderManager orderManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String order_sn = params.get("order_sn").toString();

		Order order = null;

		if (!StringUtil.isEmpty(order_sn)) {
			order = orderManager.get(order_sn);
		} else {
			throw new UrlNotFoundException();
		}

		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		String ctx = this.getRequest().getContextPath();
		if ("/".equals(ctx)) {
			ctx = "";
		}
		String url = ctx + "/member/order.html";

		Member member = UserConext.getCurrentMember();

		// 如果订单的member_id与当前登录会员的member_id不相同
		if (!order.getMember_id().equals(member.getMember_id())) {
			try {
				response.sendRedirect(url);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return order;
	}

}
