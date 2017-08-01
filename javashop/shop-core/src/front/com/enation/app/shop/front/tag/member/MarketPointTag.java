package com.enation.app.shop.front.tag.member;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 消息积分使用数显示标签
 * @author kingapex
 *2013-8-4下午9:43:10
 */
@Component
@Scope("prototype")
public class MarketPointTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer	userpoint = (Integer)ThreadContextHolder.getSession().getAttribute("use_point_num");
	
		if(userpoint==null) userpoint =0;
			return userpoint;
	}

}
