package com.enation.app.base.taglib;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class SessionPutDirectiveModel implements TemplateDirectiveModel{

	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] arg2,
			TemplateDirectiveBody arg3) throws TemplateException, IOException {
		
		String name = params.get("name").toString();
		String value = params.get("value").toString();
		
		ThreadContextHolder.getSession().setAttribute(name,value);
		
	}
	
	
 

}
