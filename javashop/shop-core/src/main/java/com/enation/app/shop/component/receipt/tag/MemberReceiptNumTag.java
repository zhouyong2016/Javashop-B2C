package com.enation.app.shop.component.receipt.tag;



import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.service.IMemberReceiptManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * 结算页会员发票数量 标签
 * @author wanglu
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月27日 下午3:46:54
 */
@Component
@Scope("prototype")
public class MemberReceiptNumTag extends BaseFreeMarkerTag{
	
	@Autowired
	private IMemberReceiptManager memberReceiptManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Member member = UserConext.getCurrentMember();
		int memberReceiptNum = 0;
		if(member==null){
			return memberReceiptNum;
		}
		memberReceiptNum = memberReceiptManager.receiptCount(member.getMember_id());
		return memberReceiptNum;
	}


}
