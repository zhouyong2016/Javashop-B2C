package com.enation.app.shop.component.goodscore.plugin.params;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.IntrospectorCleanupListener;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.GoodsParam;
import com.enation.app.shop.core.goods.model.GoodsType;
import com.enation.app.shop.core.goods.model.support.ParamGroup;
import com.enation.app.shop.core.goods.plugin.AbstractGoodsPlugin;
import com.enation.app.shop.core.goods.plugin.IGoodsTabShowEvent;
import com.enation.app.shop.core.goods.service.GoodsTypeUtil;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.StringUtil;

/**
 * 商品自定义参数管理插件
 * @author kingapex
 * 2012-1-5下午4:53:09
 */
@Service("goodsParamsAdminPlugin")
public class GoodsParamsAdminPlugin extends AbstractGoodsPlugin implements IGoodsTabShowEvent {

	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	@Autowired
	private IGoodsTypeManager goodsTypeManager;
	
	/**
	 * -----------------------------------------------------
	 *                    处理插件页面的显示
	 * -----------------------------------------------------
	 */
	
	
	/**
	 * 返回添加时的页面
	 */
	@Override
	public String getAddHtml(HttpServletRequest request) {
		int catid = StringUtil.toInt(request.getParameter("catid"), true) ;
		Cat cat =goodsCatManager.getById(catid);
		int typeid = cat.getType_id();
		ParamGroup[] paramAr = this.goodsTypeManager.getParamArByTypeId(typeid); 
		
		
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
	 
		freeMarkerPaser.setPageName("params_input");
		freeMarkerPaser.putData("paramAr", paramAr);
		return freeMarkerPaser.proessPageContent();
	}
	
	
	
	
	/**
	 * 返回修改时的页面
	 */
	@Override
	public String getEditHtml(Map goods, HttpServletRequest request) {
		//读取参数信息
		ParamGroup[] type_paramAr = this.goodsTypeManager.getParamArByTypeId((Integer)goods.get("type_id")); 
		IntrospectorCleanupListener a;
		String params  = goods.get("params")==null ? "" : goods.get("params").toString();
		ParamGroup[] paramAr = GoodsTypeUtil.converFormString( params);// 处理参数
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
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setPageName("params_input");
		freeMarkerPaser.putData("paramAr", type_paramAr);
		freeMarkerPaser.putData("is_edit", true);
		return freeMarkerPaser.proessPageContent();
	}
	
	
	
	
	/**
	 * 处理商品添加前业务逻辑
	 */
	@Override
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {
		
		// 处理参数信息
		String[] paramnums = request.getParameterValues("paramnums");
		String[] groupnames = request.getParameterValues("groupnames");
		String[] paramnames = request.getParameterValues("paramnames");
		String[] paramvalues = request.getParameterValues("paramvalues");
		
		String params = goodsTypeManager.getParamString(paramnums, groupnames,
				paramnames, paramvalues);
		goods.put("params", params);
	}


	/**
	 * 处理商品修改前业务逻辑
	 */
	@Override
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request) {
		// 处理参数信息
		String[] paramnums = request.getParameterValues("paramnums");
		String[] groupnames = request.getParameterValues("groupnames");
		String[] paramnames = request.getParameterValues("paramnames");
		String[] paramvalues = request.getParameterValues("paramvalues");
		
		
		String params = goodsTypeManager.getParamString(paramnums, groupnames,
				paramnames, paramvalues);
		goods.put("params", params);
		 

	}
	
	

	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {
		 

	}

	@Override
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
	

	}




	@Override
	public String getTabName() {
	
		return "参数";
	}

	@Override
	public int getOrder() {
	
		return 9;
	}




}
