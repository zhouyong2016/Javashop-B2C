package com.enation.app.base.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;


/**
 * smpt
 * @author kingapex
 * @date 2011-11-1 下午12:07:07 
 * @version V1.0
 * @author kanon 2015-9-21 添加 SMTP注释
 */
public class Smtp  implements Serializable  {
	
	private static final long serialVersionUID = 4645737054149076379L; // 序列化ID
	private Integer id;			//ID
	private String host;		//HOST
	private String username;	//用户名
	private String password;	//密码
	private long last_send_time;	//最后一次发信事件
	private int send_count;		//已发送
	private int max_count;		//最大发信数量
	private String mail_from;	//发信邮箱
	//新增ssl支持
	private int port;//端口
	private int open_ssl;//ssl是否开启
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public long getLast_send_time() {
		return last_send_time;
	}
	public void setLast_send_time(long last_send_time) {
		this.last_send_time = last_send_time;
	}
	public int getSend_count() {
		return send_count;
	}
	public void setSend_count(int send_count) {
		this.send_count = send_count;
	}
	public int getMax_count() {
		return max_count;
	}
	public void setMax_count(int max_count) {
		this.max_count = max_count;
	}
	public String getMail_from() {
		return mail_from;
	}
	public void setMail_from(String mail_from) {
		this.mail_from = mail_from;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getOpen_ssl() {
		return open_ssl;
	}
	public void setOpen_ssl(int open_ssl) {
		this.open_ssl = open_ssl;
	}
	
	
}
