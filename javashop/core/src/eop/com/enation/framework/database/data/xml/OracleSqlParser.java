package com.enation.framework.database.data.xml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.springframework.util.Assert;

import com.enation.framework.database.data.IBindVariableInsert;
import com.enation.framework.database.data.ISqlParser;
import com.enation.framework.database.data.entity.Field;
import com.enation.framework.database.data.entity.Index;
import com.enation.framework.database.data.entity.SqlBo;
import com.enation.framework.util.StringUtil;

/**
 * Oracle Sql解析器<br>
 * 实现 {@link IBindVariableInsert} 接口， 其Insert语句要带有变量，因为Clob字段拼接插入的话不能大于400字节
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月14日上午11:43:56
 */
public class OracleSqlParser implements ISqlParser,IBindVariableInsert {

	/**
	 * 转换为此数据库方言的数据库型声明
	 * @param type 字段类型
	 * @param size 字段大小
	 * @return 完整的字段声明，如: decimal(20,2)
	 */
	public String toDialectType(String type, String size) {
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

	private String[] getTriggerSQL(String table, String field) {
		String sql1 = getBlockSQL("DROP TRIGGER TIB_" + table);
		
		String sql2 = 
				 "CREATE TRIGGER \"TIB_" + table + "\" BEFORE INSERT\n"
				+ "\tON " + table + " FOR EACH ROW\n" + "\tDECLARE\n"
				+ "\tINTEGRITY_ERROR  EXCEPTION;\n"
				+ "\tERRNO            INTEGER;\n"
				+ "\tERRMSG           CHAR(200);\n" + "\tMAXID			INTEGER;\n";
		
		sql2 += "BEGIN\n" + "\tIF :NEW." + field + " IS NULL THEN\n"
				+ "\t\t SELECT S_"+table+".NEXTVAL INTO :NEW."
				+ field + " FROM DUAL;\n" + "\t\t\n" + "\tEND IF;\n"
				+ "EXCEPTION\n" + "\tWHEN INTEGRITY_ERROR THEN\n"
				+ "\t\tRAISE_APPLICATION_ERROR(ERRNO, ERRMSG);\n" + "END;";
		
		return new String[]{sql1,sql2};
	}
	
	@Override
	public String[] parseCreateSql(String tableName, List<Field> fieldList) {
		
		//转为大写
		tableName = tableName.toUpperCase();

		String[] dropSql = this.parseDropSql(tableName)  ;

		String createSql = "CREATE TABLE " + tableName + " (";

	 
		List<String> seqList = new ArrayList<String>();
		
		String key = ""; 
		for (int i = 0; i < fieldList.size(); i++) {
			String nl = "";
			Field field = fieldList.get(i);
			String name = "\"" +field.getName().toUpperCase() + "\"";
			String size = field.getSize();
			String type = toDialectType(field.getType().toLowerCase(), size);
			
			
			Object def = field.getDefaultValue();

			//为主键
			if ( field.isPrimaryKey() ) { 
				
				//序列1
				seqList.add( getBlockSQL("DROP SEQUENCE S_" + tableName) ) ;
				
				String startWith="1000";
				
				//已下逻辑错误   修正人  xulipeng
//				//es_menu序列起始于1，确保跟es_auth_action中objvalue的值一致
//				String startWith = ("es_menu".equalsIgnoreCase(tableName)) ? "1" : "1";
//				//地区表的起始值为4000
//				startWith = ("ES_REGIONS".equalsIgnoreCase(tableName)) ? "4000" : "1000";
				
				if("es_menu".equalsIgnoreCase(tableName)){
					startWith="1";
				}else if("ES_REGIONS".equalsIgnoreCase(tableName)){
					startWith="4000";
				}
				
				//序列2
				seqList.add( "CREATE SEQUENCE S_" + tableName+" START WITH " + startWith );
				
				//序列3
				String[] triggerSqlAr =  getTriggerSQL(tableName, name);
				for (String s : triggerSqlAr) {
					seqList.add( s);
				}
				
				
				key = "CONSTRAINT PK_" + tableName + " PRIMARY KEY (" + name + ")";//lzf add 20120308
			}

			if ( field.isNotNull() ){
				nl = " NOT NULL";
			}
			
			if (def != null){
				nl = " default " + def + nl;
			}
			
			createSql = createSql + name + " " + type + nl + ",";
		}
	 
		if(!StringUtil.isEmpty(key)){
			createSql = createSql + key + ")"   + "\n";
		}
		else{
			createSql = createSql.substring(0, createSql.length() - 1) + ")"  ;
		}
//		createSql = createSql + sequence;
		 if(seqList.isEmpty()){
			 return new String[]{dropSql[0],dropSql[1],dropSql[2],createSql};
		 }else{
			 return new String[]{dropSql[0],dropSql[1],dropSql[2],createSql,seqList.get(0),seqList.get(1),seqList.get(2),seqList.get(3)};
		 }
	}

 
	
	private String getBlockSQL(String sql) {
		return "BEGIN\n" + "\tEXECUTE IMMEDIATE '" + sql + "';\n"
				+ "\tEXCEPTION WHEN OTHERS THEN NULL;\n" + "END;"  
				+ "\n";
	}
	
	private String decodeValue(String value) {
		value = value.replace("'", "");
		value = value.replace("!quote;", "''");
		return value.replaceAll("!comma;", ",");
	}
	
	

	@Override
	public SqlBo parseInsertSqlWidthVariable(String tableName, List<Field> fieldList) {
		Assert.hasText(tableName, "表名不能为空");

		StringBuffer sql = new StringBuffer();
		StringBuffer fieldSql = new StringBuffer();
		StringBuffer valueSql = new StringBuffer();
		
		//存储值变量的list
		List<Object> valueList  = new ArrayList<Object>();
	 
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
			
			valueSql.append("?");
			
			valueList.add(value);
			
		}
		
		sql.append("INSERT INTO " + tableName +"(" );
		sql.append(fieldSql);
		sql.append( ") VALUES ("  );
		sql.append(valueSql);
		sql.append( ")");
		
		Object[] vars = new Object[]{};
		
		SqlBo sqlBo= new SqlBo();
		sqlBo.setSql(sql.toString());
		sqlBo.setVariables( valueList.toArray(vars) );
		return sqlBo;
	}
	
	

