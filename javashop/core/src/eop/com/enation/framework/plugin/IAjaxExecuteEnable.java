package com.enation.framework.plugin;

/**
 * 可被异步调用接口
 * 实现此接口的插件，可通过如下url来调用：
 * /admin/plugin?beanid=插件的beanid
 * @author kingapex
 *
 */
public interface IAjaxExecuteEnable {
	
	/**
	 * 异步调用时调用此方法 
	 * @return
	 */
	public String execute();
	
}
