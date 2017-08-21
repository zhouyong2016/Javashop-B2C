package com.enation.app.base.core.service;

/**
 * 任务执行器 
 * @author kingapex
 *
 */
public interface IJobExecuter {
	
	/**
	 * 每小时执行
	 */
	public void everyHour();
	
	/**
	 * 每天执行
	 */
	public void everyDay();
	
	/**
	 * 每月执行
	 */
	public void everyMonth();
	
	
}
