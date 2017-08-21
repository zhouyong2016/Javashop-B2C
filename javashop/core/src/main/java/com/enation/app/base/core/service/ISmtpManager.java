package com.enation.app.base.core.service;

import java.util.List;

import com.enation.app.base.core.model.Smtp;


/**
 * smtp管理接口 
 * @author kingapex
 * @date 2011-11-1 下午12:09:36 
 * @version V1.0
 */
public interface ISmtpManager {
	
	/**
	 * 添加一个smtp
	 * @param smtp
	 */
	public void add(Smtp smtp);
	
	
	/**
	 * 修改一个smtp
	 * @param smtp
	 */
	public void edit(Smtp smtp);
	
	
	/**
	 * 删除一个smtp
	 * @param idAr  要删除的id数组
	 */
	public void delete(Integer[] idAr);
	
	
	
	/**
	 * 读取所有的smpt列表
	 * @return
	 */
	public List<Smtp> list();
	
	
	/**
	 * 标记某个smtp发送了一封邮件
	 * @param currSmtp
	 */
	public void sendOneMail(Smtp currSmtp);
	
	
	/**
	 * 获取一个smtp
	 * @param id
	 * @return
	 */
	public Smtp get(int id);
	
	
	/**
	 * 获取当前Smtp
	 * @return
	 */
	public Smtp getCurrentSmtp();

	/**
	 * 发送测试邮件
	 * @param smtp
	 */
	public void testSend(Smtp smtp, String send_to) throws Exception;
	
	
	
	
}
