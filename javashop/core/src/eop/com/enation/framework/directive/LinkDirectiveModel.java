package com.enation.framework.directive;

import java.io.IOException;
import java.util.Map;

import com.enation.framework.context.webcontext.ThreadContextHolder;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * 图片根据url判断内链和外链
 * @author zh
 * @version v1.0
 * @since v6.1
 * 2016年10月17日 下午3:54:16
 */
public class LinkDirectiveModel implements TemplateDirectiveModel{

	@Override
	public void execute(Environment env, Map params, TemplateModel[] arg2,
			TemplateDirectiveBody arg3) throws TemplateException, IOException {
		//获取url
		String url = params.get("url").toString();
		//如果以http开始的则为外链 否则是内链
		if(!url.startsWith("http")){
			String path=ThreadContextHolder.getHttpRequest().getContextPath();
			if(url.startsWith("/")){
				url = path + url;
			}else{
				url = path + "/" + url;
			}
		}
		env.getOut().write(url.toString());
	}
}
