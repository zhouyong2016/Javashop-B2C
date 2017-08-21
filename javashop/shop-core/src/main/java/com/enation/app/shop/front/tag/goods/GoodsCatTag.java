package com.enation.app.shop.front.tag.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;
/**
 * 商品分类标签
 * @author lina
 * 2013-12-19
 */
@Component
@Scope("prototype")
public class GoodsCatTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	/**
	 * 商品分类数据
	 * @param parentid 上一级id
	 * @param catimage 是否显示类别图片 on:显示类别图片  off:不显示类别图片
	 * @return 分类数据信息，Map型，其中的key结构为:
	 * showimg:String型,是否显示分类图片
	 * cat_tree:List型,分类列表数据
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer parentid=(Integer) params.get("parentid");
		if(parentid==null){
			parentid = 0;
		}
		List<Cat> cat_tree =  goodsCatManager.listAllChildren(parentid);
		String catimage = (String) params.get("catimage");
		boolean showimage  = catimage!= null &&catimage.equals("on") ?true:false;
		
		String imgPath="";
		if(!cat_tree.isEmpty()){
			for(Cat cat:cat_tree){
				
				if (cat.getImage() != null && !StringUtil.isEmpty(cat.getImage())) {
					imgPath = StaticResourcesUtil.convertToUrl(cat.getImage());
					cat.setImage(imgPath);
				}
				
			}
		}
		
		Map<String, Object> data =new HashMap();
		data.put("showimg", showimage);//是否显示分类图片
		data.put("cat_tree", cat_tree);//分类列表数据
		return data;
	}
	

	
}
