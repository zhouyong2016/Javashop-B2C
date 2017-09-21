package com.enation.app.cms.core.service.impl.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.cms.core.model.DataCat;
import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.cache.AbstractCacheProxy;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;

/**
 * 文章分类缓存代理<br>
 * 首先在分类缓存中以cacheName+"_"+site.getUserid() +"_"+site.getId();  为key存储当站点的分类缓存。以实现站点缓存隔离<br>
 * 在上述缓存中再以 cacheName+"_cat_"+catid;为key存储各分类的所有子类数据。
 * 
 * @author kingapex
 * 2010-7-5上午09:46:44
 */
@Service("dataCatManager")
public class DataCatCacheProxy extends AbstractCacheProxy<Map> implements
IDataCatManager { 
	public static final String cacheName ="cms_data_cat";

	@Autowired
	private IDataCatManager dataCatManager;

	@Autowired
	public DataCatCacheProxy(IDataCatManager dataCatDbManager) {
		this.dataCatManager = dataCatDbManager;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#add(com.enation.app.cms.core.model.DataCat)
	 */
	@Override
	public void add(DataCat cat) {
		this.dataCatManager.add(cat);
		this.cleanCache( );
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#delete(java.lang.Integer)
	 */
	@Override
	public int delete(Integer catid) {
		int r = this.dataCatManager.delete(catid);
		if(r==0){
			this.cleanCache( );
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#edit(com.enation.app.cms.core.model.DataCat)
	 */
	@Override
	public void edit(DataCat cat) {
		this.dataCatManager.edit(cat);
		this.cleanCache( );
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#get(java.lang.Integer)
	 */
	@Override
	public DataCat get(Integer catid) {

		return this.dataCatManager.get(catid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#listAllChildren(java.lang.Integer)
	 */
	@Override
	public List<DataCat> listAllChildren(Integer parentid) {
		List<DataCat> catList = null;
		if (catList == null) {
			catList = this.dataCatManager.listAllChildren(parentid);
			put(this.getKey(parentid), catList);
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load article cat form database");
			}
		}else{
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load article cat form cache");
			}
		}
		return catList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#listLevelChildren(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<DataCat> listLevelChildren(Integer catid, Integer level) {
		List<DataCat> catList = this.dataCatManager.listLevelChildren(catid, level);
		return catList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#saveSort(int[], int[])
	 */
	@Override
	public void saveSort(int[] catIds, int[] catSorts) {
		this.dataCatManager.saveSort(catIds, catSorts);
		this.cleanCache();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#getParents(java.lang.Integer)
	 */
	@Override
	public List<DataCat> getParents(Integer catid) {

		return this.dataCatManager.getParents(catid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.cms.core.service.IDataCatManager#del(int)
	 */
	@Override
	public int del(int cat_id) {
		int r = this.dataCatManager.del(cat_id);
		if(r==0){
			this.cleanCache( );
		}
		return r;

	}


	private String getKey(int catid){
		return cacheName+"_cat_"+catid;
	}

	/**
	 * 压入cat缓存
	 * @param key
	 * @param list
	 */
	private void put(String key,List<DataCat> list){
		//首先由缓存中获取本站点的cat缓存
		String mainkey = cacheName;
		ICache cache=CacheFactory.getCache(mainkey);
		Map catCache= (Map) cache.get(mainkey );

		//无缓存新建cat缓存map,并压入缓存
		if(catCache==null){
			catCache = new HashMap();
			cache.put(mainkey, catCache);
		}
		catCache.put(key, list);
	}

	/**
	 * 由缓存中读取分类缓存
	 * @param key
	 * @return
	 */
	private List<DataCat> get(String key){
		//首先由缓存中获取本站点的cat缓存
		String mainkey = cacheName;
		ICache cache=CacheFactory.getCache(mainkey);
		Map<String,List<DataCat>> catCache= (Map<String, List<DataCat>>) cache.get(mainkey );
		if(catCache==null){
			return  null;
		}
		//由类别缓存中获取数据
		return catCache.get(key);
	}

	/**
	 * 清除类别缓存
	 */
	private void cleanCache( ){
		String mainkey = cacheName;
		ICache cache=CacheFactory.getCache(mainkey);
		cache.remove( mainkey);
	}

	@Override
	public Integer getDataCat(String name) {
		return this.dataCatManager.getDataCat(name);
	}
}
