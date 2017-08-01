package com.enation.framework.database.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * 数据库结果集过滤器
 * 可对RowMapper的结果某或多列进行过滤 
 * @author kingapex
 *
 */
public interface IRowMapperColumnFilter {
	
	
	/**
	 * 对结果集的行进行过滤
	 * @param ColValues 结果集一行的map
	 * @param rs 结果集
	 */
	public void filter(Map colValues,ResultSet rs) throws SQLException;
	
	
}
