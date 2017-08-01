package com.enation.app.base.core.service;

import java.util.Map;

/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：sql server安装接口
 *  修改人：xulipeng
 *  修改时间：2015-12-11
 *  修改内容：制定初版
 */
public interface ISqlServerManager {
	
	/**
	 * 安装接口
	 * @param table
	 * @param data
	 */
	public void installData(String table,Map data);

}
