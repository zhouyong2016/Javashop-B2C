package com.enation.app.shop.core.member.model;

import java.io.Serializable;
import java.util.List;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 会员中心-接收地址
 * 
 * @author lzf<br/>
 *         2010-3-17 下午02:34:48<br/>
 *         version 1.0<br/>
 * @author kanon 2015-9-21 添加 会员中心-接收地址注释
 */
public class MemberAddress implements Serializable {
	private Integer addr_id;	//ID
	private Integer member_id;	//会员名称
	private String name;		//会员名称
	private String country;
	private Integer province_id; 	//省ID
	private Integer city_id;		//市ID
	private Integer region_id;		//区ID
	private Integer town_id;		//街道ID
	private String province;		//省
	private String city;			//市
	private String region;			//区
	private String town;		//街道
	private String addr;			//详细地址
	private String zip;				//邮编
	private String tel;				//电话
	private String mobile;			//手机号
	private Integer def_addr;		//是否为默认收货地址
	private String remark;			//备注
	private Integer isDel;
	private String shipAddressName; //地址别名
	
	
	private String addressToBeEdit;//编辑时候引用的数组
	
	
	
	@NotDbField
	public String getAddressToBeEdit() {
		return addressToBeEdit;
	}

	public void setAddressToBeEdit(String addressToBeEdit) {
		this.addressToBeEdit = addressToBeEdit;
	}

	public Integer getIsDel() {
		return isDel;
	}

	public void setIsDel(Integer isDel) {
		this.isDel = isDel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@PrimaryKeyField
	public Integer getAddr_id() {
		return addr_id;
	}

	public void setAddr_id(Integer addr_id) {
		this.addr_id = addr_id;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public Integer getProvince_id() {
		return province_id;
	}

	public void setProvince_id(Integer province_id) {
		this.province_id = province_id;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public Integer getDef_addr() {
		if(def_addr==null){
			def_addr=0;
		}
		return def_addr;
	}

	public void setDef_addr(Integer def_addr) {
		this.def_addr = def_addr;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getShipAddressName() {
		return shipAddressName;
	}

	public void setShipAddressName(String shipAddressName) {
		this.shipAddressName = shipAddressName;
	}

	public Integer getTown_id() {
		return town_id;
	}

	public void setTown_id(Integer town_id) {
		this.town_id = town_id;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

}