package com.enation.app.shop.component.promotion.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.plugin.promotion.IPromotionPlugin;
import com.enation.app.shop.core.order.service.promotion.PromotionConditions;
import com.enation.app.shop.core.order.service.promotion.PromotionType;
import com.enation.framework.plugin.AutoRegisterPlugin;



/**
 * 购物车金额满多少就免运费
 * @author kingapex
 *
 */
@Component
public class EnoughPriceFreeDeliveryPlugin  extends AutoRegisterPlugin implements
IPromotionPlugin {

	
	public void register() {
		 
		
	}

	
	public String[] getConditions() {
		 
		return new String[]{ PromotionConditions.ORDER ,PromotionConditions.MEMBERLV};
	}

	
	public String getMethods() {
	 
		return "freeFreight";
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "enoughPriceFreeDeliveryPlugin";
	}

	
	public String getName() {
		
		return "免运费———购物车中商品总金额大于指定金额,免运费。";
	}

	
	public String getType() {
		
		return PromotionType.PMTTYPE_ORDER;
	}

	
	public String getVersion() {
		
		return "1.0";
	}

	
	public void perform(Object... params) {
		
		
	}
 

}
