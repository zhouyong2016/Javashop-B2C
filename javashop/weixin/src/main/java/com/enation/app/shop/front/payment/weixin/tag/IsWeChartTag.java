package com.enation.app.shop.front.payment.weixin.tag;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.weixin.WeixinUtil;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class IsWeChartTag extends BaseFreeMarkerTag {
	 
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return WeixinUtil.isWeChat();
	}

	
}
