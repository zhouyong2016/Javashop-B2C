package com.enation.app.shop.core.other.model;

/**
 * 促销活动详细实体类
 * 2016-5-23
 * @author DMRain
 * @version 1.0
 */
public class ActivityDetail {

	/** 促销活动详细ID */
	private Integer detail_id;
	
	/** 促销活动ID */
	private Integer activity_id;
	
	/** 活动门槛(满多少钱) */
	private Double full_money;
	
	/** 满减-减多少钱 */
	private Double minus_value;
	
	/** 满送-送多少积分 */
	private Integer point_value;
	
	/** 是否包含满减现金 0：不包含，1包含，默认为0 */
	private Integer is_full_minus;
	
	/** 是否包邮 0：不包邮，1：包邮,默认为0 */
	private Integer is_free_ship;
	
	/** 是否包含满送积分 0：不包含，1包含，默认为0 */
	private Integer is_send_point;
	
	/** 是否包含满送赠品 0：不包含，1包含，默认为0 */
	private Integer is_send_gift;
	
	/** 是否包含满送优惠券 0：不包含，1包含，默认为0 */
	private Integer is_send_bonus;
	
	/** 赠品ID */
	private Integer gift_id;
	
	/** 优惠券ID */
	private Integer bonus_id;
	
	/** 打折-打几折 */
	private Double  discount_value;
	
	/** 是否包含满打折 0：不包含，1包含，默认为0 */
	private Integer is_discount;

	public Integer getDetail_id() {
		return detail_id;
	}

	public void setDetail_id(Integer detail_id) {
		this.detail_id = detail_id;
	}

	public Integer getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(Integer activity_id) {
		this.activity_id = activity_id;
	}

	public Double getFull_money() {
		return full_money;
	}

	public void setFull_money(Double full_money) {
		this.full_money = full_money;
	}

	public Double getMinus_value() {
		//sqlserver:minus_value位null,默认值“0D”
		minus_value = minus_value==null?0D:minus_value;
		return minus_value;
	}

	public void setMinus_value(Double minus_value) {
		this.minus_value = minus_value;
	}

	public Integer getPoint_value() {
		return point_value;
	}

	public void setPoint_value(Integer point_value) {
		this.point_value = point_value;
	}

	public Integer getIs_full_minus() {
		return is_full_minus;
	}

	public void setIs_full_minus(Integer is_full_minus) {
		this.is_full_minus = is_full_minus;
	}

	public Integer getIs_free_ship() {
		return is_free_ship;
	}

	public void setIs_free_ship(Integer is_free_ship) {
		this.is_free_ship = is_free_ship;
	}

	public Integer getIs_send_point() {
		return is_send_point;
	}

	public void setIs_send_point(Integer is_send_point) {
		this.is_send_point = is_send_point;
	}

	public Integer getIs_send_gift() {
		return is_send_gift;
	}

	public void setIs_send_gift(Integer is_send_gift) {
		this.is_send_gift = is_send_gift;
	}

	public Integer getIs_send_bonus() {
		return is_send_bonus;
	}

	public void setIs_send_bonus(Integer is_send_bonus) {
		this.is_send_bonus = is_send_bonus;
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

	public Double getDiscount_value() {
		return discount_value;
	}

	public void setDiscount_value(Double discount_value) {
		this.discount_value = discount_value;
	}

	public Integer getIs_discount() {
		return is_discount;
	}

	public void setIs_discount(Integer is_discount) {
		this.is_discount = is_discount;
	}
	
	
}
