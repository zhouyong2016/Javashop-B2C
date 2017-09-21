package com.enation.app.base.core.tag;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.HeaderConstants;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;
/**
 * 网站头标签
 * @author lina
 * 2014-5-27
 */
@Component
@Scope("prototype")
public class HeaderTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map siteHeader = new HashMap();
		String ctx = ThreadContextHolder.getHttpRequest().getContextPath();
		EopSite site  =EopSite.getInstance();
		siteHeader.put("title", StringUtil.isEmpty(site.getTitle()) ? site.getSitename() : site.getTitle());
		siteHeader.put("keywords", site.getKeywords());
		siteHeader.put("description", site.getDescript());
		siteHeader.put("ctx",ctx);
		siteHeader.put("sitename", site.getSitename());
		return siteHeader;
	}

}