	/**
	 * 生成修改表的sql语句
	 */
	@Override
	public String[] parseAlterSql(String tableName,  List<Field> fieldList) {
		
		
		//转为大写
		tableName = tableName.toUpperCase();
		Assert.hasText(tableName, "表名不能为空");

		
		int length = fieldList.size();
		List<String> sqlList  = new ArrayList<String>();
		
		for(int i=0;i<length; i++) {
			
			String  sql ="alter table "+tableName ;
			
			Field field = fieldList.get(i);
			String optype =field.getOptype();
			String name =field.getName();
			String size =field.getSize();
			Assert.hasText(name, "字段名不能为空");

			name= name.toUpperCase();
	 
			if("add".equals(optype)){
				String datatype = field.getType();

				//oracle和sqlserver的都不写column关键字
				//区分oracle 和 sqlserver 和 mysql， alter语句的不同
				sql+=" add  "+ name +" " ;

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
		Assert.hasText(tableName, "表名不能为空");

		tableName = tableName.toUpperCase();
		String[] sqlar = new String[]{
		 getBlockSQL("DROP TRIGGER TIB_" + tableName) 
		, getBlockSQL("DROP TABLE " + tableName)
		,getBlockSQL("DROP SEQUENCE S_" + tableName)
		};
		return sqlar;
	}

	/**
	 * 生成Oracle的 创建索引sql，生成效果如下:<br>
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
			sql.append(")\n");
	 
			sqlList.add(sql.toString());
		}
		
		String[] sqls = new String[]{};
		return sqlList.toArray( sqls);
		
	}


}
