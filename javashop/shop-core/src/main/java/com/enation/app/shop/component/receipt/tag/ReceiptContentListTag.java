package com.enation.app.shop.component.receipt.tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.other.service.IReceiptContentManager;
import com.enation.eop.sdk.context.UserConext;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 
 * 发票内容列表标签
 * @author wanglu
 * @version v6.5.0
 * @since v6.5.1
 * 2017年7月2日 上午10:54:19
 */
@Component
@Scope("prototype")
public class ReceiptContentListTag implements TemplateMethodModel {
	
	@Autowired
	private IReceiptContentManager receiptContentManager;
	
	/**
	 * 读取发票内容列表
	 * @param 无
	 * @return 发票内容列表
	 */
	@Override
	public Object exec(List args) throws TemplateModelException {
		 
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[ReceiptContentListTag]");
		}
		 
		return receiptContentManager.listReceiptContent();
	}

}
