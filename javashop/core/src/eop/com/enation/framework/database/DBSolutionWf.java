package com.enation.framework.database;

import java.io.File;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

@Service("dbinstall")
public class DBSolutionWf   {
	 
	@Autowired
	private IDaoSupport daoSupport;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
//	@Test	
//	public void testImport() {
//		this.daoSupport.execute("SET IDENTITY_INSERT es_auth_action ON");
//		DBSolutionFactory.dbImport("/Users/kingapex/workspace/trunk/javashop/b2c/src/main/webapp/products/simple/auth.xml","es_");
//	}
	
	
	private Document loadDocument(String xmlFile) throws DocumentException {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		File file = new File(xmlFile);
		if (file.exists())
			document = saxReader.read(new File(xmlFile));
		return document;
	}
	
	
	
	public String decodeValue(String value) {
		value = value.replace("!quote;", "'");
		return value.replaceAll("!comma;", ",");
	}
	
	private static String  LAST_TABLE ="nothing";
	
	private boolean isTableChanged(String table){
//		
//		if(!LAST_TABLE.equals(table)){
//			LAST_TABLE= table;
//			return true;
//		}else{
//			return false;
//		}
		
		if(!table.equals("es_type_brand") && !table.equals("es_tag_rel") && !table.equals("es_tag_relb")  && !table.equals("es_depot_user") &&  !table.equals("es_settings") ){	//判断某些表没有主键
			return true;
		}else{
			return false;
		}
		
	}
	
	
	private String parseSql(Element action){
		
			String table =  action.elementText("table");
			if(!table.startsWith("es_")){
				table="es_"+table;
			}
			String command = action.elementText("command").toLowerCase();
		
			String fields = action.elementText("fields");
			String values1 = action.elementText("values");
			String sql = "";
			
			if("insert".equals(command)){

				values1 = decodeValue(values1);
				
				Assert.hasText(table, "表名不能为空");
				Assert.hasText(fields, "字段不能为空");

				final String[] field_ar= fields.split(",");
				final String[] value_ar= values1.split(",");
	 
				
		 
				Object[] cols = field_ar;
				Object[] values = value_ar;
				
				boolean isTableChanged = isTableChanged(table);
				
			
				
				if("insert".equals(command)){
				if(isTableChanged){
					sql	+="SET IDENTITY_INSERT "+table+" ON;\n";
				}
				sql += "INSERT INTO " + table + " (" + StringUtil.implode(", ", cols);
				sql = sql + ") VALUES (" + StringUtil.implode(", ", values);
				sql = sql + ");\n";
				
				if(isTableChanged){
					sql += "SET IDENTITY_INSERT "+table+" OFF;\n";
				}
					
				}
			}
			if("create".equals(command)){

				sql +=this.getCreateSQL(action)+"\n";
			}
			
			
			if("drop".equals(command)){
				sql +=this.getDropSQL(table)+"\n";
			}
			
			if("alter".equals(command)){
				sql += this.getAlterSQl(action)+"\n";
			}
			
			
			
			
			return sql;
			
	}
	
	

	private String getAlterSQl(Element action ){

 
			String table= action.elementText("table");;
			
			if(!table.startsWith("es_")){
				table="es_"+table;
			}
			
			
			String sql="" ;
 
			List<Element> fields = action.elements("field");
			for(int i=0,len=fields.size();i<len;i++) {
				Element element = fields.get(i);
				String type = element.attributeValue("type");
				String name = element.elementText("name") ;
				String size = element.elementText("size") ;

				if(i!=0){
					sql+=",";
				}
				if("add".equals(type)){
					String datatype = element.elementText("type") ;

					//oracle和sqlserver的都不写column关键字
					//区分oracle 和 sqlserver 和 mysql， alter语句的不同

					if( EopSetting.DBTYPE.equals("2") || EopSetting.DBTYPE.equals("3")){
						sql+=" "+ name +" " ;	
					}else{
						sql+=" add column "+ name +" " ;
					}

					sql+= toLocalType(datatype, size);

					String def = element.elementText("default") ;
					if(!StringUtil.isEmpty(def)){
						sql+=" default "+def;
					}
				}
				if("drop".equals(type)){
					sql+=" drop column "+ name ;
				} 

			}

			if( EopSetting.DBTYPE.equals("2")){
				sql=" add ("+sql+")";
			}else if(EopSetting.DBTYPE.equals("3")){
				sql=" add "+sql+"";
			}

			sql ="alter table "+table +" "+sql+";";
	 
			return sql;
	 
	}
	
	
	
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
	public static final String EXECUTECHAR = "!-->";

	public String getCreateSQL(Element action) {
		String table = action.elementText("table");
		
		if(!table.startsWith("es_")){
			table="es_"+table;
		}
		
		
		List<Element> fields = action.elements("field");
		String sql = getDropSQL(table)+";\n" ;
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
		sql = sql.substring(0, sql.length() - 1) + ");";

		return sql;
	}

	

 
	public String getFieldValue(int fieldType, Object fieldValue) {
		if(fieldValue instanceof Timestamp){
			Date value = DateUtil.toDate(fieldValue.toString(), "yyyy-MM-dd HH:mm:ss.S");
			return "time(" + value.getTime() + ")";
		} else
			return getFieldValue1(fieldType, fieldValue);
	}
	
	
	public String getFieldValue1(int fieldType, Object fieldValue) {
		String value = ""+ fieldValue;
		
		if (Types.VARCHAR == fieldType || Types.NVARCHAR == fieldType|| Types.CHAR == fieldType){
			return "'" + value + "'";
		}else if (Types.NVARCHAR == fieldType || Types.LONGVARCHAR == fieldType|| Types.CLOB == fieldType){
			return "'" + value + "'";
		}
		return "" + value;
	}
 
	public String getDropSQL(String table) {
		if(!table.startsWith("es_")){
			table="es_"+table;
		}
		String sql = "if exists (select 1 from sysobjects where id = object_id('"
				+ table
				+ "')"
				+ "and type = 'U') drop table "
				+ table+";";
		return sql;
	}
	
	
	
	/**
	 * 导入一个xml文件到数据库中
	 * 
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean doImport(String xml) {
		Document xmlDoc = null;
		try {
		 
			if (xml.startsWith("file:")) {
				xml = FileUtil.readFile(xml.replaceAll("file:", ""));
				xmlDoc = DocumentHelper.parseText(xml);
			} else if (xml.startsWith("<?xml version")) {
				xmlDoc = DocumentHelper.parseText(xml);
			}
			else {
				xmlDoc = loadDocument(xml);
			}
			 
		 
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		}
		List<Element> actions = xmlDoc.getRootElement().elements("action");
		List<String> sqlList  = new ArrayList<String>();
		for (Element action : actions) {
			
			String sql  =parseSql(action);
			if(!StringUtil.isEmpty(sql)){
				sqlList.add(sql);
				//System.out.println(sql);
			}
		}
		
		if(!sqlList.isEmpty()){
			String[] sqls = new String[]{};
			sqls = sqlList.toArray(sqls);
			this.jdbcTemplate.batchUpdate(sqls);
		}
 
		return true;
	}
	
	
	public static void main(String[] args) {
		DBSolutionWf test = new DBSolutionWf();
//		test.doImport("/Users/kingapex/workspace/trunk/javashop/b2c/src/main/webapp/products/simple/auth.xml");
		test.doImport("/Users/kingapex/workspace/trunk/javashop/b2c/src/main/webapp/products/simple/example_data.xml");

		
	}
	
	 
	 
}
