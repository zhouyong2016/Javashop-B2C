package com.enation.app.shop.front.tag.goods.search;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.utils.UrlUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 搜索页码标签 
 * @author kingapex
 *2013-7-29下午4:53:41
 */
@Component
@Scope("prototype")
public class SearchPageNoTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String uri = request.getServletPath();
		String page_str = UrlUtils.getParamStringValue(uri, "page");
		int page=1;
		if(page_str!=null && !page_str.equals("")){
			page= Integer.valueOf(page_str);
		}		
		return  page;
	}

}
