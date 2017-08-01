package com.enation.app.shop.front.tag.order;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class CartCheckTag extends BaseFreeMarkerTag{
	@Autowired
	private ICartManager cartManager;
	
	/**
	 * 返回购物车中选中的购物列表
	 * @param 无 
	 * @return 选中的购物列表 类型List<CartItem>
	 * {@link CartItem}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		List goodsList = cartManager.selectListGoods( sessionid ); //商品列表
		
		return goodsList;
	}

}
