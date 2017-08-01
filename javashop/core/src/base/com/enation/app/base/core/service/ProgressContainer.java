/**
 * 
 */
package com.enation.app.base.core.service;

import java.util.HashMap;
import java.util.Map;

import com.enation.app.base.core.model.TaskProgress;

/**
 * 进度容器
 * @author kingapex
 *2015-5-13
 */
public abstract class  ProgressContainer {
	
	private  ProgressContainer(){}
	
	private static Map<String,TaskProgress>  map =new HashMap<String, TaskProgress>();
	
	public static void putProgress(String id,TaskProgress progress){
		progress.setId(id);
		map.put(id, progress);
	}
	
	
	public static  TaskProgress getProgress(String id){
		return map.get(id);
	}
	
	
	public static void remove(String id){
		map.remove(id);
	}
	
}


