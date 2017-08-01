package com.enation.app.shop.core.member.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class MemberComment {
	
	private int comment_id;
	private int goods_id;
	private Integer member_id;
	private String content;
	private String img;
	private long dateline;
	private String ip;
	private String reply;
	private long replytime;
	private int status;
	private int type;
	private int replystatus;
	private int grade;
	
	private String imgPath;
	
	private Integer product_id;//新增字段货品id  2016/12/29  zjp
	
	public MemberComment() {
		super();
	}

	public MemberComment(int comment_id, int goods_id, Integer member_id,
			String content, String img, long dateline, String ip, String reply,
			long replytime, int status, int type, int replystatus, int grade) {
		super();
		this.comment_id = comment_id;
		this.goods_id = goods_id;
		this.member_id = member_id;
		this.content = content;
		this.img = img;
		this.dateline = dateline;
		this.ip = ip;
		this.reply = reply;
		this.replytime = replytime;
		this.status = status;
		this.type = type;
		this.replystatus = replystatus;
		this.grade = grade;
	}

	@PrimaryKeyField
	public int getComment_id() {
		return comment_id;
	}

	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	public int getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public long getReplytime() {
		return replytime;
	}

	public void setReplytime(long replytime) {
		this.replytime = replytime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getReplystatus() {
		return replystatus;
	}

	public void setReplystatus(int replystatus) {
		this.replystatus = replystatus;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	@NotDbField
	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}
	
	
	
}
