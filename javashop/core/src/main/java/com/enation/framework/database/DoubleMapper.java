package com.enation.framework.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class DoubleMapper implements RowMapper<Double> {

	
	public Double mapRow(ResultSet rs, int rowNum) throws SQLException {
		Double dobule = new Double(rs.getDouble(1));
		return dobule;
	}

}
