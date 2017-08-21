package com.enation.eop.resource.impl.cache;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.eop.resource.IThemeUriManager;
import com.enation.eop.resource.model.ThemeUri;
import com.enation.framework.cache.AbstractCacheProxy;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;

/**
 * Theme Uri 缓存代理
 * 
 * @author kingapex
 *         <p>
 *         2009-12-16 下午05:48:25
 *         </p>
 * @version 1.0
 */
@Service("themeUriManager")
public class ThemeUriCacheProxy implements IThemeUriManager {
	protected final Logger logger = Logger.getLogger(getClass());
	@Autowired
	private IThemeUriManager themeUriManager;
	
 
	
	public static final String LIST_KEY_PREFIX = "theme_uri_list_";

	@Autowired
	public ThemeUriCacheProxy(IThemeUriManager themeUriDbManager) {
		this.themeUriManager = themeUriDbManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IThemeUriManager#clean()
	 */
	@Override
	public void clean() {
		this.themeUriManager.clean();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IThemeUriManager#add(com.enation.eop.resource.model.ThemeUri)
	 */
	@Override
	public void add(ThemeUri uri) {
		this.themeUriManager.add(uri);
		this.cleanCache();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IThemeUriManager#edit(java.util.List)
	 */
	@Override
	public void edit(List<ThemeUri> uriList) {
		themeUriManager.edit(uriList);
		this.cleanCache();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IThemeUriManager#edit(com.enation.eop.resource.model.ThemeUri)
	 */
	@Override
	public void edit(ThemeUri themeUri) {
		themeUriManager.edit(themeUri);
		this.cleanCache();
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IThemeUriManager#list(java.util.Map)
	 */
	@Override
	public List<ThemeUri> list(Map map) {
		ICache cache=CacheFactory.getCache(LIST_KEY_PREFIX);
		List<ThemeUri> uriList = (List<ThemeUri>) cache.get(LIST_KEY_PREFIX);

		if (uriList == null || map != null) {
			uriList = this.themeUriManager.list(map);

			cache.put(LIST_KEY_PREFIX, uriList);
		} else {
			// if(logger.isDebugEnabled()){
			// logger.debug("get user:"+userid+" site: "+ siteid
			// +" theme uri list from cache");
			// }
		}

		return uriList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IThemeUriManager#getPath(java.lang.String)
	 */
	@Override
	public ThemeUri getPath(String uri) {
		if (uri.equals("/")) {
			uri = "/index.html";
		}
		// if(logger.isDebugEnabled()){
		// logger.debug("parse uri["+ uri+"]...");
		// }

		List<ThemeUri> uriList = this.list(null);
		for (ThemeUri themeUri : uriList) {
			Matcher m = themeUri.getPattern().matcher(uri);

			// if(logger.isDebugEnabled()){
			// logger.debug("compile["+key+"],matcher["+uri+"],replace["+path+"]");
			// }

			if (m.find()) {
				// String s = m.replaceAll(path);
				// if(logger.isDebugEnabled()){
				// logger.debug("found...");
				// //logger.debug("dispatch  uri["+key+"=="+ uri+"] to "+
				// s+"["+path+"]");
				//
				// }
				return themeUri;
			} else {
				// if(logger.isDebugEnabled())
				// logger.debug("not found...");
			}
		}
		return null;
		// throw new UrlNotFoundException();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IThemeUriManager#delete(int)
	 */
	@Override
	public void delete(int id) {
		this.themeUriManager.delete(id);
		this.cleanCache();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.eop.resource.IThemeUriManager#get(java.lang.Integer)
	 */
	@Override
	public ThemeUri get(Integer id) {
		return this.themeUriManager.get(id);
	}
	private void cleanCache() {
		ICache cache=CacheFactory.getCache(LIST_KEY_PREFIX);
		cache.remove(LIST_KEY_PREFIX);
	}
}
