package com.enation.app.shop.front.tag.member;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 获得该会员订单在各个状体下的个数
 * @author wanghongjun    2015-04-17
 *
 */
@Component
@Scope("prototype")
public class MemberOrderNumTag extends BaseFreeMarkerTag{
	
	@Autowired
	private IOrderManager orderManager;
	
	@Override
	public Object exec(Map params) throws TemplateModelException {
		Integer status=(Integer) params.get("status");
		int count = this.orderManager.orderStatusNum(status);
		return count;
	}

	
}
