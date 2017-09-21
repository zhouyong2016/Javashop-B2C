package com.enation.app.shop.core.goods.action;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.enation.app.base.core.model.PluginTab;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.model.support.GoodsEditDTO;
import com.enation.app.shop.core.goods.plugin.GoodsPluginBundle;
import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.goods.service.ITagManager;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * @author LiFenLong 4.0版本改造  2014-4-1
 * @author Kanon 2016.2.15;6.0版本改造
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Scope("prototype")
@Controller 
@RequestMapping("/shop/admin/goods")
public class GoodsController extends GridController {
	@Autowired
	protected IGoodsCatManager goodsCatManager;
	
	@Autowired
	protected IBrandManager brandManager;
	
	@Autowired
	protected IGoodsManager goodsManager;
	
	@Autowired
	private ICartManager cartManager;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private GoodsPluginBundle goodsPluginBundle;
	
	@Autowired
	private ITagManager tagManager;

	/**
	 * 跳转选择添加商品的分类
	 * @return 选择添加商品的分类页面
	 */
	@RequestMapping(value="/select-cat")
	public String selectCat() {
		  return "/shop/admin/goods/select_cat";
	}
	/**
	 * 商品搜索
	 * @author xulipeng 2014年5月14日16:22:13
	 * @param stype 搜索类型,Integer
	 * @param keyword 关键字,String
	 * @param name 商品名称,String
	 * @param sn 商品编号,String
	 * @param catid 商品分类Id,Integer
	 * @param page 页码,Integer
	 * @param PageSize 每页显示数量,Integer
	 * @param sort 排序,String
	 * @return 商品搜索json
	 */
	@ResponseBody
	@RequestMapping(value="/search-goods")
	public GridJsonResult searchGoods(Integer catid,Integer stype,String keyword,String name,String sn) {
		Map goodsMap = new HashMap();
		if(stype!=null){
			if(stype==0){
				goodsMap.put("stype", stype);
				goodsMap.put("keyword", keyword);
			}else if(stype==1){
				goodsMap.put("stype", stype);
				goodsMap.put("name", name);
				goodsMap.put("sn", sn);
				goodsMap.put("catid", catid);
			}
		}
		webpage =this.goodsManager.searchGoods(goodsMap, this.getPage(), this.getPageSize(), null,this.getSort(),this.getOrder());
		
		return JsonResultUtil.getGridJson(webpage);
	}

	/**
	 * 商品列表
	 * @param brand_id 品牌Id,Integer
	 * @param catid 商品分类Id,Integer
	 * @param name 商品名称,String
	 * @param sn 商品编号,String 
	 * @param tagids 商品标签Id,Integer[]
	 * @return 商品列表页
	 */
	@RequestMapping(value="/list")
	public ModelAndView list() {
		ModelAndView view=this.getGridModelAndView();
		String market_enable=ThreadContextHolder.getHttpRequest().getParameter("market_enable");
		view.addObject("tagList", tagManager.listMap());
		view.addObject("brandList",brandManager.list());
		view.addObject("optype","no");
		view.addObject("market_enable",market_enable);
		view.setViewName("/shop/admin/goods/goods_list");

		return view;
	}

	/**
	 * @author LiFenLong
	 * 2014年5月14日18:09  xulipeng  修改
	 * @param stype 搜索类型,Integer
	 * @param keyword 搜索关键字,String
	 * @param catid 商品分类Id,Integer
	 * @param name 商品名称,String
	 * @param sn 商品编号,String 
	 * @return 商品列表页json
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(Integer market_enable, Integer catid,Integer stype,String keyword,String name,String sn) {

		Map goodsMap = new HashMap();
		if(stype!=null){
			if(stype==0){
				goodsMap.put("stype", stype);
				goodsMap.put("keyword", keyword);
			}else if(stype==1){
				goodsMap.put("stype", stype);
				goodsMap.put("name", name);
				goodsMap.put("sn", sn);
				goodsMap.put("catid", catid);
			}
		}
		goodsMap.put("market_enable", market_enable);
		webpage =this.goodsManager.searchGoods(goodsMap, this.getPage(), this.getPageSize(), null,this.getSort(),this.getOrder());
		
		return JsonResultUtil.getGridJson(webpage);
	}

	/**
	 * 所有的商品分类
	 * @param catList 商品分类列表,List
	 * @return 商品分类页面
	 */
	@RequestMapping(value="/get-cat-tree")
	public ModelAndView getCatTree(List catList) {
		
		ModelAndView view=new ModelAndView();
		view.addObject("catList", this.goodsCatManager.listAllChildren(0));
		view.setViewName("/shop/admin/cat/select");
		return view;
	}

