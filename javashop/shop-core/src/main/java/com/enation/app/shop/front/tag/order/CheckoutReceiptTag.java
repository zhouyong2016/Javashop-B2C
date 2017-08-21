package com.enation.app.shop.front.tag.order;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 结算发票
 * @author xulipeng
 * @version 1.0
 * @since v6.2
 * 2016年11月28日
 */
@Component
@Scope("prototype")
public class CheckoutReceiptTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpSession session =  ThreadContextHolder.getSession();
		Map receiptMap = (Map) session.getAttribute("checkoutReceiptSession");

		if(receiptMap==null){	//如果session中没有记录,设置默认
			receiptMap = new HashMap();
			receiptMap.put("is_have", 0);
		}else{
			receiptMap.put("is_have", 1);
		}
		return receiptMap;
	}

}
