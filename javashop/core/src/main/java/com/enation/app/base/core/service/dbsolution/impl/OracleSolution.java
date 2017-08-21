package com.enation.app.base.core.service.dbsolution.impl;

import java.io.Reader;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.enation.framework.util.StringUtil;

/**
 * Oracle数据库导入导出
 * 
 * @author liuzy
 * 
 */
@Service
public class OracleSolution extends DBSolution {
	/**
	 * 返回与当前类匹配的类型名称
	 * 
	 * @param type
	 * @return
	 */
	@Override
	public String toLocalType(String type, String size) {
		if ("int".equals(type)) {
			if ("1".equals(size))
				return "NUMBER(2)";
			return "NUMBER(" + size + ")";
		}
		if ("memo".equals(type))
			return "CLOB";

		if ("datetime".equals(type))
			return "TIMESTAMP";

		if ("long".equals(type))
			return "NUMBER(20)";

		if ("decimal".equals(type))
			return "NUMBER(20,2)";
		
		if ("bigint".equals(type))
			return "NUMBER(11)";

		return type.toUpperCase() + "(" + size + ")";
	}

	private String getBlockSQL(String sql) {
		return "BEGIN\n" + "\tEXECUTE IMMEDIATE '" + sql + "';\n"
				+ "\tEXCEPTION WHEN OTHERS THEN NULL;\n" + "END;" + EXECUTECHAR
				+ "\n";
	}

