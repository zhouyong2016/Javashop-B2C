package com.enation.app.shop.front.tag.order;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.member.service.IMemberPointManger;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/** 
 * 获取退款订单积分是否充足 
 * @author	Chopper
 * @version	v1.0, 2015-12-31 下午6:21:28
 * @since
 */
@Component
public class CheckPointPlentyTag extends BaseFreeMarkerTag{
	
	@Autowired
	private IMemberPointManger memberPointManger;
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IOrderManager orderManager;   
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		int member_id = Integer.parseInt( params.get("member_id").toString());
		int order_id =  Integer.parseInt( params.get("order_id").toString());
		Member member = memberManager.get(member_id);
		Order order = orderManager.get(order_id);
		
		//如果该用户积分大于 退掉全部货物所需要的积分那么返回1 否则 0
		if(member.getMp()>getPointmp(order.getNeedPayMoney())){
			return 1;
		}  
		return 0;
	}
	
	/**
	 * 获取商品消费积分
	 * @param goodsPrice
	 * @return
	 */
	private int getPointmp(Double goodsPrice)
	{
		/**
		 * --------------------------------------
		 * 增加会员积分--商品价格*倍数(倍数在设置处指定)
		 * --------------------------------------
		 */
		if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_BUYGOODS) ){
			
			int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_BUYGOODS+"_num_mp");
			point = goodsPrice.intValue() * point;
			return point;
		}
		
		return 0;
	}	
	
}
