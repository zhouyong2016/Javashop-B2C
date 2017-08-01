package com.enation.app.shop.front.tag.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 购物车标签
 * @author kingapex
 *2013-8-1上午11:00:20
 */
@Component
@Scope("prototype") 
public class CartTag implements TemplateMethodModel {
	
	@Autowired
	private ICartManager cartManager;
	
	/**
	 * 返回购物车中的购物列表
	 * @param 无 
	 * @return 购物列表 类型List<CartItem>
	 * {@link CartItem}
	 */
	@Override
	public Object exec(List args) throws TemplateModelException {
		 
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		List goodsList = cartManager.listGoods( sessionid ); //商品列表
		return goodsList;
	}
 

}
