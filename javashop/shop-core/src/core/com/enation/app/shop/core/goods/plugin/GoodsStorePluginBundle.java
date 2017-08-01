package com.enation.app.shop.core.goods.plugin;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 商品库存插件桩
 * 
 * @author kingapex
 * 
 */
@Service("goodsStorePluginBundle")
public class GoodsStorePluginBundle extends AutoRegisterPluginsBundle {
	
	private void logerOut(String outString) {
		if (AutoRegisterPluginsBundle.loger.isDebugEnabled()) {
			AutoRegisterPluginsBundle.loger.debug(outString);
		}
	}
	
	private boolean canBeExecute(Map goods) {
		return true;
	}

	
	/**
	 * 库存维护html
	 * 
	 * @param goods
	 * @return
	 */
	public String getStoreHtml(Map goods) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("goods", goods);
		String html = null;
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof AbstractGoodsStorePlugin) {
					AbstractGoodsStorePlugin event = (AbstractGoodsStorePlugin) plugin;
					freeMarkerPaser.setClz(event.getClass());

					if (event.canBeExecute(goods)) {
						html = event.getStoreHtml(goods);
						logerOut("处理商品[" + goods.get("name") + "]获取商品存维护页面html事件:执行插件[" + plugin.getClass() + "]");
					} else {
						logerOut("处理商品[" + goods.get("name") + "]获取商品存维护页面html事件:插件[" + plugin.getClass() + "]不被执行");
					}
				}
			}
		}
		return html;
	}

	/**
	 * 获取进货html
	 * 
	 * @param goods
	 * @return
	 */
	public String getStockHtml(Map goods) {
		List<IPlugin> plugins = this.getPlugins();

		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("goods", goods);
		String html = null;
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof AbstractGoodsStorePlugin) {
					AbstractGoodsStorePlugin event = (AbstractGoodsStorePlugin) plugin;
					freeMarkerPaser.setClz(event.getClass());

					if (event.canBeExecute(goods)) {
						html = event.getStockHtml(goods);
						logerOut("处理商品[" + goods.get("name") + "]获取商品进货页面html事件:执行插件[" + plugin.getClass() + "]");
					} else {
						logerOut("处理商品[" + goods.get("name") + "]获取商品进货页面html事件:插件[" + plugin.getClass() + "]不被执行");
					}
				}
			}
		}
		return html;
	}

	/**
	 * 获取出货html
	 * 
	 * @param goods
	 * @return
	 */
	public String getShipHtml(Map goods) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("goods", goods);
		String html = null;
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof AbstractGoodsStorePlugin) {
					AbstractGoodsStorePlugin event = (AbstractGoodsStorePlugin) plugin;
					freeMarkerPaser.setClz(event.getClass());
					if (event.canBeExecute(goods)) {
						html = event.getShipHtml(goods);
						logerOut("处理商品[" + goods.get("name") + "]获取商品出货页面html事件:执行插件["	+ plugin.getClass() + "]");
					} else {
						logerOut("处理商品[" + goods.get("name") + "]获取商品出货页面html事件:插件[" + plugin.getClass() + "]不被执行");
					}
				}
			}
		}
		return html;
	}

	/**
	 * 保存商品库存事件
	 * 
	 * @param goods
	 */
	public void onStoreSave(Map goods) {
		List<IPlugin> plugins = this.getPlugins();
		for (IPlugin plugin : plugins) {
			if (plugin instanceof IStoreSaveEvent) {
				IStoreSaveEvent event = (IStoreSaveEvent) plugin;
				event.onStoreSave(goods);
				logerOut("处理商品[" + goods.get("name") + "]库存保存事件:执行插件[" + plugin.getClass() + "]");
				/*if (event.canBeExecute(goods)) {
					
				} else {
					logerOut("处理商品[" + goods.get("name") + "]库存保存事件:插件[" + plugin.getClass() + "]不被执行");
				}*/
			}
		}
	}

	/**
	 * 获取报警html
	 * 
	 * @param goods
	 * @return
	 */
	public String getWarnHtml(Map goods) {
		List<IPlugin> plugins = this.getPlugins();

		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("goods", goods);
		String html = null;
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof AbstractGoodsStorePlugin) {
					AbstractGoodsStorePlugin event = (AbstractGoodsStorePlugin) plugin;
					freeMarkerPaser.setClz(event.getClass());

					if (event.canBeExecute(goods)) {
						html = event.getWarnNumHtml(goods);
						logerOut("处理商品[" + goods.get("name") + "]获取商品报警设置页面html事件:执行插件[" + plugin.getClass() + "]");
					} else {
						logerOut("处理商品[" + goods.get("name") + "]获取商品报警设置页面html事件:插件["	+ plugin.getClass() + "]不被执行");
					}
				}
			}
		}
		return html;
	}

	/**
	 * 保存报警事件
	 * 
	 * @param goods
	 */

	public void onWarnSave(Map goods) {
		List<IPlugin> plugins = this.getPlugins();

		for (IPlugin plugin : plugins) {
			if (plugin instanceof AbstractGoodsStorePlugin) {
				AbstractGoodsStorePlugin event = (AbstractGoodsStorePlugin) plugin;
				if (this.canBeExecute(goods)) {
					event.onWarnSave(goods);
					logerOut("处理商品[" + goods.get("name") + "]报警保存事件:执行插件[" + plugin.getClass() + "]");
				} else {
					logerOut("处理商品[" + goods.get("name") + "]库存保存事件:插件[" + plugin.getClass() + "]不被执行");
				}
			}
		}
	}

	/**
	 * 保存商品进货事件
	 * 
	 * @param goods
	 */
	public void onStockSave(Map goods) {
		List<IPlugin> plugins = this.getPlugins();

		for (IPlugin plugin : plugins) {
			if (plugin instanceof  IStockSaveEvent ) {
				IStockSaveEvent event = (IStockSaveEvent) plugin;
				event.onStockSave(goods);
				logerOut("处理商品[" + goods.get("name") + "]进货事件:执行插件[" + plugin.getClass() + "]");
				/*if (this.canBeExecute(goods)) {
					
				} else {
					logerOut("处理商品[" + goods.get("name") + "]进货事件:插件[" + plugin.getClass() + "]不被执行");
				}*/

			}
		}
	}
	/**
	 * 响应库存变更事件
	 * @param goods
	 */
	public void onStockChange(Map goods){
		
		List<IPlugin> plugins = this.getPlugins();

		for (IPlugin plugin : plugins) {
			if (plugin instanceof  IStockChangeEvent ) {
				IStockChangeEvent event = (IStockChangeEvent) plugin;
				event.onStockChange(goods);
				logerOut("处理商品[" + goods.get("name") + "]库存变更事件:执行插件[" + plugin.getClass() + "]");

			}
		}
	}
	
	/**
	 * 响应库存退货入库事件
	 * @param goods
	 */
	public void onStockReturn(Map goods){
		
		List<IPlugin> plugins = this.getPlugins();

		for (IPlugin plugin : plugins) {
			if (plugin instanceof  IStockReturnEvent ) {
				IStockReturnEvent event = (IStockReturnEvent) plugin;
				event.onStockReturn(goods);
				logerOut("处理商品[" + goods.get("name") + "]库存退货入库事件:执行插件[" + plugin.getClass() + "]");

			}
		}
	}
	

	/**
	 * 保存商品出货事件
	 * 
	 * @param goods
	 */

	public void onShipSave(Map goods) {
		List<IPlugin> plugins = this.getPlugins();

		for (IPlugin plugin : plugins) {
			if (plugin instanceof AbstractGoodsStorePlugin) {
				AbstractGoodsStorePlugin event = (AbstractGoodsStorePlugin) plugin;
				if (event.canBeExecute(goods)) {
					event.onShipSave(goods);
					logerOut("处理商品[" + goods.get("name") + "]出货事件:执行插件[" + plugin.getClass() + "]");
				} else {
					logerOut("处理商品[" + goods.get("name") + "]出货事件:插件[" + plugin.getClass() + "]不被执行");
				}
			}
		}
	}

	@Override
	public String getName() {
		return "商品库存插件桩";
	}
	
	/**
	 * 预警库存维护html
	 * 
	 * @param goods
	 * @return
	 */
	public String getWarningStoreHtml(Map goods) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("goods", goods);
		String html = null;
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof AbstractGoodsStorePlugin) {
					AbstractGoodsStorePlugin event = (AbstractGoodsStorePlugin) plugin;
					freeMarkerPaser.setClz(event.getClass());

					if (event.canBeExecute(goods)) {
						html = event.getWarningStoreHtml(goods);
						logerOut("处理商品[" + goods.get("name") + "]获取商品存维护页面html事件:执行插件[" + plugin.getClass() + "]");
					} else {
						logerOut("处理商品[" + goods.get("name") + "]获取商品存维护页面html事件:插件[" + plugin.getClass() + "]不被执行");
					}
				}
			}
		}
		return html;
	}

}
