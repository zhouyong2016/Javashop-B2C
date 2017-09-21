package com.enation.app.shop.core.goods.action;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.goods.service.IGoodsWarningStoreManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonResultUtil;

/**
 * 
 * (预警商品库存管理) 
 * @author zjplist-goods-warningstore
 * @version v1.0
 * @since v6.2
 * 2016年12月12日 下午5:23:30
 */
@Controller 
@Scope("prototype")
@RequestMapping("/shop/admin/goods-warning-store")
@Validated
public class GoodsWarningStoreController extends GridController {

	@Autowired
	private IGoodsWarningStoreManager goodsWarningStoreManager;
	
	/**
	 * 跳转至预警货品管理页面
	 * @param goodsStoreList 仓库列表,List
	 * @return 商品预警货品管理页面
	 */
	@RequestMapping(value="/list-goods-warningstore")
	public ModelAndView listWarningGoodsStore(String optype){
		ModelAndView view=new ModelAndView();
		view.addObject("optype", optype);
		view.setViewName("/shop/admin/goodswarningstore/goodswarningstore_list");
		return view;
	}
	/**
	 * 获取商品预警库存管理列表Json
	 * @param stype 搜索类型,INteger
	 * @param keyword 搜索关键字,String
	 * @param name 商品名称,String
	 * @param sn 商品编号,String
	 * @param depot_id 库房Id,Integer
	 * @return 商品库存管理列表Json
	 */
	@ResponseBody
	@RequestMapping(value="/list-goods-store-json")
	@SuppressWarnings("unchecked")
	public GridJsonResult listGoodsStoreJson(Integer stype,Integer depot_id,String sn,String name,String keyword){
		Map storeMap = new HashMap();
		storeMap.put("stype", stype);
		storeMap.put("keyword", keyword);
		storeMap.put("name", name);
		storeMap.put("sn", sn);
		depot_id = depot_id==null?0:depot_id;
		storeMap.put("depotid", depot_id);
		
		Page page=this.goodsWarningStoreManager.listGoodsStore(storeMap, this.getPage(), this.getPageSize(), null,this.getSort(),this.getOrder());
		return JsonResultUtil.getGridJson(page);
	}
	
	/**
	 * 获取预警库存维护对话框页面html;
	 * @param goodsid 商品Id,Integer
	 * @return
	 */
	@RequestMapping(value="/get-warning-store-dialog-html")
	public ModelAndView getStoreDialogHtml( @NotNull(message="商品id不能为空")Integer goodsid) {
		ModelAndView view=new ModelAndView();
		view.addObject("goodsid",goodsid);
		view.addObject("html", goodsWarningStoreManager.getWarningStoreHtml(goodsid));
		view.setViewName("/shop/admin/goodsstore/dialog");
		return view;
	}
}
