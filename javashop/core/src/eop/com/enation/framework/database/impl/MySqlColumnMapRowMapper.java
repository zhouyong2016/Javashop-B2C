package com.enation.framework.database.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.support.JdbcUtils;

public class MySqlColumnMapRowMapper extends ColumnMapRowMapper {
	@Override
	public Map<String, Object>  mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Map mapOfColValues = createColumnMap(columnCount);
		for (int i = 1; i <= columnCount; i++) {
			String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
			key = key.toLowerCase();
			Object obj = null;
			String typename= rsmd.getColumnTypeName(i).toUpperCase();
			if("DECIMAL".equals(typename)){
				obj = rs.getDouble(i);
			}else{
				obj = getColumnValue(rs, i);
			}
			 
			mapOfColValues.put(key, obj);
		}
		return mapOfColValues;
	}
}
