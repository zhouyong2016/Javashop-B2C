package com.enation.app.shop.component.spec.plugin.order;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.core.order.model.Cart;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.plugin.cart.ICartAddEvent;
import com.enation.app.shop.core.order.plugin.cart.ICartItemFilter;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 规格购物车插件
 * @author kingapex
 *2012-3-26上午10:19:02
 */
@Component
public class GoodsSpecCartPlugin extends AutoRegisterPlugin implements ICartAddEvent, ICartItemFilter {

	@Autowired
	private ISpecManager specManager;
	
	
	/**
	 * 对cartitem的addon字段进行过滤
	 * 这个字段在购物车添加时由规格List形成
	 * 在这个方法中还原为List，并put至cartItem.others的map中
	 * 以specList为key，在页面中可通过cartItem.others.specList引用到此变量 
	 * @see #add(Cart)
	 */
	@Override
	public void filter(List<CartItem> itemlist, String sessionid) {
		
		for( CartItem cartItem:itemlist ){
			String addon = cartItem.getAddon();
			if(!StringUtil.isEmpty(addon)){
				JSONArray specArray=	JSONArray.fromObject(addon);
				List<Map> list = (List) JSONArray.toCollection(specArray,Map.class);
				cartItem.getOthers().put("specList",list);
			}
		}
		
	}
	

	/**
	 * 
	 * 响应购物车添加事件
	 * 如果有规格，读取这个货品的规格List
	 * 然后形成Json串存在cart表的附加字段中
	 */
	@Override
	public void add(Cart cart) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String havespec= request.getParameter("havespec");
		if("1".equals(havespec)){
			int productid= StringUtil.toInt(request.getParameter("productid"),true);
			List<Map<String,String>> specList = this.specManager.getProSpecList(productid);
			if(specList!=null && !specList.isEmpty())
			{
				String specstr =JSONArray.fromObject(specList).toString();
				cart.setAddon(specstr);
			}
		}
		//如果有规格
		if("1".equals(request.getAttribute("havespec"))){
			int productid= StringUtil.toInt(request.getAttribute("productid"),true);
			List<Map<String,String>> specList = this.specManager.getProSpecList(productid);
			if(specList!=null && !specList.isEmpty())
			{
				String specstr =JSONArray.fromObject(specList).toString();
				cart.setAddon(specstr);
			}
		}
		
		
		
	}

	@Override
	public void afterAdd(Cart cart) {
		// TODO Auto-generated method stub
		
	}

}
