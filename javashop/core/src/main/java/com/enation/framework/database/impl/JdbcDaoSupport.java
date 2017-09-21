package com.enation.framework.database.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.DBRuntimeException;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.database.Page;
import com.enation.framework.util.ReflectionUtil;
import com.enation.framework.util.StringUtil;

/**
 * jdbc数据库操作支撑
 * 
 * @author kingapex 2010-1-6下午01:54:18
 * @version v2.0 :对于spring jdbc 4.4进行重构，废弃了SimpleJdbcTemplate，全部使用JdbcTemplate at 2016-10-08
 * 
 */
public class JdbcDaoSupport implements IDaoSupport {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	protected final Logger logger = Logger.getLogger(getClass());
	
 
	@Override
	public void execute(String sql, Object... args) {
		try {
			this.jdbcTemplate.update(sql, args);
		} catch (Exception e) {
			throw new DBRuntimeException(e, sql);
		}
	}

	@Override
	public int getLastId(String table) {
		if (EopSetting.DBTYPE.equals("1")) {
			return queryForInt("SELECT last_insert_id() as id");
		} else if (EopSetting.DBTYPE.equals("2")) {
			int result = 0;
			result = queryForInt("SELECT s_" + table + ".currval as id from DUAL");
			return result;
		} else if (EopSetting.DBTYPE.equals("3")) {
			int result = 0;
			result = queryForInt("select @@identity");
			return result;

		}
		throw new RuntimeException("未知的数据库类型");
	}

	@Override
	public void insert(String table, Map fields) {
		String sql = "";

		try {

			Assert.hasText(table, "表名不能为空");
			Assert.notEmpty(fields, "字段不能为空");
			table = quoteCol(table);

			Object[] cols = fields.keySet().toArray();
			Object[] values = new Object[cols.length];

			for (int i = 0; i < cols.length; i++) {
				if (fields.get(cols[i]) == null) {
					values[i] = null;
				} else {
					values[i] = fields.get(cols[i]).toString();
				}
				cols[i] = quoteCol(cols[i].toString());
			}

			sql = "INSERT INTO " + table + " (" + StringUtil.implode(", ", cols);

			sql = sql + ") VALUES (" + StringUtil.implodeValue(", ", values);

			sql = sql + ")";

			if (table.equals("es_settings")) {
				// jdbcTemplate.update("set identity_insert es_settings off");
			}

			jdbcTemplate.update(sql, values);
		} catch (Exception e) {
			// e.printStackTrace();
			throw new DBRuntimeException(e, sql);
		}
	}

	@Override
	public void insert(String table, Object po) {
		insert(table, ReflectionUtil.po2Map(po));
	}

