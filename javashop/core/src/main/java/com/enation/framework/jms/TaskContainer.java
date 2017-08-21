package com.enation.framework.jms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * 任务容器
 * @author Kanon
 *
 */
public class TaskContainer {
	
	private static Map<String,ITaskView> taskMap;
	
	static{
		taskMap = new HashMap<String, ITaskView>();
	}
	
	public static void pushTask(ITaskView taskView){
		taskMap.put(taskView.getTaskId(), taskView);
	}
	
	
	public static ITaskView getTask(String taskid){
		return taskMap.get(taskid);		
	}
	
	
	public static  void removeTask(String taskid){
		taskMap.remove(taskid);
	}
	
	
	public static  Collection<ITaskView> listTask(){
		
		return taskMap.values();
	}
	
	public static void  clear(){
		
		taskMap.clear();
	}
	
 
	
}
