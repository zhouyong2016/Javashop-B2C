package com.enation.app.base.core.plugin.data;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;


/**
 * 数据导出事件
 * 
 * <p>
 * 此事件在由站点导出解决方案时激发<br>
 * 此事件主要目的是：使插件可返回必要的数据，导出程序会将这些数据保存在解决方案数据xml中。
 * </p>
 * @author kingapex
 * 2012-10-10下午9:27:51
 */	
public interface IDataExportEvent {
	
	
	/**
	 * 数据导出事件
	 * <p>
	 * 事件具体实现者应返回要导出的数据。
	 * 截止javashop3.1 可通过  {@link DBSolutionFactory#dbExport(String[], boolean, String)}  来得到要出表的数据
	 * 
	 * </p>
	 * @return
	 */
	public String onDataExport();
	
}
