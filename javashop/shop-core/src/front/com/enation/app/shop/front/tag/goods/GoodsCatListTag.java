package com.enation.app.shop.front.tag.goods;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商品分类列表标签
 * @author kingapex
 *2013-8-20下午7:55:54
 */
@Component
@Scope("prototype")
public class GoodsCatListTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	/**
	 * 商品分类列表标签
	 * @param 需要传递catid参数
	 * @return 返回此分类下的所有子分类
	 * {@link Cat} 注意此对象的children属性存储了此分类的子类别。
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer catid = (Integer)params.get("catid");
		if(catid==null){
			catid=0;
		}
		List<Cat> catList =goodsCatManager.listAllChildren(catid);
		return catList;
	}
	


}
