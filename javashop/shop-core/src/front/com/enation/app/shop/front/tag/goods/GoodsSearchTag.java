package com.enation.app.shop.front.tag.goods;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.service.IGoodsSearchManager;
import com.enation.app.shop.core.goods.service.SearchEngineFactory;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品搜索标签
 * @author kingapex
 *2013-7-29下午3:42:33
 */
@Component
@Scope("prototype")
public class GoodsSearchTag extends BaseFreeMarkerTag {
	
	
	/* (non-Javadoc)
	 * @see com.enation.framework.taglib.BaseFreeMarkerTag#exec(java.util.Map)
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer pageSize = (Integer)params.get("pageSize");
		if(pageSize==null) pageSize = this.getPageSize();
		IGoodsSearchManager goodsSearchManager = SearchEngineFactory.getSearchEngine();

		int page=this.getPage();//使支持？号传递
		Page webPage  =  goodsSearchManager.search(page, pageSize);
		webPage.setCurrentPageNo(page);
		return webPage;
	}
	
//
//	//@Override
//	protected Object exec1(Map params) throws TemplateModelException {
//		
//		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
//		String uri = request.getServletPath();
//		 
//		
//		Integer pageSize = (Integer)params.get("pageSize");
//		if(pageSize==null) pageSize = this.getPageSize();
//		
//		String page_str = UrlUtils.getParamStringValue(uri, "page");
//		
//		int page=this.getPage();//使支持？号传递
//		
//		if(page_str!=null && !page_str.equals("")){
//			page= Integer.valueOf(page_str);
//		}
//		
// 		Page webpage = 	goodsSearchManager2.search(page, pageSize, uri);
//		webpage.setCurrentPageNo(page);
//		//List<Goods> list = (List<Goods>) webpage.getResult();
//		return webpage;
//	}
 

 

	
}
