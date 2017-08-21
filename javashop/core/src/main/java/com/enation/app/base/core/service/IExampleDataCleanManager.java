/**
 * 
 */
package com.enation.app.base.core.service;

/**
 * 示例数据清除接口
 * @author kingapex
 *2015-6-3
 */
public interface IExampleDataCleanManager {
	
	
	/**
	 * 清除示例数据
	 * @param moudels 要清除数据的模块
	 */
	public void clean(String[] moudels);
	
	
}
