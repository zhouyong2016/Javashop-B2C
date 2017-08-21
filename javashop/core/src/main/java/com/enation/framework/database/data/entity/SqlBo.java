package com.enation.framework.database.data.entity;

/**
 * sql业务对象。<br>
 * 表示一个可以执行的sql<br>
 * 由一个sql和其相应的变量集体组成<br>
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月20日下午3:18:38
 */
public class SqlBo {
	
	
	//要执行的sql
	private String sql ;
	
	//sql中?号对应的变量
	private Object[] variables;
	
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getVariables() {
		return variables;
	}
	public void setVariables(Object[] variables) {
		this.variables = variables;
	} 
	
}
