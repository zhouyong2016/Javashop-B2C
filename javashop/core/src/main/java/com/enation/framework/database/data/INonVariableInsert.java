package com.enation.framework.database.data;

import java.util.List;

import com.enation.framework.database.data.entity.Field;


/**
 * 不带变量的插入语句解析接口
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月20日下午9:21:51
 */
public interface INonVariableInsert {
	
	
	/**
	 * 生成不绑定变量的Insert语句 
	 * @param tableName 表名
	 * @param fieldList 字段列表
	 * @return 插入的sql语句  
	 * @see Field
	 */
	public String parseInsertSqlNonVariable(String tableName, List<Field> fieldList);
	
	
}
