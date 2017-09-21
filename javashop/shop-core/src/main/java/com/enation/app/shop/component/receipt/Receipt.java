package com.enation.app.shop.component.receipt;


import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 
 * 发票实体类
 * @author wanglu
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月27日 下午3:50:56
 */
public class Receipt implements Serializable {
	/** 发票ID*/
	private Integer id;
	/** 会员ID*/
	private Integer member_id;
	/** 发票抬头*/
	private String title;
	/** 发票类别*/
	private String content;
	/** 是否为默认*/
	private int is_default; //0   1 默认 
	/** 创建时间*/
	private long add_time;
	/** 发票税号*/
	private String duty;
	/** 发票类型*/
	private Integer type;
	


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getIs_default() {
		return is_default;
	}
	public void setIs_default(int is_default) {
		this.is_default = is_default;
	}
	public long getAdd_time() {
		return add_time;
	}
	public void setAdd_time(long add_time) {
		this.add_time = add_time;
	}
	public String getDuty() {
		return duty;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
}
