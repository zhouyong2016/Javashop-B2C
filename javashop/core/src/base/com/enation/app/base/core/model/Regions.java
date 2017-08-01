package com.enation.app.base.core.model;

import java.util.List;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 行政区划实体
 * @author lzf<br/>
 * 2010-3-16 下午03:02:33<br/>
 * version 1.0<br/>
 * @author kanon 2015-9-21 添加 行政区划注释
 */
public class Regions implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8793615515414923123L;
	private Integer region_id;		//ID
	private Integer p_region_id;	//父id
	private String region_path;		//路径
	private Integer region_grade;	//级别
	private String local_name;		//名称
	private String zipcode;			//邮编
	private Integer cod;			//是否支持货到付款

	/************以下为非数据库字段*************/
	private List<Regions> children;  //子类别
	private String state;
	
    
	@PrimaryKeyField
	public Integer getRegion_id() {
		return region_id;
	}
	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}
	public Integer getRegion_grade() {
		return region_grade;
	}
	public void setRegion_grade(Integer region_grade) {
		this.region_grade = region_grade;
	}
	public Integer getCod() {
		return cod;
	}
	public void setCod(Integer cod) {
		this.cod = cod;
	}
	public Integer getP_region_id() {
		return p_region_id;
	}
	public void setP_region_id(Integer pRegionId) {
		p_region_id = pRegionId;
	}
	public String getRegion_path() {
		return region_path;
	}
	public void setRegion_path(String regionPath) {
		region_path = regionPath;
	}
	public String getLocal_name() {
		return local_name;
	}
	public void setLocal_name(String localName) {
		local_name = localName;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	@NotDbField
	public List<Regions> getChildren() {
		return children;
	}
	public void setChildren(List<Regions> children) {
		this.children = children;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
}
