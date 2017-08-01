package com.enation.app.shop.core.goods.model;

import java.util.ArrayList;
import java.util.List;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;
/**
 * 
 * (货品快照实体) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2017年1月6日 上午12:41:32
 */
public class ProductSnapshot extends Product {
	private Integer product_id;
	private Integer snapshot_id;	//快照id

	public Integer getSnapshot_id() {
		return snapshot_id;
	}

	public void setSnapshot_id(Integer snapshot_id) {
		this.snapshot_id = snapshot_id;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}
	
}
