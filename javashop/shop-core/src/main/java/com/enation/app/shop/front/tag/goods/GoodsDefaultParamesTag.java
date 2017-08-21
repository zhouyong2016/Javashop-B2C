package com.enation.app.shop.front.tag.goods;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.util.IntrospectorCleanupListener;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.GoodsParam;
import com.enation.app.shop.core.goods.model.support.ParamGroup;
import com.enation.app.shop.core.goods.service.GoodsTypeUtil;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 查询默认的参数信息
 * 根据分类id先查类型，根据类型查询参数的属性。
 * @author xulipeng
 * 2015年07月09日11:42:05
 */

@Component
public class GoodsDefaultParamesTag extends BaseFreeMarkerTag {

	private IGoodsCatManager goodsCatManager;
	private IGoodsTypeManager goodsTypeManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		ParamGroup[] type_paramAr ;
		Map goods =(Map)request.getAttribute("goods");
		if(params.get("type").equals(1)){
		
			int catid = StringUtil.toInt(request.getParameter("catid"), true) ;
			Cat cat =goodsCatManager.getById(catid);
			int typeid = cat.getType_id();
			type_paramAr = this.goodsTypeManager.getParamArByTypeId(typeid); 
			
		}else{
			
			type_paramAr = this.goodsTypeManager.getParamArByTypeId((Integer)goods.get("type_id")); 
			IntrospectorCleanupListener a;
			String parames  = goods.get("params")==null ? "" : goods.get("params").toString();
			ParamGroup[] paramAr = GoodsTypeUtil.converFormString( parames);// 处理参数
			Map<String,GoodsParam> temp=new HashMap<String,GoodsParam>();
			if(paramAr!=null && paramAr.length>0){
				for(int i=0;i<paramAr.length;i++){
					ParamGroup paramGroup=paramAr[i];
					List<GoodsParam> list=paramGroup.getParamList();
					if(list!=null){
						Iterator<GoodsParam> it=list.iterator();
						while(it.hasNext()){
							GoodsParam goodsParam=it.next();
							temp.put(paramGroup.getName()+"="+goodsParam.getName(), goodsParam);
						}
					}
				}
			}
			if(type_paramAr!=null && type_paramAr.length>0){
				for(int i=0;i<type_paramAr.length;i++){
					ParamGroup paramGroup=type_paramAr[i];
					List<GoodsParam> list=paramGroup.getParamList();
					if(list!=null && list.size()>0){
						for(int j=0;j<list.size();j++){
							GoodsParam goodsParam=list.get(j);
							if(temp.containsKey(paramGroup.getName()+"="+goodsParam.getName())){
								goodsParam=temp.get(paramGroup.getName()+"="+goodsParam.getName());
								list.set(j, goodsParam);
							}
						
						}
					}
					
				}
			}
		}
		if(type_paramAr==null){
			type_paramAr = new ParamGroup[0];
		}
		return type_paramAr;
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
