package com.enation.app.shop.core.order.model;

/**
 * 订单优惠券实体类
 * 
 * @author DMRain
 * @date 2016年4月19日
 * @version v1.0
 * @since v1.0
 */
public class OrderGift {

	/** 主键ID */
	private Integer og_id;
	
	/** 订单ID */
	private Integer order_id;
	
	/** 订单号 */
	private String order_sn;
	
	/** 赠品ID */
	private Integer gift_id;
	
	/** 赠品名称 */
	private String gift_name;
	
	/** 赠品价值 */
	private Double gift_price;
	
	/** 赠品图片 */
	private String gift_img;
	
	/** 赠品类型 0:纯赠品，1：商品赠品 */
	private Integer gift_type;
	
	/** 订单赠品状态 0:正常，1：申请退货/退款，2：退货/退款申请通过 ，3：入库/退款完成 */
	private Integer gift_status;

	public Integer getOg_id() {
		return og_id;
	}

	public void setOg_id(Integer og_id) {
		this.og_id = og_id;
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

	public Integer getGift_id() {
		return gift_id;
	}

	public void setGift_id(Integer gift_id) {
		this.gift_id = gift_id;
	}

	public String getGift_name() {
		return gift_name;
	}

	public void setGift_name(String gift_name) {
		this.gift_name = gift_name;
	}

	public Double getGift_price() {
		return gift_price;
	}

	public void setGift_price(Double gift_price) {
		this.gift_price = gift_price;
	}

	public String getGift_img() {
		return gift_img;
	}

	public void setGift_img(String gift_img) {
		this.gift_img = gift_img;
	}

	public Integer getGift_type() {
		return gift_type;
	}

	public void setGift_type(Integer gift_type) {
		this.gift_type = gift_type;
	}

	public Integer getGift_status() {
		return gift_status;
	}

	public void setGift_status(Integer gift_status) {
		this.gift_status = gift_status;
	}

}
