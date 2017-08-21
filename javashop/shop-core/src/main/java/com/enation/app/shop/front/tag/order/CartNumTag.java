package com.enation.app.shop.front.tag.order;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 购物车数量标签
 * @author kingapex
 *2013-7-31下午6:31:08
 */
@Component
@Scope("prototype")
public class CartNumTag extends BaseFreeMarkerTag {

	private ICartManager cartManager;
	
	/**
	 * 购物车数量标签
	 * @param 无
	 * @return 购物车中的商品数量 int型
	 * 
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		return cartManager.countItemNum(sessionid);
		 
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	
	
}
