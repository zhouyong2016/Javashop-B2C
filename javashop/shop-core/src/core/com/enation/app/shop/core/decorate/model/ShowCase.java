package com.enation.app.shop.core.decorate.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 橱窗实体
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@SuppressWarnings("serial")
public class ShowCase implements Serializable{
	private Integer id;//橱窗id
	private String title;//橱窗标题
	private String flag;//橱窗标识
	private String content;//橱窗内容
	private Integer is_display;//显示状态
	private Integer sort;//排序
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
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	
	
}
