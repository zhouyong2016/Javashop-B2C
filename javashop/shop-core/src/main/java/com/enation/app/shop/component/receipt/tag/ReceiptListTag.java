package com.enation.app.shop.component.receipt.tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.service.IMemberReceiptManager;
import com.enation.eop.sdk.context.UserConext;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 
 * 当前会员发票列表标签
 * @author wanglu
 * @version v6.5.1
 * @since v6.5.0
 * 2017年6月27日 下午3:47:55
 */
@Component
@Scope("prototype")
public class ReceiptListTag implements TemplateMethodModel {
	
	@Autowired
	private IMemberReceiptManager memberReceiptManager;
	
	/**
	 * 读取当前会员发票列表
	 * @param 无
	 * @return 发票列表
	 */
	@Override
	public Object exec(List args) throws TemplateModelException {
		 
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[ReceiptListTag]");
		}
		 
		return memberReceiptManager.listReceipt();
	}

}
