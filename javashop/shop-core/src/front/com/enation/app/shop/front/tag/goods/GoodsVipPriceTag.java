package com.enation.app.shop.front.tag.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.goods.model.GoodsLvPrice;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.member.service.IMemberPriceManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.CurrencyUtil;

import freemarker.template.TemplateModelException;

/**
 * 会员vip价格标签
 * 
 * @author lina
 * @2014-2-14
 */
@Component
@Scope("prototype")
public class GoodsVipPriceTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IMemberLvManager memberLvManager;
	
	@Autowired
	private IMemberPriceManager memberPriceManager;
	
	@Autowired
	private IProductManager productManager;
	


	/**
	 * 会员vip价格标签
	 * @param 无
	 * @return 返回值类型为Map,如下：
	 * 1、会员价格vipprice:double型最低的会员价格
	 * 2、会员级别memberLvList:{@MemberLv}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result = new HashMap(2);
		Map goods = (Map) ThreadContextHolder.getHttpRequest().getAttribute("goods");
		double price = Double.valueOf("" + goods.get("price"));
		double vipprice = price;
		//获取当前登录会员
		Member member = UserConext.getCurrentMember();
		if (price == 0) {
			result.put("vipprice", 0);
		}
		List<MemberLv> memberLvList = memberLvManager.list();
		if (memberLvList != null && memberLvList.size() > 0) {
			List<GoodsLvPrice> glpList = this.memberPriceManager.listPriceByGid(Integer.valueOf(goods.get("goods_id").toString()));
			if (glpList != null && glpList.size() > 0) {
				// 设置了会员价格
				double discount = 1;
				for (MemberLv lv : memberLvList) {
					double lvprice1 = 0; // 会员价格
					if (lv.getDiscount() != null) {
						discount = lv.getDiscount() / 100.00;
						lvprice1 = CurrencyUtil.mul(price, discount);
					}
					double lvPrice = this
							.getMemberPrice(lv.getLv_id(), glpList); // 定义的会员价格

					if (lvPrice == 0) {
						lv.setLvPrice(lvprice1);
						lvPrice = lvprice1;
					} else {
						lv.setLvPrice(lvPrice);
					}
					if (vipprice > lvPrice) {
						vipprice = lvPrice;
					}
				}
				vipprice=Math.round(vipprice*100);
				result.put("vipprice", vipprice/100);
			} else{
				if(member!=null){
					//获取当前会员的等级折扣
					MemberLv memberlvdiscount=memberLvManager.get(member.getLv_id());
					double discount = memberlvdiscount.getDiscount() / 100.00;
					double lvprice = CurrencyUtil.mul(price, discount);
					result.put("vipprice", lvprice);

					
				}else{
				double discount = 1;
				for (MemberLv lv : memberLvList) {
					if (lv.getDiscount() != null) {
						discount = lv.getDiscount() / 100.00;
						double lvprice = CurrencyUtil.mul(price, discount);
						lv.setLvPrice(lvprice);
						if (vipprice > lvprice) {
							vipprice = lvprice;
						}
					}
				}
				vipprice=Math.round(vipprice*100);
				result.put("vipprice", vipprice/100);
			}}
		}
		result.put("vipprice",result.get("vipprice"));
		result.put("memberLvList", memberLvList);
		return result;
	}

	/**
	 * 根据级别获取 该级别某商品的价格
	 * 
	 * @param lv_id
	 * @param memPriceList
	 * @return
	 */
	private double getMemberPrice(int lv_id, List<GoodsLvPrice> memPriceList) {
		for (GoodsLvPrice lvPrice : memPriceList) {
			if (lv_id == lvPrice.getLvid()) {
				return lvPrice.getPrice();
			}
		}
		return 0;
	}

 
}