	/**
	 * 跳转至商品回收站列表
	 * @return 商品回收站列表
	 */
	@RequestMapping(value="/trash-list")
	public ModelAndView trashList() {
		ModelAndView view =getGridModelAndView();
		view.setViewName("/shop/admin/goods/goods_trash");
		return view;
	}
	/**
	 * 获取商品回收站列表json
	 * @param name 商品名称,String
	 * @param sn 商品编号,String
	 * @param order 排序,String
	 * @return 商品回收站列表json
	 */
	@ResponseBody
	@RequestMapping(value="/trash-list-json")
	public GridJsonResult trashListJson(String name,String sn,String order){
		this.webpage = this.goodsManager.pageTrash(name, sn, order,
				this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);
	}

	/**
	 * 删除商品
	 * @author LiFenLong
	 * @param goods_id 商品Id数组,Integer[]
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer[] goods_id) {
		if(EopSetting.IS_DEMO_SITE){
			for(Integer gid : goods_id){
				if(gid<=261){
					return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				}
			}
		}
		try {
			if (goods_id != null)
				for (Integer goodsid : goods_id) {
					if (cartManager.checkGoodsInCart(goodsid)) {

						return JsonResultUtil.getErrorJson("删除失败，此商品已加入购物车");
					
					}
					if (orderManager.checkGoodsInOrder(goodsid)) {
						return JsonResultUtil.getErrorJson("删除失败，此商品已经下单！");
					}
				}
			this.goodsManager.delete(goods_id);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException e) {
			logger.error("商品删除失败", e);
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}

	/**
	 * 还原商品
	 * @param goods_id 商品Id数组,Integer[]
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/revert")
	public JsonResult revert(Integer[] goods_id) {
		try {
			this.goodsManager.revert(goods_id);
			return JsonResultUtil.getSuccessJson("还原成功");
		} catch (RuntimeException e) {
			logger.error("商品还原失败", e);
			return JsonResultUtil.getErrorJson("还原失败");
		}
	}

	/**
	 * 清除商品
	 * @param goods_id 商品Id数组,Integer[]
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/clean")
	public JsonResult clean(Integer[] goods_id) {
		try {
			this.goodsManager.clean(goods_id);
			return JsonResultUtil.getSuccessJson("清除成功");
		} catch (RuntimeException e) {
			logger.error("商品清除失败",e);
			return JsonResultUtil.getErrorJson("清除失败");
		}
	}

	/**
	 * 跳转到商品添加页
	 * @param actionName 添加商品方法,String
	 * @param is_edit 是否为修改商品,boolean
	 * @param pluginTabs 商品tab标题List,List
	 * @param pluginHtmls 商品添加内容List,List
	 * @return 商品添加页
	 */
	@RequestMapping(value="/add")
	public ModelAndView add() {
		
		ModelAndView view=new ModelAndView();
		
		view.addObject("actionName","goods!saveAdd.do");
		view.addObject("is_edit", false);
		view.addObject("pluginTabs", this.goodsPluginBundle.onFillAddInputData());
		
		view.setViewName("/shop/admin/goods/goods_input");
		
		return view;
	}