	@Override
	public Integer queryForInt(String sql, Object... args) {
		try {
			Integer value = jdbcTemplate.queryForObject(sql, Integer.class, args);
			return  value==null?0:value;
		}catch(EmptyResultDataAccessException e){
			return 0;
		} catch (RuntimeException e) {
			this.logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public Float queryForFloat(String sql, Object... args) {
		try {

			Float value = jdbcTemplate.queryForObject(sql, Float.class, args);
			return  value==null?0F:value;

		} catch(EmptyResultDataAccessException e){
			return 0F;
		} catch (RuntimeException e) {
			this.logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public Long queryForLong(String sql, Object... args) {
		try {
			Long value = jdbcTemplate.queryForObject(sql, Long.class, args);
			return  value==null?0L:value;
		} catch(EmptyResultDataAccessException e){
			return 0L;
		} catch (RuntimeException e) {
			this.logger.error(e.getMessage(), e);
			throw e;
		}
 
	}	

	@Override
	public Double queryForDouble(String sql, Object... args) {
		try {

			Double value = jdbcTemplate.queryForObject(sql, Double.class, args);
			return  value==null?0D:value;

		} catch(EmptyResultDataAccessException e){
			return 0D;
		} catch (RuntimeException e) {
			this.logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public String queryForString(String sql, Object... args) {
		String s = "";
		try {
			s = (String) this.jdbcTemplate.queryForObject(sql, String.class,args);
		} catch (RuntimeException e) {
			e.printStackTrace();

		}
		return s;
	}

	@SuppressWarnings("unchecked")
	public List queryForList(String sql, Object... args) {
		return this.jdbcTemplate.queryForList(sql, args);
	}

	@Override
	public <T> List<T> queryForList(String sql, RowMapper<T> mapper, Object... args) {
		try {
			return this.jdbcTemplate.query(sql, args, mapper);
		} catch (Exception ex) {
			throw new DBRuntimeException(ex, sql);
		}
	}

	@Override
	public <T> List<T> queryForList(String sql, Class<T> clazz, Object... args) {
		return this.jdbcTemplate.query(sql, new BeanPropertyRowMapper<T>(clazz), args);

	}

	@Override
	public List queryForListPage(String sql, int pageNo, int pageSize, Object... args) {

		try {
			Assert.hasText(sql, "SQL语句不能为空");
			Assert.isTrue(pageNo >= 1, "pageNo 必须大于等于1");
			String listSql = this.buildPageSql(sql, pageNo, pageSize);
			return queryForList(listSql, args);
		} catch (Exception ex) {
			throw new DBRuntimeException(ex, sql);
		}

	}

	@Override
	public <T> List<T> queryForList(String sql, int pageNo, int pageSize, RowMapper mapper) {

		try {
			Assert.hasText(sql, "SQL语句不能为空");
			Assert.isTrue(pageNo >= 1, "pageNo 必须大于等于1");
			String listSql = this.buildPageSql(sql, pageNo, pageSize);
			return this.queryForList(listSql, mapper);
		} catch (Exception ex) {
			throw new DBRuntimeException(ex, sql);
		}

	}


	public <T> T queryForObject(String sql, Class<T> clazz, Object... args) {
		List<T> objList = this.queryForList(sql, clazz, args);
		if (objList.isEmpty()) {
			return null;
		}
		return objList.get(0);
	}
 

	public Map queryForMap(String sql, Object... args) {
		// Map map = this.simpleJdbcTemplate.queryForMap(sql, args);
		try {
			Map map = this.jdbcTemplate.queryForMap(sql, args);
			if (EopSetting.DBTYPE.equals("2")) {
				Map newMap = new HashMap();
				Iterator keyItr = map.keySet().iterator();
				while (keyItr.hasNext()) {
					String key = (String) keyItr.next();
					Object value = map.get(key);
					newMap.put(key.toLowerCase(), value);
				}
				return newMap;
			} else
				return map;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ObjectNotFoundException(ex, sql);
		}
	}

	public Page queryForPage(String sql, int pageNo, int pageSize, Object... args) {
		String countSql = "SELECT COUNT(*) " + removeSelect(removeOrders(sql));
		return this.queryForPage(sql, countSql, pageNo, pageSize, args);
	}

	@Override
	public Page queryForPage(String sql, String countSql, int pageNo, int pageSize, Object... args) {
		Assert.hasText(sql, "SQL语句不能为空");
		Assert.isTrue(pageNo >= 1, "pageNo 必须大于等于1");
		String listSql = buildPageSql(sql, pageNo, pageSize);

		List list = queryForList(listSql, args);
		int totalCount = queryForInt(countSql, args);
		return new Page(pageNo, totalCount, pageSize, list);

	}
	@Override
	public <T> Page queryForPage(String sql, int pageNo, int pageSize, RowMapper<T> rowMapper, Object... args) {

		Assert.hasText(sql, "SQL语句不能为空");
		Assert.isTrue(pageNo >= 1, "pageNo 必须大于等于1");
		String listSql = buildPageSql(sql, pageNo, pageSize);
		String countSql = "SELECT COUNT(*) " + removeSelect(removeOrders(sql));
		List<T> list = this.queryForList(listSql, rowMapper, args);
		int totalCount = queryForInt(countSql, args);
		return new Page(pageNo, totalCount, pageSize, list);

	}
	@Override
	public <T> Page queryForPage(String sql, int pageNo, int pageSize, Class<T> clazz, Object... args) {

		Assert.hasText(sql, "SQL语句不能为空");
		Assert.isTrue(pageNo >= 1, "pageNo 必须大于等于1");
		String listSql = buildPageSql(sql, pageNo, pageSize);
		String countSql = "SELECT COUNT(*) " + removeSelect(removeOrders(sql));
		List<T> list = this.queryForList(listSql, clazz, args);
		int totalCount = queryForInt(countSql, args);
		return new Page(pageNo, totalCount, pageSize, list);

	}
	@Override
	public void update(String table, Map fields, Map where) {
		String whereSql = "";

		if (where != null) {
			Object[] wherecols = where.keySet().toArray();
			for (int i = 0; i < wherecols.length; i++) {
				wherecols[i] = quoteCol(wherecols[i].toString()) + "=" + quoteValue(where.get(wherecols[i]).toString());
			}
			whereSql += StringUtil.implode(" AND ", wherecols);
		}
		update(table, fields, whereSql);
	}
	@Override
	public void update(String table, Object po, Map where) {
		String whereSql = "";
		// where值
		if (where != null) {
			Object[] wherecols = where.keySet().toArray();
			for (int i = 0; i < wherecols.length; i++) {
				wherecols[i] = quoteCol(wherecols[i].toString()) + "=" + quoteValue(where.get(wherecols[i]).toString());
			}
			whereSql += StringUtil.implode(" AND ", wherecols);
		}
		update(table, ReflectionUtil.po2Map(po), whereSql);

	}
	@Override
	public void update(String table, Object po, String where) {
		this.update(table, ReflectionUtil.po2Map(po), where);

	}
	@Override
	public void update(String table, Map fields, String where) {
		String sql = "";
		Assert.hasText(table, "表名不能为空");
		Assert.notEmpty(fields, "字段不能为空");
		Assert.hasText(where, "where条件不能为空");
		table = quoteCol(table);

		String key = null; // SQLServer 专用
		Object value = null; // SQLServer 专用

		if ("3".equals(EopSetting.DBTYPE)) { // SQLServer主键不允许更新，执行update前先删除
			try {
//				where = where.replaceAll(" ", "");
				key = StringUtils.split(where, "=")[0];
				if (key != null)
					value = fields.get(key);
				fields.remove(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 字段值
		Object[] cols = fields.keySet().toArray();

		Object[] values = new Object[cols.length];
		for (int i = 0; i < cols.length; i++) {
			if (fields.get(cols[i]) == null) {
				values[i] = null;
			} else {
				values[i] = fields.get(cols[i]).toString();
			}
			cols[i] = quoteCol(cols[i].toString()) + "=?";

		}

		sql = "UPDATE " + table + " SET " + StringUtil.implode(", ", cols) + " WHERE " + where;
		this.jdbcTemplate.update(sql, values);

		if (value != null) {// SQLServer时可能不为空
			fields.put(key, value);
		}
	}
	@Override
	public String buildPageSql(String sql, int page, int pageSize) {

		String sql_str = null;

		String db_type = EopSetting.DBTYPE;
		if (db_type.equals("1")) {
			db_type = "mysql";
		} else if (db_type.equals("2")) {
			db_type = "oracle";
		} else if (db_type.equals("3")) {
			db_type = "sqlserver";
		}

		if (db_type.equals("mysql")) {
			sql_str = sql + " LIMIT " + (page - 1) * pageSize + "," + pageSize;
		} else if (db_type.equals("oracle")) {
			StringBuffer local_sql = new StringBuffer("SELECT * FROM (SELECT t1.*,rownum sn1 FROM (");
			local_sql.append(sql);
			local_sql.append(") t1) t2 WHERE t2.sn1 BETWEEN ");
			local_sql.append((page - 1) * pageSize + 1);
			local_sql.append(" AND ");
			local_sql.append(page * pageSize);
			sql_str = local_sql.toString();
		} else if (db_type.equals("sqlserver")) {
			StringBuffer local_sql = new StringBuffer();
			// 找到order by 子句
			String order = SqlPaser.findOrderStr(sql);

			// 剔除order by 子句
			if (order != null)
				sql = removeOrders(sql);
			else
				order = "order by id desc"; // SQLServer分页必需有order by
											// 子句，如果默认语句不包含order
											// by，自动以id降序，如果没有id字段会报错

			// 拼装分页sql
			local_sql.append("select * from (");
			local_sql.append(SqlPaser.insertSelectField("ROW_NUMBER() Over(" + order + ") as rowNum", sql));
			local_sql.append(") tb where rowNum between ");
			local_sql.append((page - 1) * pageSize + 1);
			local_sql.append(" AND ");
			local_sql.append(page * pageSize);

			return local_sql.toString();

		}

		return sql_str.toString();

	}

	/**
	 * 格式化列名 只适用于Mysql
	 * 
	 * @param col
	 * @return
	 */
	private String quoteCol(String col) {
		if (col == null || col.equals("")) {
			return "";
		} else {
			// if("2".equals(EopSetting.DBTYPE))//Oracle
			// return "\"" + col + "\"";
			// else if("1".equals(EopSetting.DBTYPE))//mysql
			// return "`" + col + "`";
			// else //mssql
			// return "[" + col + "]";
			return col;
		}
	}

	/**
	 * 格式化值 只适用于Mysql
	 * 
	 * @param value
	 * @return
	 */
	private String quoteValue(String value) {
		if (value == null || value.equals("")) {
			return "''";
		} else {
			return "'" + value.replaceAll("'", "''") + "'";
		}
	}

	/*
	 * private String removeSelect(String hql) { Assert.hasText(hql); int
	 * beginPos = hql.toLowerCase().lastIndexOf("from"); Assert.isTrue(beginPos
	 * != -1, " hql : " + hql + " must has a keyword 'from'");
	 * 
	 * return hql.substring(beginPos); }
	 */
	private String getStr(int num, String str) {
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < num; i++) {
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * 去除sql的select 子句，未考虑union的情况,用于pagedQuery.
	 * 
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	private String removeSelect(String sql) {
		// 不明白之前的修改什么意思，还原包含group by的sql处理
		// Author chopper 2016-06-28
		sql = sql.toLowerCase();
		if (sql.indexOf("group by") != -1) {
			return " from (" + sql + ") temp_table";
		}

		// FIXME 当查询中含有函数，比如SUM(),替换SQL出错
		Pattern p = Pattern.compile("\\(.*\\)", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			int c = m.end() - m.start();
			m.appendReplacement(sb, getStr(c, "~"));
		}
		m.appendTail(sb);

		String replacedSql = sb.toString();

		int index = replacedSql.indexOf("from");

		// 如果不存在
		if (index == -1) {
			index = replacedSql.indexOf("FROM");
		}
		return sql.substring(index);
	}

	/**
	 * 去除hql的orderby 子句，用于pagedQuery.
	 * 
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	private String removeOrders(String hql) {
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

}
