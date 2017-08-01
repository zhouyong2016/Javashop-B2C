package com.enation.app.shop.core.other.service.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.other.model.ArticleCat;
import com.enation.app.shop.core.other.service.IArticleCatManager;
import com.enation.framework.cache.AbstractCacheProxy;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;

@Service("articleCatManager")
public class ArticleCatCacheProxy extends AbstractCacheProxy<List<ArticleCat>> implements
		IArticleCatManager {
	
	public static final String cacheName ="article_cat";
	
	@Autowired
	private IArticleCatManager articleCatManager ;
	
	@Autowired
	public ArticleCatCacheProxy(IArticleCatManager articleCatDbManager) {
		this.articleCatManager = articleCatDbManager;
	}
	
	private String getKey(){
		return cacheName;
	}
	private void cleanCache(){
		ICache cache=CacheFactory.getCache(getKey());
		cache.remove(getKey());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IArticleCatManager#delete(int)
	 */
	@Override
	public int delete(int catId) {
		int r = this.articleCatManager.delete(catId);
		if(r==0){
			this.cleanCache();
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IArticleCatManager#getById(int)
	 */
	@Override
	public ArticleCat getById(int catId) {
		return this.articleCatManager.getById(catId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IArticleCatManager#listChildById(java.lang.Integer)
	 */
	@Override
	public List listChildById(Integer catId) {
		ICache cache=CacheFactory.getCache(getKey());
		List<ArticleCat> catList  = (List<ArticleCat>) cache.get(this.getKey());
		if(catList== null ){
			catList  = this.articleCatManager.listChildById(catId);
			cache.put(this.getKey(), catList);
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
	 * @see com.enation.app.shop.core.other.service.IArticleCatManager#listHelp(int)
	 */
	@Override
	public List listHelp(int catId) {
		return this.articleCatManager.listHelp(catId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IArticleCatManager#saveAdd(com.enation.app.shop.core.other.model.ArticleCat)
	 */
	@Override
	public void saveAdd(ArticleCat cat) {
		this.articleCatManager.saveAdd(cat);
		this.cleanCache();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IArticleCatManager#saveSort(int[], int[])
	 */
	@Override
	public void saveSort(int[] catIds, int[] catSorts) {
		this.articleCatManager.saveSort(catIds, catSorts);
		this.cleanCache();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IArticleCatManager#update(com.enation.app.shop.core.other.model.ArticleCat)
	 */
	@Override
	public void update(ArticleCat cat) {
		this.articleCatManager.update(cat);
		this.cleanCache();
	}



}
