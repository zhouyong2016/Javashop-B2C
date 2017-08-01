package com.enation.app.shop.core.decorate.model;

/**
 * 
 * 风格实体
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
public class Style {
	private Integer id;//风格id
	private String style;//风格名称
	private String path;//风格路径
	private Integer is_top_style;//是否为顶级风格
	private Integer is_default_style;//是否为默认风格
	private Integer page_id;
	
	
	public Integer getPage_id() {
		return page_id;
	}
	public void setPage_id(Integer page_id) {
		this.page_id = page_id;
	}
	//需要添加type
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Integer getIs_top_style() {
		return is_top_style;
	}
	public void setIs_top_style(Integer is_top_style) {
		this.is_top_style = is_top_style;
	}
	public Integer getIs_default_style() {
		return is_default_style;
	}
	public void setIs_default_style(Integer is_default_style) {
		this.is_default_style = is_default_style;
	}
	
}
