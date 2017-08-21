package com.enation.app.shop.core.member.model;

/**
 * 站内消息前台
 * @author fk
 *
 */
public class MessageFront {

	/**
	 * 消息主键
	 */
	private Integer msg_id;
	
	/**
	 * 消息内容
	 */
	private String msg_title;
	
	/**
	 * 消息内容
	 */
	private String msg_content;
	
	/**
	 * 会员标号
	 */
	private Integer member_id;
	
	/**
	 * 管理员标号
	 */
	private Integer adminuser_id;
	
	/**
	 * 管理员姓名
	 */
	private String adminuser_name;
	
	/**
	 * 消息时间
	 */
	private Long send_time;
	
	/**
	 * 0 删除   1  没有删除
	 */
	private Integer is_delete;
	
	/**
	 * 0 未读   1   已读
	 */
	private Integer is_read;

	
	public Integer getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(Integer msg_id) {
		this.msg_id = msg_id;
	}

	public String getMsg_content() {
		return msg_content;
	}

	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public Integer getAdminuser_id() {
		return adminuser_id;
	}

	public void setAdminuser_id(Integer adminuser_id) {
		this.adminuser_id = adminuser_id;
	}

	public Long getSend_time() {
		return send_time;
	}

	public void setSend_time(Long send_time) {
		this.send_time = send_time;
	}

	public Integer getIs_delete() {
		return is_delete;
	}

	public void setIs_delete(Integer is_delete) {
		this.is_delete = is_delete;
	}

	public Integer getIs_read() {
		return is_read;
	}

	public void setIs_read(Integer is_read) {
		this.is_read = is_read;
	}

	public String getMsg_title() {
		return msg_title;
	}

	public void setMsg_title(String msg_title) {
		this.msg_title = msg_title;
	}

	public String getAdminuser_name() {
		return adminuser_name;
	}

	public void setAdminuser_name(String adminuser_name) {
		this.adminuser_name = adminuser_name;
	}
	
}
