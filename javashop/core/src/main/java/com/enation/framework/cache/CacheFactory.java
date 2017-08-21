package com.enation.framework.cache;


import com.enation.app.base.core.model.ClusterSetting;
import com.enation.framework.context.spring.SpringContextHolder;

/**
 * Cache实现类工厂
 * 
 * @author kingapex
 *         <p>
 *         2009-12-16 下午05:23:28
 *         </p>
 * @version 1.0
 */
public final class CacheFactory {


	public static final String APP_CACHE_NAME_KEY = "appCache";
	public static final String THEMEURI_CACHE_NAME_KEY = "themeUriCache";
	public static final String SITE_CACHE_NAME_KEY = "siteCache";
	public static final String WIDGET_CACHE_NAME_KEY = "widgetCache";
	private static int IS_INIT=0;//是否执初始化标记 0未执行 1 执行过


	private static ICache cache;

	public CacheFactory(){ }

	/**
	 * 获取缓存
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> ICache<T> getCache(String name) {
		if(cache==null){
			cache=getCacheFa();
		}
		if(CacheFactory.IS_INIT==0){
			cache.initCache(name);
			CacheFactory.IS_INIT=1;
		}

		return cache;
	}
	/**
	 * 得到缓存方案 
	 * 0	ehcache
	 * 1	memcache
	 * 2	rdis
	 * @return
	 */
	public static ICache getCacheFa(){
		ICache cache =(ICache)SpringContextHolder.getBean("ehCacheImpl");

		int cache_fa=ClusterSetting.getCluster_cache();
		if(cache_fa==1){
			return (ICache)SpringContextHolder.getBean("memcachedImpl");
		}else if(cache_fa==2){
			return (ICache)SpringContextHolder.getBean("redisImpl");
		}
		return cache;

	}


}
