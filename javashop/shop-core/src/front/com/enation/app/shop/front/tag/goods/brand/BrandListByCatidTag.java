package com.enation.app.shop.front.tag.goods.brand;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Brand;
import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


@Component
@Scope("prototype")
public class BrandListByCatidTag extends BaseFreeMarkerTag{

	@Autowired
	private IBrandManager brandManager;
	
	/**
	 * 根据tagid获得该分类下的品牌列表
	 */
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer tagid  =(Integer)params.get("tagid");
		List<Brand> brandList  =brandManager.listBrands(tagid);
		return brandList;
	}

}
