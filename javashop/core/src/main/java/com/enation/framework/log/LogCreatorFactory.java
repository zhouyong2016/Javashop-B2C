package com.enation.framework.log;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;

/**
 * 获取日志bean的工厂
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2016年12月13日 上午10:19:51
 */
public class LogCreatorFactory {

	/**
	 * 获取创建日志的bean
	 * @return
	 */
	public static ILogCreator getLogCreator(){
		if("b2c".equals(EopSetting.PRODUCT)){
			
			return  SpringContextHolder.getBean("b2cLogCreator");
		}
					
		if("b2b2c".equals(EopSetting.PRODUCT)){
			
			return  SpringContextHolder.getBean("b2b2cLogCreator");
		}
		
		return null;
	}
}
