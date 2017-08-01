package com.enation.app.shop.front.tag.goods.activity;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.OrderBonus;
import com.enation.app.shop.core.order.service.IOrderBonusManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 订单优惠券详细Tag
 * @author DMRain
 * @date 2016-6-14
 * @version 1.0
 */
@Component
public class OrderBonusDetailTag extends BaseFreeMarkerTag{

	@Autowired
	private IOrderBonusManager orderBonusManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer bonus_id = (Integer) params.get("bonus_id");
		Integer order_id = (Integer) params.get("order_id");
		OrderBonus bonus = this.orderBonusManager.getOrderBonus(bonus_id, order_id);
		if(bonus==null){
			bonus=new OrderBonus();
		}
		return bonus;
	}

}
