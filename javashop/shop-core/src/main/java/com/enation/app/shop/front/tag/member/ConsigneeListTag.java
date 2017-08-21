package com.enation.app.shop.front.tag.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.eop.sdk.context.UserConext;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

/**
 * 读取当前会员收货地址列表
 * @author kingapex
 *2013-7-26下午2:48:02
 */
@Component
@Scope("prototype")
public class ConsigneeListTag implements TemplateMethodModel {
	
	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	/**
	 * 读取当前会员收货地址列表
	 * @param 无
	 * @return 收货地址列表
	 * {@link MemberAddress}
	 */
	@Override
	public Object exec(List args) throws TemplateModelException {
		 
		Member member = UserConext.getCurrentMember();
		if(member==null){
			throw new TemplateModelException("未登录不能使用此标签[ConsigneeListTag]");
		}
		 
		return memberAddressManager.listAddress();
	}

}
