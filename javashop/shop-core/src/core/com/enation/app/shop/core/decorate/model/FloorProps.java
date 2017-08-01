package com.enation.app.shop.core.decorate.model;

import java.io.Serializable;

/**
 * 
 * 楼层商品条件实体
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@SuppressWarnings("serial")
public class FloorProps implements Serializable{
	private String keyword;//关键字
	private Integer cat_id;//分类id
	private Integer goods_num;//商品数量
	private String goods_sort;//商品排序关键字
	private String goods_order;//商品排序方式
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Integer getCat_id() {
		return cat_id;
	}
	public void setCat_id(Integer cat_id) {
		this.cat_id = cat_id;
	}
	public Integer getGoods_num() {
		return goods_num;
	}
	public void setGoods_num(Integer goods_num) {
		this.goods_num = goods_num;
	}
	public String getGoods_sort() {
		return goods_sort;
	}
	public void setGoods_sort(String goods_sort) {
		this.goods_sort = goods_sort;
	}
	public String getGoods_order() {
		return goods_order;
	}
	public void setGoods_order(String goods_order) {
		this.goods_order = goods_order;
	}
	
}
