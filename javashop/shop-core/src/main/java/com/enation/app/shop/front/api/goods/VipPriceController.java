package com.enation.app.shop.front.api.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.goods.model.GoodsLvPrice;
import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.member.service.IMemberPriceManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 会员价格api
 * @author lina
 * 2014-2-17
 * @author Kanon 2016-2-18 6.0版本改造
 */
@Scope("prototype")
@Controller 
@RequestMapping("/api/shop/vipprice")
public class VipPriceController {
	@Autowired
	private IMemberLvManager memberLvManager;
	
	@Autowired
	private IMemberPriceManager memberPriceManager;
	
	@Autowired
	private IProductManager productManager;

	/**
	 * 有规格商品的会员vip价格
	 * @param productid 
	 * @return json格式的商品会员价格及重量
	 * vipprice:会员价格
	 * weight:重量
	 */
	@ResponseBody
	@RequestMapping(value="/show-vip-price",produces = MediaType.APPLICATION_JSON_VALUE)
	public Object showVipPrice(Integer productid) {
		Product product = this.productManager.get(productid);
		double price = product.getPrice(); // 此货品的价格
		double vipprice = price;
		List<MemberLv> memberLvList = memberLvManager.list();
		// 读取此货品的会员价格
		List<GoodsLvPrice> glpList = this.memberPriceManager.listPriceByPid(productid);
		// 设置了会员价格，读取出低的价格
		if (glpList != null && glpList.size() > 0) {
			// 设置了会员价格
			double discount = 1;
			for (MemberLv lv : memberLvList) {
				double lvprice1 = 0; // 会员价格
				if (lv.getDiscount() != null) {
					discount = lv.getDiscount() / 100.00;
					lvprice1 = CurrencyUtil.mul(price, discount);
				}
				double lvPrice = this.getMemberPrice(lv.getLv_id(), glpList); // 定义的会员价格
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
		} else {
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
		}

		Map vip = new HashMap();
		vip.put("vipprice", vipprice);
		vip.put("weight", product.getWeight());
		
		JsonResult jsonResult=new JsonResult();
		jsonResult.setResult(1);
		jsonResult.setData(vip);
		
		return jsonResult;
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
