package com.enation.app.base.test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.data.xml.SqlParserFactory;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 提供安装测试的基础类
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月20日下午9:17:53
 */
public class BaseInstall {

	/**
	 * 模拟安装界面顺序来测试：初始化测试-测试连接 -执行安装- 跳转到安装成功页<br>
	 * @throws Exception
	 */

	public static void install(MockMvc mockMvc,String dbtype,String uname,String dbhost,String pwd,String dbname ) throws Exception {
 
		if("sqlserver".equals(dbtype)){
			EopSetting.DBTYPE="3";
			
		}
		if("mysql".equals(dbtype)){
			EopSetting.DBTYPE="1";
			
		}
		if("oracle".equals(dbtype)){
			EopSetting.DBTYPE="2";
		}
		
		SqlParserFactory.resetParser();
		EopSetting.INSTALL_LOCK="NO";
		
		
		
		
		//参数初始化测试
		mockMvc.perform(
				
				MockMvcRequestBuilders.post("/install/step3.do")
				.param("dbtype", dbtype)
				.param("uname",uname)
				.param("pwd", pwd)
				.param("dbhost",dbhost)
				.param("dbname",dbname)

		).andDo(MockMvcResultHandlers.log())
		.andExpect(MockMvcResultMatchers.view().name("/install/step3"));
		
		//连接测试
		mockMvc.perform(
				
				MockMvcRequestBuilders.post("/install/test-connection.do")
				.param("dbtype", dbtype)
				.param("uname",uname)
				.param("pwd", pwd)
				.param("dbhost",dbhost)
				.param("dbname",dbname)
				.param("auto_create", "yes")
				.contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );
		
		//安装测试
		mockMvc.perform(
				
				MockMvcRequestBuilders.post("/install/do-install.do")
				.param("productid", "simple")
				.param("uname", "admin")
				.param("pwd", "admin")
				
				.contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)

		).andDo(MockMvcResultHandlers.print())
		.andExpect(MockMvcResultMatchers.jsonPath("$.result").value(1) );

		//因数据库的重新加载，test_model参数被覆盖，在这里重新设置为测试模式
		SystemSetting.setTest_mode(1);

		//跳转至成功页
		mockMvc.perform(
				
				MockMvcRequestBuilders.post("/install/success.do")
				.param("uname", "admin")
				.param("pwd", "admin")

		);
		
	}
}
