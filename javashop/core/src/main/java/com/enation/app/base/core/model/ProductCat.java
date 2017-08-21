package com.enation.app.base.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 解决方案分类
 * 
 * @author kingapex 2010-9-1下午01:50:18
 */
public class ProductCat {
	private Integer id;
	private String name;
	private Integer num;
	private Integer sort;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

}
