package com.enation.app.shop.front.tag.goods.activity;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.other.service.IActivityManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 促销活动详细Tag
 * @author DMRain
 * @date 2016-6-14
 * @version 1.0
 */
@Component
public class ActivityDetailTag extends BaseFreeMarkerTag{

	@Autowired
	private IActivityManager activityManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer activity_id = (Integer) params.get("activity_id");
		Map map = this.activityManager.getActMap(activity_id);
		return map;
	}

}
