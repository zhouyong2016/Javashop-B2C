package com.enation.app.shop.front.tag.goods.activity;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.other.model.ActivityGift;
import com.enation.app.shop.core.other.service.IActivityGiftManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 赠品详细Tag
 * @author DMRain
 * @date 2016-6-14
 * @version 1.0
 */
@Component
public class GiftDetailTag extends BaseFreeMarkerTag{

	@Autowired
	private IActivityGiftManager activityGiftManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer gift_id = (Integer) params.get("gift_id");
		ActivityGift gift = this.activityGiftManager.get(gift_id);
		if(gift == null){
			gift = new ActivityGift();
		}else{
			gift.setGift_img(StaticResourcesUtil.convertToUrl(gift.getGift_img()));
		}
		return gift;
	}

}
