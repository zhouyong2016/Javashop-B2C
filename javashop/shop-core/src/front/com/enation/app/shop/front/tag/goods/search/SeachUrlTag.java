package com.enation.app.shop.front.tag.goods.search;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.utils.UrlUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 搜索url标签
 * 根据输入的名称参数，得到排除某参数后的url字串，不带.html	
 * @author kingapex
 *2013-7-30下午2:35:03
 */
@Component
@Scope("prototype")
public class SeachUrlTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String name =(String)params.get("name");
		
		if(StringUtil.isEmpty(name)){
			throw new TemplateModelException("必须传递name参数");
		}
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String uri = request.getServletPath();
		String exSelfurl = UrlUtils.getExParamUrl(uri, name).replaceAll(".html", ""); //获取排除此过滤器的url
		
		return exSelfurl;
	}

}
