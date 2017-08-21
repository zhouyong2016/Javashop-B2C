package com.enation.app.shop.core.goods.action;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 商品规格操作action
 * 
 * @author kingapex 2010-3-8下午04:22:31
 */
@Controller
@RequestMapping("/shop/admin/goods-spec")
public class GoodsSpecController {
	
	@Autowired
	private IOrderManager orderManager;
	

	@Autowired
	private ISpecManager specManager;
	
	/**
	 * 单店系统后台删除商品规格
	 * @author xulipeng
	 * 2015-11-05 16:33:56
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/delete-spec")
	public JsonResult deleteSpec(Integer productid){
		
		boolean isinorder = this.orderManager.checkProInOrder(productid);
		//通过productid获取order
//		String orderstr=this.orderManager.getOrderByProductId(productid);
		if (isinorder) {
			return JsonResultUtil.getSuccessJson("此货品已经有顾客购买，不能删除规格!");
		} else {
			return JsonResultUtil.getErrorJson("无订单使用");
		}
	}
	
	
	@ResponseBody
	@RequestMapping(value="/close-spec")
	public JsonResult closeSpec(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String action = request.getParameter("action");
		if ("check-pro-in-order".equals(action)) {
			int productid = StringUtil.toInt(request.getParameter("productid"),	true);
			boolean isinorder = this.orderManager.checkProInOrder(productid);
			if (isinorder) {
				return JsonResultUtil.getSuccessJson("有订单使用");
			} else {
				return JsonResultUtil.getErrorJson("无订单使用");
			}
		} else if ("check-goods-in-order".equals(action)) {
			int goodsid = StringUtil.toInt(request.getParameter("goodsid"),	true);
			boolean isinorder = this.orderManager.checkGoodsInOrder(goodsid);
			if (isinorder) {
				return JsonResultUtil.getSuccessJson("有订单使用");
			} else {
				return JsonResultUtil.getErrorJson("无订单使用");
			}
		}
		return null;
	}
	
}
