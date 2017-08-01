/**
 * 
 */
package com.enation.app.shop.core.goods.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.shop.core.goods.model.Brand;
import com.enation.app.shop.core.goods.plugin.search.SearchSelector;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 品牌url工具
 * @author kingapex
 *2015-4-28
 */
public class BrandUrlUtils {


	/**
	 * 生成品牌的url
	 * @param brandid
	 * @return
	 */
	public static  String createBrandUrl(String brandid){
		Map<String,String> params=ParamsUtils.getReqParams();
		 
		params.put("brand",brandid);
		
		return ParamsUtils.paramsToUrlString(params);
	}
	
	/**
	 * 生成没有品牌的url
	 * @return
	 */
	private static String createUrlWithOutBrand(){
		Map<String,String> params=ParamsUtils.getReqParams();
		 
		params.remove("brand");
		
		return ParamsUtils.paramsToUrlString(params);
	}
	
	
	
	
	/**
	 * 生成已经选择的品牌
	 * @param brandList
	 * @return
	 */
	public static  List<SearchSelector> createSelectedBrand(List<Brand> brandList){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String servlet_path = request.getServletPath();
		List<SearchSelector> selectorList  = new ArrayList();
		String brandid  = request.getParameter("brand");
		if(StringUtil.isEmpty(brandid)){
			return selectorList; 
		}
		int bid= StringUtil.toInt( brandid ,0);
		String brandname ="";
		Brand findbrand  = findBrand(brandList, bid);
		if(findbrand!=null){
			brandname = findbrand.getName();
		}
		
		SearchSelector selector = new SearchSelector();
		selector.setName(brandname);
		String url =servlet_path +"?"+ createUrlWithOutBrand();
		selector.setUrl(url);
		selectorList.add(selector);
		return selectorList; 
	} 
	/**
	 * 根据id查找brand
	 * @param brandList
	 * @param brandid
	 * @return
	 */
	public static Brand findBrand(List<Brand> brandList,int brandid){
		
		for (Brand brand : brandList) {
			if(brand.getBrand_id() == brandid) return brand;
		}
		return null;
	}
}
