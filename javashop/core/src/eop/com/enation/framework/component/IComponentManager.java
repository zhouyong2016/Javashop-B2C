package com.enation.framework.component;

import java.util.List;

public interface IComponentManager {

	public void startComponents();


	/**
	 * 获取所有组件列表
	 * 
	 * @return
	 */
	public List<ComponentView> list();

	public void install(String id);

	public void unInstall(String id);

	/**
	 * 启用某个组件 将其插件及挂件启用
	 * 
	 * @param componentid
	 *            组件标识id
	 */
	public void start(String id);

	/**
	 * 停用某个组件 将其插件及挂件停用
	 * 
	 * @param componentid
	 *            组件标识id
	 */
	public void stop(String id);
	/**
	 * 获取组件视图
	 * @param componentName
	 * @return ComponentView
	 */
	public ComponentView getCmptView(String componentName);
}
