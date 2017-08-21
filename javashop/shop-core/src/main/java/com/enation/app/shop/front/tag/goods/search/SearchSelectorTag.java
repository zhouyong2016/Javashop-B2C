package com.enation.app.shop.front.tag.goods.search;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.service.IGoodsSearchManager;
import com.enation.app.shop.core.goods.service.SearchEngineFactory;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品搜索选择器标签
 * 
 * @author kingapex 2013-7-29下午5:40:46
 */
@Component
@Scope("prototype")
public class SearchSelectorTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		IGoodsSearchManager goodsSearchManager = SearchEngineFactory
				.getSearchEngine();
		// //处理SEO标题
		// SearchSeoParser seoParser = new SearchSeoParser(selectorMap,cat,tag);
		// seoParser.parse();
		
		Map map = goodsSearchManager.getSelector();
		
		return map;

	}

}
