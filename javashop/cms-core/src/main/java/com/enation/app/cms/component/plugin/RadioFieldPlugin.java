package com.enation.app.cms.component.plugin;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.plugin.AbstractFieldPlugin;

/**
 * 单选按钮插件
 * @author kingapex
 * 2010-7-5下午03:14:22
 */
@Component
public class RadioFieldPlugin extends AbstractFieldPlugin {

	public String onDisplay(DataField field, Object value) {
		StringBuffer html = new StringBuffer();

		String values = field.getSave_value();
		int i=0;
		if (values != null) {
			String[] valueAr = StringUtils.split(values, ",");// values.split(",");
			for (String v : valueAr) {
				html.append("<input type=\"radio\"");
				html.append(" name=\"");
				html.append(field.getEnglish_name());
				html.append("\" value=\"");
				html.append(i);
				html.append("\"");
				if (value == null && i == 0) {
					html.append(" checked=\"true\"");
				}
				if (value != null && i == Integer.valueOf("" + value)) {
					html.append(" checked=\"true\"");
				}

				html.append(" />");
				html.append(v);
				i++;
			}
		}
		return html.toString();
	}
	
	public Object onShow(DataField field, Object value) {
		if (value != null) {
			int index = Integer.valueOf(value.toString());
			String valueStr = field.getSave_value();
			if (valueStr != null) {
				String[] values = StringUtils.split(valueStr, ",");// valueStr.split(",");
				return values[index];
			}
			return "";
		} else
			return "";
	}

	@Override
	public String getId() {
		return "radio";
	}

	@Override
	public String getName() {
		return "单选按钮";
	}

	public int getHaveSelectValue() {
		return 1;
	}

}
