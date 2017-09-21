package com.enation.app.shop.core.decorate.model;

import java.io.Serializable;
/**
 * 
 * 楼层实体
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@SuppressWarnings(value={"serial"})
public class Floor implements Serializable{
	private Integer id;//楼层id
	private Integer page_id;//页面id
	private String title;//楼层标题
	private Integer parent_id;//父id 为v6.1模板改版后的子楼层做预留字段
	private Integer type;//楼层类型 预留字段 为可能实现的功能做准备
	private String style;//风格
	private String logo;//楼层logo图片路径
	private Integer is_display;//显示状态
	private Integer sort;//排序
	//楼层内容
	private String cat_id;//分类id
	private String guid_cat_id;//导航分类id
	private String goods_ids;//商品id json串
	private String props;//搜索条件数据存放
	private String brand_ids;//品牌id json串
	private String adv_ids;//广告id json串
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPage_id() {
		return page_id;
	}
	public void setPage_id(Integer page_id) {
		this.page_id = page_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getParent_id() {
		return parent_id;
	}
	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public Integer getIs_display() {
		return is_display;
	}
	public void setIs_display(Integer is_display) {
		this.is_display = is_display;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getCat_id() {
		return cat_id;
	}
	public void setCat_id(String cat_id) {
		this.cat_id = cat_id;
	}
	
	public String getGuid_cat_id() {
		return guid_cat_id;
	}
	public void setGuid_cat_id(String guid_cat_id) {
		this.guid_cat_id = guid_cat_id;
	}
	public String getGoods_ids() {
		return goods_ids;
	}
	public void setGoods_ids(String goods_ids) {
		this.goods_ids = goods_ids;
	}
	public String getProps() {
		return props;
	}
	public void setProps(String props) {
		this.props = props;
	}
	public String getBrand_ids() {
		return brand_ids;
	}
	public void setBrand_ids(String brand_ids) {
		this.brand_ids = brand_ids;
	}
	public String getAdv_ids() {
		return adv_ids;
	}
	public void setAdv_ids(String adv_ids) {
		this.adv_ids = adv_ids;
	}
	
	
}
