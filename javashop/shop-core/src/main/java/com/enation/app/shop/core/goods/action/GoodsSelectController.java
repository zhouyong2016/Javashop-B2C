package com.enation.app.shop.core.goods.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;

import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;
/**
 * 商品选择Action
 * @author fenlongli
 *
 */
@Controller
@RequestMapping("/shop/admin/goods-select")
public class GoodsSelectController extends GridController{

	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	@Autowired
	private IGoodsManager goodsManager;
	
	@Autowired
	private IProductManager productManager;
	/**
	 * 跳转至商品选择页面
	 * @return 商品选择页面
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Integer sing){
		ModelAndView view=new ModelAndView();
		view.addObject("sing", sing);
		view.setViewName("/shop/admin/goods/goods_opt");
		return view;
	}
	/**
	 * 获取商品分类列表
	 * @return 分类列表Json
	 */
	@ResponseBody
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/list-json")
	public String listJson() {
		List catList = goodsCatManager.listAllChildren(0);
		//String s = JSONArray.fromObject(catList).toString();
		return  JSONArray.fromObject(catList).toString();
	}
	/**
	 * 获取商品列表
	 * @param catid 商品分类Id,Integer
	 * @param goodslist 商品列表,List
	 * @return 商品列表Json
	 */
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/list-goods-by-id")
	public GridJsonResult listGoodsById(Integer catid){
		Map goodsMap = new HashMap();
		goodsMap.put("catid", catid);
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String keyword = request.getParameter("keyword");
		String stype = request.getParameter("stype");
		
		goodsMap.put("keyword", keyword);
		goodsMap.put("stype", StringUtil.toInt(stype,0));
		
		Page goodslist = goodsManager.searchGoods(goodsMap,this.getPage(),this.getPageSize(),null,null,null);
		return JsonResultUtil.getGridJson(goodslist);
	}

	/**
	 * 根据分类id查询货品
	 * @return
	 */
	@ResponseBody
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/list-product-by-cat-id")
	public GridJsonResult listProductByCatid(Integer catid){
		List list = productManager.listProductByCatId(catid);
		return JsonResultUtil.getGridJson(list);
	}
	
	
	
}
