package com.enation.app.base.core.service.dbsolution.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.enation.eop.sdk.utils.DateUtil;

/**
 * SQLServer数据库导入导出
 * 
 * @author liuzy
 * 
 */
@Service("sqlserverSolution")
public class SQLServerSolution extends DBSolution {
	/**
	 * 解决id插入值问题开始
	 */
	
	protected boolean setIdentity(String table, boolean on) {
		sqlExchange = "SET IDENTITY_INSERT " + table + " " + (on ? "ON" : "OFF");
		return true;
	}
	
	@Override
	protected boolean beforeInsert(String table, String fields, String values) {
		return setIdentity(table,true);
	}
	
	@Override
	protected void afterInsert(String table, String field, String values) {
		setIdentity(table,false);
	}

	/**
	 * 解决id插入值问题结束
	 */


	/**
	 * 返回与当前类匹配的类型名称
	 * 
	 * @param type
	 * @return
	 */
	@Override
	public  String toLocalType(String type, String size) {
		if ("int".equals(type)) {
			if ("1".equals(size))
				return "smallint";
			else
				return "int";
		}
		if("smallint".equals(type))
			return "smallint";
		
		if("bigint".equals(type))
			return "bigint";
		
		if ("memo".equals(type))
			return "text";

		if ("datetime".equals(type))
			return "datetime";

		if ("long".equals(type))
			return "bigint";

		return type + "(" + size + ")";
	}

	@Override
	public String getCreateSQL(Element action) {
		String table = getTableName(action.elementText("table"));
		List<Element> fields = action.elements("field");
		String sql = getDropSQL(table) + EXECUTECHAR;
		sql = sql + "create table " + table + " (";

		String pk = "";
		for (int i = 0; i < fields.size(); i++) {
			String nl = "";
			Element field = fields.get(i);
			String name = "[" + field.elementText("name") + "]";
			String size = field.elementText("size");
			String type = toLocalType(field.elementText("type").toLowerCase(),
					size);
			String option = field.elementText("option");
			String def = field.elementText("default");

			if ("1".equals(option.substring(1, 2))) // 如果第二位为1，不允许空值
				nl = " not null";

			if (def != null)
				nl = nl + " default " + def;

			if ("1".equals(option.substring(0, 1))) { // 如果第一位为1，则为主键
				pk = "constraint PK_" + table.toUpperCase()
						+ " primary key nonclustered (" + name + "),";
				nl = nl + " identity";
			}

			sql = sql + name + " " + type + nl + ",";
		}
		sql = sql + pk;
		sql = sql.substring(0, sql.length() - 1) + ")";

		return sql;
	}

	/**
	 * 由基类调用的多态函数，返回当前类所要捕获的自定义function列表，与getFuncValue配合使用
	 */
	@Override
	public String[] getFuncName() {
		String[] name = { "time" };
		return name;
	}

	@Override
	public String getFieldValue(int fieldType, Object fieldValue) {
		if(fieldValue instanceof Timestamp){
			Date value = DateUtil.toDate(fieldValue.toString(), "yyyy-MM-dd HH:mm:ss.S");
			return "time(" + value.getTime() + ")";
		} else
			return super.getFieldValue(fieldType, fieldValue);
	}
	
	@Override
	public String getDropSQL(String table) {
		String sql = "if exists (select 1 from sysobjects where id = object_id('"
				+ table
				+ "')"
				+ "and type = 'U') drop table "
				+ table;
		return sql;
	}

	@Override
	public String getSaasCreateSQL(Element action, int userid, int siteid) {
		String table = getSaasTableName(action.elementText("table"), userid, siteid);
		List<Element> fields = action.elements("field");
		String sql = getDropSQL(table) + EXECUTECHAR;
		sql = sql + "create table " + table + " (";

		String pk = "";
		for (int i = 0; i < fields.size(); i++) {
			String nl = "";
			Element field = fields.get(i);
			String name = "[" + field.elementText("name") + "]";
			String size = field.elementText("size");
			String type = toLocalType(field.elementText("type").toLowerCase(),
					size);
			String option = field.elementText("option");
			String def = field.elementText("default");

			if ("1".equals(option.substring(1, 2))) // 如果第二位为1，不允许空值
				nl = " not null";

			if (def != null)
				nl = nl + " default " + def;

			if ("1".equals(option.substring(0, 1))) { // 如果第一位为1，则为主键
				pk = "constraint PK_" + table.toUpperCase()
						+ " primary key nonclustered (" + name + "),";
				nl = nl + " identity";
			}

			sql = sql + name + " " + type + nl + ",";
		}
		sql = sql + pk;
		sql = sql.substring(0, sql.length() - 1) + ")";

		return sql;
	}

	
}
