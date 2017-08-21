package com.enation.app.shop.component.bonus.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 红包类型实体
 * 
 * @author kingapex 2013-8-13下午2:39:27
 */
public class BonusType {

	private int type_id;
	private String type_name;
	private Double type_money;
	private int send_type;
	private Long send_start_date;
	private Long send_end_date;
	private Long use_start_date;
	private Long use_end_date;
	private Double min_goods_amount;
	private String recognition;
	private int create_num;
	private int use_num;
	
	/** 优惠券已被领取的数量 by_DMRain 2016-6-24 */
	private int received_num;
	
	/** 新增字段 xlp 2015年08月04日14:31:12 */
	private int belong;			//优惠券归属，1：平台，2：商家
	
	private Integer can_be_edit;// 能否被编辑 0：能  1：不能

	public Integer getCan_be_edit() {
		return can_be_edit;
	}
	public void setCan_be_edit(Integer can_be_edit) {
		this.can_be_edit = can_be_edit;
	}
	
	@PrimaryKeyField
	public int getType_id() {
		return type_id;
	}
	public void setType_id(int type_id) {
		this.type_id = type_id;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public Double getType_money() {
		return type_money;
	}
	public void setType_money(Double type_money) {
		this.type_money = type_money;
	}
	public int getSend_type() {
		return send_type;
	}
	public void setSend_type(int send_type) {
		this.send_type = send_type;
	}

	public void setMin_goods_amount(Double min_goods_amount) {
		this.min_goods_amount = min_goods_amount;
	}
	public String getRecognition() {
		return recognition;
	}
	public void setRecognition(String recognition) {
		this.recognition = recognition;
	}
	public int getCreate_num() {
		return create_num;
	}
	public void setCreate_num(int create_num) {
		this.create_num = create_num;
	}
	public int getUse_num() {
		return use_num;
	}
	public void setUse_num(int use_num) {
		this.use_num = use_num;
	}
	public Long getSend_start_date() {
		return send_start_date;
	}
	public void setSend_start_date(Long send_start_date) {
		this.send_start_date = send_start_date;
	}
	public Long getSend_end_date() {
		return send_end_date;
	}
	public void setSend_end_date(Long send_end_date) {
		this.send_end_date = send_end_date;
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
	public int getReceived_num() {
		return received_num;
	}
	public void setReceived_num(int received_num) {
		this.received_num = received_num;
	}
	public int getBelong() {
		return belong;
	}
	public void setBelong(int belong) {
		this.belong = belong;
	}
	
}
