package com.enation.app.shop.front.api.goods;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 商品静态页标签
 * @author fenlongli
 *
 */
@Scope("prototype")
@Controller 
@RequestMapping("/api/shop/goodsStatics")
public class GoodsStaticsApiController {
	
	@Autowired
	private IProductManager productManager;
	
	/**
	 * 根据商品Id获取货品库存
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-goods-store",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult getGoodsStore(Integer goods_id){
		
		return JsonResultUtil.getSuccessJson(productManager.getByGoodsId(goods_id).getEnable_store()+"");
	}
	/**
	 * 根据货品Id获取货品库存
	 * @param productid 货品Id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-product-store",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult getProductStore(Integer productid){
		return JsonResultUtil.getSuccessJson(productManager.get(productid).getEnable_store()+"");
	}
	
}
