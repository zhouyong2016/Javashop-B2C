package com.enation.app.base.core.model;

import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 广告
 * 
 * @author 李志富 lzf<br/>
 *         2010-2-4 下午03:19:38<br/>
 *         version 1.0<br/>
 * <br/>
 * @author kanon 2015-9-21 添加 广告注释
 */
public class Adv {
	private Integer aid;	//广告id
	private Integer acid;	//广告位id
	private Integer atype;	//类型
	private Long begintime;	//开始时间
	private Long endtime;	//结束时间
	private Integer isclose;	//是否开启
	private String attachment;	//附件
	private String atturl;	//网络资源
	private String url;		//链接地址
	private String aname;	//名称
	private Integer clickcount;	//点击数
 
	private String disabled;	//是否删除
	private String linkman;	//联系人
	private String company;	//所属单位
	private String contact;	//联系方式

	/**
	 * 所属广告位名
	 * 非数据库字段
	 */
	private String cname;
	
	@PrimaryKeyField
	public Integer getAid() {
		return aid;
	}

	public void setAid(Integer aid) {
		this.aid = aid;
	}

	public Integer getAcid() {
		return acid;
	}

	public void setAcid(Integer acid) {
		this.acid = acid;
	}

	public Integer getAtype() {
		return atype;
	}

	public void setAtype(Integer atype) {
		this.atype = atype;
	}

	public Long getBegintime() {
		return begintime;
	}

	public void setBegintime(Long begintime) {
		this.begintime = begintime;
	}

	public Long getEndtime() {
		return endtime;
	}

	public void setEndtime(Long endtime) {
		this.endtime = endtime;
	}

	public Integer getIsclose() {
		return isclose;
	}

	public void setIsclose(Integer isclose) {
		this.isclose = isclose;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getAtturl() {
		return atturl;
	}

	public void setAtturl(String atturl) {
		this.atturl = atturl;
	}
	
	
	@NotDbField
	/**
	 * 得到转换后的，可访问的 图片url
	 * @return
	 */
	public String getHttpAttUrl(){
		return  StaticResourcesUtil.convertToUrl( atturl );
		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

	public Integer getClickcount() {
		return clickcount;
	}

	public void setClickcount(Integer clickcount) {
		this.clickcount = clickcount;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@NotDbField
	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

}