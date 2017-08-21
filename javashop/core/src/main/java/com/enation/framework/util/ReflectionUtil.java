package com.enation.framework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.enation.framework.database.DynamicField;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

public class ReflectionUtil {
		
	public static Object invokeMethod(String className, String methodName,
			Object[] args) {

		try {

			Class serviceClass = Class.forName(className);
			Object service = serviceClass.newInstance();

			Class[] argsClass = new Class[args.length];
			for (int i = 0, j = args.length; i < j; i++) {
				argsClass[i] = args[i].getClass();
			}

			Method method = serviceClass.getMethod(methodName, argsClass);
			return method.invoke(service, args);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static Object newInstance(String className,Object... _args ){

		try {
			  Class[] argsClass = new Class[_args.length];                                  
			                                                                                 
			   for (int i = 0, j = _args.length; i < j; i++) {   
					   
				   if(_args[i]==null){
					   argsClass[i]=null;
				   }
				   else{
					    
					   argsClass[i] = _args[i].getClass();
				   }
			    }      
			   
			   
			 Class newoneClass  = Class.forName(className);
			 Constructor cons = newoneClass.getConstructor(argsClass);                    
             
			 Object obj= cons.newInstance(_args);
			 return obj;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} 
	
		 
		 return null;
	 
	}

	/**
	 * 将po对象中有属性和值转换成map
	 * 
	 * @param po
	 * @return
	 */
	public static Map po2Map(Object po) {
		Map poMap = new HashMap();
		Map map = new HashMap();
		try {
			map = BeanUtils.describe(po);
		} catch (Exception ex) {
		}
		Object[] keyArray = map.keySet().toArray();
		for (int i = 0; i < keyArray.length; i++) {
			String str = keyArray[i].toString();
			if (str != null && !str.equals("class")) {
				if (map.get(str) != null) {
					poMap.put(str, map.get(str));
				}
			}
		}

		Method[] ms =po.getClass().getMethods();
		for(Method m:ms){
			String name = m.getName();
			
			if(name.startsWith("get")||name.startsWith("is")){
				if(m.getAnnotation(NotDbField.class)!=null||m.getAnnotation(PrimaryKeyField.class)!=null){
					poMap.remove(getFieldName(name)); 
				} 
			}

		}
		
		/**
		 * 如果此实体为动态字段实体，将动态字段加入
		 */
		if(po instanceof DynamicField){
			DynamicField dynamicField = (DynamicField) po;
			Map fields = dynamicField.getFields();
			poMap.putAll(fields);
		}
		return poMap;
	}

	private static String getFieldName(String methodName){
		 
		methodName = methodName.substring(3);
		methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
		return methodName;
	}
	
	public static void main(String[] args){
		String methodName = "getWidgetList";
		methodName = methodName.substring(3);
		methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
		//System.out.println(methodName);
	}
}
