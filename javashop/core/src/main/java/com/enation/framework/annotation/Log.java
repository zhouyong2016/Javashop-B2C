package com.enation.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 记录操作日志自定义注解
 * @author fk
 * @version v1.0
 * @since v6.1
 * 2016年12月7日 下午1:10:14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Log {

	/**
	 * 类型
	 * @return
	 */
	public String type();
	
	/**
	 * 操作说明
	 * @return
	 */
	public String detail();
	
	
	
}
