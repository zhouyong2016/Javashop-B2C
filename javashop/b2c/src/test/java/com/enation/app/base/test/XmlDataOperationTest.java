package com.enation.app.base.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.data.IDataOperation;
import com.enation.framework.test.SpringTestSupport;

/**
 * XmlDataOperation 单元测试类
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月20日下午9:19:12
 */
public class XmlDataOperationTest extends SpringTestSupport {
	
	
	@Autowired
	private IDataOperation xmlDataOperation;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
 
	public void testSqlserver(){
		
		String  expectedSql = "SET IDENTITY_INSERT es_tb ON;\n";
		expectedSql += "INSERT INTO es_tb(f1,f2) VALUES (?,?);\n";
		expectedSql += "SET IDENTITY_INSERT es_tb OFF;\n";
		
		this.jdbcTemplate.update(expectedSql, 2,"b");
	}
	
	//@Test
	//@Rollback(false) 
	public void mytest(){
//		String sql1="BEGIN\n";
//		sql1+="EXECUTE IMMEDIATE 'DROP TRIGGER TIB_ES_MY_TEST';\n";
//		sql1+="EXCEPTION WHEN OTHERS THEN NULL;\n";
//		sql1+="END;\n";
//		
//		String sql2="BEGIN\n";
//		sql2+="EXECUTE IMMEDIATE 'DROP TABLE ES_MY_TEST';\n";
//		sql2+="EXCEPTION WHEN OTHERS THEN NULL;\n";
//		sql2+="END;\n";
//		
//		String sql3 = "CREATE TABLE es_my_test (\"ID\" NUMBER(8) NOT NULL,\"FIELD1\" NUMBER(8),\"FIELD2\" VARCHAR(255) NOT NULL,\"FIELD3\" NUMBER(20,2),CONSTRAINT PK_es_my_test PRIMARY KEY (\"ID\"))";
//		
//		String[] sql = new String[]{sql1,sql2,sql3};
//		jdbcTemplate.batchUpdate( sql);
		EopSetting.DBTYPE="2";
		testInsert();
	}
	
	/**
	 * 测试创建表、修改表、建立索引、插入数据
	 */
	@Test
	public void test(){
		
		
		SystemSetting.setTest_mode(1);
		//EopSetting.DBTYPE="2";
		
		this.testCreate();
		this.testAlter();
		this.testIndex();
		this.testInsert();
		
	}
	
	//@Test
	public void testCreate(){
		String createFilePath = "file:com/enation/app/base/test/create.xml";
		xmlDataOperation.imported(createFilePath);
	}
	
	
	//@Test
	public void testInsert(){
		String insertFilePath = "file:com/enation/app/base/test/insert.xml";

		xmlDataOperation.imported(insertFilePath);
	}
	
	
	//@Test
	public void testAlter(){
		String insertFilePath = "file:com/enation/app/base/test/alter.xml";
		xmlDataOperation.imported(insertFilePath);
	}
	
	

	//@Test
	public void testIndex(){
		String insertFilePath = "file:com/enation/app/base/test/index.xml";
		xmlDataOperation.imported(insertFilePath);
	}
	
	
	
}
