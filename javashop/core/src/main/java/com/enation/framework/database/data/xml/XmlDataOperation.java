package com.enation.framework.database.data.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.enation.framework.database.data.IBindVariableInsert;
import com.enation.framework.database.data.IDataOperation;
import com.enation.framework.database.data.INonVariableInsert;
import com.enation.framework.database.data.ISqlParser;
import com.enation.framework.database.data.entity.Field;
import com.enation.framework.database.data.entity.Index;
import com.enation.framework.database.data.entity.SqlBo;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;


/**
 * xml格式 数据操作类
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月14日下午6:48:47
 */
@Service("dataOperation")
public class XmlDataOperation implements IDataOperation {
 
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	@Override
	public void imported(String filePath) {

		
		//加载sql xml文档
		Document xmlDoc = this.loadXml(filePath);
 
		List<Element> actions = xmlDoc.getRootElement().elements("action");
		
		
		
		for (Element action : actions) {
			
			//节点命令类型
			String command = action.elementText("command").toLowerCase();
			Assert.hasText(command, "命令不能为空");
			
			//表名
			String tableName =  action.elementText("table");
			Assert.hasText(tableName, "表名不能为空");
			
			//如果表名没有es_开头，加上es_
			if(!tableName.startsWith("es_") && !tableName.startsWith("eop_")){
				tableName="es_"+tableName;
			}
			
		 
			
			//创建表
			if("create".equals(command)){
				String[] sqls = this.parseCreateSql(tableName, action);
				this.jdbcTemplate.batchUpdate(sqls);
			}
			
			//插入数据
			if("insert".equals(command)){
				
				ISqlParser sqlParser = SqlParserFactory.getSqlParser() ;
				if( sqlParser instanceof IBindVariableInsert){
					SqlBo sqlbo =  this.parseInsertSqlWithVar(tableName, action);
					this.jdbcTemplate.update(sqlbo.getSql(),sqlbo.getVariables());
					 
				}
				if( sqlParser instanceof INonVariableInsert){
					String sql = this.parseInsertSqlNonVar(tableName, action);
					this.jdbcTemplate.execute(sql);
				}
				 
			
			}
			
			
			//修改表
			if("alter".equals(command)){
				String[] sqls =this.parseAlterSql(tableName, action);
				this.jdbcTemplate.batchUpdate(sqls);
			}
			
			
			if("index".equals(command)){
				String[] sqls =this.parseIndexSql(tableName, action);
				if(sqls!=null && sqls.length>0){
					this.jdbcTemplate.batchUpdate(sqls);
				}
			}
			
		 
		}
		
		
	}
	

	
	/**
	 * 根据xml Element 生成索引的语句
	 * @param tableName 表名
	 * @param action xml文档中的action节点
	 * @return 生成索引sql
	 */
	private String[] parseIndexSql(String tableName,Element action){
		
		//准备index list 集合
		List<Index> indexList = new ArrayList<Index>();
		
		//读取xml中的index 节点
		List<Element> indexElList = action.elements("index");
		
		//循环生成index对象，并压入list
		for (Element indexEl : indexElList) {
			
			//索引名称
			String indexName = indexEl.attributeValue("name");
			
			//创建索引
			Index index = new  Index();
			index.setName(indexName);
			
			
			//字段节点集合
			List<Element> fieldElList  = indexEl.elements("field");
			
			//准备field list 集合
			List<Field> fieldList = new ArrayList<Field>();
			
			
			//循环field 节点
			for (Element fieldEl : fieldElList) {
				 
				String name =   fieldEl.elementText("name")  ;
				//生成field字段
				Field field = new Field();
				field.setName(name);
				
				fieldList.add(field);
				 
			}
			index.setFieldList(fieldList);
			indexList.add(index);
			
		}
		
		String sql[]  =SqlParserFactory.getSqlParser().parseIndexSql(tableName, indexList);
		return sql;
				
		
	}
	
	
	
	/**
	 * 根据xml Element 生成插入的语句，不带变量式的
	 * @param tableName 表名
	 * @param action xml文档中的action节点
	 * @return 插入sql
	 */
	private String parseInsertSqlNonVar(String tableName,Element action){
	
		//生成字段集合
		List<Field> fieldList  = createFieldList(action);
		
		//不带变量的插入sql解析器
		INonVariableInsert sqlParser = (INonVariableInsert)SqlParserFactory.getSqlParser() ;
		String sql  =  sqlParser.parseInsertSqlNonVariable(tableName, fieldList);
		return sql;
		
	}

	
	/**
	 * 根据xml Element 生成插入的语句，不带变量式的
	 * @param tableName 表名
	 * @param action xml文档中的action节点
	 * @return 插入sql以及变量
	 */
	private SqlBo parseInsertSqlWithVar(String tableName,Element action){
	
		//生成字段集合
		List<Field> fieldList  = createFieldList(action);
		
		//不带变量的插入sql解析器
		IBindVariableInsert sqlParser = (IBindVariableInsert)SqlParserFactory.getSqlParser() ;
		SqlBo sql  =  sqlParser.parseInsertSqlWidthVariable(tableName, fieldList);
		return sql;
		
	}
	
	
	
	
	
