package com.enation.app.cms.component.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.plugin.AbstractFieldPlugin;

/**
 * 多行文本框插件
 * 
 * @author kingapex 2010-7-5下午03:10:04
 */
@Component
public class TextareaFieldPlugin extends AbstractFieldPlugin {

	public String onDisplay(DataField field, Object value) {
		StringBuffer html = new StringBuffer();
		html.append("<textarea");
		html.append(" name=\"");
		html.append(field.getEnglish_name());
		html.append("\" style=\"width:250px;height:100px\" ");
		html.append(this.wrappValidHtml(field));
		html.append(">");
		if (value != null) {
			html.append(value);
		}

		html.append("</textarea>");

		return html.toString();
	}

	public String getDataType() {
		return "text";
	}

	@Override
	public String getId() {
		return "textarea";
	}

	@Override
	public String getName() {
		return "多行文本框";
	}

	@Override
	public int getHaveSelectValue() {
		return 0;
	}

}
