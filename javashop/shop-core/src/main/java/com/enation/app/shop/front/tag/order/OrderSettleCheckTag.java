package com.enation.app.shop.front.tag.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.bonus.model.Bonus;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 订单结算检测。是否因修改了购物车的数量等而导致优惠券、实体券等的可用性<br>
 * 检测信息有：<br>1.检测已使用的优惠券<br>2.检测已使用的实体券<br>
 * @author xulipeng
 * @version v1.0
 * @since	v6.2.1
 */
@Component
@Scope("prototype")
public class OrderSettleCheckTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IBonusManager bonusManager;
	
	@Autowired
	private ICartManager cartManager;
	
	/**
	 * 
	 * @return result：1检测有问题，0则为无问题 ， errorMessager：错误提示
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		Map map = new HashMap();
		map.put("result", 0);
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		//读取所有已选的购物车商品总价
		Double goodsTotlePrice = this.cartManager.countGoodsTotal(request.getSession().getId());
		
		StringBuffer errorMessage = new StringBuffer();
		
		//读取已使用的优惠券
		Bonus bonus = BonusSession.getOne();
		//如果商品总价小于优惠券最低使用限额，则清空已使用的优惠券
		if(bonus!=null && goodsTotlePrice<bonus.getMin_goods_amount()){
			BonusSession.clean();
			errorMessage.append("优惠券不可用，请重新选择。\\n");
		}
		
		//读取已使用实体券
		List<Bonus> bounsList= BonusSession.get();
		if(bounsList!=null && !bounsList.isEmpty()){
			for (Bonus entityBonus : bounsList) {
				//如果商品总价小于实体券最低使用限额，则取消此实体券的使用
				if(goodsTotlePrice < entityBonus.getMin_goods_amount()){
					BonusSession.cancel(entityBonus.getBonus_sn());
					errorMessage.append("实体券 ["+entityBonus.getBonus_sn()+"] 不可用。\\n");
				}
			}
		}
		
		//检测是否有问题
		if(!StringUtil.isEmpty(errorMessage.toString())){
			map.put("errorMessage", errorMessage);
			map.put("result", 1);
		}
		
		return map;
	}

}
