package com.enation.framework.database.data;

import java.util.List;

import com.enation.framework.database.data.entity.Field;
import com.enation.framework.database.data.entity.Index;

/**
 * sql解析接口<br>
 * 在此类中不定义Insert语句的解析<br>
 * 实现体要实现 {@link IBindVariableInsert} 或 {@link INonVariableInsert} 接口来确定具体的生成Insert语句形为，原因是：<br>
 * Sqlserver的insert语句要指定SET IDENTITY_INSERT table ON和OFF，并要和Insert语句中一起批量执行。此批量方式无法绑定变量<br>
 * 而Oracle的Insert语句中Clob不能通过拼接字串的方式，因为Clob字段的值不太大于4000字节，否则要通过绑定变量的方式 
 * @author kingapex
 * @version v1.0
 * @since v6.0 2016年10月14日上午11:37:41
 */
public interface ISqlParser {
	
	/**
	 * 解析创建表的sql
	 * @param tableName 表名
	 * @param fields 字段Map，key为fieldName，value为field value 
	 * @see Field
	 */
	String[] parseCreateSql(String tableName,List<Field> fieldList);
	
 
	/**
	 * 解析更改表的sql
	 * @param tableName 表名
	 * @param fieldList 字段列表
	 * @see Field
	 */
	String[] parseAlterSql(String tableName,  List<Field> fieldList);
 
	
	/**
	 * 解析删除表的sql
	 * @param tableName 表名
	 * @param fields 字段Map，key为fieldName，value为field value 
	 */
	String[] parseDropSql(String tableName);
 
	
	
	/**
	 * 解析索引表的sql
	 * @param tableName 表名
	 * @param indexList 索引列表
	 * @see Index
	 */
	String[] parseIndexSql(String tableName, List<Index> indexList);
	
	
	
}
