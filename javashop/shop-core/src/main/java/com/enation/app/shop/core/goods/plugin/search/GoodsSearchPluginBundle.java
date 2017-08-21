package com.enation.app.shop.core.goods.plugin.search;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

@Service
public class GoodsSearchPluginBundle extends AutoRegisterPluginsBundle {

	@Override
	public String getName() {
		 
		return "商品搜索插件桩";
	}
	
	public List getPluginList(){
		
		return this.getPlugins();
	}
 
	
	/**
	 * 生成 选择器列表
	 * @param map
	 * @param cat
	 */
	public void createSelectorList(Map map,Cat cat){
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsFrontSearchFilter) {
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " IGoodsSearchFilter 开始...");
					}
					IGoodsFrontSearchFilter event = (IGoodsFrontSearchFilter) plugin;
					event.createSelectorList(map, cat);
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " IGoodsSearchFilter 结束.");
					}
				} else {
					if (loger.isDebugEnabled()) {
						loger.debug(" no,skip...");
					}
				}
			}
		}
	}
 
	/**
	 * 过滤搜索条件
	 * @param sql
	 * @param cat
	 */
	public void filter(StringBuffer sql,Cat cat) {
		
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsFrontSearchFilter) {
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " IGoodsSearchFilter 开始...");
					}
					IGoodsFrontSearchFilter event = (IGoodsFrontSearchFilter) plugin;
					event.filter(sql, cat);
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " IGoodsSearchFilter 结束.");
					}
				} else {
					if (loger.isDebugEnabled()) {
						loger.debug(" no,skip...");
					}
				}
			}
		}
	}

	/**
	 * 过滤sql语句前置条件
	 * @param sql
	 */
	public void filterFrontSql(StringBuffer sql) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsFrontSearchSqlFilter) {
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " IGoodsFrontSearchSqlFilter 开始...");
					}
					IGoodsFrontSearchSqlFilter event = (IGoodsFrontSearchSqlFilter) plugin;
					event.filterFrontSql(sql);
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " IGoodsFrontSearchSqlFilter 结束.");
					}
				} else {
					if (loger.isDebugEnabled()) {
						loger.debug(" no,skip...");
					}
				}
			}
		}
	}
}
