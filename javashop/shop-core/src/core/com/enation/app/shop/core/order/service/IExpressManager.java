package com.enation.app.shop.core.order.service;

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.ExpressPlatform;

/**
 * 快递
 * @author xulipeng
 *
 */
public interface IExpressManager {

	/**
	 * 获取快递列表 list
	 * @return
	 */
	public List getList();
	
	/**
	 * 添加快递平台
	 * @param platform
	 */
	public void add(ExpressPlatform platform);
	
	/**
	 * 根据code获取短信平台信息
	 * @param code
	 * @return
	 */
	public ExpressPlatform getPlateform(Integer id);
	
	
	/**
	 * 根据code查询 对应html页
	 * @param code
	 * @return
	 */
	public String getPlateformHtml(String code,Integer id); 
	
	
	/**
	 * 设置相应的快递接口参数
	 * @param id
	 * @param param
	 */
	public void setParam(Integer id ,Map<String,String> param);
	
	/**
	 * 设置默认启用的快递平台
	 * @param id
	 */
	public void open(Integer id);
	
	/**
	 * 根据快递单查询信息
	 * @param com	快递公司编码
	 * @param nu	快递单号
	 * @return
	 */
	public Map getDefPlatform(String com,String nu);
	
	/**
	 * 获取平台对象，通过code值
	 * @param code
	 * @return 如返回1则已存在，0为不存在
	 */
	public int getPlateform(String code);
	
}
