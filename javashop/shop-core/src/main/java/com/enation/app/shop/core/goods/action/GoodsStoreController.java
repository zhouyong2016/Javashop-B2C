package com.enation.app.shop.core.goods.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;

import com.enation.app.shop.core.goods.service.IGoodsStoreManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonResultUtil;

/**
 * 商品库存管理
 * @author kingapex
 * @author Kanon 2016-2-22;6.0版本改造
 */
@Controller 
@Scope("prototype")
@RequestMapping("/shop/admin/goods-store")
public class GoodsStoreController extends GridController {

	@Autowired
	private IGoodsStoreManager goodsStoreManager;
	
	
	/**
	 * 跳转至商品库存管理页面
	 * @param goodsStoreList 仓库列表,List
	 * @return 商品库存管理页面
	 */
	@RequestMapping(value="/list-goods-store")
	public ModelAndView listGoodsStore(String optype){
		ModelAndView view=new ModelAndView();
		view.addObject("optype", optype);
		view.setViewName("/shop/admin/goodsstore/goodsstore_list");
		return view;
	}
	/**
	 * 获取商品库存管理列表Json
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
		
		Page page=this.goodsStoreManager.listGoodsStore(storeMap, this.getPage(), this.getPageSize(), null,this.getSort(),this.getOrder());
		return JsonResultUtil.getGridJson(page);
	}
	/**
	 * 获取所有的仓库Json
	 * @return 所有的仓库Json
	 */
	@ResponseBody
	@RequestMapping(value="/list-store-json")
	@SuppressWarnings("rawtypes")
	public String listStoreJson(){
		List list = this.goodsStoreManager.getStoreList();
		return JSONArray.fromObject(list).toString();
	}

	/**
	 * 获取库存维护对话框页面html;
	 * @param goodsid 商品Id,Integer
	 * @return
	 */
	@RequestMapping(value="/get-store-dialog-html")
	public ModelAndView getStoreDialogHtml( Integer goodsid) {
		ModelAndView view=new ModelAndView();
		view.addObject("goodsid",goodsid);
		view.addObject("html", goodsStoreManager.getStoreHtml(goodsid));
		view.setViewName("/shop/admin/goodsstore/dialog");
		return view;
	}

	/**
	 * 获取进货对话框页面html
	 * @param goodsid 商品Id,Integer
	 */
	@RequestMapping(value="/get-stock-dialog-html")
	public ModelAndView getStockDialogHtml( Integer goodsid) {
		ModelAndView view=new ModelAndView();
		view.addObject("goodsid",goodsid);
		view.addObject("html", this.goodsStoreManager.getStockHtml(goodsid));
		view.setViewName("/shop/admin/goodsstore/dialog");
		return view;
		
	}

	/**
	 * 获取出货对话框页面html
	 * @param goodsid 商品Id,Integer
	 * @return
	 */
	@RequestMapping(value="/get-ship-dialog-html")
	public ModelAndView getShipDialogHtml( Integer goodsid) {
		ModelAndView view=new ModelAndView();
		view.addObject("goodsid",goodsid);
		view.addObject("html",this.goodsStoreManager.getShipHtml(goodsid));
		view.setViewName("/shop/admin/goodsstore/dialog");
		return view;
	}
	
	/**
	 * 获取报警设置对话框页面html
	 * @param goodsid 商品Id
	 */
	@RequestMapping(value="/get-warn-dialog-html")
	public ModelAndView getWarnDialogHtml( Integer goodsid) {
		ModelAndView view=new ModelAndView();
		view.addObject("goodsid",goodsid);
		view.addObject("html",this.goodsStoreManager.getWarnHtml(goodsid));
		view.setViewName("/shop/admin/goodsstore/dialog");
		return view;
		
	}
	/**
	 * 保存库存维护
	 * @param goodsid 商品Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-store")
	public JsonResult saveStore( Integer goodsid) {
		try {
			goodsStoreManager.saveStore(goodsid);
			return JsonResultUtil.getSuccessJson("保存商品库存成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("保存商品库存出错", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}

	/**
	 * 保存进货
	 * @param goodsid 商品Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-stock")
	public JsonResult saveStock( Integer goodsid) {
		try {
			goodsStoreManager.saveStock(goodsid);
			return JsonResultUtil.getSuccessJson("保存进货成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("保存进货出错", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}

	

	/**
	 * 保存报警
	 * @param goodsid 商品Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-warn")
	public JsonResult saveWarn( Integer goodsid) {
		try {
			goodsStoreManager.saveWarn(goodsid);
			return JsonResultUtil.getSuccessJson("保存报警成功");
		} catch (RuntimeException e) {
			this.logger.error("保存报警出错", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}

	/**
	 * 保存出货
	 * @param goodsid 商品Id
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-ship")
	public JsonResult saveShip( Integer goodsid) {
		try {
			goodsStoreManager.saveShip(goodsid);
			return JsonResultUtil.getSuccessJson("保存出货成功");
		} catch (RuntimeException e) {
			this.logger.error("保存出货出错", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
	
}
