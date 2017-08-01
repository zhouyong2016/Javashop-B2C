package com.enation.eop.processor.facade;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;

/**
 * 模板路径 工厂
 * @author Kanon
 *
 */
public class ThemePathGeterFactory {
	
	public static  IThemePathGeter getThemePathGeter(){
		
		if(EopSetting.PRODUCT.equals("b2c")){
			return  SpringContextHolder.getBean("b2cThemePathGeter");
		}
		if(EopSetting.PRODUCT.equals("b2b2c")){
			return SpringContextHolder.getBean("b2b2cThemePathGeter");
		}
		if(EopSetting.PRODUCT.equals("fenxiao")){
			return  SpringContextHolder.getBean("fenxiaoThemePathGeter");
		}
		return null;
	}
}
