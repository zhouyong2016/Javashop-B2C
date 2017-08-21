package com.enation.app.base.core.model;

import com.enation.framework.database.PrimaryKeyField;

public class ProductColor {
	private Integer id;
	private String colorname;
	private Integer num;

	@PrimaryKeyField
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public String getColorname() {
		return colorname;
	}

	public void setColorname(String colorname) {
		this.colorname = colorname;
	}

}
