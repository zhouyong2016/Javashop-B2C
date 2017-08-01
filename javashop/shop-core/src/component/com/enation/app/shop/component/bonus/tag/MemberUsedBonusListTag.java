package com.enation.app.shop.component.bonus.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.bonus.model.Bonus;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 结算页——获取会员已使用的线下发放优惠卷(实体券)列表
 * @author xulipeng
 * 2017年01月09日
 */
@Component
@Scope("prototype")
public class MemberUsedBonusListTag extends BaseFreeMarkerTag {
	
	/**
	 * 获取会员已使用的线下实体券列表
	 * @param 无
	 * @return 红包列表，List<Map>型
	 * map内容
	 * 所有MemberBonus的属性{@link MemberBonus }以及：
	 */
	@Override
	protected Object exec(Map arg0) throws TemplateModelException {
		List<Bonus> bonusList  =BonusSession.get();
		bonusList=bonusList==null?new ArrayList<Bonus>():bonusList;
		return bonusList;
	}

}
