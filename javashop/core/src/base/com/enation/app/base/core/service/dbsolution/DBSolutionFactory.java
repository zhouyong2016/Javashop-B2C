package com.enation.app.base.core.service.dbsolution;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.data.IDataOperation;

/**
 * 数据解决方案工厂类
 * @author liuzy
 * @author kanon 2015-12-17 version 1.1 添加注释
 */
public class DBSolutionFactory {
	
	/***
	 * 获取数据库解决方案
	 * @return
	 */
	public static IDBSolution getDBSolution() {
		IDBSolution result = null;
		//判断数据库类型
		if (EopSetting.DBTYPE.equals("1")) {
			result = SpringContextHolder.getBean("mysqlSolution");
		} else if (EopSetting.DBTYPE.equals("2")) {
			result = SpringContextHolder.getBean("oracleSolution");
		} else if (EopSetting.DBTYPE.equals("3")) {
			result = SpringContextHolder.getBean("sqlserverSolution");
		} else{
			throw new RuntimeException("未知的数据库类型");
		}
		return result;
	}
	
	
	/***
	 * 创建数据库连接
	 * @param jdbcTemplate
	 * @return
	 */
	public static Connection getConnection(JdbcTemplate jdbcTemplate){
		
		if(jdbcTemplate==null){
			jdbcTemplate =  SpringContextHolder.getBean("jdbcTemplate");
		}
		
		try {
			return jdbcTemplate.getDataSource().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

 
	/***
	 * 导入数据
	 * @param xml xml文件
	 * @param prefix 命令
	 * @return 导入结果
	 * 已废弃  add by 江宏岩 2016-11-24
	 */
	@Deprecated
	public static boolean dbImport1(String xml, String prefix) {
//		DBSolutionWf dbinstall= SpringContextHolder.getBean("dbinstall");
//		dbinstall.doImport(xml);
		
		IDataOperation xmlDataOperation= SpringContextHolder.getBean("xmlDataOperation");
		 
		xmlDataOperation.imported(xml);
//		//获取数据库
//		IDBSolution dbsolution = getDBSolution();
//		//转换成大写
//		dbsolution.setPrefix(prefix);
//	 
//		boolean result;
//		result = dbsolution.dbImport(xml);
		return true;
	}
	
	/***
	 * 导出数据
	 * @param tables 表名
	 * @param dataOnly 是否只导出数据
	 * @param prefix 命令
	 * @return 导出结果
	 */
	public static String dbExport(String[] tables,boolean dataOnly,String prefix) {
		Connection conn = getConnection(null);
		IDBSolution dbsolution = getDBSolution();
		dbsolution.setPrefix(prefix);
 
		String result = "";
		result = dbsolution.dbExport(tables,dataOnly);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
}
