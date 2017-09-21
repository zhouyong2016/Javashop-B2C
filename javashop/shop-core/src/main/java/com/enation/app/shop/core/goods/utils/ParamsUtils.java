/**
 * 
 */
package com.enation.app.shop.core.goods.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.shop.core.goods.service.Separator;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * @author kingapex
 *2015-4-23
 */
public abstract class ParamsUtils {
	
	
	/**
	 * 将参数mapl转为url字串
	 * @param params
	 * @return
	 */
	public static String paramsToUrlString(Map<String,String> params){
		
		StringBuffer url = new StringBuffer();
		Iterator<String> keyIter  = params.keySet().iterator();
		while (keyIter.hasNext()) {
			String key = (String) keyIter.next();
			String value  = params.get(key);
			
			if(StringUtil.isEmpty(value)) continue;
			if(url.length()!=0){
				url.append("&");
			}
			url.append(key+"="+value);
			
			
		}
		
		return  url.toString() ;
	}
	
	
	/**
	 * 由request中获取参数map
	 * @param some
	 * @return
	 */
	public static  Map<String,String> getReqParams(){
		
		Map<String,String> map = new HashMap<String,String>();
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		Enumeration<String> paramNames = request.getParameterNames();
		if(paramNames!=null){
			while (paramNames.hasMoreElements()) {
				
				String name = paramNames.nextElement();
		
				String value = request.getParameter(name);
				map.put(name, value);
			}
		}
		
		return map;
		
	}
	
	
	
	/**
	 * 由request中根据prop参数获取属性数组<br>
	 * 由\@分割的
	 * @return
	 */
	public static  String[] getProps(){
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String prop = request.getParameter("prop");
		
		
		if(!StringUtil.isEmpty(prop)){
			
			String[] prop_ar = prop.split(Separator.separator_prop);
			return prop_ar;
		}
		
		return new String[]{};

	}
	
}
