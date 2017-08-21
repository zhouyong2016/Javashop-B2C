package com.enation.framework.component;

import java.util.ArrayList;
import java.util.List;

public class PluginView {
	private String id;
	private String name;
	private List<String> bundleList;
	
	public PluginView(){
		bundleList = new ArrayList<String>();
	}

	public void addBundle(String beanid){
		bundleList.add(beanid);
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getBundleList() {
		return bundleList;
	}
 
	
	
}
