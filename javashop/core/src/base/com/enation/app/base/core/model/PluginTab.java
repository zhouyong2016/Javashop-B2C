package com.enation.app.base.core.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 插件Tab对象
 * @author xulipeng
 * 2016年02月25日11:12:46
 */

public class PluginTab {

	
	private Integer order;		//排序
	
	private String tabTitle;	//tab标题
	
	private String tabHtml;		//tab数据
	
	
	/**
	 * 将tabList按 order进行排序列
	 * @param tabList
	 * @return
	 */
	public static void  sort(List<PluginTab> tabList){
		
		if( tabList!=null){
			
			//利用treemap的key排序特性来将List排序
			Map<Integer,PluginTab> treeMap = new TreeMap<Integer,PluginTab> ();
			
			//首先将List中的tab全部压入map，当然要以tab.order来做为key
			for (PluginTab tab : tabList) {
				treeMap.put(tab.getOrder(), tab);
			}
			
			//清空原有list
			tabList.clear();
			
			//从map中取出tab，已经排好序了,重新压入已经清空的List中
			Iterator<Integer> keys = treeMap.keySet().iterator();
			 while(keys.hasNext()){
				 Integer key = keys.next();
				 PluginTab tab =treeMap.get(key);
				 tabList.add(tab);
			 }
			 
			 
		}
		
	}

	//set  get
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}


	public String getTabTitle() {
		return tabTitle;
	}

	public void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
	}

	public String getTabHtml() {
		return tabHtml;
	}

	public void setTabHtml(String tabHtml) {
		this.tabHtml = tabHtml;
	}
	
	
}
