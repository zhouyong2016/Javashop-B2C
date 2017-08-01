package com.enation.app.shop.front.tag.decorate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.decorate.model.Subject;
import com.enation.app.shop.core.decorate.service.ISubjectManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * 专题列表获取标签
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Component
@Scope("prototype")
public class SubjectListTag extends BaseFreeMarkerTag{
	
	@Autowired
	private ISubjectManager subjectManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		// TODO 过滤在将来可以考虑用插件
		List<Map> list=this.subjectManager.listAll();
		List list2=new ArrayList<Subject>();
		for (Map subject_map : list) {
			if("0".equals(subject_map.get("is_display").toString())){
				list2.add(subject_map);
			}
		}
		return list2;
	}

}
