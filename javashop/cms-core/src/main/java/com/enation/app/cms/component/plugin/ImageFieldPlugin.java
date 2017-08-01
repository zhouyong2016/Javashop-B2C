package com.enation.app.cms.component.plugin;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.plugin.AbstractFieldPlugin;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * 图片字段插件
 * @author kingapex
 * 2010-7-6下午05:40:29
 */
@Component
public class ImageFieldPlugin extends AbstractFieldPlugin {

	@Override
	public int getHaveSelectValue() {

		return 0;
	}

	/**
	 * 覆盖默认的字段值显示，将本地存储的图片路径转换为静态资源服务器路径
	 */
	@Override
	public Object onShow(DataField field, Object value) {
		if (value != null) {
			value = StaticResourcesUtil.convertToUrl(value.toString());
		}
		return value;
	}

	/**
	 * 显示字段入项
	 */
	public String onDisplay(DataField field, Object value) {
		try {
			Map data = new HashMap();
			data.put("fieldname", field.getEnglish_name());
			if (value != null) {
				value = StaticResourcesUtil.convertToUrl(value.toString());
			}
			data.put("value", value);
			data.put("ctx", ThreadContextHolder.getHttpRequest().getContextPath());
			Configuration cfg = new Configuration();
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setDefaultEncoding("UTF-8");
			cfg.setLocale(java.util.Locale.CHINA);
			cfg.setEncoding(java.util.Locale.CHINA, "UTF-8");
			
			cfg.setClassForTemplateLoading(this.getClass(), "");
			Template temp = cfg.getTemplate("ImageFieldPlugin.html");
			ByteOutputStream stream = new ByteOutputStream();

			Writer out = new OutputStreamWriter(stream);
			temp.process(data, out);

			out.flush();
			String html = stream.toString();

			return html;
		}
		catch(Exception e){
			return "CMS插件解析出错"+e.getMessage();
		}
	}
	
	@Override
	public void onSave(Map article, DataField field) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String path = request.getParameter(field.getEnglish_name());
		if (path != null){
			String static_server_domain= SystemSetting.getStatic_server_domain();

			path = path.replaceAll(static_server_domain,	EopSetting.FILE_STORE_PREFIX);
		}
		article.put(field.getEnglish_name(), path);
	}

	@Override
	public String getId() {
		return "image";
	}

	@Override
	public String getName() {
		return "图片";
	}

}
