package com.enation.app.base.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.enation.framework.database.data.INonVariableInsert;
import com.enation.framework.database.data.entity.Field;
import com.enation.framework.database.data.entity.Index;
import com.enation.framework.database.data.xml.SqlServerSqlParser;
 

/**
 * SqlServer Sql解析器测试
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月14日下午2:55:34
 */

public class SqlServerSqlParserTest {
	
	
	/**
	 * 测试生成表sql语句
	 */
	@Test
	public void testCreateSql(){
		String tableName ="es_tb";
		
		Field f1 = new Field();
		f1.setName("f1");
		f1.setPrimaryKey(true);
		f1.setType("int");
		f1.setSize("8");
		f1.setNotNull(true);
		
		Field f2 = new Field();
		f2.setName("f2");
		f2.setType("int");
		f2.setSize("8");
		f2.setNotNull(true);
		
		
		Field f3 = new Field();
		f3.setName("f3");
		f3.setType("decimal");
		f3.setSize("10,2");
		
		
		List<Field> fieldList  = new ArrayList<Field>();
		fieldList.add(f1);
		fieldList.add(f2);
		fieldList.add(f3);
		
		
		SqlServerSqlParser sqlServerSqlParser = new SqlServerSqlParser();
		String[] sql  =sqlServerSqlParser.parseCreateSql(tableName, fieldList);
		
		String expectedSql= "if exists (select 1 from sysobjects where id = object_id('es_tb')and type = 'U') drop table es_tb;\n";
		expectedSql+="create table es_tb ([f1] int not null identity,[f2] int not null,[f3] decimal(10,2),constraint PK_ES_TB primary key nonclustered ([f1]));\n";
		assertEquals(expectedSql, sql[0]);
	}
	
	
	
	
	
	
	/**
	 * 测试生成插入sql语句
	 */
	@Test
	public void  testInsertSql(){
		String tableName ="es_goods";
 
		Field f1 = new Field();
		f1.setName("f1");
		f1.setValue(1);
		
		Field f2 = new Field();
		f2.setName("f2");
		f2.setValue("''a'");
		
		List<Field> fieldList  = new ArrayList<Field>();
		fieldList.add(f1);
		fieldList.add(f2);
		
		SqlServerSqlParser sqlServerSqlParser = new SqlServerSqlParser();
		INonVariableInsert insertParser =(INonVariableInsert) sqlServerSqlParser;
		String sql  =insertParser.parseInsertSqlNonVariable(tableName, fieldList);
		
		String  expectedSql = "SET IDENTITY_INSERT es_goods ON;\n";
		expectedSql += "INSERT INTO es_goods(f1,f2) VALUES (1,''a');\n";
		expectedSql += "SET IDENTITY_INSERT es_goods OFF;\n";
		assertEquals(expectedSql, sql);
		
	}
	
	

	/**
	 * 测试修改表sql语句
	 */
	@Test
	public void testAlterSql(){
		String tableName ="es_tb";
		
		Field f1 = new Field();
		f1.setName("f2");
		f1.setOptype("drop");
		
		Field f2 = new Field();
		f2.setName("f4");
		f2.setType("int");
		f2.setSize("8");
		f2.setOptype("add");
		f2.setNotNull(true);
		f2.setDefaultValue("0");
		
		List<Field> fieldList  = new ArrayList<Field>();
		fieldList.add(f1);
		fieldList.add(f2);
		
		
		SqlServerSqlParser sqlServerSqlParser = new SqlServerSqlParser();
		String[] sql  =sqlServerSqlParser.parseAlterSql(tableName, fieldList);
		String expectedSql= "alter table es_tb drop column f2;\n";
		expectedSql+="alter table es_tb add f4 int default 0;\n";
		assertEquals(expectedSql, sql[0]);
		
	}
	
	
	@Test
	public void testIndex(){
		
		Field f1 = new Field();
		f1.setName("f1");
		
		
		Field f2 = new Field();
		f2.setName("f2");
		
		
		List<Field> fieldList = new ArrayList<Field>();
		fieldList.add(f1);
		fieldList.add(f2);
		
		
		Index i1  = new  Index();
		i1.setName("index1");
		i1.setFieldList(fieldList);
		
		Index i2  = new  Index();
		i2.setName("index2");
		i2.setFieldList(fieldList);
		
		
		List<Index> indexList  = new ArrayList<Index>();
		indexList.add(i1);
		indexList.add(i2);
		
		SqlServerSqlParser sqlServerSqlParser = new SqlServerSqlParser();
		String[] sql  = sqlServerSqlParser.parseIndexSql("es_tb", indexList);
		
		String expectedSql ="CREATE INDEX index1 ON es_tb(f1,f2);\n";
		expectedSql +="CREATE INDEX index2 ON es_tb(f1,f2);\n";
		assertEquals(expectedSql, sql[0]);
		
		
	}
	
	
	
	
	
}
