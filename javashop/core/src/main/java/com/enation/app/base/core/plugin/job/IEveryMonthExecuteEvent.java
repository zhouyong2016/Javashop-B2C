package com.enation.app.base.core.plugin.job;

/**
 * 每月执行任务事件
 * @author kingapex
 *
 */
public interface IEveryMonthExecuteEvent {
	
	/**
	 * 每月调用一些此方法 
	 */
	public void everyMonth();
}
