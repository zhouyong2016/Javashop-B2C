package com.enation.app.base.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

import com.enation.framework.test.SpringTestSupport;

/**
 * 安装测试类<br>
 * 因其它测试需要依赖于安装完成，所以需要第一个执行此测试，<br>
 * 故命名为'A'InstallTest(maven-surefire-plugin插件已经配置为按字母排序测试)<br>
 * 本测试模拟安装界面顺序来测试：初始化测试-测试连接 -执行安装- 跳转到安装成功页<br>
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月6日下午4:39:37
 */
@Rollback(false)
public class AInstallTest extends SpringTestSupport{
	
	private static String dbtype="mysql";
	private static String uname ="root";
	private static String dbhost="localhost:3306";
	private static String pwd="kingapex";
	private static String dbname="b2b2c_test";
	
	
	/**
	 * 根据数据库配置文件来获取要安装的数据库参数
	 * @throws IOException
	 */
	@BeforeClass
	public static  void getParam() throws IOException{
		
		
		String path = AInstallTest.class.getResource("").toString();
		
		int end  = path.indexOf("target");
		
		//like this : file:/Users/kingapex/workspace/trunk62/b2b2c-test/
		path= path.substring(0, end);
		 
		//like this : /Users/kingapex/workspace/trunk62/b2b2c-test/
		path= path.substring(5);
		
		//把rootpath保留起来
		String rootPath=path;
		
		path= path +"src/main/webapp/config/jdbc.properties";
		System.out.println("read from  " + path);
		InputStream in  = new FileInputStream( new File(path));

		Properties props = new  Properties();
		props.load(in);
		String url = props.getProperty("jdbc.url");
		url =url.replaceAll("\\\\", "");
		
		//jdbc:mysql://localhost:3306/b2b2c_test?useUnicode=true&characterEncoding=utf8&autoReconnect=true
		//jdbc:sqlserver://192.168.1.88:1433;databaseName=wangfeng
		//jdbc:oracle:thin:@192.168.1.88:1521:WANGFENG
		
		Pattern pattern =null;
		
		if( url.startsWith("jdbc:mysql" ) ){
			dbtype = "mysql";
			pattern=  Pattern.compile("(.*)://(.*):(\\d+)/(.*)\\?(.*)");
		}
		
		if( url.startsWith("jdbc:oracle" ) ){
			dbtype = "oracle";
			pattern=  Pattern.compile("(.*):thin:@(.*):(\\d+):(.*)");
		}
		
		if( url.startsWith("jdbc:sqlserver" ) ){
			dbtype = "sqlserver";
			pattern= Pattern.compile("(.*)://(.*):(\\d+);databaseName=(.*)");
		}
		
		
		Matcher matcher = pattern.matcher(url);
	
		
		if (matcher.find()) {
			 
				dbhost = ( matcher.group(2) ); //localhost
				String port  = ( matcher.group(3) ); //3306
				dbname =  ( matcher.group(4) ); //b2b2c_test
				dbhost=dbhost+":"+port;
			 
			
		}
		
		uname = props.getProperty("jdbc.username");
		pwd = props.getProperty("jdbc.password");
		writeDbType(dbtype,rootPath);
		
	}
	
	/**
	 * 向eop.properties 写入dbtype
	 * @param dbtype 数据库类型
	 * @param rootPath 项目根录径
	 */
	private static void writeDbType(String dbtype,String rootPath){
		Properties props = new Properties();
		try {
			String path  = rootPath +"src/main/webapp/config/eop.properties";
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
			System.out.println("write to "+path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 声明是否是安装测试
	 */
	@Override
	protected boolean getInstallTest() {
		return true;
	}

	/**
	 * 对安装程序进行测试
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception{
		System.out.println(dbtype);
		System.out.println(uname );
		System.out.println(dbhost);
		System.out.println(pwd);
		System.out.println(dbname);
		
		BaseInstall.install(this.mockMvc,dbtype, uname, dbhost, pwd, dbname);
		
	}
	
 
	
}