	/**
	 * 跳转到商品修改页
	 * @param catList 商品分类列表,List
	 * @param actionName 修改商品方法,String
	 * @param is_edit 是否为修改商品,boolean
	 * @param goodsView 商品信息,Map
	 * @param pluginTabs 商品tab标题List,List
	 * @param pluginHtmls 商品添加内容List,List
	 * @return 商品编辑页
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer goodsId) {
		
		GoodsEditDTO editDTO=goodsManager.getGoodsEditData(goodsId);
		
		List<PluginTab> tabList  = editDTO.getTabs();
		
		
		ModelAndView view=new ModelAndView();
		view.addObject("is_edit", true);
		view.addObject("catList", goodsCatManager.listAllChildren(0));
		view.addObject("goodsView", editDTO.getGoods());
		view.addObject("pluginTabs",tabList);
		
		view.setViewName("/shop/admin/goods/goods_input");
		
		return view;
	}

	/**
	 * 保存商品添加
	 * @param goods 商品,Goods
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(Goods goods) {
		try {
			if(goods.getName()!=null&&goods.getName().trim().length()==0){
				return JsonResultUtil.getErrorJson("商品名称为空");
			}
			goodsManager.add(goods);

			JsonResult jsonRetult=new JsonResult();
			jsonRetult.setData(goods.getGoods_id());
			jsonRetult.setMessage("商品添加成功！");
			jsonRetult.setResult(1);
			return jsonRetult;

		} catch (RuntimeException e) {
			this.logger.error("添加商品出错", e);
			return JsonResultUtil.getErrorJson("添加商品出错" + e.getMessage());
		}

	}
	
	/**
	 * 保存商品修改
	 * @param goods 商品,Goods
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(Goods goods) {
		try {
			if(goods.getName()!=null&&goods.getName().trim().length()==0){
				return JsonResultUtil.getErrorJson("商品名称为空");
			}
			if(goods.getMarket_enable()==null){
				goods.setMarket_enable(0);
			}
			goodsManager.edit(goods);
			JsonResult jsonRetult=new JsonResult();
			jsonRetult.setResult(1);
			jsonRetult.setData(goods.getGoods_id());
			jsonRetult.setMessage("商品修改成功");
			
			return jsonRetult;
			

		} catch (RuntimeException e) {
			this.logger.error("修改商品出错", e);
			return JsonResultUtil.getErrorJson("修改商品出错" + e.getMessage());
		}
	}

	/**
	 * 更新商品上架状态
	 * @param goodsId 商品Id,Integer
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/update-market-enable")
	public JsonResult updateMarketEnable(Integer goodsId) {
		try {
			this.goodsManager.updateField("market_enable", 1, goodsId);
			return JsonResultUtil.getSuccessJson("更新上架状态成功");
		} catch (RuntimeException e) {
			logger.error("商品更新上架失败", e);
			return JsonResultUtil.getErrorJson("商品更新上架失败");
		}
	}
	
	/**
	 * 添加草稿商品
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/add-draft-goods")
	public JsonResult addDraftGoods(Goods goods){
		try {
			if(goods.getName()==null || goods.getName().equals("")){
				goods.setName("无标题");
			}
			goods.setMarket_enable(2);
			goodsManager.add(goods);
			
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("goods_id", goods.getGoods_id());
			map.put("market_enable", goods.getMarket_enable());
			
			return JsonResultUtil.getObjectJson(map);

		} catch (RuntimeException e) {
			this.logger.error("添加商品出错", e);
			return JsonResultUtil.getErrorJson("添加商品出错" + e.getMessage());
		}
	}
	
	/**
	 * 修改草稿商品
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/edit-draft-goods")
	public JsonResult editDraftGoods(Goods goods){
		try {
			
			if(goods.getName()==null || goods.getName().equals("")){
				goods.setName("无标题");
			}
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String previewGoodsId = request.getParameter("preview_goods_id");
			
			if(!StringUtil.isEmpty(previewGoodsId)){
				goods.setGoods_id(Integer.parseInt(previewGoodsId));
			}
			goods.setMarket_enable(2);
			Map goodsMap = goodsManager.get(goods.getGoods_id());
			if(goodsMap.size()==0){
				return JsonResultUtil.getErrorJson("草稿商品已删除");
			}
			if(goodsMap!=null){
				if(goodsMap.get("market_enable").equals(1)){//已经是上架状态
					goods.setMarket_enable(1);
				}else{
					goodsManager.edit(goods);
				}
			}
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("goods_id", goods.getGoods_id());
			map.put("market_enable", goods.getMarket_enable());
			
			return JsonResultUtil.getObjectJson(map);
			
		} catch (RuntimeException e) {
			this.logger.error("修改商品出错", e);
			return JsonResultUtil.getErrorJson("修改商品出错" + e.getMessage());
			
		}
	}
	/**
	 * 根据sn获取商品信息
	 * @param sn 商品sn
	 * @return 商品信息
	 */
	@ResponseBody
	@RequestMapping(value="/search-goods-by-sn")
	public JsonResult searchGoodsBySn() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String sn = request.getParameter("sn");
			/**进行必要判断*/
			if(sn==null||sn==""){
				return JsonResultUtil.getErrorJson("商品sn为空");
			}
			/**通过sn获取商品信息*/
			Goods goodBySn = goodsManager.getGoodBySn(sn);
			return JsonResultUtil.getObjectJson(goodBySn);
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("获取商品信息出错" + e.getMessage());
		}
		
	}
	
}
