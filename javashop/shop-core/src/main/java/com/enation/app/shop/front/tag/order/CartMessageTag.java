package com.enation.app.shop.front.tag.order;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.service.impl.CartManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 购物车消息获取
 * @author Chopper
 * @version v1.0
 * @since v6.1
 * 2016年10月21日 下午12:20:30 
 *
 */
@Component
public class CartMessageTag extends BaseFreeMarkerTag{

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Object obj = ThreadContextHolder.getSession().getAttribute(CartManager.CART_MESSAGE);
		//如果没有消息返回空
		if(obj==null){
			return "0";
		}
		//如果获取到，则删除这条消息
		if(!StringUtil.isEmpty((String)obj)){
			ThreadContextHolder.getSession().removeAttribute(CartManager.CART_MESSAGE);
		}
		return (String)obj;
	}
	
}
