package com.enation.app.base.core.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;



/**
 * 会员级别
 * @author kingapex
 * @author kanon 2015-9-21 添加 会员级别注释
 *
 */
public class MemberLv  implements java.io.Serializable {

    // Fields 
	
	public MemberLv(){};
	public MemberLv(Integer lv_id, String name){
		this.lv_id = lv_id;
		this.name = name;
	}

     private Integer lv_id; //ID
     private String name;	//名称
     private Integer default_lv;	//是否为默认会员级别
     private Integer discount;	//折扣，如果80，表示该会员等级以销售价80%的价格购买。
     private Double lvPrice;	
     private int point;		//需要积分
     private boolean selected;
     
	public Integer getDefault_lv() {
		return default_lv;
	}
	public void setDefault_lv(Integer default_lv) {
		this.default_lv = default_lv;
	}
	@PrimaryKeyField
	public Integer getLv_id() {
		return lv_id;
	}
	public void setLv_id(Integer lv_id) {
		this.lv_id = lv_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@NotDbField
	public boolean getSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Integer getDiscount() {
		return discount;
	}
	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
	
	@NotDbField
	public Double getLvPrice() {
		return lvPrice;
	}
	public void setLvPrice(Double lvPrice) {
		this.lvPrice = lvPrice;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	} 

}