package com.enation.framework.component;

/**
 * 组件接口
 * @author kingapex
 * @date 2011-12-23 下午9:54:10
 */
public interface IComponent {
	
	
	/**
	 * 组件被安装时调用此方法
	 */
	public void install();
	
	
	/**
	 * 组件卸载时调用此方法 
	 */
	public void unInstall();
	
}
