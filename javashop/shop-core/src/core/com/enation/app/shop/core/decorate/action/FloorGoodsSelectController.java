package com.enation.app.shop.core.decorate.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.core.decorate.service.IFloorManager;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

@Controller
@RequestMapping("/core/admin/floor-goods-select")
public class FloorGoodsSelectController extends GridController{
	
	@Autowired
	private IGoodsManager goodsManager;
	
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
}
