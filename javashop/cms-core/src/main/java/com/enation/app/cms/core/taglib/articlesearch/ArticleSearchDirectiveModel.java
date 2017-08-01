package com.enation.app.cms.core.taglib.articlesearch;

import java.io.IOException;
import java.util.Map;

import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 文章搜索标签
 * @author liuzy
 *
 */
public class ArticleSearchDirectiveModel implements TemplateDirectiveModel{

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.setClz(this.getClass());
		freeMarkerPaser.setPageName("article_search");
		String html=freeMarkerPaser.proessPageContent();
		env.getOut().write(html);
	}

}
