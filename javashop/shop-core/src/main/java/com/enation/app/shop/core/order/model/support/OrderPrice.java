package com.enation.app.shop.core.order.model.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.enation.framework.util.CurrencyUtil;

/**
 * 订单价格信息实体
 * @author kingapex
 * 2010-5-30上午11:58:33
 * 
 */
public class OrderPrice implements Serializable {
	
	//商品价格，经过优惠过的
	private Double goodsPrice;
		
	//订单总价，商品价格+运费
	private Double orderPrice;

	//配送费用
	private Double shippingPrice; 
	
	//需要支付的金额(应付款)
	private Double needPayMoney; 
	
	//优惠的价格总计
	private Double discountPrice; 
	
	
 
	//商品重量
	private Double weight ; 
	
	//可获得积分
	private Integer point; 
	
	private Map<String,Object> discountItem; //使用优惠的项目
	
	//促销活动每个order总优惠   add by DMRain 2016-1-15
	private Double actDiscount;
	
	//可获得的赠品ID 0：没有赠品	add by DMRain 2016-1-18
	private Integer gift_id;
 
	//可获得的优惠券ID 0：没有优惠券  add by DMRain 2016-1-19
	private Integer bonus_id;
	
	//是否免运费 0：否，1：是 默认为否 add by DMRain 2016-6-6
	private Integer is_free_ship = 0;
	
	//促销活动减免的运费 add by DMRain 2016-6-23
	private Double act_free_ship;
	
	//商品积分 add by jianghongyan 2016-06-15
	private Integer exchange_point;
	
	//促销活动赠送的积分 add by DMRain 2016-7-22
	private Integer activity_point;
	
	//余额支付的金额
	private Double credit_pay;
	
	public Double getCredit_pay() {
		return credit_pay;
	}

	public void setCredit_pay(Double credit_pay) {
		this.credit_pay = credit_pay;
	}

	public OrderPrice(){
		discountItem = new HashMap<String, Object>();
	
	}
 
	public Double getGoodsPrice() {
		if(goodsPrice!=null){
			goodsPrice=CurrencyUtil.round(goodsPrice, 2);
		}else{
			return 0.00d;
		}
		return goodsPrice;
	}
	public void setGoodsPrice(Double goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	public Double getOrderPrice() {
		if(orderPrice!=null){
			orderPrice=CurrencyUtil.round(orderPrice, 2);
		}else{
			return 0.00d;
		}
		return orderPrice;
	}
	public void setOrderPrice(Double orderPrice) {
		this.orderPrice = orderPrice;
	}
	public Double getDiscountPrice() {
		discountPrice= discountPrice==null?0:discountPrice;
		if(discountPrice!=null){
			discountPrice=CurrencyUtil.round(discountPrice, 2);
		}else{
			return 0.00d;
		}
		return discountPrice;
	}
	public void setDiscountPrice(Double discountPrice) {
		this.discountPrice = discountPrice;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	 
	public Double getWeight() {
		if(weight!=null){
			weight=CurrencyUtil.round(weight, 2);
		}else{
			return 0.00d;
		}
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Double getShippingPrice() {
		if(shippingPrice!=null){
			shippingPrice=CurrencyUtil.round(shippingPrice, 2);
		}else{
			return 0.00d;
		}
		return shippingPrice;
	}
	public void setShippingPrice(Double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
	public Map<String, Object> getDiscountItem() {
		return discountItem;
	}
	public void setDiscountItem(Map<String, Object> discountItem) {
		this.discountItem = discountItem;
	}
	
	public Double getNeedPayMoney() {
		if(needPayMoney!=null){
			needPayMoney=CurrencyUtil.round(needPayMoney, 2);
		}else{
			return 0.00d;
		}
		return needPayMoney;
	}
	public void setNeedPayMoney(Double needPayMoney) {
		this.needPayMoney = needPayMoney;
	}

	public Double getActDiscount() {
		if(actDiscount!=null){
        	actDiscount=CurrencyUtil.round(actDiscount, 2);
        }else{
            return 0.00d;
        }
        return actDiscount;
	}

	public void setActDiscount(Double actDiscount) {
		this.actDiscount = actDiscount;
	}

	public Integer getGift_id() {
		return gift_id;
	}

	public void setGift_id(Integer gift_id) {
		this.gift_id = gift_id;
	}

	public Integer getBonus_id() {
		return bonus_id;
	}

	public void setBonus_id(Integer bonus_id) {
		this.bonus_id = bonus_id;
	}

	public Integer getIs_free_ship() {
		return is_free_ship;
	}

	public void setIs_free_ship(Integer is_free_ship) {
		this.is_free_ship = is_free_ship;
	}
	
	public Double getAct_free_ship() {
		if(act_free_ship!=null){
			act_free_ship=CurrencyUtil.round(act_free_ship, 2);
        }else{
            return 0.00d;
        }
		return act_free_ship;
	}

	public void setAct_free_ship(Double act_free_ship) {
		this.act_free_ship = act_free_ship;
	}

	public Integer getExchange_point() {
		return exchange_point;
	}

	public void setExchange_point(Integer exchange_point) {
		this.exchange_point = exchange_point;
	}

	public Integer getActivity_point() {
		return activity_point;
	}

	public void setActivity_point(Integer activity_point) {
		this.activity_point = activity_point;
	}

	

}
