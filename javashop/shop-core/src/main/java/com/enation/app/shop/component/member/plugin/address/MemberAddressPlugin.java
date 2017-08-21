package com.enation.app.shop.component.member.plugin.address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.plugin.IMemberTabShowEvent;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 会员收货地址插件
 * @author DMRain 
 * @date 2016-4-27
 */
@Component
public class MemberAddressPlugin extends AutoRegisterPlugin implements IMemberTabShowEvent {

	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	@Override
	public String getTabName(Member member) {
		// TODO Auto-generated method stub
		return "他的收货地址";
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 34;
	}

	@Override
	public boolean canBeExecute(Member member) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String onShowMemberDetailHtml(Member member) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		List addressList = this.memberAddressManager.listAddress(member.getMember_id());
		freeMarkerPaser.putData("addressList",addressList);
		freeMarkerPaser.setPageName("member_address");//解析此类同级目录中的member_address.html
		return freeMarkerPaser.proessPageContent();//返回上述页面的内容作为tab页的内容
	}

}
