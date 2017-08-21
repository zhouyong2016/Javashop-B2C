package com.enation.app.shop.component.activity.plugin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderBonus;
import com.enation.app.shop.core.order.model.OrderGift;
import com.enation.app.shop.core.order.model.OrderMeta;
import com.enation.app.shop.core.order.model.OrderMetaEnumKey;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.plugin.order.IAfterOrderCreateEvent;
import com.enation.app.shop.core.order.service.IOrderBonusManager;
import com.enation.app.shop.core.order.service.IOrderGiftManager;
import com.enation.app.shop.core.order.service.IOrderMetaManager;
import com.enation.app.shop.core.other.model.ActivityGift;
import com.enation.app.shop.core.other.service.IActivityGiftManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 创建订单后添加赠品和优惠券信息
 * @author DMRain
 * @date 2016-6-8
 * @version 1.0
 */
@Component
public class OrderAfterGiftBonusPlugin extends AutoRegisterPlugin implements IAfterOrderCreateEvent{

	@Autowired
	private IActivityGiftManager activityGiftManager;
	
	@Autowired
	private IBonusTypeManager bonusTypeManager;
	
	@Autowired
	private IOrderGiftManager orderGiftManager;
	
	@Autowired
	private IOrderBonusManager orderBonusManager;
	
	@Autowired
	private IOrderMetaManager orderMetaManager;
	
	@Override
	public void onAfterOrderCreate(Order order, List<CartItem> itemList, String sessionid) {
		Integer gift_id = order.getGift_id();
		Integer bonus_id = order.getBonus_id();
		Integer get_point = order.getActivity_point();
		Double act_discount = order.getAct_discount();
		
		//如果订单中的赠品ID不为0并且不为空，也就证明此订单中有赠送的赠品
		if(gift_id != null && gift_id != 0){
			ActivityGift gift = this.activityGiftManager.get(gift_id);
			
			//如果赠品的可用库存大于0
			if (gift.getEnable_store() > 0) {
				OrderGift orderGift = new OrderGift();
				orderGift.setOrder_id(order.getOrder_id());
				orderGift.setOrder_sn(order.getSn());
				orderGift.setGift_id(gift_id);
				orderGift.setGift_name(gift.getGift_name());
				orderGift.setGift_price(gift.getGift_price());
				orderGift.setGift_type(gift.getGift_type());
				orderGift.setGift_img(gift.getGift_img());
				orderGift.setGift_status(0);
				
				this.orderGiftManager.addOrderGift(orderGift, gift.getEnable_store());
			}
		}
		
		//如果订单中的优惠券ID不为0并且不为空，也就证明此订单中有赠送的优惠券
		if(bonus_id != null && bonus_id != 0){
			BonusType bonus = this.bonusTypeManager.get(bonus_id);
			
			//如果当前运行的程序为b2b2c项目
			if (EopSetting.PRODUCT.equals("b2b2c")) {
				
				//如果优惠券创建数量大于优惠券已被领取的数量
				if (bonus.getCreate_num() > bonus.getReceived_num()) {
					this.addOrderBonus(order, bonus_id, bonus);
					this.orderBonusManager.updateBonusReceivedNum(bonus_id, 0);
				}
			} else {
				this.addOrderBonus(order, bonus_id, bonus);
			}
		}
		
		//如果订单获取的积分不等于0并且不为空
		if (get_point != null && get_point != 0) {
			OrderMeta meta = new OrderMeta();
			meta.setOrderid(order.getOrder_id());
			meta.setMeta_key(OrderMetaEnumKey.get_point.toString());
			meta.setMeta_value(get_point.toString());
			this.orderMetaManager.add(meta);
		}
		
		//如果促销活动优惠不等于0并且不为空
		if (act_discount != null && act_discount != 0) {
			OrderMeta meta = new OrderMeta();
			meta.setOrderid(order.getOrder_id());
			meta.setMeta_key(OrderMetaEnumKey.activity_reduce_price.toString());
			meta.setMeta_value(act_discount.toString());
			this.orderMetaManager.add(meta);
		}
		
	}

	/**
	 * 添加订单获得的优惠券信息
	 * @param order 订单信息
	 * @param bonus_id 优惠券id
	 * @param bonus 优惠券信息
	 */
	private void addOrderBonus(Order order, Integer bonus_id, BonusType bonus) {
		//如果赠送的优惠券最后使用期限大于当前时间
		if (bonus.getUse_end_date() > DateUtil.getDateline()) {
			OrderBonus orderBonus = new OrderBonus();
			orderBonus.setOrder_id(order.getOrder_id());
			orderBonus.setOrder_sn(order.getSn());
			orderBonus.setBonus_id(bonus_id);
			orderBonus.setBonus_name(bonus.getType_name());
			orderBonus.setBonus_money(bonus.getType_money());
			orderBonus.setMin_goods_amount(bonus.getMin_goods_amount());
			orderBonus.setUse_start_date(bonus.getUse_start_date());
			orderBonus.setUse_end_date(bonus.getUse_end_date());
			orderBonus.setSend_type(bonus.getSend_type());
			
			this.orderBonusManager.add(orderBonus);
		}
	}

}
