package com.enation.app.base.core.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * @author kingapex
 * @version 1.0
 * @created 11-十月-2009 23:26:27
 */
public class User {
	private Integer user_id;
	private String username;
	private String password;
	
	@PrimaryKeyField
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
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
	
	

}