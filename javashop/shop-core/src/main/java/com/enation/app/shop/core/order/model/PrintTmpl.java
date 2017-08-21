package com.enation.app.shop.core.order.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 打印模板
 * 
 * @author lzf<br/>
 *         2010-5-4上午09:58:30<br/>
 *         version 1.0
 */
public class PrintTmpl implements java.io.Serializable {
	private Integer prt_tmpl_id;
	private String prt_tmpl_title;
	private String shortcut;
	private String disabled;
	private String prt_tmpl_width;
	private String prt_tmpl_height;
	private String prt_tmpl_data;
	private String bgimage;

	@PrimaryKeyField
	public Integer getPrt_tmpl_id() {
		return prt_tmpl_id;
	}

	public void setPrt_tmpl_id(Integer prtTmplId) {
		prt_tmpl_id = prtTmplId;
	}

	public String getPrt_tmpl_title() {
		return prt_tmpl_title;
	}

	public void setPrt_tmpl_title(String prtTmplTitle) {
		prt_tmpl_title = prtTmplTitle;
	}

	public String getShortcut() {
		return shortcut;
	}

	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getPrt_tmpl_width() {
		return prt_tmpl_width;
	}

	public void setPrt_tmpl_width(String prt_tmpl_width) {
		this.prt_tmpl_width = prt_tmpl_width;
	}

	public String getPrt_tmpl_height() {
		return prt_tmpl_height;
	}

	public void setPrt_tmpl_height(String prt_tmpl_height) {
		this.prt_tmpl_height = prt_tmpl_height;
	}

	public String getPrt_tmpl_data() {
		return prt_tmpl_data;
	}

	public void setPrt_tmpl_data(String prtTmplData) {
		prt_tmpl_data = prtTmplData;
	}

	public String getBgimage() {
		return bgimage;
	}

	public void setBgimage(String bgimage) {
		this.bgimage = bgimage;
	}
}
