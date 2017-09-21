package com.enation.app.shop.front.tag.goods.brand;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 根据品牌标签ID获取品牌列表
 * @author Kanon
 * @since v6.3.1
 * @version1.1
 */
@Component
@Scope("prototype")
public class BrandListByTagIdTag extends BaseFreeMarkerTag{

	@Autowired
	private IBrandManager brandManager;
	
	/**
	 * 根据tagId获得该分类下的品牌列表
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return brandManager.listBrands(StringUtil.toInt(params.get("tagId").toString(), 0));
	}

}
