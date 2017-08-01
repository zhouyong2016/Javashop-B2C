package com.enation.app.cms.core.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.model.DataModel;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 文章插件桩
 * 
 * @author kingapex 2010-7-5下午02:29:17
 */
@Service
public class ArticlePluginBundle extends AutoRegisterPluginsBundle {

	public String getName() {
		return "文章插件桩";
	}

	/**
	 * 根据DataField获取
	 * 
	 * @param field
	 * @return
	 */
	public AbstractFieldPlugin getFieldPlugin(DataField field) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof AbstractFieldPlugin) {
					AbstractFieldPlugin fieldPlugin = (AbstractFieldPlugin) plugin;
					if (fieldPlugin.getId().equals(field.getShow_form())) {
						return fieldPlugin;
					}
				}
			}
		}
		return null;
	}

	public void onSave(Map article, DataField field) {
		List<IPlugin> plugins = this.getPlugins();

		try {
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof AbstractFieldPlugin) {
						AbstractFieldPlugin fieldPlugin = (AbstractFieldPlugin) plugin;
						if (fieldPlugin.getId().equals(field.getShow_form())) {
							fieldPlugin.onSave(article, field);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onSave(Map<String, Object> article, DataModel dataModel, int dataSaveType) {
		List<IPlugin> plugins = this.getPlugins();

		try {
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IDataSaveEvent) {
						IDataSaveEvent dataSaveEvent = (IDataSaveEvent) plugin;
						dataSaveEvent.onSave(article, dataModel, dataSaveType);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 数据删除事件 lzf add 2010-12-01
	 * 
	 * @param catid
	 * @param articleid
	 */
	public void onDelete(Integer catid, Integer articleid) {
		List<IPlugin> plugins = this.getPlugins();

		try {
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IDataDeleteEvent) {
						IDataDeleteEvent dataDeleteEvent = (IDataDeleteEvent) plugin;
						dataDeleteEvent.onDelete(catid, articleid);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String onDisplay(DataField field, Object value) {
		List<IPlugin> plugins = this.getPlugins();

		try {
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof AbstractFieldPlugin) {
						AbstractFieldPlugin fieldPlugin = (AbstractFieldPlugin) plugin;
						if (fieldPlugin.getId().equals(field.getShow_form())) {
							return fieldPlugin.onDisplay(field, value);
						}
					}
				}
			}
			return "输入项" + field.getShow_form() + "未找到插件解析";
		} catch (Exception e) {
			e.printStackTrace();
			return "输入项" + field.getShow_form() + "发生错误";
		}
	}

	public IPlugin findPlugin(String id) {
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof AbstractFieldPlugin) {
					AbstractFieldPlugin p = (AbstractFieldPlugin) plugin;
					if (id.equals(p.getId())) {
						return plugin;
					}
				}
			}
		}
		return null;
	}

	public List getFieldPlugins() {
		List<IPlugin> plugins = this.getPlugins();

		List<IPlugin> pluginList = new ArrayList<IPlugin>();
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof AbstractFieldPlugin) {
					pluginList.add(plugin);
				}
			}
		}
		return pluginList;
	}
}
