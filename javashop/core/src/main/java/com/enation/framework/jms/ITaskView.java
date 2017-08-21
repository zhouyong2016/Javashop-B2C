package com.enation.framework.jms;


/**
 * 任务视图
 * @author kingapex
 *
 */
public interface ITaskView {
	
	/**
	 * 任务id
	 * @return
	 */
	public String getTaskId();
	
	/**
	 * 任务名称
	 * @return
	 */
	public String getTaskName();
	
	/**
	 * 任务状态
	 * @return
	 * 0:已下达任务，正在执行。
	 * 1:执行成功
	 * 2:执行失败 
	 */
 	public int getState();
 	
 	/**
 	 * 设置任务状态
 	 * @param state
 	 */
 	public void setState(int state);
 	
 	/**
 	 * 获取错误信息
 	 * @return
 	 */
 	public String  getErrorMessage();
 	
 	/**
 	 * 设置错误信息
 	 * @param errorMessage
 	 */
 	public void setErrorMessage(String errorMessage);
}
