package com.enation.app.shop.front.tag.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Attribute;
import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.GoodsType;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 查询默认的属性信息
 * 根据分类id先查类型，根据类型查默认的属性。
 * @author xulipeng
 * 2015年07月09日11:42:05
 */

@Component
public class GoodsDefaultPropsTag extends BaseFreeMarkerTag {

	private IGoodsCatManager goodsCatManager;
	private IGoodsTypeManager goodsTypeManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Map goods =(Map)request.getAttribute("goods");
		List attrList = new ArrayList();
		
		if(params.get("type").equals(1)){
			int catid = StringUtil.toInt(request.getParameter("catid"), true);
			Cat cat =goodsCatManager.getById(catid);
			int typeid = cat.getType_id();
			
			attrList = this.goodsTypeManager.getAttrListByTypeId(typeid);
		}else{
			Integer typeid  = Integer.valueOf( "" +goods.get("type_id") );
			GoodsType goodsType = this.goodsTypeManager.getById(typeid);
			
			if(goodsType.getHave_prop()==1){
				//读取属性信息
				Map propMap = new HashMap();
				
				for(int i=0;i<20;i++){
					String value = goods.get("p" + (i+1))==null ? "" : goods.get("p" + (i+1)).toString();
					propMap.put("p"+i,value);
				}
				
				goods.put("propMap",propMap);
				attrList  = proessProps(goods,typeid);
			}
		}
		return attrList;
	}
	
	/**
	 *  处理属性信息
	 * @param goodsView
	 * @return
	 */
	private List proessProps(Map goodsView,Integer typeid) {
		

		List  propList = this.goodsTypeManager.getAttrListByTypeId( typeid );
		if(propList==null) return propList;
		Map<String, String> propMap = (Map)goodsView.get("propMap");
		for (int i = 0; i < propList.size(); i++) {
			Attribute attribute = (Attribute) propList.get(i);
			String value = propMap.get("p" + i);
			attribute.setValue(value);
		}
		return propList;
	}


	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}


	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}


	public IGoodsTypeManager getGoodsTypeManager() {
		return goodsTypeManager;
	}


	public void setGoodsTypeManager(IGoodsTypeManager goodsTypeManager) {
		this.goodsTypeManager = goodsTypeManager;
	}
	
	

}
