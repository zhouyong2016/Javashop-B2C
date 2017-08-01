package com.enation.app.shop.core.order.service;

import java.util.List;

import com.enation.app.shop.core.order.model.PrintTmpl;

public interface IPrintTmplManager {
	/**
	 * 模板列表
	 * @return
	 */
	public List list();
	/**
	 * 模板回收站列表
	 * @return
	 */
	public List trash();
	
	/**
	 * 启用模板列表
	 * @return
	 */
	public List listCanUse();
	/**
	 * 添加快递模板
	 * @param printTmpl 快递模板
	 */
	public void add(PrintTmpl printTmpl);
	/**
	 * 修改快递模板
	 * @param printTmpl 模板
	 */
	public void edit(PrintTmpl printTmpl);
	/**
	 * 根据Id获取模板
	 * @param prt_tmpl_id 模板Id
	 * @return
	 */
	public PrintTmpl get(int prt_tmpl_id);
	/**
	 * 删除模板
	 * @param id 模板Id数组,Integer[]
	 */
	public void delete(Integer[] id);
	/**
	 * 还原模板
	 * @param id 模板数组Id,Integer[]
	 */
	public void revert(Integer[] id);
	/**
	 * 清空模板 
	 * @param id 模板数组Id,Integer[]
	 */
	public void clean(Integer[] id);
	/**
	 * 根据模板名称查看是否存在此模板
	 * @param title 模板名称
	 * @return true.存在.false.不存在
	 */
	public boolean check(String title);
}
