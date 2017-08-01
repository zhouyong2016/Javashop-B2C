package com.enation.app.base.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 短消息
 * @author kingapex
 *2013-8-2下午6:13:10
 */
public class SmsMessage {
	private int message_id;
	private int user_id;
	private int admin_id;
	private long send_time;
	private String message_active;
	private String message_info;
	private String tel;
	
	
	@PrimaryKeyField
	public int getMessage_id() {
		return message_id;
	}
	public void setMessage_id(int message_id) {
		this.message_id = message_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getAdmin_id() {
		return admin_id;
	}
	public void setAdmin_id(int admin_id) {
		this.admin_id = admin_id;
	}
	public long getSend_time() {
		return send_time;
	}
	public void setSend_time(long send_time) {
		this.send_time = send_time;
	}
	public String getMessage_active() {
		return message_active;
	}
	public void setMessage_active(String message_active) {
		this.message_active = message_active;
	}
	public String getMessage_info() {
		return message_info;
	}
	public void setMessage_info(String message_info) {
		this.message_info = message_info;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	
  
}
