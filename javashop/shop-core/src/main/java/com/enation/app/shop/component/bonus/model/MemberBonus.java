package com.enation.app.shop.component.bonus.model;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 会员红包
 * @author kingapex
 *2013-8-15下午10:05:37
 */
public class MemberBonus implements Serializable{
	
	private int bonus_id;
	private int bonus_type_id;
	private String bonus_sn;
	private Integer member_id;
	private Long used_time;
	private Long create_time;
	private Integer order_id;
	private String order_sn; //在使用时写入
	private String member_name; //会员用户名在使时写入，如果是按用户发放，同在发放时写入
	private String type_name;//红包类型名在发放时写入
	private int bonus_type;  //红包类型在发放时写入
	
	private double type_money;//红包金额，非数据库字段
	private double min_goods_amount; //最小商品金额，非数据库字段
	private int use_start_date;//使用开始时间，非数据库字段
	private int use_end_date;//使用截至时间，非数据库字段
	private Integer used;	//1为已使用
	
	
	@PrimaryKeyField
	public int getBonus_id() {
		return bonus_id;
	}
	public void setBonus_id(int bonus_id) {
		this.bonus_id = bonus_id;
	}
	public int getBonus_type_id() {
		return bonus_type_id;
	}
	public void setBonus_type_id(int bonus_type_id) {
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
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public int getBonus_type() {
		return bonus_type;
	}
	public void setBonus_type(int bonus_type) {
		this.bonus_type = bonus_type;
	}
	@NotDbField
	public double getType_money() {
		return type_money;
	}
	public void setType_money(double type_money) {
		this.type_money = type_money;
	}
	@NotDbField
	public double getMin_goods_amount() {
		return min_goods_amount;
	}
	public void setMin_goods_amount(double min_goods_amount) {
		this.min_goods_amount = min_goods_amount;
	}
	@NotDbField
	public int getUse_start_date() {
		return use_start_date;
	}
	public void setUse_start_date(int use_start_date) {
		this.use_start_date = use_start_date;
	}
	@NotDbField
	public int getUse_end_date() {
		return use_end_date;
	}
	public void setUse_end_date(int use_end_date) {
		this.use_end_date = use_end_date;
	}
	public Long getUsed_time() {
		return used_time;
	}
	public void setUsed_time(Long used_time) {
		this.used_time = used_time;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Integer getUsed() {
		return used;
	}
	public void setUsed(Integer used) {
		this.used = used;
	}

}
