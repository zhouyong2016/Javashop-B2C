package com.enation.app.shop.component.member.plugin.price;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.goods.model.GoodsLvPrice;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.member.service.IMemberPriceManager;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.plugin.cart.ICartItemFilter;
import com.enation.app.shop.core.order.service.IPromotionManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.CurrencyUtil;
@Component
public class OrderVipPricePlugin extends AutoRegisterPlugin implements ICartItemFilter {

	@Autowired
	private IPromotionManager promotionManager;
	
	@Autowired
	private IMemberPriceManager memberPriceManager;
	
	@Autowired
	private IMemberLvManager memberLvManager;
 
	@Autowired
	private IGoodsManager goodsManager;
	
	@Override
	public void filter(List<CartItem> list, String sessionid) {
	 
		if(EopSetting.PRODUCT.equals("b2c")){
			
		}else if(EopSetting.PRODUCT.equals("b2b2c")){
			//b2b2c 不参与vip价格优惠
			return ;
		}
		Member member = UserConext.getCurrentMember();
		List<GoodsLvPrice> memPriceList = new ArrayList<GoodsLvPrice>();
		double discount =1; //默认是原价,防止无会员级别时出错
		if(member!=null && member.getLv_id()!=null){
			this.promotionManager.applyGoodsPmt(list, member.getLv_id()); 
			memPriceList  = this.memberPriceManager.listPriceByLvid(member.getLv_id());//某级别的会员价列表
			MemberLv lv =this.memberLvManager.get(member.getLv_id());
			discount = lv.getDiscount()/100.00;
			this.applyMemPrice(list, memPriceList, discount);
			
			/**美睛网的逻辑，以后考虑加上
			//xiaokx 
			//某级别下 有折扣的类别列表
			List discountList = this.memberLvManager.getCatDiscountByLv(member.getLv_id());
			double framePrice = 0;
			for(CartItem item : list ){
 
 
				double price  = CurrencyUtil.mul(item.getPrice() ,  discount);

				double memPrice=getMemberPrice(memPriceList,item.getGoods_id().intValue());
				if(memPrice!=0){
					
					//会员级别价
			 
					item.setCoupPrice(memPrice);
					continue;
				}else{
					item.setCoupPrice(price);
				}
				int zhekou = getCatDicount(discountList,item.getCatid());
				if(zhekou>0){
					//该类别有折扣
					discount = zhekou/100.00;
					item.setCoupPrice(CurrencyUtil.mul(item.getPrice() ,  discount));
					continue;
				}
				
			}
			
			**/
		}
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
		for(CartItem item : itemList ){
			double oldprice =  item.getPrice();
			if(item.getCoupPrice() <oldprice){continue;} //如果已经优惠过了，则不再应用会员价格
			double price  = CurrencyUtil.mul(item.getPrice() ,  discount);
			price = CurrencyUtil.round(price, 2);
			for(GoodsLvPrice lvPrice:memPriceList){
				if( item.getProduct_id().intValue() == lvPrice.getProductid() ){
					price = lvPrice.getPrice();
				}
			}
			 
			item.setPrice(oldprice);
			item.setCoupPrice(price);
		} 
	}
	
}
