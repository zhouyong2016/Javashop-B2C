package com.enation.app.shop.component.search.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Brand;
import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.plugin.search.IGoodsFrontSearchFilter;
import com.enation.app.shop.core.goods.plugin.search.SearchSelector;
import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.app.shop.core.goods.utils.BrandUrlUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 品牌过滤器
 * @author kingapex
 *
 */
@Component
public class BrandSearchFilter extends AutoRegisterPlugin implements
		IGoodsFrontSearchFilter {

	private IBrandManager brandManager;
	public void filter(StringBuffer sql,Cat cat) {
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String brandid  = request.getParameter("brand");
		if (!StringUtil.isEmpty(brandid) && !"0".equals(brandid)) {
			sql.append(" and g.brand_id=" + brandid);
		}
	} 

	

	 
	@Override
	public void createSelectorList(Map map,Cat cat) {
		List<Brand> allbrand = brandManager.list();
		List<SearchSelector> selectorList = new ArrayList<SearchSelector>();

		//此分类的品牌列表
		List<Brand> brandList = null;
		
		if(cat!=null){
			brandList  = this.brandManager.listByTypeId(cat.getType_id());
		}else{
			brandList  =allbrand;
		}
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String servlet_path = request.getServletPath();
 
 
		for (Brand brand : brandList) {
			SearchSelector selector = new SearchSelector();
			selector.setName(brand.getName());
			//因首页可以进入品牌搜索列表，所以改为固定html页
			String brandurl ="goods_list.html" +"?"+ BrandUrlUtils.createBrandUrl(""+brand.getBrand_id());
			selector.setUrl(brandurl);
			selector.setValue(brand.getLogo());
			selectorList.add(selector);
		}
		map.put("brand", selectorList);
		
		List selectedList  =BrandUrlUtils.createSelectedBrand(allbrand);
		map.put("selected_brand", selectedList); //已经选择的品牌
		
	}
	
	

	public String getAuthor() {

		return "kingapex";
	}

	public String getId() {

		return "brandSearchFilter";
	}

	public String getName() {

		return "品牌搜索过虑器";
	}

	public String getType() {

		return "goodssearch";
	}

	public String getVersion() {

		return "1.0";
	}

	public void perform(Object... params) {

	}

	public void register() {

	}

	public IBrandManager getBrandManager() {
		return brandManager;
	}

	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}

}
