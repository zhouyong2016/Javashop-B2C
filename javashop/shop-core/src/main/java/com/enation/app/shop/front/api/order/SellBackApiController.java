package com.enation.app.shop.front.api.order;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.SellBackGoodsList;
import com.enation.app.shop.core.order.model.SellBack;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 退换货Api
 * @author fenlongli
 * @version v2.0,2016年2月20日 sylow 版本改造
 * @since v6.0
 */
@Controller
@RequestMapping("/api/shop/sell-back")
public class SellBackApiController {
	
	@Autowired
	private ISellBackManager sellBackManager;
	
	@Autowired
	private IOrderManager orderManager;
	
	
	/**
	 * 退货申请
	 * @param goodsId 【必填】 商品id数组
	 * @param goodsNum 【必填】 商品退货申请数量数组
	 * @param payNum 【必填】 购买数量
	 * @param productId 【必填】 产品id数组
	 * @param price 【必填】 价钱数组
	 * @param item_id 【必填】订单项id数组
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/return-goods", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult apply(Integer[] goodsId,Integer[] goodsNum, Integer[] payNum, Integer[] productId, Double[] price
			,Integer[] item_id,SellBack sellBack){
		try {
			
			
			//记录会员信息
			Member member =  UserConext.getCurrentMember();
			sellBack.setMember_id(member.getMember_id());
			sellBack.setSndto(member.getName());
			sellBack.setTradeno(com.enation.framework.util.DateUtil.toString(DateUtil.getDateline(),"yyMMddhhmmss"));//退货单号
			sellBack.setRegoperator("会员["+member.getUname()+"]");
			sellBack.setTradestatus(0);
			sellBack.setRegtime(DateUtil.getDateline());
			sellBack.setType(2);
			/**
			 * 循环页面中选中的商品，形成退货明细:goodsList
			 */
			List goodsList = new ArrayList();
			
			for(int i=0;i<goodsId.length;i++){
				
				SellBackGoodsList sellBackGoods = new SellBackGoodsList();
				sellBackGoods.setPrice(price[i]);
				sellBackGoods.setReturn_num(goodsNum[i]);
				sellBackGoods.setShip_num(payNum[i]);
				sellBackGoods.setGoods_id(goodsId[i]);	
				sellBackGoods.setGoods_remark(sellBack.getRemark());
				sellBackGoods.setProduct_id(productId[i]);
				sellBackGoods.setItem_id(item_id[i]);
				goodsList.add(sellBackGoods);
				
			}
			
			this.sellBackManager.addSellBack(sellBack, goodsList);
			return JsonResultUtil.getSuccessJson("退货申请成功");
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("退货申请失败：" + e.getMessage());
		}
	}
	
	/**
	 * 添加退款申请
	 * @param sellBack 申请
	 * @return 
	 */
	@ResponseBody
	@RequestMapping(value="/return-money", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult addRefund(SellBack sellBack){
		try{
			//记录会员信息
			Member member =  UserConext.getCurrentMember();
			sellBack.setMember_id(member.getMember_id());
			sellBack.setSndto(member.getName());
			sellBack.setTradeno(com.enation.framework.util.DateUtil.toString(DateUtil.getDateline(),"yyMMddhhmmss"));//退款单号
			sellBack.setRegoperator("会员["+member.getUname()+"]");
			sellBack.setTradestatus(0);
			sellBack.setRegtime(DateUtil.getDateline());
			sellBack.setType(1);
			sellBackManager.addSellBack(sellBack);
			return JsonResultUtil.getSuccessJson("退款申请成功");
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("退款申请失败：" + e.getMessage());
		}
	}
}
