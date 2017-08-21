package com.enation.framework.component;

/**
 * 
 * 组件可停用接口
 * 当某组件需要在停用时进行操作的情况下,请实现此接口并复写stop()方法
 * @author    jianghongyan
 * @version   1.0.0,2016年7月13日
 * @since     v6.1
 */
public interface IComponentStopAble {
	public void stop();
}
