package com.enation.framework.database.data;



/**
 * 数据操作接口<br>
 * 负责数据的导入导出操作
 * @author kingapex
 * @version v1.0
 * @since v6.0
 * 2016年10月14日上午11:31:08
 */
public interface IDataOperation {
	
	/**
	 * 数据导入
	 * @param filePath 导入文件路径
	 */
	void imported(String filePath);
	
	
	/**
	 * 数据导出
	 * @param filePath 导出文件路径
	 */
	void exported(String filePath);
}
