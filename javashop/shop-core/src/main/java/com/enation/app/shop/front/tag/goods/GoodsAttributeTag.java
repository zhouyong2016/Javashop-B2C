package com.enation.app.shop.front.tag.goods;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品属性标签
 * @author kingapex
 *2013-8-1上午8:53:50
 */
@Component
@Scope("prototype")
public class GoodsAttributeTag extends BaseFreeMarkerTag {
	
	/**
	 * 商品属性标签
	 * 特殊说明：调用商品属性标签前，必须先调用商品基本信息标签
	 * @param 无
	 * @return 商品属性，类型Map
	 * 返回的map的key是属性索引由p1-p21，通过这个可以显示商品属性，如
	 * 假设变量名为attr，则${attr.p1}可以输出属性1.
	 * 注：属性是在商品类型管理中，自定义的属性
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request  = this.getRequest();
		Map goods =(Map)request.getAttribute("goods");
		if(goods==null) throw new TemplateModelException("调用商品属性标签前，必须先调用商品基本信息标签");
	 
		return goods.get("propMap");
		
	}
 
	
	
}
