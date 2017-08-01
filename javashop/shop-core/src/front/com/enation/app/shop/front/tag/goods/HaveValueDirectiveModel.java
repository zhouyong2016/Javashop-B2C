package com.enation.app.shop.front.tag.goods;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class HaveValueDirectiveModel implements TemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] arg2,
			TemplateDirectiveBody arg3) throws TemplateException, IOException {
		
		String v1=params.get("v1").toString();
		String v2=params.get("v2").toString();
		if(v1.indexOf("#"+v2)>=0){
			env.getOut().write("checked");
		}

	}

}
