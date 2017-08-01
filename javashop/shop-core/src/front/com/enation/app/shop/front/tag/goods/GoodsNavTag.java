package com.enation.app.shop.front.tag.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 商品详细当前位置标签
 * @author whj
 * 2014-07-29下午14:44:41
 */
@Component
@Scope("prototype")
public class GoodsNavTag extends BaseFreeMarkerTag {
	private IGoodsCatManager goodsCatManager;

	
	/**
	 * 当前位置标签
	 * @param 无
	 * @return 返回值Map型，其值如下：
	 * isSearch:是否为搜索  true是    false否
	 * cat:商品类别{@link Cat}
	 */
	protected Object exec(Map params) throws TemplateModelException {
		Map goods  = (Map)ThreadContextHolder.getHttpRequest().getAttribute("goods");
		if(goods==null) throw new RuntimeException("参数显示挂件必须和商品详细显示挂件同时存在");
		
		StringBuffer navHtml = new StringBuffer();
		int catstr  = (Integer)goods.get("cat_id");
		Integer brandstr = (Integer)goods.get("brand_id");
		
		List<Cat> cats = this.goodsCatManager.getParents(catstr);
		String brandname =(String) goods.get("brand_name");
		Map result = new HashMap();
		
		navHtml.append("<span><a href=\"index.html\">首页</a></span>&gt;");
		
		//类别
		for(Cat cat : cats) {
			navHtml.append("<span><a href='goods_list.html?cat=");
			navHtml.append(cat.getCat_id());
			navHtml.append("'>");
			navHtml.append(cat.getName());
			navHtml.append("</a></span>");
			
			navHtml.append("&gt;");
		}
		
		if(brandstr != null && brandstr != 0){
			//品牌
			navHtml.append("<span><a href='goods_list.html?cat=");
			navHtml.append(catstr);
			navHtml.append("&brand=");
			navHtml.append(brandstr);
			navHtml.append("'>");
			navHtml.append(brandname);
			navHtml.append("</a></span>&gt;");
		}
		
		navHtml.append("<span class=\"last\">");
		navHtml.append(goods.get("name"));
		navHtml.append("</span>");
		
		result.put("navHtml", navHtml);
		return result;
	}



	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

}
