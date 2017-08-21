package com.enation.framework.directive;

import java.io.IOException;
import java.util.Map;

import com.enation.eop.SystemSetting;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.util.StringUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 图片实际URL输出指令
 * @author kingapex
 *2012-2-11上午9:51:22
 */
public class ImageUrlDirectiveModel implements TemplateDirectiveModel {

	
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {

		String pic = params.get("pic").toString();
		String postfix =null;
		if (params.get("postfix") != null) {
			 postfix = params.get("postfix").toString();
		}	
		pic= this.getImageUrl(pic, postfix);
		env.getOut().write(pic);

	}
	
	
	
	private String getImageUrl(String pic,String postfix){
		if (StringUtil.isEmpty(pic))
			pic =SystemSetting.getDefault_img_url();
		if(pic.toUpperCase().startsWith("HTTP"))//lzf add 20120321
			return pic;
		if (pic.startsWith("fs:")) {//静态资源式分离式存储
			pic = StaticResourcesUtil.convertToUrl(pic);
		} 
		if (!StringUtil.isEmpty(postfix )) {
			return StaticResourcesUtil.getThumbPath(pic, postfix);
		} else {
			return pic;
		}
	}
}
