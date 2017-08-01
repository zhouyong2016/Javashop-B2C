package com.enation.app.base.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.stereotype.Service;

import com.enation.framework.database.data.INonVariableInsert;
import com.enation.framework.database.data.entity.Field;
import com.enation.framework.database.data.entity.Index;
import com.enation.framework.database.data.xml.MysqlSqlParser;
 

/**
 * mysql  Sql解析器测试
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月14日下午2:55:34
 */
@Service
public class MysqlSqlParserTest {
	
	
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
		
		
		MysqlSqlParser mysqlSqlParser = new MysqlSqlParser();
		String[] sql  =mysqlSqlParser.parseCreateSql(tableName, fieldList);
//		System.out.println(sql);
		String expectedSql1= "drop table if exists es_tb";
		String expectedSql2="create table es_tb (f1 int(8) auto_increment NOT NULL ,f2 int(8) NOT NULL ,f3 decimal(10,2),primary key (f1)) ENGINE = InnoDB";
		assertArrayEquals(new String[]{expectedSql1,expectedSql2},sql);
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
		f2.setValue("'!quote;abc!quote;'");
		
		List<Field> fieldList  = new ArrayList<Field>();
		fieldList.add(f1);
		fieldList.add(f2);
		
		MysqlSqlParser mysqlSqlParser = new MysqlSqlParser();
		INonVariableInsert insertParser =(INonVariableInsert) mysqlSqlParser;
		String sql  =insertParser.parseInsertSqlNonVariable(tableName, fieldList);
		String expectedSql  = "INSERT INTO es_goods(f1,f2) VALUES (1,'\\'abc\\'');\n";
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
		f2.setDefaultValue("0");
		f2.setNotNull(true);
		
		List<Field> fieldList  = new ArrayList<Field>();
		fieldList.add(f1);
		fieldList.add(f2);
		
		
		MysqlSqlParser mysqlSqlParser = new MysqlSqlParser();
		String sqls[]  =mysqlSqlParser.parseAlterSql(tableName, fieldList);
		String expectedSql1= "alter table es_tb drop column f2";
		String expectedSql2 ="alter table es_tb add column f4 int(8) default 0";
		
		assertArrayEquals(new String[]{expectedSql1,expectedSql2}, sqls);

		
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
		
		MysqlSqlParser mysqlSqlParser = new MysqlSqlParser();
		String[] sqls  = mysqlSqlParser.parseIndexSql("es_tb", indexList);
		
		String expectedSql1 ="CREATE INDEX index1 ON es_tb(f1,f2)";
		String expectedSql2 ="CREATE INDEX index2 ON es_tb(f1,f2)";
		assertArrayEquals(new String[]{expectedSql1,expectedSql2}, sqls);
		
	}
	
	
	
	
	
}
