package com.enation.framework.util;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * json数据结果生成成工具 
 * 结果总是返回result 为1表示成功 为0表示失败
 * 
 * @author kingapex
 *
 */
public class JsonMessageUtil {
	
	public static String getObjectJson(Object object){
		
		if(object==null){
			 return getErrorJson("object is null");
		}
		
		try{
		
			String objStr = JSONObject.fromObject(object).toString();
			
			return   "{\"result\":1,\"data\":"+objStr+"}";
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
		 
		
	}
	
	public static String getObjectJson(Object object,String objectname){
		
		if(object==null){
			 return getErrorJson("object is null");
		}
		
		try{
		
			String objStr = JSONObject.fromObject(object).toString();
			
			return   "{\"result\":1,\""+objectname+"\":"+objStr+"}";
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
		
	}
	
	public static String getStringJson(String name,String value){
		 return   "{\"result\":1,\""+name+"\":\""+value+"\"}";
	}
	
	public static String getNumberJson(String name,Object value){
		 return   "{\"result\":1,\""+name+"\":"+value+"}";
	}
	
	public static String getListJson(List list){
		 if(list==null){
			 return getErrorJson("list is null");
		 }
		 String listStr = JSONArray.fromObject(list).toString();		 
		 return   "{\"result\":1,\"data\":"+listStr+"}";
	}
	
	public static String getErrorJson(String message){
		
		return "{\"result\":0,\"message\":\""+message+"\"}";
		
	}
	
	public static String getSuccessJson(String message){
		
		return "{\"result\":1,\"message\":\""+message+"\"}";
		
	}	
}