	private String getTriggerSQL(String table, String field) {
		String trigger = getBlockSQL("DROP TRIGGER TIB_" + table)
				+ "CREATE TRIGGER \"TIB_" + table + "\" BEFORE INSERT\n"
				+ "\tON " + table + " FOR EACH ROW\n" + "\tDECLARE\n"
				+ "\tINTEGRITY_ERROR  EXCEPTION;\n"
				+ "\tERRNO            INTEGER;\n"
				+ "\tERRMSG           CHAR(200);\n" + "\tMAXID			INTEGER;\n"
				+ "BEGIN\n" + "\tIF :NEW." + field + " IS NULL THEN\n"
				+ "\t\t SELECT S_"+table+".NEXTVAL INTO :NEW."
				+ field + " FROM DUAL;\n" + "\t\t\n" + "\tEND IF;\n"
				+ "EXCEPTION\n" + "\tWHEN INTEGRITY_ERROR THEN\n"
				+ "\t\tRAISE_APPLICATION_ERROR(ERRNO, ERRMSG);\n" + "END;";
		return trigger;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getCreateSQL(Element action) {
		String table = getTableName(action.elementText("table").toUpperCase());
		List<Element> fields = action.elements("field");

		String sql = getDropSQL(table) + EXECUTECHAR;

		sql = sql + "CREATE TABLE " + table + " (";

		String sequence = "";
		String key = "";//lzf add 20120308
		for (int i = 0; i < fields.size(); i++) {
			String nl = "";
			Element field = fields.get(i);
			String name = "\"" + field.elementText("name").toUpperCase() + "\"";
			String size = field.elementText("size");
			String type = toLocalType(field.elementText("type").toLowerCase(),
					size);
			String option = field.elementText("option"); // 获取字段参数，第一位为自增，第二位为允许空值
			String def = field.elementText("default");

			if ("1".equals(option.substring(0, 1))) { // 如果第一位为1，则为主键
				sequence = getBlockSQL("DROP SEQUENCE S_" + table);
				//es_menu序列起始于1，确保跟es_auth_action中objvalue的值一致
				String startWith = ("es_menu".equalsIgnoreCase(table)) ? "1" : "1000";
				//地区表的起始值为4000
				startWith = ("ES_REGIONS".equalsIgnoreCase(table)) ? "4000" : "1000";
				sequence = sequence + "CREATE SEQUENCE S_" + table+" START WITH " + startWith
						+ EXECUTECHAR + " \n";
				sequence = sequence + getTriggerSQL(table, name);
				
				key = "CONSTRAINT PK_" + table + " PRIMARY KEY (" + name + ")";//lzf add 20120308
			}

			if ("1".equals(option.substring(1, 2)))
				nl = " NOT NULL";

			if (def != null)
				nl = " default " + def + nl;

			sql = sql + name + " " + type + nl + ",";
		}
		//lzf add 20120308
		if(!StringUtil.isEmpty(key))
			sql = sql + key + ")" + EXECUTECHAR + "\n";
		else
		//lzf add end
			sql = sql.substring(0, sql.length() - 1) + ")" + EXECUTECHAR + "\n";
		sql = sql + sequence;
		return sql;
	}

	@Override
	public void setPrefix(String prefix) {
		this.prefix = prefix.toUpperCase();
	}

	/**
	 * 重载此函数，因为Oracle中&字符代表变量
	 */
	@Override
	protected String getConvertedSQL(String sql) {
		sql = sql.replaceAll("&", "&'||'");
		return sql;
	}

	/**
	 * 由基类调用的多态函数，传入参数为function名称和值，返回自定义值给基类
	 */
	@Override
	protected Object getFuncValue(String name, String value) {
		if ("time".equals(name)) {
			Date date = new Date(Long.parseLong(value));
			return "TIMESTAMP'"
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
					+ "'";
		}
		return super.getFuncValue(name, value);
	}
	
	//private String clobToString
	@Override
	public String getFieldValue(int fieldType, Object fieldValue) {
		Object value = fieldValue;
		if(fieldValue instanceof Clob) {
			try {
				Clob clob = (Clob) fieldValue;
				Reader inStream = clob.getCharacterStream();
				char[] buf = new char[(int)clob.length()];
				inStream.read(buf);
				value = new String(buf);
				inStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(fieldValue instanceof oracle.sql.TIMESTAMP) {
			oracle.sql.TIMESTAMP time = (oracle.sql.TIMESTAMP) fieldValue;
			try {
				value = "time(" + time.dateValue().getTime() + ")";
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.getFieldValue(fieldType, value);
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
	public String getDropSQL(String table) {
		table = table.toUpperCase();
		String sql = getBlockSQL("DROP TRIGGER TIB_" + table) 
				+ getBlockSQL("DROP TABLE " + table)
				+ getBlockSQL("DROP SEQUENCE S_" + table);
		return sql;
	}

//	@Override
//	public String getCreateSaasSQL(String table, int userid, int siteid, String keyname) {
//		table = getTableName(table).toUpperCase();
//		String newtable = table + "_" + userid + "_" +siteid;
//		String sql = getDropSQL(newtable) + EXECUTECHAR + "\n";
//		sql = sql + "CREATE TABLE " + newtable + " AS (SELECT * FROM " + table + " WHERE 0=1)" + EXECUTECHAR + "\n";
//		
//		String sequence = getBlockSQL("DROP SEQUENCE S_" + newtable);
//		sequence = sequence + "CREATE SEQUENCE S_" + newtable
//				+ EXECUTECHAR + "\n";
//		sequence = sequence + getTriggerSQL(newtable, keyname);
//		return sql + sequence;
//	}

	@Override
	public String getSaasCreateSQL(Element action, int userid, int siteid) {
		String table = getSaasTableName(action.elementText("table").toUpperCase(), userid, siteid);
		List<Element> fields = action.elements("field");

		String sql = getDropSQL(table) + EXECUTECHAR;

		sql = sql + "CREATE TABLE " + table + " (";

		String sequence = "";
		String key = "";//lzf add 20120308
		for (int i = 0; i < fields.size(); i++) {
			String nl = "";
			Element field = fields.get(i);
			String name = "\"" + field.elementText("name").toUpperCase() + "\"";
			String size = field.elementText("size");
			String type = toLocalType(field.elementText("type").toLowerCase(),
					size);
			String option = field.elementText("option"); // 获取字段参数，第一位为自增，第二位为允许空值
			String def = field.elementText("default");

			if ("1".equals(option.substring(0, 1))) { // 如果第一位为1，则为主键
				sequence = getBlockSQL("DROP SEQUENCE S_" + table);
				sequence = sequence + "CREATE SEQUENCE S_" + table
						+ EXECUTECHAR + "\n";
				sequence = sequence + getTriggerSQL(table, name);
				
				key = "CONSTRAINT PK_" + table + " PRIMARY KEY (" + name + ")";//lzf add 20120308
			}

			if ("1".equals(option.substring(1, 2)))
				nl = " NOT NULL";

			if (def != null)
				nl = " default " + def + nl;

			sql = sql + name + " " + type + nl + ",";
		}
		//lzf add 20120308
		if(!StringUtil.isEmpty(key))
			sql = sql + key + ")" + EXECUTECHAR + "\n";
		else
		//lzf add end
			sql = sql.substring(0, sql.length() - 1) + ")" + EXECUTECHAR + "\n";
		sql = sql + sequence;
		return sql;
	}

}
