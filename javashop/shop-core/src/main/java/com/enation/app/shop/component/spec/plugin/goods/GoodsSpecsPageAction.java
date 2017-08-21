/**
 * 
 */
package com.enation.app.shop.component.spec.plugin.goods;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 商品规格页面获取action
 * @author kingapex
 * @version [v1.0 2016年3月2日]
 * @since v5.3
 */
@Controller
@RequestMapping("/shop/admin/goods-specs")
public class GoodsSpecsPageAction  {

	@Autowired
	private IGoodsManager goodsManager;
	
	@Autowired
	private GoodsSpecPlugin goodsSpecPlugin;
	
	
	/**
	 * 获取规格添加页面
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-add-page-html")
	public String getAddPageHtml(){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		
		Map goods = new HashMap();
		freeMarkerPaser.putData("goods",goods);
		
		freeMarkerPaser.setClz(this.getClass());
		return goodsSpecPlugin.getAddHtml(request);
		
		
	}
	
	
	/**
	 * 获取商品规格编辑页面
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-edit-page-html")
	public String getEditPageHtml(int goodsid){
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		 
		Map goods  =this.goodsManager.get(goodsid);
		
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("goods", goods);
		freeMarkerPaser.setClz(this.getClass());
		return goodsSpecPlugin.getEditHtml(goods, request);
		
	}
	
}
