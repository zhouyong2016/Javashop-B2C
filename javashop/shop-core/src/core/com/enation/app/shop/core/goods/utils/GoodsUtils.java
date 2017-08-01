package com.enation.app.shop.core.goods.utils;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import com.enation.app.shop.core.goods.model.support.SpecJson;

public abstract class GoodsUtils {
	
	
	public static List getSpecList(String specString){
		if(specString==null || specString.equals("[]") ||specString.equals("") ){
			return new ArrayList();
		} 
		JSONArray j1 = JSONArray.fromObject(specString);
		List<SpecJson> list =(List) JSONArray.toCollection(j1, SpecJson.class);		
		return list;
	}
}
