package com.enation.app.base.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.User;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.StringUtil;


/**
 * dao单元测试
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月20日下午9:18:12
 */
public class DaoSupportTestBackup extends SpringTestSupport {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
 
	
	/**
	 * 建立表结构
	 * @throws Exception 
	 * 
	 */
	@Before
	public void beforeTest() throws Exception {
		EopSetting.init(StringUtil.getRootPath() + "/config/eop.properties");
		if (EopSetting.DBTYPE.equals(3)) {
			String dropSql = "if exists (select 1 from sysobjects where id = object_id('enation_user')and type = 'U') drop table enation_user";
			String createSql = "create table enation_user ([user_id] int not null identity,[username] varchar(50),[password] varchar(50),constraint PK_ENATION_USER primary key nonclustered ([user_id]))";

			this.jdbcTemplate.execute(dropSql);
			this.jdbcTemplate.execute(createSql);
		} else {
			this.jdbcTemplate.execute("drop table if exists enation_user;");
			this.jdbcTemplate.execute(
					"create table enation_user(user_id int not null auto_increment,username  varchar(50),password  varchar(50),primary key (user_id));");

		}

	}


	@Test
	public void testDao(){
		
		//测试两种插入
		testIntertPo();
		testIntertMap();
		
		testGet();
		
		//测试各种更新
		this.testUpdate1();
		this.testUpdate2();
		this.testUpdate3();
		this.testUpdate4();
		
		//测试读取列表
		testList();
	}
	
	
	
	
	/**
	 * 测试读取一个实体
	 */
	public void testGet(){
		
		
 		//测试读取一个对象
 		User user = daoSupport.queryForObject("select * from enation_user where user_id=?", User.class, 1);
		assertEquals("王峰",user.getUsername());

		//测试读取Map
		Map userMap = this.daoSupport.queryForMap("select * from enation_user where user_id=?", 1);
		assertEquals("王峰",userMap.get("username"));
		
		//测试queryForInt
		int userid = this.daoSupport.queryForInt("select user_id from enation_user where user_id=?",1);
		assertEquals(1,userid);
		
		//测试queryForLong
		long l_uid = this.daoSupport.queryForLong("select user_id from enation_user where user_id=?",1);
		assertEquals(1,l_uid);
		
		
		float f_uid = this.daoSupport.queryForFloat("select user_id from enation_user where user_id=?",1);
		assertEquals(1f,f_uid,0);
		
		
		double d_uid = this.daoSupport.queryForDouble("select user_id from enation_user where user_id=?",1);
		assertEquals(1d,d_uid,0);
				
		//测试queryForString
		String username = this.daoSupport.queryForString("select username from enation_user where user_id=1", 1);
		assertEquals("王峰",username);
		
		
	}
	
	
	/**
	 * 测试可以将一个Po实体的相应数据插入数据库中的情况
	 * 
	 */
	
	public void testIntertPo() {

		User user = new User(); 
		user.setUsername("王峰");
		user.setPassword("test");
 
		daoSupport.insert("enation_user", user);
		
		//测试getLastId
		int userid = this.daoSupport.getLastId("enation_user");
		assertEquals(1, userid);
		assertInsert();

	}
	

	/**
	 * 测试可将一个map形式的数据插入到数据库
	 * 
	 */
	
	public void testIntertMap() {

		Map data = new HashMap();
		data.put("username", "王峰2");
		data.put("password", "test2");

		this.daoSupport.insert("enation_user", data);
		
		//对刚刚插入的数据进行断言
		Map user_map = this.jdbcTemplate .queryForMap("select * from enation_user where user_id=2");
		assertEquals("王峰2", user_map.get("username"));
		assertEquals("test2", user_map.get("password"));
		
		
	}

	
	/**
	 * 测试更新数据库
	 *数据通过Map传递 ，条件通过字串传递
	 */
	
	public void testUpdate1() {
		 
		HashMap data = new HashMap();
		data.put("username", "王峰1");
		data.put("password", "test1");
		 
		daoSupport.update("enation_user", data, "user_id=1");
		assertUpdate(1,"王峰1","test1");
		
	}
	
	
	/**
	 * 测试更新数据库
	 *数据通过Map传递 ，条件通过Map递
	 */
	
	public void testUpdate2() {
	 
		HashMap data = new HashMap();
		data.put("username", "王峰1");
		data.put("password", "test1");

		HashMap where =new HashMap();
		where.put("user_id", "2");
		 
		daoSupport.update("enation_user", data, where);
		assertUpdate(2,"王峰1","test1");
		
		
	}
	
	

	/**
	 * 测试更新数据库
	 *数据通过po实体传递 ，条件通字串传递
	 */
	
	public void testUpdate3() {
	 
		User user = new User();
		user.setUsername("王峰1");
		user.setPassword("test1");
		
		 
		daoSupport.update("enation_user", user, "user_id=1");
		assertUpdate(1,"王峰1","test1");
		
	}
	
	
	/**
	 * 测试更新数据库
	 *数据通过po实体传递 ，条件通Map传递
	 */
	
