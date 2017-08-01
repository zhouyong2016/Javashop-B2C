package com.enation.app.shop.component.bonus.model;

import java.io.Serializable;

/**
 * 非数据库表，订单结算时优惠券的读取和使用等
 * @author xulipeng
 * @version v1.0
 * @since v6.2.1
 */
public class Bonus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7657936823522688676L;
	
	private Integer bonus_id;			//会员优惠券id
	private Double type_money;			//优惠券金额
	private Double min_goods_amount;	//使用限额，满*元可用
	private Integer is_used;			//本地结算中是够已经使用		1：为已使用
	private Integer usable;				//是否可用
	private String usable_term;			//可用条件说明
	private Long use_start_date;		//使用起始时间		没有到达此时间则为不可用
	private Long use_end_date;			//使用结束时间		超过此时间则为不可用
	private Long used_time;				//实体券使用时间
	private String bonus_sn;			//实体券的编码
	private Integer bonus_type_id;		//优惠券id
	private Integer member_id;
	
	public Integer getBonus_id() {
		return bonus_id;
	}
	public void setBonus_id(Integer bonus_id) {
		this.bonus_id = bonus_id;
	}
	public Double getType_money() {
		return type_money;
	}
	public void setType_money(Double type_money) {
		this.type_money = type_money;
	}
	public Integer getIs_used() {
		if(is_used==null){
			return is_used;
		}
		return is_used;
	}
	public void setIs_used(Integer is_used) {
		this.is_used = is_used;
	}
	public Integer getUsable() {
		return usable;
	}
	public void setUsable(Integer usable) {
		this.usable = usable;
	}
	public String getUsable_term() {
		return usable_term;
	}
	public void setUsable_term(String usable_term) {
		this.usable_term = usable_term;
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
	public Long getUsed_time() {
		return used_time;
	}
	public void setUsed_time(Long used_time) {
		this.used_time = used_time;
	}
	public Integer getBonus_type_id() {
		return bonus_type_id;
	}
	public void setBonus_type_id(Integer bonus_type_id) {
		this.bonus_type_id = bonus_type_id;
	}
	public String getBonus_sn() {
		return bonus_sn;
	}
	public void setBonus_sn(String bonus_sn) {
		this.bonus_sn = bonus_sn;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	
	
}
