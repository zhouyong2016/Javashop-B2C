package com.enation.app.shop.core.order.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 *物流公司
 */

public class Logi implements java.io.Serializable {

	private Integer id;

	private String name;
	
	private String code;

	@PrimaryKeyField
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
    
}