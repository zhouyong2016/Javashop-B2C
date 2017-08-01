package com.enation.app.base.core.service;

import java.util.List;

import com.enation.app.base.core.model.ShortMsg;



/**
 * 短消息管理
 * @author kingapex
 *
 */
public interface IShortMsgManager {
 
	
	/**
	 * 读取所有未读的消息 
	 * @return
	 */
	public List<ShortMsg> listNotReadMessage();
	
	 
	
	
}
