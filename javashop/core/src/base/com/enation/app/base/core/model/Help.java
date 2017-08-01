package com.enation.app.base.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 帮助实体
 * 
 * @author kingapex 2010-10-17下午10:10:44
 */
public class Help {
	private Integer id;
	private String helpid;
	private String title;
	private String content;

	@PrimaryKeyField
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHelpid() {
		return helpid;
	}

	public void setHelpid(String helpid) {
		this.helpid = helpid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
