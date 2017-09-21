package com.enation.app.cms.component.plugin;

import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.plugin.AbstractFieldPlugin;

/**
 * 日期控件字段插件
 * @author kingapex
 * 2010-7-7上午11:04:30
 */
@Component
public class DateFieldPlugin extends AbstractFieldPlugin {

	public int getHaveSelectValue() {

		return 0;
	}
	
	public String onDisplay(DataField field, Object value) {
		StringBuffer html = new StringBuffer();

		html.append("<input type=\"text\" name=\"");
		html.append(field.getEnglish_name());
		html.append("\" data-options=\"showSeconds:false\" ");
		if (value != null) {
			html.append(" value=\"");
			html.append(value);
			html.append("\"");
		}
		html.append(" class=\"\" style=\"height:25px;\" ");
		html.append(">");

		return html.toString();
	}

	@Override
	public String getId() {

		return "dateinput";
	}

	@Override
	public String getName() {

		return "日期控件";
	}

}
