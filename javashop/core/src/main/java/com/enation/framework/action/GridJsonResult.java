package com.enation.framework.action;

import java.util.List;

import com.enation.framework.database.Page;


/**
 * 符合easy ui grid 格式的json result<br>
 * 
 * @author kingapex
 * @version v1.0
 * 2016年2月16日下午4:33:03
 * @since v6.0
 */
public class GridJsonResult {
	
	public GridJsonResult(){}
	
	/**
	 * 带有Page参数的构造函数<br>
	 * 填充好total 和rows
	 * @param page
	 */
	public GridJsonResult(Page page){
		this.total= page.getTotalCount();
		this.rows= (List)page.getResult();
	}
	
	/**
	 * 带有List参数的构造函数<br>
	 * 填充好total 和rows
	 * @param list
	 */
	public GridJsonResult(List list){
		this.total=list.size();
		this.rows= list;
	}
	
	/**
	 * 记录数
	 */
	private long total;
	
	
	/**
	 * 分页的数据对象
	 */
	private List rows;
	
	
	
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	
	
	
}
