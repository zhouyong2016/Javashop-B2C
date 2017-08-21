package com.enation.framework.pager;

import java.io.IOException;
import java.util.Map;

import com.enation.framework.pager.impl.SimplePageHtmlBuilder;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 普通分页指令
 * @author kingapex
 *2012-2-9上午9:46:01
 */
public class PagerDirectiveModel implements TemplateDirectiveModel {

	public void execute(Environment env, Map params, TemplateModel[] arg2,TemplateDirectiveBody arg3) throws TemplateException, IOException {
		//获取页数
		String pageno = params.get("pageno").toString();
		
		//获取每页显示数量
		String pagesize = params.get("pagesize").toString();
		
		//获取总数量
		String totalcount =params.get("totalcount").toString();
		int _pageNum = Integer.valueOf(pageno);
		int _totalCount = Integer.valueOf(totalcount);
		int _pageSize = Integer.valueOf(pagesize);
		
		//获取分页
		SimplePageHtmlBuilder pageHtmlBuilder = new SimplePageHtmlBuilder(_pageNum, _totalCount, _pageSize);
		String html = pageHtmlBuilder.buildPageHtml();
		
		//输出
		env.getOut().write(html);
	}

}
