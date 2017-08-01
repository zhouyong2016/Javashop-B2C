package com.enation.app.shop.core.other.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ArticleMapper implements RowMapper {

	public Object mapRow(ResultSet rs, int arg1) throws SQLException {

		Article article = new Article();
		article.setTitle(rs.getString("title"));
		article.setContent(rs.getString("content"));
		article.setId(rs.getInt("id"));
		article.setCreate_time(rs.getLong("create_time"));

		return article;
	}

}
