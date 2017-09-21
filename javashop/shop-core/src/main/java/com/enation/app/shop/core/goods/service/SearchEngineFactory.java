/**
 * 
 */
package com.enation.app.shop.core.goods.service;

import com.enation.app.base.core.model.ClusterSetting;
import com.enation.eop.SystemSetting;
import com.enation.framework.context.spring.SpringContextHolder;


/**
 * @author kingapex
 *2015-5-7
 */
public abstract class SearchEngineFactory {

	private SearchEngineFactory(){}

	public static IGoodsSearchManager getSearchEngine(){
		int solr_open = ClusterSetting.getSolr_open();//获取solr开关
		int lucene_open=SystemSetting.getLucene();//获取lucene开关
		//如果solr 和 lucene同时开启  solr优先于lucene 
		if(solr_open==0){
			if(lucene_open==0){
				return SpringContextHolder.getBean("goodsSearchManager");
			}else{
				return SpringContextHolder.getBean("goodsLuceneSearch");
			}
		}else{
			return SpringContextHolder.getBean("goodsSolrSearch");
		}
		/*if(solr_open==0){
			return SpringContextHolder.getBean("goodsSearchManager");
		}else{
			return SpringContextHolder.getBean("goodsLuceneSearch");
		}*/
	}
}
