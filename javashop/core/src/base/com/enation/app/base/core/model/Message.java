package com.enation.app.base.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 留言、短消息
 * 
 * @author lzf<br/>
 *         2010-3-18 上午11:18:35<br/>
 *         version 1.0<br/>
 */
public class Message implements Serializable {
	private Integer msg_id;
	private Integer for_id;
	private String msg_from;// 发送者uname
	private Integer from_id;
	private Integer from_type;
	private Integer to_id;
	private String msg_to;// 接收者uname
	private Integer to_type;
	private String unread; // enum('1','0') not null default '0'
	private String folder; // enum('inbox','outbox') not null default 'inbox'
	private String email;
	private String tel;
	private String subject;
	private String message;
	private Integer rel_order;
	private Long date_line;
	private String is_sec; // enum('true','false') not null default 'true'
	private String del_status; // enum('0','1','2') default '0', '0'=>正常,
								// '1'=>收信人已删除，'2'=>发信人已删除
	private String disabled; // enum('true','false') not null default 'false'
	private String msg_ip;
	private String msg_type; // enum('default','payment') not null default
								// 'default'

	public String getMsg_from() {
		return msg_from;
	}

	public void setMsg_from(String msgFrom) {
		msg_from = msgFrom;
	}

	public String getMsg_to() {
		return msg_to;
	}

	public void setMsg_to(String msgTo) {
		msg_to = msgTo;
	}

	public String getUnread() {
		return unread;
	}

	public void setUnread(String unread) {
		this.unread = unread;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@PrimaryKeyField
	public Integer getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(Integer msg_id) {
		this.msg_id = msg_id;
	}

	public Integer getFor_id() {
		return for_id;
	}

	public void setFor_id(Integer for_id) {
		this.for_id = for_id;
	}

	public Integer getFrom_id() {
		return from_id;
	}

	public void setFrom_id(Integer from_id) {
		this.from_id = from_id;
	}

	public Integer getFrom_type() {
		return from_type;
	}

	public void setFrom_type(Integer from_type) {
		this.from_type = from_type;
	}

	public Integer getTo_id() {
		return to_id;
	}

	public void setTo_id(Integer to_id) {
		this.to_id = to_id;
	}

	public Integer getTo_type() {
		return to_type;
	}

	public void setTo_type(Integer to_type) {
		this.to_type = to_type;
	}

	public Integer getRel_order() {
		return rel_order;
	}

	public void setRel_order(Integer rel_order) {
		this.rel_order = rel_order;
	}

	public Long getDate_line() {
		return date_line;
	}

	public void setDate_line(Long dateLine) {
		date_line = dateLine;
	}

	public String getIs_sec() {
		return is_sec;
	}

	public void setIs_sec(String isSec) {
		is_sec = isSec;
	}

	public String getDel_status() {
		return del_status;
	}

	public void setDel_status(String delStatus) {
		del_status = delStatus;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getMsg_ip() {
		return msg_ip;
	}

	public void setMsg_ip(String msgIp) {
		msg_ip = msgIp;
	}

	public String getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(String msgType) {
		msg_type = msgType;
	}

}
