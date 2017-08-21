/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：商品评论插件桩
 *  修改人：Sylow
 *  修改时间：2015-11-05
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.goods.plugin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enation.app.shop.core.member.model.MemberComment;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 商品评论插件桩
 * @author Sylow
 * @version v1.0,2015-11-05
 * @since v5.2
 *
 */
@Service("goodsCommentsBundle")
public class GoodsCommentsBundle extends AutoRegisterPluginsBundle {
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "商品评论插件桩";
	}
	
	
	/**
	 *  商品增加前事件
	 * @param comments
	 */
	public void onGoodsCommentsBeforeAdd(MemberComment memberComment){
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsCommentsAddEvent) {
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " onGoodsCommentsBeforeAdd 开始...");
					}
					IGoodsCommentsAddEvent event = (IGoodsCommentsAddEvent) plugin;
					event.beforeAdd(memberComment);
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " onGoodsCommentsBeforeAdd 结束.");
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
	 *  商品增加后事件
	 * @param comments
	 */
	public void onGoodsCommentsAfterAdd(MemberComment memberComment){
		List<IPlugin> plugins = this.getPlugins();

		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if (plugin instanceof IGoodsCommentsAddEvent) {
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " onGoodsCommentsAfterAdd 开始...");
					}
					IGoodsCommentsAddEvent event = (IGoodsCommentsAddEvent) plugin;
					event.afterAdd(memberComment);
					if (loger.isDebugEnabled()) {
						loger.debug("调用插件 : " + plugin.getClass() + " onGoodsCommentsAfterAdd 结束.");
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
