package com.enation.app.shop.component.member.plugin.bonus;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.member.plugin.IMemberTabShowEvent;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 会员优惠券插件
 * @author DMRain 
 * @date 2016-4-27
 */
@Component
public class MemberBonusPlugin extends AutoRegisterPlugin implements IMemberTabShowEvent{

	@Autowired
	private IBonusManager bonusManager;
	
	@Override
	public String getTabName(Member member) {
		// TODO Auto-generated method stub
		return "他的优惠券";
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 33;
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
		List bonusList = this.bonusManager.listBonus(member.getMember_id());
		freeMarkerPaser.putData("bonusList",bonusList);
		freeMarkerPaser.setPageName("member_bonus");//解析此类同级目录中的member_bonus.html
		return freeMarkerPaser.proessPageContent();//返回上述页面的内容作为tab页的内容
	}

}
