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
		
		//wap站点没有使用全文检索组件，所以如果是wap的话，不使用全文检索  add by lyh  2017.4.15
		int wap_open=SystemSetting.getWap_open();//获取wap站点开关  
		if(wap_open==1&&lucene_open==1){
			return SpringContextHolder.getBean("goodsSearchManager");
		}
		
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
