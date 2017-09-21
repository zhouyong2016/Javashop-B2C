package com.enation.app.shop.core.other.service.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.framework.cache.AbstractCacheProxy;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;
import com.enation.framework.util.StringUtil;

/**
 * 商品分类缓存代理
 * @author kingapex
 * 2010-5-25上午10:52:51
 */
@Service("goodsCatManager")
public class GoodsCatCacheProxy  implements
		IGoodsCatManager {
	protected final Logger logger = Logger.getLogger(getClass());
	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	public static final String CACHE_KEY= "goods_cat" ;
	
	@Autowired
	public GoodsCatCacheProxy(IGoodsCatManager goodsCatDbManager) {
		this.goodsCatManager = goodsCatDbManager;
	}
	
	public void cleanCache(){
		ICache cache=CacheFactory.getCache(CACHE_KEY);
		cache.remove(CACHE_KEY+"_0") ;
		cache.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#delete(int)
	 */
	@Override
	public int delete(int catId) {
		int r  =this.goodsCatManager.delete(catId);
		if(r == 0){
			this.cleanCache();
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#getById(int)
	 */
	@Override
	public Cat getById(int catId) {
		return goodsCatManager.getById(catId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#listAllChildren(java.lang.Integer)
	 */
	@Override
	public List<Cat> listAllChildren(Integer catId) {
		ICache cache=CacheFactory.getCache(CACHE_KEY);
		List<Cat> catList  = (List<Cat>) cache.get(CACHE_KEY+"_"+catId);
		if(catList == null){
			catList  = this.goodsCatManager.listAllChildren(catId);
			cache.put(CACHE_KEY+"_"+catId, catList);
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load goods cat from database");
			}			
		} else{
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load goods cat from cache");
			}
		}
		return catList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#listChildren(java.lang.Integer)
	 */
	@Override
	public List<Cat> listChildren(Integer catId) {
		return this.goodsCatManager.listChildren(catId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#saveAdd(com.enation.app.shop.core.goods.model.Cat)
	 */
	@Override
	public void saveAdd(Cat cat) {
		this.goodsCatManager.saveAdd(cat);
		this.cleanCache();
	} 

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#saveSort(int[], int[])
	 */
	@Override
	public void saveSort(int[] catIds, int[] catSorts) {
		this.goodsCatManager.saveSort(catIds, catSorts);
		this.cleanCache();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#update(com.enation.app.shop.core.goods.model.Cat)
	 */
	@Override
	public void update(Cat cat) {
		this.goodsCatManager.update(cat);
		this.cleanCache();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#checkname(java.lang.String, java.lang.Integer)
	 */
	@Override
	public boolean checkname(String name,Integer catid) {
		return this.goodsCatManager.checkname(name,catid);
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#getParents(int)
	 */
	@Override
	public List<Cat> getParents(int catid) {
		
		return this.goodsCatManager.getParents(catid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsCatManager#getListChildren(java.lang.Integer)
	 */
	@Override
	public List<Map> getListChildren(Integer cat_id) {
		return this.goodsCatManager.getListChildren(cat_id);
	}

	private List getNavpath(int catId) {
		List list = new ArrayList();
		Map map = new HashMap();
		map.put("name", "首页");
		map.put("value", "0");
		list.add(map);
		Cat cat = getById(catId);
		String path = cat.getCat_path();
		path = path.substring(2, path.length()-1);
		path = path.replace("|", ",");
		String[] ids = path.split(",");
		for(String id:ids){
			Cat pcat = getById(StringUtil.toInt(id));
			Map pmap = new HashMap();
			pmap.put("name", pcat.getName());
			pmap.put("value", id);
			list.add(pmap);
		}
		return list;
	}

	@Override
	public List getGoodsParentsType() {
		return goodsCatManager.getGoodsParentsType();
	}

	@Override
	public List<Cat> getGoodsParentsType(Integer cat_id) {
		return goodsCatManager.getGoodsParentsType(cat_id);
	}
}
