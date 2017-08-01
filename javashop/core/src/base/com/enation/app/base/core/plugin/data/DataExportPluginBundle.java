package com.enation.app.base.core.plugin.data;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;


/**
 * 数据导出插件桩
 * 
 * @author kingapex
 * 2012-10-10下午9:45:28
 */
@Service
public class DataExportPluginBundle  extends AutoRegisterPluginsBundle {

	@Override
	public String getName() {
	 
		return "数据导出插件桩";
	}
	
	
	
	/**
	 * 激发导出事件
	 * <p>
	 * 调用所有插件的数据导出接口，获取其返回数据，并拼装返回
	 * </p>
	 * @return
	 */
	public String exportData(){
		List<IPlugin> plugins = this.getPlugins();
		StringBuffer data = new StringBuffer();
		if(plugins!=null){ 
			for(IPlugin plugin:plugins){
				if( plugin instanceof IDataExportEvent  ){
					IDataExportEvent  DataExportEvent = (IDataExportEvent)plugin;
					String plugindata = DataExportEvent.onDataExport();
					data.append(plugindata);
					
				}
			}
		}
		
		return data.toString();
	}
	

}
