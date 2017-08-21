package com.enation.app.shop.core.order.plugin.cart;

import java.util.List;

import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.order.model.Cart;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.model.support.OrderPrice;
import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 购物车插件
 * @author Sylow
 * @version v2.0,2016年2月17日
 * @since v6.0
 */
@Service("cartPluginBundle")
public class CartPluginBundle extends AutoRegisterPluginsBundle {
	
	/**
	 * 计算价格
	 * @param orderpice
	 * @return
	 */
	public OrderPrice coutPrice(OrderPrice orderpice){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICountPriceEvent ) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.countPrice开始...");
						}
						ICountPriceEvent event = (ICountPriceEvent) plugin;
						orderpice =event.countPrice(orderpice);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.countPrice结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
		
			
		}catch(Exception e){
			e.printStackTrace();
			 
		}
		 
 		return orderpice;
	}
	 
	/**
	 * 填充itemList
	 * @param itemList
	 * @param sessionid
	 */
	public void filterList(List<CartItem> itemList,String sessionid){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICartItemFilter) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass() + "cart.add开始...");
						}
						ICartItemFilter event = (ICartItemFilter) plugin;
						event.filter(itemList, sessionid);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass() + " cart.add结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			 
		}
	}
	
	/**
	 * 当购物车增加时
	 * @param cart
	 */
	public void onAdd(Cart cart){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICartAddEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.add开始...");
						}
						ICartAddEvent event = (ICartAddEvent) plugin;
						event.add(cart);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.add结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
			 
			
		}catch(RuntimeException e){
			throw e;
		}
	}
	
	/**
	 * 添加完成后事件
	 * @param cart
	 */
	public void onAfterAdd(Cart cart) throws RuntimeException{
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICartAddEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.afterAdd开始...");
						}
						ICartAddEvent event = (ICartAddEvent) plugin;
						event.afterAdd(cart);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.afterAdd结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
			 
			
		}catch(RuntimeException e){
			throw e;
			 
		}
	}
	
	/**
	 * 购物车商品修改前
	 * @param sessionId sessionID
	 * @param cartId 购物车Id
	 * @param num 购物车数量
	 */
	public void onBeforeUpdate(String sessionId,Integer cartId,Integer num){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICartUpdateEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.update开始...");
						}
						ICartUpdateEvent event = (ICartUpdateEvent) plugin;
						event.onBeforeUpdate(sessionId, cartId, num);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.update结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
			 
			
		}catch(RuntimeException e){
			throw e;
			
		}
	}
	
	/**
	 * 当修改购物车时
	 * @param sessionId
	 * @param cartId
	 */
	public void onUpdate(String sessionId,Integer cartId){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICartUpdateEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.update开始...");
						}
						ICartUpdateEvent event = (ICartUpdateEvent) plugin;
						event.onUpdate(sessionId, cartId);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.update结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
			 
			
		}catch(Exception e){
			e.printStackTrace();
			 
		}
	}
	
	/**
	 * 当查询购物车是否存在货品之前过滤sql  add by jianghongyan 2016-06-15
	 * @param sql
	 */
	public String filterGetCountSql(String sql){
		try {
			List<IPlugin> plugins =this.getPlugins();
			if(plugins!=null){
				for (IPlugin plugin : plugins) {
					if(plugin instanceof ICartFilterSqlEvent){
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + "cart.filterGetCountSql开始...");
						}
						ICartFilterSqlEvent event = (ICartFilterSqlEvent) plugin;
						sql=event.filterGetCountSql(sql);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.filterGetCountSql结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}
	
	/**
	 * 当更新购物车数据时 过滤sql add by jianghongyan 2016-06-15
	 * @param sql
	 * @return
	 */
	public String filterUpdateSql(String sql){
		try {
			List<IPlugin> plugins =this.getPlugins();
			if(plugins!=null){
				for (IPlugin plugin : plugins) {
					if(plugin instanceof ICartFilterSqlEvent){
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + "cart.filterUpdateSql开始...");
						}
						ICartFilterSqlEvent event = (ICartFilterSqlEvent) plugin;
						sql=event.filterUpdateSql(sql);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.filterUpdateSql结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}
	/**
	 * 当删除购物车时
	 * @param sessionid
	 * @param cartid
	 */
	public void onDelete(String sessionid,Integer cartid){
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICartDeleteEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " +plugin.getClass() + "cart.delete开始...");
						}
						ICartDeleteEvent event = (ICartDeleteEvent) plugin;
						event.delete(sessionid, cartid);
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.delete结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			}
			 
			
		}catch(Exception e){
			e.printStackTrace();
			 
		}
		
	}
	
	/**
	 * 
	 * @param cart
	 * @return 
	 * @throws RuntimeException 
	 */
	public void beforeUpdateNum(Integer cart_id,Integer num) throws RuntimeException{
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICartBeforeUpdateNumEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.updateBefore开始...");
						}
						ICartBeforeUpdateNumEvent event = (ICartBeforeUpdateNumEvent) plugin;
						event.updateBefore(cart_id, num);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.updateBefore结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			} 
			
		}catch(RuntimeException e){
			throw e;
		}
	}

	/**
	 * 当获取购物车Item时对sql进行过滤  add by jianghongyan 2016-06-15
	 * @param sql
	 */
	public void filterListGoodsSql(StringBuffer sql) {
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICartFilterSqlEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.filterListGoodsSql开始...");
						}
						ICartFilterSqlEvent event = (ICartFilterSqlEvent) plugin;
						event.filterListGoodsSql(sql);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.filterListGoodsSql结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			} 
			
		}catch(RuntimeException e){
			throw e;
		}
	}
	
	
	@Override
	public String getName() {
		return "购物车插件桩";
	}

	public void filterSelectListGoods(StringBuffer sql) {
				try{
					List<IPlugin> plugins = this.getPlugins();
					
					if (plugins != null) {
						for (IPlugin plugin : plugins) {
							if (plugin instanceof ICartFilterSqlEvent) {
								if (loger.isDebugEnabled()) {
									loger.debug("调用插件 : " + plugin.getClass()+ "cart.filterSelectListGoods开始...");
								}
								ICartFilterSqlEvent event = (ICartFilterSqlEvent) plugin;
								event.filterSelectListGoods(sql);
								 
								if (loger.isDebugEnabled()) {
									loger.debug("调用插件 : " + plugin.getClass()+ " cart.filterSelectListGoods结束.");
								}
							}else{
								if (loger.isDebugEnabled()) {
									loger.debug(" no,skip...");
								}
							}
						}
					} 
					
				}catch(RuntimeException e){
					throw e;
				}
	}

	public void filterCheckProductSql(StringBuffer sql) {
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICartFilterSqlEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.filterCheckProductSql开始...");
						}
						ICartFilterSqlEvent event = (ICartFilterSqlEvent) plugin;
						event.filterCheckProductSql(sql);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.filterCheckProductSql结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			} 
			
		}catch(RuntimeException e){
			throw e;
		}
	}
	/**
	 * 添加货品到购物车时触发 仅为了适配积分商城 请其他地方不要调用 add by jianghongyan
	 */
	public void onAddProductToCart(Product product, int num) {
		try{
			List<IPlugin> plugins = this.getPlugins();
			
			if (plugins != null) {
				for (IPlugin plugin : plugins) {
					if (plugin instanceof ICartUpdateEvent) {
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ "cart.onAddProductToCart开始...");
						}
						ICartUpdateEvent event = (ICartUpdateEvent) plugin;
						event.onAddProductToCart(product, num);
						 
						if (loger.isDebugEnabled()) {
							loger.debug("调用插件 : " + plugin.getClass()+ " cart.onAddProductToCart结束.");
						}
					}else{
						if (loger.isDebugEnabled()) {
							loger.debug(" no,skip...");
						}
					}
				}
			} 
			
		}catch(RuntimeException e){
			throw e;
		}
	}
}
