package com.enation.framework.component.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.framework.component.ComponentView;
import com.enation.framework.component.IComponentManager;
import com.enation.framework.component.IComponentStartAble;
import com.enation.framework.component.IComponentStopAble;
import com.enation.framework.component.PluginView;
import com.enation.framework.component.context.ComponentContext;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.IPlugin;
import com.enation.framework.plugin.IPluginBundle;
 
/**
 * 组件管理
 * @author kingapex
 *2012-5-15上午6:58:38
 *@version 2.0  6.0升级改造    wangxin  2016-2-24
 */
@Service("componentManager")
public class ComponentManager implements IComponentManager {
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IDaoSupport daoSupport;
	
	
	/**
	 * 先由组件上下文中加载组件列表
	 * 再由数据库中加载组件状态
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.component.IComponentManager#list()
	 */
	@Override
	public List<ComponentView> list() {
		List<ComponentView> viewList = new ArrayList<ComponentView>();
		List<ComponentView> componentViews = ComponentContext.getComponents();// 加载所有声明的组件
		List<ComponentView> dbList = getDbList();
		
		if (componentViews != null) {
			for (ComponentView view : componentViews) {
				ComponentView componentView = (ComponentView) view.clone();

				if (logger.isDebugEnabled()) {
					logger.debug("load component["	+ componentView.getName() + "] start ");
				}
				try {
					componentView.setInstall_state(0); // 默认为未安装状态
					componentView.setEnable_state(0); // 默认为未启用					

					this.loadComponentState(componentView, dbList); // 由数据库加载组件信息					

					if (logger.isDebugEnabled()) {
						logger.debug("load component["	+ componentView.getName() + "] end ");
					}
				} catch (Exception e) {
					logger.error("加载组件[" + componentView.getName() + "]出错", e);
					componentView.setEnable_state(2);
					componentView.setError_message(e.getMessage());
				}
				viewList.add(componentView);
			}
		}
		return viewList;
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.component.IComponentManager#install(java.lang.String)
	 */
	@Override
	public void install(String componentid) {
		if (logger.isDebugEnabled()) {
			logger.debug("install component[" + componentid + "]...");
		}
		
		try {
			ComponentView componentView = this.getComponentView(componentid);
			if (componentView != null) {
				componentView.getComponent().install();
				if (!this.isInDb(componentView.getComponentid())) {// 数据库中还不存在，需要先插入一条
					ComponentView temp = (ComponentView) componentView.clone();
					temp.setInstall_state(1);

					daoSupport.insert("es_component", temp);
				} else {
					componentView.setInstall_state(1);
					daoSupport.execute("update es_component set install_state=1 where componentid=?",	componentView.getComponentid());
				}
			}
		} catch (RuntimeException e) {
			logger.error("安装组件[" + componentid + "]出错", e);
			throw e;
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.component.IComponentManager#unInstall(java.lang.String)
	 */
	@Override
	public void unInstall(String componentid) {
		if (logger.isDebugEnabled()) {
			logger.debug("install component[" + componentid + "]...");
		}
		
		ComponentView componentView = this.getComponentView(componentid);
		if (componentView != null) {
			componentView.getComponent().unInstall();
			if (!this.isInDb(componentView.getComponentid())) {
				ComponentView temp = (ComponentView) componentView.clone();
				temp.setInstall_state(0);
				daoSupport.insert("es_component", temp);
			} else {
				daoSupport.execute("update es_component set install_state=0 where componentid=?",	componentView.getComponentid());
			}
		}
	}
	

	/**
	 * 获取组件视图根据组件名称
	 * @param componentName
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.component.IComponentManager#getCmptView(java.lang.String)
	 */
	@Override
	public ComponentView getCmptView(String componentName) {
		List<ComponentView> componentList = ComponentContext.getComponents();
		for (ComponentView componentView : componentList) {
			if (componentView.getName().equals(componentName)) {
				return componentView;
			}
		}
		return null;
	}
	/**
	 * 启用某个组件
	 * 将其插件及挂件启用
	 * @param componentid 组件标识id
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.component.IComponentManager#start(java.lang.String)
	 */
	@Override
	public void start(String componentid) {
		
		//判断是否为debug 记录日志
		if (logger.isDebugEnabled()) {
			logger.debug("start component[" + componentid + "]...");
		}
		
		//获取组件 判断是否组件已经启用
		Object object = SpringContextHolder.getBean(componentid);
		if (object instanceof IComponentStartAble) {//某组件有用到
			IComponentStartAble startAble = (IComponentStartAble) object;
			startAble.start();
		}
		
		//获取当前的组件视图
		ComponentView componentView= this.getComponentView(componentid);
		componentView.setEnable_state(1);
		
		//获取当前组件下的插件列表
		List<PluginView> pluginList = componentView.getPluginList();
		
		//循环插件列表
		for (PluginView pluginView : pluginList) {
			
			//获取当前的插件
			String pluginid = pluginView.getId();
			IPlugin plugin = SpringContextHolder.getBean(pluginid);
			
			//获取当前插件的插件桩列表
			List<String> bundleList = pluginView.getBundleList();
			
			//判断是否桩为空，并且为插件桩注册插件
			if (bundleList != null) {
				for (String bundleId : bundleList) {
					IPluginBundle pluginBundle = SpringContextHolder.getBean(bundleId);
					
					//在插件桩下注册插件
					pluginBundle.registerPlugin(plugin);
				}
			}
		}				
				
		
				
		/**
		 * 更新数据库的状态
		 */
		String sql = "update es_component set enable_state=1 where componentid=?";
		daoSupport.execute(sql, componentid);			
		
		if (logger.isDebugEnabled()) {
			logger.debug("start component[" + componentid + "] complete");
		}		
	}

	/**
	 * 停用某个组件
	 * 将其插件及挂件停用
	 * @param componentid 组件标识id
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.component.IComponentManager#stop(java.lang.String)
	 */
	@Override
	public void stop(String componentid) {
		if (logger.isDebugEnabled()) {
			logger.debug("stop component[" + componentid + "]...");
		}	
		
		
		
		ComponentView componentView= this.getComponentView(componentid);
		
		
		
		Object object = SpringContextHolder.getBean(componentid);
		if (object instanceof IComponentStopAble) {//某组件是否实现可停用接口
			if(componentView.getEnable_state()!=0){//获取组件 判断是否组件已经启用
				IComponentStopAble stopAble = (IComponentStopAble) object;
				stopAble.stop();
			}
		}
		
		componentView.setEnable_state(0);		
		/**
		 * 
		 * 停用相应插件
		 */
		List<PluginView> pluginList = componentView.getPluginList();
		for (PluginView pluginView : pluginList) {
			String pluginid = pluginView.getId();
			IPlugin plugin = SpringContextHolder.getBean(pluginid);
			List<String> bundleList = pluginView.getBundleList();
			for (String bundleId : bundleList) {
				IPluginBundle pluginBundle = SpringContextHolder.getBean(bundleId);
				pluginBundle.unRegisterPlugin(plugin);
			}
		}
				
		 
				
		/**
		 * 更新数据库的状态
		 */
		String sql = "update es_component set enable_state=0 where componentid=?";
		daoSupport.execute(sql, componentid);				
				
		if (logger.isDebugEnabled()) {
			logger.debug("stop component[" + componentid + "] complete");
		}		
	}
	
	/**
	 * 启动站点组件
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.framework.component.IComponentManager#startComponents()
	 */
	@Override
	public void startComponents() {
		
		//判断是否为debug 记录日志
		if (logger.isDebugEnabled()) {
			logger.debug("start components start...");
		}
 
		//获取组件列表
		List<ComponentView> dbList = getDbList();
		
		//循环组件列表，启动组件
		for (ComponentView componentView : dbList) {
			
			//判断组件是否安装
			if (componentView.getInstall_state() != 2) {
				
				//判断组件是否启用
				if (componentView.getEnable_state() == 1) {
					
					//启动组件，将组件下插件启动
					this.start(componentView.getComponentid());
				}
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("start components end!");
		}
	}
 
	/**
	 * 获取组件列表
	 * @return 组件列表
	 */
	private List<ComponentView> getDbList() {
		String sql = "select * from es_component ";
		return this.daoSupport.queryForList(sql, ComponentView.class);
	}
	/**
	 * 
	 * @param componentid 组件
	 * @return 组件视图
	 */
	private ComponentView getComponentView(String componentid) {
		List<ComponentView> componentList = ComponentContext.getComponents();
		for (ComponentView componentView : componentList) {
			if (componentView.getComponentid().equals(componentid)) {
				return componentView;
			}
		}
		return null;
	}
	private boolean isInDb(String componentid) {
		String sql = "select count(0)  from es_component where componentid=?";
		return daoSupport.queryForInt(sql, componentid) > 0;
	}
	/**
	 * 用数据库的组件列表加载组件上下文中的组件状态
	 * @param componentView
	 * @param dbList
	 */
	private void loadComponentState(ComponentView componentView, List<ComponentView> dbList) {
		for (ComponentView dbView : dbList) {
			if (dbView.getComponentid().equals(componentView.getComponentid())) {
				if (logger.isDebugEnabled()) {
					logger.debug("load component[" + componentView.getName()
							+ "]state->install state:"
							+ dbView.getInstall_state() + ",enable_state:"
							+ dbView.getEnable_state());
				}				
				componentView.setInstall_state(dbView.getInstall_state());
				componentView.setEnable_state(dbView.getEnable_state());
				componentView.setId(dbView.getId());
				
				if (componentView.getInstall_state() != 2) {
					if (dbView.getEnable_state() == 0) {
						// this.stop(dbView.getComponentid() );
					} else if (dbView.getEnable_state() == 1) {
						// this.start(dbView.getComponentid());
					} else {
						componentView.setError_message(dbView.getError_message());
					}
				}
			}
		}
	}

}
