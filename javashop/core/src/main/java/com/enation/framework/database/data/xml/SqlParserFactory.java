package com.enation.framework.database.data.xml;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.data.ISqlParser;

/**
 * sql解析器单例工厂
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月17日下午9:59:38
 */
public class SqlParserFactory {
	
	private static ISqlParser sqlParser;
	/**
	 * 根据数据库类型获取合适的sql解析器
	 * @return
	 */
	public static ISqlParser getSqlParser(){
		
		if(sqlParser!=null){
			return sqlParser;
		}
		resetParser();
		
		if(sqlParser ==null){
			throw new RuntimeException("未知的数据库类型");
		}else{
			return sqlParser;
		}
		
		
	}
	
	
	public static void resetParser( ){
		if (EopSetting.DBTYPE.equals("1")) {
			
			sqlParser =  new MysqlSqlParser();
		}
		
		
		if (EopSetting.DBTYPE.equals("2")) {
			sqlParser = new OracleSqlParser();
		}
		
		
		if (EopSetting.DBTYPE.equals("3")) {
			sqlParser = new SqlServerSqlParser();
		}
	}
	
	
}
