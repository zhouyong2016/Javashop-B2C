package com.enation.app.shop.front.tag.order;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.model.support.OrderPrice;
import com.enation.app.shop.core.order.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.other.service.IActivityManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 结算的订单金额标签
 * 
 * @author lina 2014-2-21
 */
@Component
@Scope("prototype")
public class CheckoutOrderTotalTag extends BaseFreeMarkerTag {

	@Autowired
	private ICartManager cartManager;

	@Autowired
	private IActivityManager activityManager;
	
	@Autowired
	private CartPluginBundle cartPluginBundle;
	
	/**
	 * 结算的订单金额
	 * @param regionId 地区
	 * @param typeId 配送方式
	 * @return orderPrice订单金额信息 {@link OrderPrice}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		Integer regionId = (Integer) params.get("regionId");// 地区id
		Integer typeId = (Integer) params.get("typeId"); // 配送方式id
		Integer actId = (Integer) params.get("activity_id"); //促销活动ID
		OrderPrice orderPrice = new OrderPrice();
		if (regionId != null && typeId != null) {
			
			//读取某用户的购物车中选中项列表
			List<CartItem> itemList = this.cartManager.selectListGoods(sessionid); 
			
			orderPrice = this.cartManager.countPrice(itemList, typeId, regionId.toString());
			
			//获取参加促销活动商品价格总计
			Double actTotalPrice = this.getTotalPrice(itemList);
			
			//促销活动与商品结算页相结合
			//如果促销活动ID不为空
			if (actId != null && actId != 0) {
				Map map = this.activityManager.getActMap(actId);
				Double full_money = StringUtil.toDouble(map.get("full_money"), false);
				Double minus_value =StringUtil.toDouble( map.get("minus_value"), false);
				Integer point_value = StringUtil.toInt( map.get("point_value"),false);
				Integer is_full_minus = StringUtil.toInt( map.get("is_full_minus"),false);
				Integer is_free_ship =StringUtil.toInt(map.get("is_free_ship"),false);
				Integer is_send_point =StringUtil.toInt(map.get("is_send_point"),false);
				Integer is_send_gift = StringUtil.toInt( map.get("is_send_gift"),false);
				Integer is_send_bonus = StringUtil.toInt(map.get("is_send_bonus"),false);
				Integer gift_id = StringUtil.toInt( map.get("gift_id"),false);
				Integer bonus_id = StringUtil.toInt( map.get("bonus_id"),false);
				
				//如果订单中的促销商品总价大于或等于促销活动的优惠门槛
				if (actTotalPrice >= full_money) {
					
					//如果促销活动开启了满减现金
					if (is_full_minus == 1) {
						orderPrice.setActDiscount(minus_value);
						orderPrice.setNeedPayMoney(orderPrice.getNeedPayMoney() - minus_value);
					}
					
					//如果促销活动开启了满送积分
					if (is_send_point == 1) {
						orderPrice.setActivity_point(point_value);
					}
					
					//如果促销活动开启了满免运费
					if (is_free_ship == 1) {
						orderPrice.setIs_free_ship(is_free_ship);
						orderPrice.setNeedPayMoney(orderPrice.getNeedPayMoney() - orderPrice.getShippingPrice());
					}
					
					//如果促销活动开启了满送赠品
					if (is_send_gift == 1) {
						orderPrice.setGift_id(gift_id);
					}
					
					//如果促销活动开启了满送优惠券
					if (is_send_bonus == 1) {
						orderPrice.setBonus_id(bonus_id);
					}
				}
			}
		}

		
		orderPrice  = this.cartPluginBundle.coutPrice(orderPrice);////激发价格计算事件 add by jianghongyan 2016-6-16
		return orderPrice;
	}
	
	/**
	 * 获取参加促销活动商品价格总计
	 * add by DMRain 2016-6-23
	 * @param cartItemList
	 * @return
	 */
	private Double getTotalPrice(List<CartItem> itemList){
		Double actTotalPrice = 0d;
		Double sameGoodsTotal = 0d;
		
		for(CartItem cartItem : itemList){
			Integer activity_id = cartItem.getActivity_id();
			
			//如果促销活动信息ID不为空
			if(activity_id != null && activity_id != 0){
				sameGoodsTotal = CurrencyUtil.mul(cartItem.getPrice(), cartItem.getNum());
				actTotalPrice = CurrencyUtil.add(actTotalPrice, sameGoodsTotal);
			}
		}
		
		return actTotalPrice;
	}

}
