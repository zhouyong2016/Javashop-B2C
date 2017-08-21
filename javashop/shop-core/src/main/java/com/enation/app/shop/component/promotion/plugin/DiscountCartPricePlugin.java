package com.enation.app.shop.component.promotion.plugin;

import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.goods.model.GoodsLvPrice;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.plugin.cart.ICartItemFilter;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.member.service.IMemberPriceManager;
import com.enation.app.shop.core.order.service.IPromotionManager;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 购物车优惠价格计算插件
 * @author kingapex
 * @date 2011-10-25 下午12:02:29 
 * @version V1.0
 */

public class DiscountCartPricePlugin extends AutoRegisterPlugin implements ICartItemFilter {

	
	private IPromotionManager promotionManager;
	private IMemberPriceManager memberPriceManager;
	private IMemberLvManager memberLvManager;
 
	private IGoodsManager goodsManager;
	

	@Override
	public void filter(List<CartItem> list, String sessionid) {
	 
		 
	}
	
 
	
	/**
	 * 获取某商品的级别会员价
	 * @param memPriceList
	 * @param productId
	 * @return
	 */
	private double getMemberPrice(List<GoodsLvPrice> memPriceList,int goodsId){
		for(GoodsLvPrice lvPrice:memPriceList){
			if( goodsId == lvPrice.getGoodsid() ){
				return lvPrice.getPrice();
			}
		}
		return 0;
	}
	/**
	 * 获取某类别的级别折扣值
	 * @param discountList
	 * @param catId
	 * @return
	 */
	private int getCatDicount(List discountList,int catId){
		for (int i = 0; i < discountList.size(); i++) {
			Map map = (Map)discountList.get(i);
			Integer cat_id = (Integer)map.get("cat_id");
			Integer discount  = (Integer)map.get("discount");
			if(cat_id.intValue()==catId){
				return discount.intValue();
			}
		}
		return 0;
	}

	/**
	 * 应用会员价
	 * @param itemList
	 * @param memPriceList
	 * @param discount
	 */
	private void applyMemPrice(List<CartItem>  itemList,List<GoodsLvPrice> memPriceList,double discount ){
//		for(CartItem item : itemList ){
//			double oldprice =  item.getPrice();
//			if(item.getCoupPrice() <oldprice){continue;} //如果已经优惠过了，则不再应用会员价格
//			double price  = CurrencyUtil.mul(item.getPrice() ,  discount);
//			for(GoodsLvPrice lvPrice:memPriceList){
//				if( item.getProduct_id().intValue() == lvPrice.getProductid() ){
//					price = lvPrice.getPrice();
//				}
//			}
//			 
//			item.setPrice(oldprice);
//			item.setCoupPrice(price);
//		} 
	}
	

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

	public IMemberPriceManager getMemberPriceManager() {
		return memberPriceManager;
	}

	public void setMemberPriceManager(IMemberPriceManager memberPriceManager) {
		this.memberPriceManager = memberPriceManager;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

 

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

}
