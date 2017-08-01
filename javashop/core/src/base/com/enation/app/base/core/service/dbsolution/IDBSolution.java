package com.enation.app.base.core.service.dbsolution;

/**
 * @author liuzy
 * 数据库导入导出解决方案接口
 * @author kanon 2015-12-17 version 1.1 添加注释
 */
public interface IDBSolution {
	/**
	 * 导入数据
	 * @param xml 导入数据xml
	 * @return 导入状态
	 */
	public boolean dbImport(String xml);
	
	/**
	 * 导出数据
	 * @param tables 导出表
	 * @param xml 导出文件名称
	 * @return 导出名称
	 */
	public boolean dbExport(String[] tables,String xml);
	
	/**
	 * 导出数据
	 * @param tables 表名
	 * @param dataOnly 是否只导出表数据
	 * @return xml格式文本
	 */
	public String dbExport(String[] tables,boolean dataOnly);
	
	/**
	 * 删除表
	 * @param table 表名
	 * @return
	 */
	public int dropTable(String table);
	
	/**
	 * 转成大写
	 * @param prefix 命令
	 */
	public void setPrefix(String prefix);
	
	/**
	 * 当前类匹配的类型名称
	 * @param type 类型
	 * @param size 大小
	 * @return 类型名称
	 */
	public String toLocalType(String type, String size) ;
}
