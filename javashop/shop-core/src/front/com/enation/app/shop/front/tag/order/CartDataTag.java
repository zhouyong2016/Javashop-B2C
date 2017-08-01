package com.enation.app.shop.front.tag.order;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 购物车数据标签
 * @author kingapex
 *2013-10-12下午4:01:40
 */
@Component
@Scope("prototype")
public class CartDataTag extends BaseFreeMarkerTag {
	private ICartManager cartManager;
	/**
	 * 购物车数量标签
	 * @param 无
	 * @return 购物车中的数据信息，Map型，其中的key结构为：
	 * count:购物车数量
	 * total:购物车总价
	 * 
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		
		Double goodsTotal  =cartManager.countGoodsTotal( sessionid );
		int count = this.cartManager.countItemNum(sessionid);
		
		java.util.Map<String, Object> data =new HashMap();
		data.put("count", count);//购物车中的商品数量
		data.put("total", goodsTotal);//总价
		return data;
	}
	public ICartManager getCartManager() {
		return cartManager;
	}
	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

}
