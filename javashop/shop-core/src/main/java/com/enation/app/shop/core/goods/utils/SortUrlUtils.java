/**
 * 
 */
package com.enation.app.shop.core.goods.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.shop.core.goods.plugin.search.SearchSelector;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * @author kingapex
 *2015-5-7
 */
public class SortUrlUtils {

 
	/**
	 * 向map中压入sort selector
	 * @param map
	 */
	public static void createAndPut(Map<String, Object> map) {
		List<SearchSelector> selectorList  = new ArrayList();
		List<Map<String,String>> sortList  = SortContainer.getSortList();
		
		for (Map<String, String> sort : sortList) {
			
			SearchSelector searchSelector =creareSortUrl(sort);
			selectorList.add(searchSelector);
			
		}
		 
		map.put("sort", selectorList);
	}
	
	
	
	
	/**
	 * 创建排序url
	 * @param sort
	 * @return 数组第一个元素是url，第二个元素是：是否为当前排序
	 */
	private static SearchSelector  creareSortUrl(Map<String, String> sort  ){
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String servlet_path = request.getServletPath();
		
		SearchSelector searchSelector = new SearchSelector();
		String is_current ="no";
		Map<String,String> params = ParamsUtils.getReqParams();
		String old_sort =  params.get("sort");
		 
		
		
		String id = sort.get("id");
		String def_sort = sort.get("def_sort");
		
		String ud = def_sort;
		
		
		if(StringUtil.isEmpty(old_sort)){
			old_sort=id+"_"+def_sort;  
			if("def".equals(id)){
				is_current ="yes";
				old_sort=id+"_asc";  
			}
		}else{
			
			String[] sortar = old_sort.split("_");
			String old_id = sortar[0];
			String upordown=def_sort;
			
			
			if( !checkExists(old_id)){ //防止非法的排序
				old_id="def";
			}
			
			
			if(old_id.equals( id )){//当前排序，则切换 升降序
				is_current="yes";	
				if(sortar.length==2){
					upordown= sortar[1];
				}
				
				 
				upordown=upordown.equals("desc")?"asc":"desc";
				
				old_sort=id+"_"+upordown;
			}else{ //非当前排序
				old_sort=id+"_"+def_sort;  
			}
			
			ud=upordown;
		}
		
		params.put("sort", old_sort);
		
		if( "yes".equals(is_current) ){ //是否当前排序
			searchSelector.setSelected(true);
		}else{
			searchSelector.setSelected(false);
		}
		
		String name = sort.get("name");
		String url= servlet_path +"?"+ParamsUtils.paramsToUrlString(params);
		searchSelector.setName(name);
		searchSelector.setUrl(url);
		searchSelector.setValue(ud.equals("desc")?"asc":"desc");
		
		return searchSelector;
		
	}

	
	/**
	 * 检测request中的排序是否有定义
	 * @param old_sort
	 * @return
	 */
	private static boolean checkExists(String old_sort){
		List<Map<String,String>> list = SortContainer.getSortList();
		for (Map<String, String> map : list) {
			if(map.get("id").equals(old_sort)){
				return true;
			}
		}		
		return false;
	}
	
}
