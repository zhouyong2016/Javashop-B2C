package com.enation.framework.directive;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 上传图片指令
 * @author Sylow
 * @version v1.0,2016-05-05
 * @serial v6.0
 */
public class ImageUploaderDirectiveModel implements TemplateDirectiveModel {

	/*
	 * (non-Javadoc)
	 * @see freemarker.template.TemplateDirectiveModel#execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[], freemarker.template.TemplateDirectiveBody)
	 */
	@Override
	public void execute(Environment env, Map params, TemplateModel[] arg2,
			TemplateDirectiveBody arg3) throws TemplateException, IOException {
		
		
		Object isBackObj = params.get("isback");
		Object uploaderNameObj = params.get("name");	// 上传控件名称
		
		
		boolean isBack = false;
		
		// 如果有值
		if (isBackObj != null) {
			isBack = Boolean.parseBoolean(isBackObj.toString());
		}
		
		String uploaderName = "";
		if (uploaderNameObj != null) {
			uploaderName = uploaderNameObj.toString();
		}
		FreeMarkerPaser freeMarkerPaser = new FreeMarkerPaser(getClass());
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ctx = request.getContextPath();
		freeMarkerPaser.putData("ctx", ctx);
		freeMarkerPaser.putData("is_back", isBack);
		freeMarkerPaser.putData("uploader_name", uploaderName);
		
		
		freeMarkerPaser.setPageName("image-uploader");
		
		String html = freeMarkerPaser.proessPageContent();
		env.getOut().write(html);
	}

}
