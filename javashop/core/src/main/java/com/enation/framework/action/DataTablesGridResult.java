package com.enation.framework.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;

/**
 * DataTables  json格式
 * @author xulipeng
 */
public class DataTablesGridResult {
	
	public DataTablesGridResult(){}
	
	public DataTablesGridResult(Page page){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String draws = request.getParameter("draw");
		draws = ( draws==null ?"1":draws);
		
		this.draw = Long.parseLong(draws);
		this.recordsFiltered= page.getTotalCount();
		this.recordsTotal= page.getTotalCount();
		this.data= (List)page.getResult();
	}
	
	/**
	 * 带有List参数的构造函数<br>
	 * 填充好total 和rows
	 * @param list
	 */
	public DataTablesGridResult(List list){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String draws = request.getParameter("draw");
		draws = ( draws==null ?"1":draws);
		
		this.draw = Long.parseLong(draws);
	}
	
	private long draw;			//本页面记录服务器端返回多少次
	
	private long recordsFiltered;	//前端过滤后的条数
	
	private long recordsTotal;	//一共多少条
	
	private List data;

	public long getDraw() {
		return draw;
	}

	public void setDraw(long draw) {
		this.draw = draw;
	}

	public long getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public long getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}
	
}
