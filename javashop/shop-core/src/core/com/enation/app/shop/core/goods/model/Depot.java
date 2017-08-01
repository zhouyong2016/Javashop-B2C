package com.enation.app.shop.core.goods.model;
import com.enation.framework.database.PrimaryKeyField;


/**
 * 库房实体
 * @author kingapex
 *
 */
public class Depot {
	public Integer id;
	public String name;
	public Integer choose;
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
	public Integer getChoose() {
		return choose;
	}
	public void setChoose(Integer choose) {
		this.choose = choose;
	}
	
}