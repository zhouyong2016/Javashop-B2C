package com.enation.framework.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * JSON 相关操作
 * @author Sylow
 * 需要GJSON
 * 2015-07-14 
 */
public class JsonUtil {
	
	/**
	 * 把json格式的字符串转换为map对线
	 * @param json
	 * @return LinkedHashMap 
	 */
	public static LinkedHashMap<String, Object> toMap(String json) {
		return toMap(parseJson(json));
	}
	
	/**
	 * 把json数组格式的字符串转换为 List
	 * @return List<Object>
	 */
	public static List<Map<String,Object>> toList(String jsonArr){
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		JsonArray jsonArray = parseJsonArray(jsonArr);
		for (int i = 0; i < jsonArray.size(); i++) {
			Object value = jsonArray.get(i);
			Map<String,Object> map = toMap(value.toString());
			list.add(map);
		}
		return list;
	}
	
	/**
	 * map转换为json格式
	 * @param map
	 * @return String
	 */
	public static String MapToJson(Map map) {
		
		Gson gson = new Gson();
		return gson.toJson(map);
	}
	
	public static String ObjectToJson(Object o){
		Gson gson =new Gson();
		return gson.toJson(o);
	}
	
	/**
	 * list 转换为 json格式
	 * @param list
	 * @return String
	 */
	public static String ListToJson(List list) {
		Gson gson = new Gson();
		return gson.toJson(list);
	} 
	
	/*public static void main(String[] args){
		String json = "[{'name':'1'},{'name':'2'}]";
		List<Map> list = new ArrayList<Map>();
		JsonArray jsonArray = parseJsonArray(json);
		for (int i = 0; i < jsonArray.size(); i++) {
			Object value = jsonArray.get(i);
			Map<String,Object> map = toMap(value.toString());
			list.add(map);
		}
		
		for(Map map : list){
			System.out.println(map.get("name"));
		}
			
	}*/
	
	/**
	 * 获取JsonObject
	 * 
	 * @param json
	 * @return
	 */
	private static JsonObject parseJson(String json) {
		JsonParser parser = new JsonParser();
		JsonObject jsonObj = parser.parse(json).getAsJsonObject();
		return jsonObj;
	}
	
	private static JsonArray parseJsonArray(String jsonArr){
		JsonParser parser = new JsonParser();
		JsonArray jsonArray = parser.parse(jsonArr).getAsJsonArray();
		return jsonArray;
	}

	/**
	 * 将JSONObjec对象转换成Map-List集合
	 * 
	 * @param json
	 * @return
	 */
	private static LinkedHashMap<String, Object> toMap(JsonObject json) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		Set<Entry<String, JsonElement>> entrySet = json.entrySet();
		for (Iterator<Entry<String, JsonElement>> iter = entrySet.iterator(); iter
				.hasNext();) {
			Entry<String, JsonElement> entry = iter.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof JsonArray) {
				map.put((String) key, toList((JsonArray) value));
			} else if (value instanceof JsonObject) {
				map.put((String) key, toMap((JsonObject) value));
			} else if (value instanceof JsonNull) {
				map.put((String) key, "");
			} else {
				String str = value.toString();
				if (str.startsWith("\"")) {
					str = str.substring(1, str.length() - 1);
					Object obj = str;
					map.put((String) key, obj);
				} else {
					map.put((String) key, value);
				}

			}

		}
		return map;
	}

	/**
	 * 将JSONArray对象转换成List集合
	 * 
	 * @param json
	 * @return
	 */
	private static List<Object> toList(JsonArray json) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < json.size(); i++) {
			Object value = json.get(i);
			if (value instanceof JsonArray) {
				list.add(toList((JsonArray) value));
			} else if (value instanceof JsonObject) {
				list.add(toMap((JsonObject) value));
			} else if (value instanceof JsonNull) {
				list.add("");
			} else {
				String str = value.toString();
				if (str.startsWith("\"")) {
					str = str.substring(1, str.length() - 1);
					Object obj = str;
					list.add(obj);
				} else {
					list.add(value);
				}
			}
		}
		return list;
	}


	
}