	public void testUpdate4() {
		
		User user = new User();
		user.setUsername("王峰2");
		user.setPassword("test2");
		
		HashMap where =new HashMap();
		where.put("user_id", "1");
	 
		daoSupport.update("enation_user", user, where);
		assertUpdate(1,"王峰2","test2");
		
	}
	

	
 
		
	/**
	 * 生成测试数据
	 */
	public void createTestData(){
		this.daoSupport.execute("TRUNCATE TABLE enation_user");
		for(int i=1;i<=10;i++){
			User user = new User(); 
			user.setUsername("王峰"+i);
			user.setPassword("test");
			daoSupport.insert("enation_user", user);
		}
	}
	
	
	/**
	 * 测试列表获取
	 */
	public void testList(){
		
		//-------生成测试数据---------
		this.createTestData();
		
		
		//-------测试对象式查询-------
		String sql  ="select * from enation_user order by user_id asc ";
		List<User> userList = this.daoSupport.queryForList(sql, User.class);
		//断言大小是12个
		assertEquals(  10,userList.size());
		
		
		//------测试map式查询-------
		List<Map<String,Object>> mapList =null;
		mapList = this.daoSupport.queryForList("select * from enation_user where user_id>? order by user_id asc", 5);
		//断言大小是5
		assertEquals( mapList.size()  ,5);
		
		
		//------测试 rowMapper 式查询-------
		userList = this.daoSupport.queryForList("select * from enation_user where user_id>? ",  new BeanPropertyRowMapper<User>(  User.class), 5);
		//断言大小是5
		assertEquals( userList.size()  ,5);
		
		
		//------测试 rowMapper 式 分页list 查询-------
		userList =this.daoSupport.queryForList(sql, 2, 5, new BeanPropertyRowMapper<User>(  User.class));
		//断言大小是5
		assertEquals( userList.size()  ,5);
		//断言第一个是 王峰6
		assertEquals(userList.get(0).getUsername() ,"王峰6");
		
		
		//------测试 Map 式 分页list 查询-------
		this.daoSupport.queryForListPage("select * from enation_user where user_id>? order by user_id asc ", 2, 5, 1);
		//断言大小是5
		assertEquals( userList.size()  ,5);
		//断言第一个是 王峰7
		assertEquals(userList.get(0).getUsername() ,"王峰6");
				
				
				
		for(User user:userList){
			System.out.println("user["+ user.getUsername()+"]");
		}
		
		
		
		//------测试分页查询查询-------
		Page page = daoSupport.queryForPage("select * from enation_user t where user_id>? order by user_id desc", 1, 5,0);
		mapList  =(List) page.getResult(); 
		
		//断言大小为5
		assertEquals( mapList.size()  ,5);
		//断言第一个是 王峰10
		assertEquals(mapList.get(0).get("username"),"王峰10");
		//断言总数是7
		assertEquals(10,page.getTotalCount());
		//断言总数是1
		assertEquals(1,page.getCurrentPageNo());
		
		//------测试分页查询查询:自定义cout sql-------
		String countSql = "select count(0) from enation_user   ";
		page= this.daoSupport.queryForPage(sql, countSql, 2, 5);
		mapList  =(List) page.getResult();
		//断言大小为5
		assertEquals( mapList.size()  ,5);
		//断言第一个是 王峰6
		assertEquals(mapList.get(0).get("username"),"王峰6");
		//断言总数是7
		assertEquals(10,page.getTotalCount());
		//断言总数是2
		assertEquals(2,page.getCurrentPageNo());
		
		//------测试分页查询查询:Class Type式-------
		page  = this.daoSupport.queryForPage("select * from enation_user t where user_id>? order by user_id desc", 1, 5, User.class, 0);
		userList  = (List)page.getResult();
		//断言大小为5
		assertEquals( userList.size()  ,5);
		//断言第一个是 王峰10
		assertEquals(userList.get(0).getUsername(),"王峰10");
		//断言总数是7
		assertEquals(10,page.getTotalCount());
		//断言总数是1
		assertEquals(1,page.getCurrentPageNo());
		
		//------测试分页查询查询:RowMapper式-------
		page =this.daoSupport.queryForPage("select * from enation_user t where user_id>? order by user_id desc", 1, 5, new BeanPropertyRowMapper<User>(  User.class), 0);
		userList  = (List)page.getResult();
		//断言大小为5
		assertEquals( userList.size()  ,5);
		//断言第一个是 王峰10
		assertEquals(userList.get(0).getUsername(),"王峰10");
		//断言总数是7
		assertEquals(10,page.getTotalCount());
		//断言总数是1
		assertEquals(1,page.getCurrentPageNo());
	}
	
	

	/**
	 * 插入测试的断言
	 * 
	 */
	private void assertInsert() {
		Map user_map = this.jdbcTemplate .queryForMap("select * from enation_user");
		assertEquals("王峰", user_map.get("username"));
		assertEquals("test", user_map.get("password"));
	}
	
	

	/**
	 * 更新测试的断言
	 * 
	 */
	private void assertUpdate(int userid,String username,String password) {
		Map user_map = this.jdbcTemplate
				.queryForMap("select * from enation_user where user_id=?",userid);
		assertEquals(username, user_map.get("username"));
		assertEquals(password, user_map.get("password"));
	}
	

}
