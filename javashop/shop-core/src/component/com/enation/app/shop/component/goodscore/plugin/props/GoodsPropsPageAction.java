/**
 * 
 */
package com.enation.app.shop.component.goodscore.plugin.props;

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
 * 商品属性页
 * @author kingapex
 * @version [v1.0 2016年2月19日]
 */
@Controller
@RequestMapping("/shop/admin/goods-props")
public class GoodsPropsPageAction {
	@Autowired
	private IGoodsManager goodsManager;
	
	@Autowired
	private GoodsPropsAdminPlugin goodsPropsAdminPlugin;
	
	/**
	 * 获取属性添加页面
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
		return goodsPropsAdminPlugin.getAddHtml(request);
	}
	
	
	
	/**
	 * 获取属性编辑页面
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
		return goodsPropsAdminPlugin.getEditHtml(goods, request);
		
	}

}
