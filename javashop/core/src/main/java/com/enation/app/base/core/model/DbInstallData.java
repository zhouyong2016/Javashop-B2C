package com.enation.app.base.core.model;

/**
 * 数据库安装 model
 * @author kingapex
 * @version v1.0
 * 2016年2月17日下午8:18:44
 * @since v6.0
 */
public class DbInstallData {
	
	/**
	 * 数据库类型
	 */
	String dbtype;
	
	/**
	 * 用户名
	 */
	String uname;
	
	/**
	 * 密码
	 */
	String pwd;
	
	/**
	 * 数据库ip地址
	 */
	String dbhost;
	
	/**
	 * 数据库名
	 */
	String dbname;
	
	/**
	 * 驱动类名
	 */
	String driver;
	
	/**
	 * 连接url
	 */
	String url;
	
	
	
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getDbhost() {
		return dbhost;
	}
	public void setDbhost(String dbhost) {
		this.dbhost = dbhost;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
}
