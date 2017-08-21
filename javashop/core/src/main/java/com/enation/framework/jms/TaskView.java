package com.enation.framework.jms;

/**
 * 任务视图，主要是为了生成Json用。
 * 否则其它实现体的内容过于复杂，通过此类过滤一下没有必要的属性
 * @author kingapex
 * 2012-11-6下午4:41:43
 */
public class TaskView implements ITaskView{
	private ITaskView task;
	
	public TaskView(ITaskView task){
		this.task=task;
	}
	@Override
	public String getTaskId() {
		return task.getTaskId();
	}

	@Override
	public String getTaskName() {
		return this.task.getTaskName();
	}

	@Override
	public int getState() {
		return this.task.getState();
	}

	@Override
	public void setState(int state) {
		 
		// do nothing
	}

	@Override
	public String getErrorMessage() {
		 
		return task.getErrorMessage();
	}

	@Override
	public void setErrorMessage(String errorMessage) {
		// do nothing
		
	}
	
}
