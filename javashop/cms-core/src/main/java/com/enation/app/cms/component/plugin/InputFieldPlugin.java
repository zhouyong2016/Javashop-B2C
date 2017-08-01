package com.enation.app.cms.component.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.plugin.AbstractFieldPlugin;

import freemarker.template.utility.StringUtil;

/**
 * 单行文本框插件
 * @author kingapex
 * 2010-7-5下午02:55:26
 */
@Component
public class InputFieldPlugin extends AbstractFieldPlugin {

	@Override
	public String getId() {

		return "input";
	}

	public String getName() {

		return "单行文本框";
	}

	public String onDisplay(DataField field, Object value) {
		StringBuffer html = new StringBuffer("<input  maxlength=\"100\" type=\"text\" class=\"input_text\" style=\"width:450px\" name=\"");
		html.append(field.getEnglish_name());
		html.append("\"");

		if (value != null) {
			html.append(" value=\"");
			value = StringUtil.HTMLEnc(value.toString());
			html.append(value);
			html.append("\"");
		}

		html.append(this.wrappValidHtml(field));

		html.append(" />");

		return html.toString();
	}

	public int getHaveSelectValue() {
		return 0;
	}

}