	/**
	 * 通过action节点创建字段列表
	 * @param action 数据xml中的action节点
	 * @return Field 列表
	 */
	private List<Field> createFieldList(Element action){
		
		//准备field list 集合
		List<Field> fieldList = new ArrayList<Field>();
		String fields = action.elementText("fields");
		String values = action.elementText("values");
		
		Assert.hasText(fields,"fields节点不能为空");
		Assert.hasText(values,"values节点不能为空");
		
		
		final String[] fieldAr= fields.split(",");
		final String[] valueAr= values.split(",");
		if(fieldAr.length != valueAr.length){
			throw new IllegalArgumentException("字段和值的个数不匹配");
		}
		 
		
		for (int i = 0; i < fieldAr.length; i++) {
			String fieldName = fieldAr[i];
			String fieldValue = valueAr[i];
			
			
			//生成field字段
			Field field = new Field();
			field.setName(fieldName);
			field.setValue(fieldValue);
			
			fieldList.add(field);
			
		}
		
		return fieldList;
	}
	
	/**
	 * 根据xml Element 生成创建表的语句
	 * @param tableName 表名
	 * @param action xml文档中的action节点
	 * @return create表的sql
	 */
	private String[] parseCreateSql(String tableName ,Element action){
		
		//准备field list 集合
		List<Field> fieldList = new ArrayList<Field>();
		
		//读取xml中的字段节点
		List<Element> fieldElList = action.elements("field");
		
		//循环生成filed对象，并压入list
		for (Element fieldEl : fieldElList) {
			
			//读取xml
			String option = fieldEl.elementText("option");
			String name =   fieldEl.elementText("name")  ;
			String size = fieldEl.elementText("size");
			String type = fieldEl.elementText("type");
			String defaultValue = fieldEl.elementText("default");
			String notnull = fieldEl.elementText("not_null");
			String primaryKey = fieldEl.elementText("primary_key");
			
			if(!StringUtil.isEmpty( option)){
				if ("1".equals(option.substring(0, 1) ) ) { // 如果第一位为1，则为主键
					primaryKey="yes";
				}
			 
				if ("1".equals(option.substring(1, 2))){ // 如果第二位为1，不允许空值
					notnull="yes";
				}
			}
			//生成field字段
			Field field = new Field();
			field.setName(name);
			field.setPrimaryKey("yes".equals(primaryKey));
			field.setType(type);
			field.setSize(size);
			field.setNotNull("yes".equals(notnull));
			
			if(!StringUtil.isEmpty( defaultValue )){
				field.setDefaultValue(defaultValue);
			}
			
			//压入集合
			fieldList.add(field);
		}
		
		//调用sqlParser生成 创建表的语句
		String[] sql =SqlParserFactory.getSqlParser().parseCreateSql(tableName, fieldList);
		return sql;
	}
	
	
	/**
	 * 根据xml Element 生成修改表的语句
	 * @param tableName 表名
	 * @param action xml文档中的action节点
	 * @return 修改表的sql
	 */
	private String[] parseAlterSql(String tableName ,Element action){
		
		//准备field list 集合
		List<Field> fieldList = new ArrayList<Field>();
		
		//读取xml中的字段节点
		List<Element> fieldElList = action.elements("field");
		
		//循环生成filed对象，并压入list
		for (Element fieldEl : fieldElList) {
			
			//读取xml
		
			String name =   fieldEl.elementText("name")  ;
			String size = fieldEl.elementText("size");
			String type = fieldEl.elementText("type");
			String optype = fieldEl.attributeValue("type");
			String default_value = fieldEl.elementText("default");
			
			//生成field字段
			Field field = new Field();
			field.setName(name);
			field.setType(type);
			field.setSize(size);
			field.setOptype(optype);
			field.setDefaultValue(default_value);
			
			//压入集合
			fieldList.add(field);
		}
		
		String sql[]  =SqlParserFactory.getSqlParser().parseAlterSql(tableName, fieldList);
		return sql;
		
	}
	
	@Override
	public void exported(String filePath) {

	}

	
	
	private Document loadXml(String filePath){
		Document xmlDoc = null;
		try {
		 
			if (filePath.startsWith("file:")) {
				filePath = FileUtil.readFile(filePath.replaceAll("file:", ""));
				xmlDoc = DocumentHelper.parseText(filePath);
			} else if (filePath.startsWith("<?xml version")) {
				xmlDoc = DocumentHelper.parseText(filePath);
			}
			else {
				SAXReader saxReader = new SAXReader();
				File file = new File(filePath);
				if (file.exists())
					xmlDoc = saxReader.read(new File(filePath));
			}
			 
		 
		} catch (DocumentException e) {
			e.printStackTrace();
		 
		}
		
		return xmlDoc;
	}
	
}
