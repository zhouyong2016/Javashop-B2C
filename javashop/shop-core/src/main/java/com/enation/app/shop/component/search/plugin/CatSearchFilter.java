package com.enation.app.shop.component.search.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.plugin.search.IGoodsFrontSearchFilter;
import com.enation.app.shop.core.goods.plugin.search.SearchSelector;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.utils.CatUrlUtils;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 商品分类搜索过虑器
 * @author kingapex
 * lzf edited 2012-11-11
 */
@Component
public class CatSearchFilter extends AutoRegisterPlugin implements
		IGoodsFrontSearchFilter {
	
	private IGoodsCatManager goodsCatManager;

	public void createSelectorList(Map map,Cat cat) {
		
		List<Cat> allCatList  = this.goodsCatManager.listAllChildren(0);

		List<SearchSelector> selectorList = new ArrayList<SearchSelector>();

		
		//此分类的品牌列表
		List<Cat> catList = null;
		
		if(cat!=null){
			catList  = this.goodsCatManager.listChildren(cat.getCat_id());
		}else{
			catList  = this.goodsCatManager.listChildren(0);
		}
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String servlet_path = request.getServletPath();
		
		for (Cat child : catList) {
			SearchSelector selector = new SearchSelector();
			selector.setName(child.getName());
			String url = servlet_path +"?"+ CatUrlUtils.createCatUrl(child, false);
			selector.setUrl(url);
			selectorList.add(selector);
		}
		map.put("cat", selectorList);
		List<SearchSelector> selectedCat = CatUrlUtils.getCatDimSelected(allCatList);
		 map.put("selected_cat", selectedCat); //已经选择的分类
	}

	
	public void filter(StringBuffer sql,Cat cat) {
		FreeMarkerPaser.getInstance().putData("cat",cat);
		if(cat!=null ){
			String cat_path  = cat.getCat_path();
			if (cat_path != null) {
				/**
				 * add by jianghongyan  添加积分商城支持
				 * 
				 */
				HttpServletRequest request=ThreadContextHolder.getHttpRequest();
				Object o=request.getAttribute("is_exchange");
				boolean is_exchange=false;
				if(o!=null){
					is_exchange=(Boolean) request.getAttribute("is_exchange");
				}
				if(is_exchange){
					sql.append( " and  et.cat_id in(" ) ;
					sql.append("select c.cat_id from es_goods_cat");
					sql.append(" c where c.cat_path like '" + cat_path + "%')");
				}else{
					sql.append( " and  g.cat_id in(" ) ;
					sql.append("select c.cat_id from es_goods_cat");
					sql.append(" c where c.cat_path like '" + cat_path + "%')");
				}
				
			}
		}
	}

	
	
	public String getAuthor() {
		return "kingapex";
	}

	public String getId() {		
		return "catSearchFilter";
	}
	
	public String getName() {	
		return "商品分类筛选器";
	}

	public String getType() {
		return "searchFilter";
	}

	public String getVersion() {
		return "1.0";
	}
	
	public void perform(Object... params) {		

	}	
	
	public void register() {

	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
}
