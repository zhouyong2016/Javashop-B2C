package com.enation.framework.database.data.xml;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.enation.framework.database.data.INonVariableInsert;
import com.enation.framework.database.data.ISqlParser;
import com.enation.framework.database.data.entity.Field;
import com.enation.framework.database.data.entity.Index;
import com.enation.framework.util.StringUtil;

/**
 * Mysql Sql解析器
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月14日上午11:43:56
 */
public class MysqlSqlParser implements ISqlParser ,INonVariableInsert{

	/**
	 * 转换为此数据库方言的数据库型声明
	 * @param type 字段类型
	 * @param size 字段大小
	 * @return 完整的字段声明，如: decimal(20,2)
	 */
	public String toDialectType(String type, String size) {
		if ("int".equals(type)) {
			if ("1".equals(size))
				return "smallint(1)";
			else
				return "int("+size+")";
		}

		if ("memo".equals(type))
			return "longtext";

		if ("datetime".equals(type))
			return "datetime";

		if ("long".equals(type))
			return "bigint";

		return type + "(" + size + ")";
	}
	
	
	@Override
	public String[] parseCreateSql(String tableName, List<Field> fieldList) {
		
		String sql1 = this.parseDropSql(tableName)[0] ;
		String sql2= "create table " + tableName + " (";
		
		
		String pk = "";
		String uq = "";
		for (int i = 0; i < fieldList.size(); i++) {
			String nl = "";
			Field field = fieldList.get(i);
			String name = field.getName();
			String size = field.getSize();
			String type = toDialectType(field.getType().toLowerCase(), size);
		 
			Object def = field.getDefaultValue();

			//默认值
			if (def != null){
				nl = nl + " default " + def;
			}
			
			//是否为主键
			if (field.isPrimaryKey()) {  
				pk = "primary key (" + name + "),";
				nl = nl + " auto_increment";
			}
			
			//是否允许为空
			if (field.isNotNull()) {  
				nl+= " NOT NULL ";
			}

			sql2 = sql2 + name + " " + type + nl + ",";
		}
		sql2 = sql2 + pk + uq;
		sql2 = sql2.substring(0, sql2.length() - 1) + ") ENGINE = InnoDB";
		return new String[]{sql1,sql2};
	}

	
	private String decodeValue(String value) {
		value = value.replace("!quote;", "\\'");
		return value.replaceAll("!comma;", ",");
	}
	
	
	@Override
	public String parseInsertSqlNonVariable(String tableName, List<Field> fieldList) {

		StringBuffer sql = new StringBuffer();
		StringBuffer fieldSql = new StringBuffer();
		StringBuffer valueSql = new StringBuffer();
		
 
	 
		for (Field field : fieldList) {
	 
			String name = field.getName();
			Object value = field.getValue();
			
			//对值进行解码
			value =decodeValue(""+value);
			
			if(fieldSql.length()!=0){
				fieldSql.append(",");
			}
			fieldSql.append(name);
			
			if(valueSql.length()!=0){
				valueSql.append(",");
			}
			
			valueSql.append(value);
		}
		
		sql.append("INSERT INTO " + tableName +"(" );
		sql.append(fieldSql);
		sql.append( ") VALUES ("  );
		sql.append(valueSql);
		sql.append( ");\n");
 
			
	 
		return sql.toString();
	}

	/**
	 * 生成修改表的sql语句
	 */
	@Override
	public String[] parseAlterSql(String tableName,  List<Field> fieldList) {

		
		
		
		int length = fieldList.size();
		List<String> sqlList  = new ArrayList<String>();
		
		for(int i=0;i<length; i++) {
			
			String  sql ="alter table "+tableName ;
			
			Field field = fieldList.get(i);
			String optype =field.getOptype();
			String name =field.getName();
			String size =field.getSize();

	 
			if("add".equals(optype)){
				String datatype = field.getType();

				//oracle和sqlserver的都不写column关键字
				//区分oracle 和 sqlserver 和 mysql， alter语句的不同
				sql+=" add column "+ name +" " ;

				sql+= this.toDialectType(datatype, size);

				Object def =field.getDefaultValue();
				
				if(def==null){def="";}
				
				if(!StringUtil.isEmpty(""+def)){
					sql+=" default "+def;
				}
			}
			if("drop".equals(optype)){
				sql+=" drop column "+ name ;
			} 
			sqlList.add(sql);
		}
		String[] sqls = new String[]{};
		return sqlList.toArray( sqls);
	}

	@Override
	public String[] parseDropSql(String tableName) {
		String sql = "drop table if exists " + tableName ;
		return new String[]{sql};
	}

	/**
	 * 生成Sqlserver的 创建索引sql，生成效果如下:<br>
	 * CREATE INDEX index_name<br>
	 * ON table_name (column_name1,column_name1)<br>
	 */
	@Override
	public String[] parseIndexSql(String tableName,List<Index> indexList) {
		Assert.hasText(tableName, "表名不能为空");
		
		List<String> sqlList  = new ArrayList<String>();

		for (Index index : indexList) {
			StringBuilder sql = new StringBuilder();
			List<Field> fieldList =  index.getFieldList();
			if(fieldList==null){
				continue;
			}
			
			sql.append("CREATE INDEX "+ index.getName());
			sql.append(" ON "+tableName);
			sql.append("(");
			for (int i = 0; i < fieldList.size(); i++) {
				
				Field  field = fieldList.get(i);
				String fieldName = field.getName();
				Assert.hasText(fieldName, "索引字段名不能为空");
				
				if(i>0){
					sql.append(",");
				}
				
				sql.append(fieldName);
				
				
			}
			sql.append(")");
	 
			sqlList.add(sql.toString());
		}
		
		String[] sqls = new String[]{};
		return sqlList.toArray( sqls);
		
	}

}
