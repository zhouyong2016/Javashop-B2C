package com.enation.app.base.core.plugin.express;

import org.apache.log4j.Logger;

import com.enation.framework.plugin.AutoRegisterPlugin;

public abstract class AbstractExpressPlugin extends AutoRegisterPlugin {
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	
	/**
	 * 为短信插件定义唯一的id
	 * @return
	 */
	public abstract String getId();
	
	
	/**
	 * 定义插件名称
	 * @return
	 */
	public abstract String getName();

}
