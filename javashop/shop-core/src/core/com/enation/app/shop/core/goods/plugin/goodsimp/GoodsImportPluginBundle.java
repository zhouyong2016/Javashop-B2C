package com.enation.app.shop.core.goods.plugin.goodsimp;

import java.util.List;

import javax.xml.ws.ServiceMode;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 商品数据导入插件桩
 * @author kingapex
 *
 */
@Service
public class GoodsImportPluginBundle extends AutoRegisterPluginsBundle {

	@Override
	public String getName() {
		
		return "商品批量导入插件桩";
	}
 
	
	public void onBeforeImport(Document configDoc){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof IBeforeGoodsImportEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass()+ " onBeforeImport 开始...");
						}
						IBeforeGoodsImportEvent event = (IBeforeGoodsImportEvent) plugin;
						event.onBeforeImport(configDoc);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass() + " onBeforeImport 结束.");
						}
					} 
				}
			}
		}catch(RuntimeException  e){
			if(this.loger.isErrorEnabled())
			this.loger.error("调用商品导入插件[导入前]事件错误", e);
			throw e;
		}
	}
}
