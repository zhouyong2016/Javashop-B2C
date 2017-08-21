package com.enation.app.shop.core.decorate.model;

import java.io.Serializable;
/**
 * 专题实体
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@SuppressWarnings("serial")
public class Subject implements Serializable{
	private Integer id;//专题id
	private String title;//专题标题
	private Integer sort;//专题顺序
	private Integer is_display;//专题显示状态
	private String banner;//专题横幅
	private String goods_ids;//专题商品id
	private String picture_path;//专题图片路径
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Integer getIs_display() {
		return is_display;
	}
	public void setIs_display(Integer is_display) {
		this.is_display = is_display;
	}
	public String getBanner() {
		return banner;
	}
	public void setBanner(String banner) {
		this.banner = banner;
	}
	public String getGoods_ids() {
		return goods_ids;
	}
	public void setGoods_ids(String goods_ids) {
		this.goods_ids = goods_ids;
	}
	public String getPicture_path() {
		return picture_path;
	}
	public void setPicture_path(String picture_path) {
		this.picture_path = picture_path;
	}
	
}
