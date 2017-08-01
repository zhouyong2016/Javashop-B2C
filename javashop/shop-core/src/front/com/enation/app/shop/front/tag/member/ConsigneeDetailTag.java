package com.enation.app.shop.front.tag.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 收货人详细标签
 * @author kingapex
 *2013-7-26下午2:26:12
 */
@Component
@Scope("prototype")
public class ConsigneeDetailTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	/**
	 * 读取某个收货人的详细信息
	 * @param addressid：收货地址id,int型，必填项
	 * @return 会员地址，即MemberAddress
	 * {@link MemberAddress}
	 */
	@Override
	public Object exec(Map arg) throws TemplateModelException {
		
		Integer addressid = Integer.parseInt((String)arg.get("addressid"));
		if(addressid == null){
			throw new TemplateModelException("必须提供收货地址id参数");
		}
		MemberAddress address = memberAddressManager.getAddress( addressid);
		if(address==null){
			 return "0";
		}
		//增加校验
		Member member = UserConext.getCurrentMember();
		if(member!=null && address.getMember_id().equals(member.getMember_id())){
			
		}else{
			throw new UrlNotFoundException();
		}
		MemberAddress memberAddress= memberAddressManager.getAddress( addressid);
		this.pushAddressToBeEdit(memberAddress);
		return memberAddress;
		
	}

	/**
	 * 将地区各级id放入memberAddress中并拼接数组
	 * @param memberAddress
	 * @param addressToBeEditList
	 */
	private void pushAddressToBeEdit(MemberAddress memberAddress) {
//		List<Integer> addressToBeEditList=new ArrayList<Integer>();
		StringBuffer addressToBeEditsb=new StringBuffer("[");
		if(memberAddress.getProvince_id()!=null&&memberAddress.getProvince_id()!=0){//省级id
			addressToBeEditsb.append(memberAddress.getProvince_id());
			addressToBeEditsb.append(",");
			
		}
		if(memberAddress.getCity_id()!=null&&memberAddress.getCity_id()!=0){//市级id
			addressToBeEditsb.append(memberAddress.getCity_id());
			addressToBeEditsb.append(",");
		}
		if(memberAddress.getRegion_id()!=null&&memberAddress.getRegion_id()!=0){//地区id
			addressToBeEditsb.append(memberAddress.getRegion_id());
			addressToBeEditsb.append(",");
		}
		if(memberAddress.getTown_id()!=null&&memberAddress.getTown_id()!=0){//镇/街道id
			addressToBeEditsb.append(memberAddress.getTown_id());
			addressToBeEditsb.append(",");
		}
		
		String addressToBeEdit=addressToBeEditsb.substring(0, addressToBeEditsb.length()-1)+"]";
		memberAddress.setAddressToBeEdit(addressToBeEdit);
	}
	

} 
