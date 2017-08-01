package com.enation.app.base.core.plugin.job;

/**
 * 任务每小时执行事件
 * @author kingapex
 *
 */
public interface IEveryHourExecuteEvent {
	
	/**
	 * 每隔一小时会激发此事件 
	 */
	public void everyHour();
	
	
}
