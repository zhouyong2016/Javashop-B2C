package com.enation.app.cms.component.plugin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.plugin.AbstractFieldPlugin;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 复选框字段插件
 * 
 * @author kingapex 2010-7-6下午05:23:47
 */
@Component
public class CheckboxFieldPlugin extends AbstractFieldPlugin {
	
	public int getHaveSelectValue() {
		return 1;
	}
	
	public String onDisplay(DataField field, Object value) {
		StringBuffer html = new StringBuffer();

		String[] haveValue = null;

		if (value != null)
			haveValue = StringUtils.split(value.toString(), ",");

		String values = field.getSave_value();
		int i = 0;
		if (values != null) {
			String[] valueAr = StringUtils.split(values, ",");// values.split(",");
			for (String v : valueAr) {
				html.append("<input type=\"checkbox\"");
				html.append(" name=\"");
				html.append(field.getEnglish_name());
				html.append("\" value=\"");
				html.append(i);
				html.append("\"");

				if (haveValue != null) {
					if (checkValue(haveValue, "" + i)) {
						html.append(" checked=\"true\"");
					}
				}
				html.append(" />");
				html.append(v);
				i++;
			}
		}
		return html.toString();
	}

	public void onSave(Map article, DataField field) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String[] values = request.getParameterValues(field.getEnglish_name());
		article.put(field.getEnglish_name(), StringUtil.arrayToString(values, ","));
	}
	
	public Object onShow(DataField field, Object value) {
		String valueStr = field.getSave_value();
		if (!StringUtil.isEmpty((String)value)  && !StringUtil.isEmpty(valueStr)) {
			StringBuffer result = new StringBuffer();
			String[] haveValues = StringUtils.split(value.toString(),",");
			int i = 0;
			for (String v : haveValues) {
				if (i != 0)
					result.append("、");

				String[] values = StringUtils.split(valueStr, ",");// valueStr.split(",");
				result.append(values[Integer.valueOf(v)]);
				i++;
			}
			return result.toString();
		} else
			return "";
	}

	private boolean checkValue(String[] haveValue, String value) {
		if (haveValue == null)
			return false;
		for (String v : haveValue) {
			if (value.equals(v))
				return true;
		}
		return false;
	}
	
	public String getId() {
		return "checkbox";
	}
	
	public String getName() {
		return "复选框";
	}

}
