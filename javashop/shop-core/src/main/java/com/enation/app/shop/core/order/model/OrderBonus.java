package com.enation.app.shop.core.order.model;

/**
 * 订单赠品实体类
 * 
 * @author DMRain
 * @date 2016年4月19日
 * @version v1.0
 * @since v1.0
 */
public class OrderBonus {

	/** 主键ID */
	private Integer ob_id;
	
	/** 订单ID */
	private Integer order_id;
	
	/** 订单号 */
	private String order_sn;
	
	/** 优惠券ID */
	private Integer bonus_id;
	
	/** 优惠券名称  */
	private String bonus_name;
	
	/** 优惠券面额 */
	private Double bonus_money;
	
	/** 优惠券使用起始时间 */
	private Long use_start_date;
	
	/** 优惠券使用结束时间 */
	private Long use_end_date;
	
	/** 最小购买商品金额 */
	private Double min_goods_amount;
	
	/**
	 * 优惠券发放方式
	 * 0:按用户发放，1：按商品发放，2：按订单金额发放，3：线下发放
	 */
	private Integer send_type;
	
	public Integer getOb_id() {
		return ob_id;
	}

	public void setOb_id(Integer ob_id) {
		this.ob_id = ob_id;
	}

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public Integer getBonus_id() {
		return bonus_id;
	}

	public void setBonus_id(Integer bonus_id) {
		this.bonus_id = bonus_id;
	}

	public String getBonus_name() {
		return bonus_name;
	}

	public void setBonus_name(String bonus_name) {
		this.bonus_name = bonus_name;
	}

	public Double getBonus_money() {
		return bonus_money;
	}

	public void setBonus_money(Double bonus_money) {
		this.bonus_money = bonus_money;
	}

	public Long getUse_start_date() {
		return use_start_date;
	}

	public void setUse_start_date(Long use_start_date) {
		this.use_start_date = use_start_date;
	}

	public Long getUse_end_date() {
		return use_end_date;
	}

	public void setUse_end_date(Long use_end_date) {
		this.use_end_date = use_end_date;
	}

	public Double getMin_goods_amount() {
		return min_goods_amount;
	}

	public void setMin_goods_amount(Double min_goods_amount) {
		this.min_goods_amount = min_goods_amount;
	}

	public Integer getSend_type() {
		return send_type;
	}

	public void setSend_type(Integer send_type) {
		this.send_type = send_type;
	}

}
