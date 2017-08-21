package com.enation.app.base.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.SmsPlatform;

/**
 * 短信接口
 * @author xulipeng
 *2015年06月09日15:10:21
 */
public interface ISmsManager {
	
	/**
	 * 查询所有短信平台
	 * @return
	 */
	public List getList();
	
	/**
	 * 添加短信平台
	 * @param smsPlatform
	 */
	public void addSmsPlatform(SmsPlatform smsPlatform);
	
	/**
	 * 获取短信平台html
	 * @param pluginid
	 * @return
	 */
	public String getSmsPlatformHtml(String pluginid,Integer smsid);
	
	/**
	 * 设置网关参数
	 * @param id
	 * @param param
	 */
	public void setParam(Integer id ,Map<String,String> param); 
	
	/**
	 * 读取网关参数
	 * @param id
	 * @return
	 */
	public SmsPlatform get(Integer id);
	
	/**
	 * 发送短信
	 * @return
	 */
	public boolean send(String phone,String content,Map param);
	
	/**
	 * 启用网关
	 * @param id
	 */
	public void open(Integer id);
	
}
