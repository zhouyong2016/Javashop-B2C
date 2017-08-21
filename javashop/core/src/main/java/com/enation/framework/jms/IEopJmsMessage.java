package com.enation.framework.jms;

import com.enation.eop.sdk.context.EopContext;


/**
 * Jms消息
 * @author kingapex
 *
 */
public interface IEopJmsMessage {
	
	/**
	 * 获取参数
	 * @return 参数
	 */
	public Object getData();
	
	/**
	 * 调用的类
	 * @return 类的bean
	 */
	public String getProcessorBeanId();
	
	
	/**
	 * 获取EOP上下文
	 * @return
	 */
	public EopContext getEopContext();
	
}
