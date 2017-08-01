package com.enation.app.shop.front.tag.member;



import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 结算页会员地址数量
 * @author whj
 *2015-09-16下午15:13:00
 */
@Component
@Scope("prototype")
public class MemberAddressNumTag extends BaseFreeMarkerTag{
	
	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Member member = UserConext.getCurrentMember();
		int memberAddressNum = 0;
		if(member==null){
			return memberAddressNum;
		}
		memberAddressNum = memberAddressManager.addressCount(member.getMember_id());
		return memberAddressNum;
	}


}
