package com.enation.app.shop.front.tag.goods.activity;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 优惠券详细Tag
 * @author DMRain
 * @date 2016-6-14
 * @version 1.0
 */
@Component
public class BonusDetailTag extends BaseFreeMarkerTag{

	@Autowired
	private IBonusTypeManager bonusTypeManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer typeid = (Integer) params.get("type_id");
		BonusType bonusType = this.bonusTypeManager.get(typeid);
		return bonusType;
	}

}
