package com.enation.app.shop.front.tag.member;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class MemberLvListTag extends BaseFreeMarkerTag {

	@Autowired
	private IMemberLvManager memberLvManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List list = this.memberLvManager.list();
		return list;
	}

}
