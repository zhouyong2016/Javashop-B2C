package com.enation.app.shop.component.goodscore.plugin.props;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Attribute;
import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.GoodsType;
import com.enation.app.shop.core.goods.plugin.AbstractGoodsPlugin;
import com.enation.app.shop.core.goods.plugin.IGoodsTabShowEvent;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.StringUtil;

/**
 * 商品自定义属性管理插件
 * @author kingapex
 * 2012-1-5下午4:52:59
 */
@Component
public class GoodsPropsAdminPlugin extends AbstractGoodsPlugin implements IGoodsTabShowEvent {
	
	@Autowired
	private IDaoSupport daoSupport; 
	
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
	 * 返回添加页面的HTML
	 */
	@Override
	public String getAddHtml(HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		
		int catid = StringUtil.toInt(request.getParameter("catid"), true) ;
		Cat cat =goodsCatManager.getById(catid);
		int typeid = cat.getType_id();
		GoodsType goodsType = goodsTypeManager.get(typeid);
		
		List attrList = this.goodsTypeManager.getAttrListByTypeId(typeid);
		
		if(goodsType.getJoin_brand()==1){
			List brandList = this.goodsTypeManager.listByTypeId(typeid);
			freeMarkerPaser.putData("brandList", brandList);
		}
		
		freeMarkerPaser.setPageName("props_input");
		freeMarkerPaser.putData("attrList", attrList);
		
		return freeMarkerPaser.proessPageContent();
	}

	
	
	/**
	 * 返回修改页面的HTML
	 */
	@Override
	public String getEditHtml(Map goods, HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		
		if( goods.get("type_id")==null ) {
			return ("类型id为空");
		}
		Integer typeid =null;
		try{
			 typeid  = Integer.valueOf( "" +goods.get("type_id") );
		}catch(NumberFormatException e){
			return ("类型不为数字");
		}
		
		GoodsType goodsType = this.goodsTypeManager.getById(typeid);
		if(goodsType.getJoin_brand()==1){
			List brandList = this.goodsTypeManager.listByTypeId(typeid);
			freeMarkerPaser.putData("brandList", brandList);
		}
		
		if(goodsType.getHave_prop()==1){
			//读取属性信息
			Map propMap = new HashMap();
			
			for(int i=0;i<20;i++){
				String value = goods.get("p" + (i+1))==null ? "" : goods.get("p" + (i+1)).toString();
				propMap.put("p"+i,value);
			}
			
			goods.put("propMap",propMap);
			List propList  = proessProps(goods,typeid);
			freeMarkerPaser.putData("attrList", propList);
		}
		
		freeMarkerPaser.setPageName("props_input");
		
		return freeMarkerPaser.proessPageContent();
	}
	
	
	
	
	
	
	/**
	 * -----------------------------------------------------
	 *                 处理保存时的业务逻辑
	 * -----------------------------------------------------
	 */	
	
	
	
	/**
	 * 商品添加前业务逻辑
	 */
	@Override
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {
		
		//去掉此处代码是为了在添加商品时商品的计价单位有值，有以下方法无法得到unit的值		修改人：DMRain 2015-12-18
//		String unit = request.getParameter("unit");
//		goods.put("unit", unit);
	}
 
	
	/**
	 * 保存更新前的业务处理
	 * 将属性值保存
	 */
	@Override
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request) {
		String[] propvalues= request.getParameterValues("propvalues");  // 值数组

		try{
			Integer goods_id  = Integer.valueOf( "" + goods.get("goods_id"));
			saveProps(goods_id,propvalues);
			
			//去掉此处代码是为了在添加商品时商品的计价单位有值，有以下方法无法得到unit的值		修改人：DMRain 2015-12-18
//			String unit = request.getParameter("unit");
//			goods.put("unit", unit);
		}
		catch(NumberFormatException e){
			throw new  RuntimeException("商品id格式错误");
		}	
	}

	
	/**
	 * 商品更新后的业务处理
	 */
	@Override
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
		//不做任何处理
	}
	
	
	
	/**
	 * 商品添加后业务处理
	 */
	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)  {
		
		 String[] propvalues= request.getParameterValues("propvalues");  // 值数组

			try{
				Integer goods_id  = Integer.valueOf( "" + goods.get("goods_id"));
				saveProps(goods_id,propvalues);	
			}
			catch(NumberFormatException e){
				throw new  RuntimeException("商品id格式错误");
			}
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 商品添加时处理相关的属性信息 是添加完后更新, 因为可能存在 有p1 到p20 个字段,所以更新要比较添加方便
	 * 
	 * @param goodsid
	 * @param propvalues
	 */
	private void saveProps(int goodsid, String[] propvalues) {
		if(propvalues!=null && propvalues.length>0){
			HashMap fields = new HashMap();
			int length = propvalues.length;
			length = length > 20 ? 20 : length; // 只支持20个自定义属性

			// 循环所有属性,按p_个数 为字段名存在 goods表中
			// 字段中存的是 值,当是下拉框时也存的是值,并不是属性的id
			for (int i = 0; i < length; i++) {
				String value = propvalues[i];
				fields.put("p" + (i + 1), value);
			}

			// 更新这个商品的属性信息
			this.daoSupport.update("es_goods", fields, "goods_id=" + goodsid);
		}
		

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
	
	
	

	@Override
	public String getTabName() {
		return "属性";
	}

	@Override
	public int getOrder() {
		return 7;
	}

}
