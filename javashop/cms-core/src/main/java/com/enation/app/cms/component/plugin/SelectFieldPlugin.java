package com.enation.app.cms.component.plugin;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.plugin.AbstractFieldPlugin;

/**
 * 下拉框字段插件
 * 
 * @author kingapex 2010-7-6下午05:20:59
 */
@Component
public class SelectFieldPlugin extends AbstractFieldPlugin {

	public int getHaveSelectValue() {
		return 1;
	}

	public String onDisplay(DataField field, Object value) {
		StringBuffer html = new StringBuffer();
		html.append("<select name=\"");

		html.append(field.getEnglish_name());
		html.append("\"");
		html.append(this.wrappValidHtml(field));
		html.append(">");

		html.append("<option value=\"0\">全部</option>");
		String values = field.getSave_value();
		int i = 0;
		if (values != null) {
			String[] valueAr = StringUtils.split(values, ",");// values.split(",");
			for (String v : valueAr) {
				html.append("<option ");
				html.append(" value=\"");
				html.append(i);
				html.append("\"");

				if (value != null && i == Integer.valueOf("" + value)) {
					html.append(" selected=\"true\"");
				}

				html.append(" >");
				html.append(v);
				html.append("</option>");
				i++;
			}
		}
		html.append("</select>");

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
		return "select_input";
	}

	@Override
	public String getName() {
		return "下拉框";
	}

}
