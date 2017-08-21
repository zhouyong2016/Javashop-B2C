package com.enation.app.base.core.model;

import java.util.List;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;
import com.enation.framework.model.Image;

/**
 * 数据日志实体
 * @author kingapex 2010-10-19下午03:29:02
 * @author kanon 2015-12-17 version 1.1 添加注释
 */
public class DataLog {
	
	/**
	 * 数据日志Id
	 */
	private Integer id;
	
	/**
	 * 数据内容
	 */
	private String content;
	
	/**
	 * 详细地址
	 */
	private String url;
	
	/**
	 * 图片
	 */
	private String pics;
	
	/**
	 * 站点名称
	 */
	private String sitename;
	
	/**
	 * 站点域名
	 */
	private String domain;
	
	/**
	 * 日志类型
	 */
	private String logtype;
	
	/**
	 * 操作类型
	 */
	private String optype;
	
	/**
	 * 操作时间
	 */
	private Long dateline;
	
	/**
	 * 用户Id
	 */
	private Integer userid;
	
	/**
	 * 站点Id
	 */
	private Integer siteid;
	
	/**
	 *  图片实体列表，由pics字串转换而来
	 */
	private List<Image> picList; 

	@PrimaryKeyField
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPics() {
		return pics;
	}

	public void setPics(String pics) {
		this.pics = pics;
	}

	public String getSitename() {
		return sitename;
	}

	public void setSitename(String sitename) {
		this.sitename = sitename;
	}

	public String getLogtype() {
		return logtype;
	}

	public void setLogtype(String logtype) {
		this.logtype = logtype;
	}

	public String getOptype() {
		return optype;
	}

	public void setOptype(String optype) {
		this.optype = optype;
	}

	public Long getDateline() {
		return dateline;
	}

	public void setDateline(Long dateline) {
		this.dateline = dateline;
	}

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public Integer getSiteid() {
		return siteid;
	}

	public void setSiteid(Integer siteid) {
		this.siteid = siteid;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@NotDbField
	public List<Image> getPicList() {
		return picList;
	}

	public void setPicList(List<Image> picList) {
		this.picList = picList;
	}

}
