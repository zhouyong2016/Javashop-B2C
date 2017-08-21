package com.enation.framework.cache;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Ehcache缓存实现
 * 
 * @author kingapex
 *
 */
@Component
public class EhCacheImpl implements ICache {

	private net.sf.ehcache.Cache cache;

	/**
	 * 
	 */
	public EhCacheImpl() { }

	/**
	 * 初始化缓存对象
	 * 
	 * @param name
	 */
	@Override
	public void initCache(String name) {
		try {
			CacheManager manager = CacheManager.getInstance();
			cache = manager.getCache(name);
			if (cache == null) {
				manager.addCache(name);
				cache = manager.getCache(name);
			}
		} catch (net.sf.ehcache.CacheException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a value of an element which matches the given key.
	 * 
	 * @param key
	 *            the key of the element to return.
	 * @return The value placed into the cache with an earlier put, or null if
	 *         not found or expired
	 * @throws CacheException
	 */
	public Object get(Object key) {

		Object obj = null;
		try {
			if (key != null) {
				Element element = cache.get((Serializable) key);
				if (element != null) {
					obj = element.getValue();
				}
			}
		} catch (net.sf.ehcache.CacheException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * Puts an object into the cache.
	 * 
	 * @param key
	 *            a {@link Serializable} key
	 * @param value
	 *            a {@link Serializable} value
	 * @throws CacheException
	 *             if the parameters are not {@link Serializable}, the
	 *             {@link CacheManager} is shutdown or another {@link Exception}
	 *             occurs.
	 */
	public void put(Object key, Object value) {
		try {
			Element element = new Element((Serializable) key,
					(Serializable) value);
			cache.put(element);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Puts an object into the cache.
	 * @param key
	 * @param value
	 * @param exp
	 */
	public void put(Object key, Object value, int exp) {
		try {
			Element element = new Element((Serializable) key,
					(Serializable) value);
			element.setTimeToLive(exp);
			cache.put(element);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes the element which matches the key.
	 * <p>
	 * If no element matches, nothing is removed and no Exception is thrown.
	 * 
	 * @param key
	 *            the key of the element to remove
	 * @throws CacheException
	 */
	public void remove(Object key) {
		try {
			cache.remove((Serializable) key);
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		EhCacheImpl cache = new EhCacheImpl();
		cache.initCache("queryCache");
		 cache.put("test","fjdkafjsdkajd");
	}

	@Override
	public void clear() {
		 try {
	        	//cache.remove(arg0)
	            cache.removeAll();
	        } catch (IllegalStateException e) {
	        	e.printStackTrace();
	        } 
	}

}
