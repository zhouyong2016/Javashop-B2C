package com.enation.app.base.core.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.DbInstallData;
import com.enation.app.base.core.service.EopInstallManager;
import com.enation.app.base.core.service.IDataSourceCreator;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

@Controller 
@RequestMapping("/install")
public class EopInstallController {
	
	@Autowired
	private IDataSourceCreator dataSourceCreator;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
 
	
	@Autowired
	private EopInstallManager eopInstallManager;

	/**
	 * 安装第一步:显示协议
	 * @return 安装第一步页面
	 */
	@RequestMapping(value="/step1")
	public String execute(){
		return "/install/step1";
	}

	
	/**
	 * 安装第二步:显示数据库设置页
	 * @return 安装第二步页面
	 */
	@RequestMapping(value="/step2")
	public String step2(){
		return "/install/step2";
	}
	
	/**
	 * 安装第三部:保存存数据库设置
	 * 切换至新的数据源
	 * @param dbtype 数据库类型
	 * @return 安装第三步页面
	 */
	@RequestMapping(value="/step3")
	public ModelAndView step3(DbInstallData dbData){
		
		/**
		 * 数据库类型
		 */
		String dbtype= dbData.getDbtype();
		
		/**
		 * 保存eop.properties 参数
		 */
		saveEopParams(dbtype);
		
		
		/**
		 * 判断数据库类型
		 */
		if("mysql".equals(dbtype)){
			this.saveMysqlDBParams(dbData);
		}else if("oracle".equals(dbtype)){
			this.saveOracleDBParams(dbData);
		}else if("sqlserver".equals(dbtype)){
			this.saveSQLServerDBParams(dbData);
		}
		
		Properties props=System.getProperties();  
		//获取操作系统的名称
		String osVersion = props.getProperty("os.name")+"("+props.getProperty("os.version")+")";
		//获取Java的运行环境版本
		String javaVersion = props.getProperty("java.version");
		
		
		ModelAndView  mv = new ModelAndView();
		mv.addObject("osVersion", osVersion);
		mv.addObject("javaVersion", javaVersion);
		mv.setViewName("/install/step3");
		
		return mv;
	}
	
	

	
	/**
	 * 测试连接
	 * @param dbtype 数据库类型
	 * @return 测试状态
	 */
	@ResponseBody
	@RequestMapping(value="/test-connection",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult testConnection( DbInstallData dbData){
		String dbtype= dbData.getDbtype();
		boolean result = false;
		//判断数据库类型，测试连接
		if("mysql".equals(dbtype)){
			result = this.mysqlTestConnection(dbData);
		}else if("oracle".equals(dbtype)){
			result = this.oracleTestConnection(dbData);
		}else if("sqlserver".equals(dbtype)){
			result = this.sqlserverTestConnection(dbData);
		}
		//判断测试状态，返回JSON	
		if(result){
			return  JsonResultUtil.getSuccessJson("成功");
		}else{
			return  JsonResultUtil.getErrorJson("失败");
		}
		
	}
	/**
	 * 测试准备
	 * @param dbtype 数据库类型
	 * @return 测试状态
	 */
	@ResponseBody
	@RequestMapping(value="/test-ready")
	public JsonResult testReady(String dbtype){
		try{
			if("mysql".equals(dbtype))
				this.jdbcTemplate.execute("drop table if exists test");
			return  JsonResultUtil.getSuccessJson("成功");
			
		}catch(RuntimeException e){
			return  JsonResultUtil.getErrorJson("失败");
		}		
		
	}
	
	
	/**
	 * 执行安装
	 * @param uname 用户名
	 * @param pwd 密码
	 * @param productid 安装类型
	 * @return 安装状态
	 */
	@ResponseBody
	@RequestMapping(value="/do-install")
	public JsonResult doInstall( String productid,String uname,String pwd){
		// 保存安装产品版本信息
		saveEopProduct(productid);
		try{
			//执行安装
			eopInstallManager.install(uname,pwd,productid);
			
			return JsonResultUtil.getSuccessJson("安装成功");
			
		}catch (RuntimeException e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("安装失败");
		}	
		 
	}
	
	/**
	 * 修改项目类型
	 */
	public static void updateProduct(String type){
		EopInstallController controller = new EopInstallController();
		controller.saveEopProduct(type);
	}
	
	/**
	 * 安装成功
	 * @return 安装成功页面
	 */
	@RequestMapping(value="/success")
	public ModelAndView success(String uname,String pwd){
		
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/install/success");
		mv.addObject("uname",uname);
		mv.addObject("pwd",pwd);
		
		//获取根目录
		String app_apth = StringUtil.getRootPath();
		
		//写入 安装锁
		FileUtil.write(app_apth+"/install/install.lock", "如果要重新安装，请删除此文件，并重新起动web容器");
		
		//设置为已安装
		EopSetting.INSTALL_LOCK ="yes";
		return mv;
	}

	private void saveEopParams(String dbtype){
		
 
		
		//获取根目录创建eop 配置文件
		String webroot = StringUtil.getRootPath();
		String path = StringUtil.getRootPath("/config/eop.properties");
		
		Properties props = new Properties();
		try {
			//创建写入
			InputStream in  = new FileInputStream( new File(path));
			props.load(in);
			
			//判断数据库类型
			if("mysql".equals(dbtype)){
				props.setProperty("dbtype", "1");
			}else if("oracle".equals(dbtype)){
				props.setProperty("dbtype", "2");
			}else if("sqlserver".equals(dbtype)){
				props.setProperty("dbtype", "3");
			}
			//创建eop 配置文件
			File file  = new File(path);		
			props.store(new FileOutputStream(file), "eop.properties");
			EopSetting.init();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
 
	/**
	 * 测试数据库连接
	 * 和test库建立连接，检测用户试图创建的数据库是否存在，如果不存在则建立相应数据库。
	 * 创建后返回相应的数据源给jdbctemplate
	 * 然后进行一个数据库操作测试以证明数据库建立并连接成功
	 * @param dbtype 数据库类型
	 * @param dbhost 连接
	 * @param dbname 库名
	 * @param uname 用户名
	 * @param pwd 密码
	 * @return 测试状态
	 */
	private boolean createAndTest(DbInstallData dbData){
		try{
			String dbname=dbData.getDbname();
			String dbhost=dbData.getDbhost();
			String driver=dbData.getDriver();
			String url=dbData.getUrl();
			String uname=dbData.getUname();
			String pwd=dbData.getPwd();
			String dbtype= dbData.getDbtype();
			
			DataSource newDataSource =   this.dataSourceCreator.createDataSource(driver,url,uname,pwd);
			this.jdbcTemplate.setDataSource(newDataSource);
			String autoCreate= ThreadContextHolder.getHttpRequest().getParameter("auto_create");
			
			if("mysql".equals(dbtype)  ) {	//	只有MySQL尝试建库
				
				if("yes".equals(autoCreate) ){ //自动创建数据库
					this.jdbcTemplate.setDataSource(newDataSource);
					this.jdbcTemplate.execute("drop database if exists "+ dbname);
					this.jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS `" + dbname +"` DEFAULT CHARACTER SET UTF8");
					newDataSource = this.dataSourceCreator.createDataSource("com.mysql.jdbc.Driver", "jdbc:mysql://"+dbhost+"/"+dbname+"?useUnicode=true&characterEncoding=utf8", uname, pwd);
					this.jdbcTemplate.execute("use "+ dbname);
				}else{
					newDataSource = this.dataSourceCreator.createDataSource("com.mysql.jdbc.Driver", "jdbc:mysql://"+dbhost+"/"+dbname+"?useUnicode=true&characterEncoding=utf8", uname, pwd);
				}
			}  
			//连接库，测试是否连接成功
			DataSource dataSource= newDataSource;
			this.jdbcTemplate.setDataSource(newDataSource);
			this.jdbcTemplate.execute("CREATE TABLE JAVAMALLTESTTABLE (ID INT not null)");
			this.jdbcTemplate.execute("DROP TABLE JAVAMALLTESTTABLE");

			return true;
			
		}catch(RuntimeException e){
			e.printStackTrace();
		 
			return false;
		}
	}

	/**
	 * 保存到jdbc.properties文件
	 * @param props 数据配置
	 */
	private void saveProperties(Properties props){
		try {
 			String path = StringUtil.getRootPath("/config/jdbc.properties");
			File file  = new File(path);
    		props.store(new FileOutputStream(file), "jdbc.properties");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存Oracle数据设置
	 * @param dbname 数据库名称
	 * @param uname 用户名
	 * @param pwd 密码
	 */
	private void saveOracleDBParams(DbInstallData dbData){
		Properties props = new Properties();
		props.setProperty("jdbc.driverClassName", "oracle.jdbc.driver.OracleDriver");
		props.setProperty("jdbc.url", "jdbc:oracle:thin:@" + dbData.getDbhost()+ ":" + dbData.getDbname());
		props.setProperty("jdbc.username", dbData.getUname());
		props.setProperty("jdbc.password", dbData.getPwd());
		saveProperties(props);
	}
	
	
	
	/**
	 * 保存SQLServer数据设置
	 * @param dbname 数据库名称
	 * @param uname 用户名
	 * @param pwd 密码
	 */
	private void saveSQLServerDBParams(DbInstallData dbData){
		Properties props = new Properties();
		props.setProperty("jdbc.driverClassName", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		props.setProperty("jdbc.url", "jdbc:sqlserver://" + dbData.getDbhost()+ ";databaseName=" + dbData.getDbname());
		props.setProperty("jdbc.username", dbData.getUname());
		props.setProperty("jdbc.password", dbData.getPwd());
		saveProperties(props);
	}
	
	/**
	 * 保存mysql数据设置
	 * @param dbname 数据库名称
	 * @param uname 用户名
	 * @param pwd 密码
	 */
	private void saveMysqlDBParams(DbInstallData dbData){
		
		Properties props = new Properties();
		props.setProperty("jdbc.driverClassName", "com.mysql.jdbc.Driver");
		props.setProperty("jdbc.url", "jdbc:mysql://"+dbData.getDbhost()+"/"+dbData.getDbname()+"?useUnicode=true&characterEncoding=utf8&autoReconnect=true");
		props.setProperty("jdbc.username",dbData.getUname());
		props.setProperty("jdbc.password", dbData.getPwd());
		saveProperties(props);	
	}



	/**
	 * 新增
	 * 保存安装产品版本信息
	 * @author xulipeng
	 * 2015年07月18日16:03:09
	 */
	private void saveEopProduct(String productid){
 
		
		//获取根目录创建eop 配置文件
		String path = StringUtil.getRootPath("/config/eop.properties");
		
		Properties props = new Properties();
		try {
			//创建写入
			InputStream in  = new FileInputStream( new File(path));
			props.load(in);
			
			if(productid.equals("simple")){
				props.setProperty("product", "b2c");
			}else if(productid.equals("b2b2c")){
				props.setProperty("product", "b2b2c");
			}
			File file  = new File(path);		
			props.store(new FileOutputStream(file), "eop.properties");
			EopSetting.init();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 测试MySQL连接
	 * @param dbhost 连接
	 * @return 测试状态
	 */
	private boolean mysqlTestConnection(DbInstallData dbData){
		
		String driver=  "com.mysql.jdbc.Driver";
		
		String dbhost= dbData.getDbhost();
		String url= "jdbc:mysql://"+dbhost+"/?useUnicode=true&characterEncoding=utf8";
		
		dbData.setUrl(url);
		dbData.setDriver(driver);
		
		return createAndTest(dbData);
	}
	
	/**
	 * 测试Oracle连接
	 * @param dbhost 连接
	 * @param dbname 库名
	 * @return @return 测试状态
	 */
	private boolean oracleTestConnection(DbInstallData dbData){
		
		String driver=  "oracle.jdbc.driver.OracleDriver";
		
		String dbname=dbData.getDbname();
		String dbhost= dbData.getDbhost();
		
		String url= "jdbc:oracle:thin:@" + dbhost + ":" + dbname;
		
		dbData.setUrl(url);
		dbData.setDriver(driver);
		
		return createAndTest(dbData);
		
		
	}
	/**
	 * 测试SqlServer连接
	 * @param dbhost 连接
	 * @param dbname 库名
	 * @return @return 测试状态
	 */
	private boolean sqlserverTestConnection(DbInstallData dbData){
		String driver=  "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		
		String dbname=dbData.getDbname();
		String dbhost= dbData.getDbhost();
		
		String url= "jdbc:sqlserver://" + dbhost + ";databaseName=" + dbname;
		
		dbData.setUrl(url);
		dbData.setDriver(driver);
		
		return createAndTest(dbData);	
	} 
	
}
