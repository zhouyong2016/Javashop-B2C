package com.enation.framework.database.data.xml;

import java.util.List;

import org.springframework.util.Assert;

import com.enation.framework.database.data.INonVariableInsert;
import com.enation.framework.database.data.ISqlParser;
import com.enation.framework.database.data.entity.Field;
import com.enation.framework.database.data.entity.Index;
import com.enation.framework.util.StringUtil;


/**
 * SqlServer Sql解析器
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月14日上午11:43:56
 */
public class SqlServerSqlParser implements ISqlParser,INonVariableInsert {
	
	

	/**
	 * 生成创建表的sql
	 */
	@Override
	public String[] parseCreateSql(String tableName,List<Field> fieldList) {
		
		String sql = this.parseDropSql(tableName)[0] ;
		
		sql = sql + "create table " + tableName + " (";

		String pk = "";
		
		for (int i = 0; i < fieldList.size(); i++) {
			Field field = fieldList.get(i);
			
			String nl = "";
			
			//字段名
			String name = "[" + field.getName()+ "]";
			
			//字段大小
			String size = field.getSize();
			
			//生成数据库类型
			String type = toDialectType(field.getType().toLowerCase(), size);
			
			Object def = field.getDefaultValue();
			
			//是否允许为空
			if (field.isNotNull()){ 
				nl = " not null";
			}
			
			//默认值
			if (def != null){
				nl = nl + " default " + def;
			}
			
			//是否为主键
			if (field.isPrimaryKey()) {  
				pk = "constraint PK_" + tableName.toUpperCase()
						+ " primary key nonclustered (" + name + "),";
				nl = nl + " identity";
			}

			sql = sql + name + " " + type + nl + ",";
		}
		
		sql = sql + pk;
		sql = sql.substring(0, sql.length() - 1) + ");\n";

		return new String[]{sql} ;
	 
	}

	

	/**
	 * 生成insert的sql
	 */
	@Override
	public String parseInsertSqlNonVariable(String tableName, List<Field> fieldList) {
 
		StringBuffer sql = new StringBuffer();
		StringBuffer fieldSql = new StringBuffer();
		StringBuffer valueSql = new StringBuffer();
		
		boolean isHavntPrimarykey = isHavntPrimarykey(tableName);
		
		if(isHavntPrimarykey){
			sql.append("SET IDENTITY_INSERT "+tableName+" ON;\n");
		}
	 
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
		
		if(isHavntPrimarykey){
			sql.append("SET IDENTITY_INSERT "+tableName+" OFF;\n");
		}
			
	 
		return sql.toString();
	}

	
	/**
	 * 生成修改表的sql语句
	 */
	@Override
	public String[] parseAlterSql(String tableName,  List<Field> fieldList) {

		String sql="" ;

 
		for(int i=0;i<fieldList.size(); i++) {
			
			sql +="alter table "+tableName ;
			
			Field field = fieldList.get(i);
			String optype =field.getOptype();
			String name =field.getName();
			String size =field.getSize();

	 
			if("add".equals(optype)){
				String datatype = field.getType();

				//oracle和sqlserver的都不写column关键字
				//区分oracle 和 sqlserver 和 mysql， alter语句的不同
				sql+=" add "+ name +" " ;	

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
			sql+=";\n";
		}
		
 
		return new String[]{sql} ;
	}

	@Override
	public String[] parseDropSql(String tableName) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("if exists (select 1 from sysobjects where id = object_id('");
		sql.append(tableName);
		sql.append("')");
		sql.append("and type = 'U') drop table ");
		sql.append(tableName + ";\n");
		
		return new String[]{sql.toString()};
	}
	

	/**
	 * 生成Sqlserver的 创建索引sql，生成效果如下:<br>
	 * CREATE INDEX index_name<br>
	 * ON table_name (column_name1,column_name1)<br>
	 */
	@Override
	public String[] parseIndexSql(String tableName,List<Index> indexList) {
		Assert.hasText(tableName, "表名不能为空");
		
		StringBuilder sql = new StringBuilder();
		
		for (Index index : indexList) {
			
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
			sql.append(");\n");
			
		}
		return  new String[]{sql.toString()};
	}
	
	
	
	/**
	 * 判断是否是没有主键的表
	 * @param table
	 * @return
	 */
	private boolean isHavntPrimarykey(String table){
		if(!table.equals("eop_app") && !table.equals("es_type_brand") && !table.equals("es_tag_rel") && !table.equals("es_tag_relb")  && !table.equals("es_depot_user") &&  !table.equals("es_settings") ){	//判断某些表没有主键
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 * 对value值进行解码
	 * @param value 解码前值
	 * @return 解码码后值
	 */
	private String decodeValue(String value) {
		value = value.replace("!quote;", "''");
		return value.replaceAll("!comma;", ",");
	}
	
	/**
	 * 转换为此数据库方言的数据库型声明
	 * @param type 字段类型
	 * @param size 字段大小
	 * @return 完整的字段声明，如: decimal(20,2)
	 */
	private   String toDialectType(String type, String size) {
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
		
	
}
