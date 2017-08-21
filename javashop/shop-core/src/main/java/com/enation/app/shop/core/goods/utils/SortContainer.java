/**
 * 
 */
package com.enation.app.shop.core.goods.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.framework.util.StringUtil;

/**
 * 
 * 排序容器<br>
 * 负责返回排序类型和检查排序参数合法
 * @author kingapex
 *2015-4-24
 */
public abstract class SortContainer {

	
	private static List<Map<String,String>> list  ;
	private static Map<String,String>  default_sort  ;
	
	
	/**
	 * 根据url中的排序和到排序map<br>
	 * 检查了参数合法的，不合法返回默认排序
	 * @param sort
	 * @return
	 */
	public static Map<String,String> getSort(String sort){
		if(StringUtil.isEmpty(sort)){
			return default_sort;
		}
		
		String[] sortar = sort.split("_");
		String sort_key = sortar[0];
		
		String sort_updown= "";
		if(sortar.length==2){
			sort_updown=sortar[1];
			if(!"desc".equals(sort_updown) && !"asc".equals(sort_updown)){
				sort_updown="asc";
			}
		}
		
		
		list = getSortList();
		for (Map<String,String> map : list) {
			String id = map.get("id");
			if(id.equals(sort_key)){ //存在此排序
				Map<String,String> result  = new HashMap<String,String>();
				result.putAll(map);
				sort_updown=StringUtil.isEmpty(sort_updown)?map.get("def_sort"):sort_updown;
				result.put("def_sort", sort_updown);
				return result; 
			}
		}
		
		//没有此排序，非法的，返回默认排序
		return default_sort; 
	}
	
	
	/**
	 * 生成排序列表
	 * @return
	 */
	public static  List<Map<String,String>> getSortList(){
		
		if(list!=null) return list;
		
		list  = new ArrayList();
		
		Map<String,String> sort_default = new HashMap(3);
		sort_default.put("id", "def");
		sort_default.put("name", "默认");
		sort_default.put("def_sort", "desc");
		
		
		Map<String,String> sort_buynum = new HashMap(3);
		sort_buynum.put("id", "buynum");
		sort_buynum.put("name", "销量");
		sort_buynum.put("def_sort", "desc");
		
		
		Map<String,String> sort_price = new HashMap(3);
		sort_price.put("id", "price");
		sort_price.put("name", "价格");
		sort_price.put("def_sort", "desc");
		
		
		Map<String,String> sort_grade = new HashMap(3);
		sort_grade.put("id", "grade");
		sort_grade.put("name", "评价");
		sort_grade.put("def_sort", "desc");
		
		default_sort= sort_default;
		
		list.add(sort_default);
		list.add(sort_buynum);
		list.add(sort_price);
		list.add(sort_grade);
		
		return list;
	}
}
