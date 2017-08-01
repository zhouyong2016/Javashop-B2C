package com.enation.app.cms.component.plugin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.plugin.AbstractFieldPlugin;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 富文本编辑器字段插件
 * 
 * @author kingapex 2010-7-7上午10:28:06
 */
@Component
public class RichEditFieldPlugin extends AbstractFieldPlugin {

	public int getHaveSelectValue() {
		return 0;
	}

	/**
	 * 覆写数据保存事件默认响应<br>
	 * 逻辑为以name为字段为字段名，值为request.getParameter(fieldname);
	 */
	public void onSave(Map article, DataField field) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String value = request.getParameter(field.getEnglish_name());
		String static_server_domain= SystemSetting.getStatic_server_domain();
		if (value != null) {
			// 替换静态服务器域名为本地标识串(fs:)
			value = value.replaceAll(
					static_server_domain
							 
							+ "/attachment/", EopSetting.FILE_STORE_PREFIX
							+ "/attachment/");
		}
		article.put(field.getEnglish_name(), value);
	}

	/**
	 * 覆写父的数据显示响应事件<br>
	 * 逻辑为直接返回字段值<br>
	 * 如果为null返回空串
	 */
	public Object onShow(DataField field, Object value) {
		if (value != null) {
			value = StaticResourcesUtil.convertToUrl(value.toString());
			return value;
		} else
			return "";
	}

	public String onDisplay(DataField field, Object value) {
		StringBuffer html = new StringBuffer();
		html.append("<script id=\""+field.getEnglish_name()+"\" name=\""+field.getEnglish_name()+"\" type=\"text/plain\" style=\"height:200px;width: 99%;\">");
		if (value != null) {
			value = StaticResourcesUtil.convertToUrl(value.toString());
			html.append(value);
		}
		html.append("</script>");
		html.append("<script type=\"text/javascript\">");
		html.append("var ue = UE.getEditor('"+field.getEnglish_name()+"',{zIndex:0,});");
		html.append("</script>");
		//System.out.println(html.toString());
		return html.toString();
	}

	public String getDataType() {
		// lzf edit 20120410
		if (EopSetting.DBTYPE.equals("2"))
			return "clob";
		else
			return "text";
	}

	@Override
	public String getId() {
		return "richedit";
	}

	@Override
	public String getName() {
		return "富文本编辑器";
	}

}
