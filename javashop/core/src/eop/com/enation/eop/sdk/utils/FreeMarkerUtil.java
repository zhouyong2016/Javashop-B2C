package com.enation.eop.sdk.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.directive.DirectiveFactory;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * freemarker工具
 * 
 * @author kingapex 2010-2-8下午04:23:18
 */
public class FreeMarkerUtil {
	private FreeMarkerUtil() {
	}

//	private static Configuration cfg;

	/**
	 * 获取servlet上下文件的Configuration
	 * 
	 * @param pageFolder
	 * @return
	 */
	public static Configuration getServletCfg(String pageFolder) {

		Configuration cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(ThreadContextHolder
				.getHttpRequest().getSession().getServletContext(), pageFolder);
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		return cfg;
	}

	public static Configuration getCfg(){
	 
			Configuration  cfg = new Configuration();
			cfg.setTemplateUpdateDelay(6000);
			cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 250));
			 
			Map<String,TemplateDirectiveModel> directiveMap = DirectiveFactory.getCommonDirective();
			Iterator<String> keyIter= directiveMap.keySet().iterator();
			
			while(keyIter.hasNext()){
				String key = keyIter.next();
				cfg.setSharedVariable(key, directiveMap.get(key));
			}
			
			
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setDefaultEncoding("UTF-8");
			cfg.setLocale(java.util.Locale.CHINA);
			cfg.setEncoding(java.util.Locale.CHINA, "UTF-8");
			cfg.setNumberFormat("#.##");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//			cfg.setTemplateExceptionHandler(templateExceptionHandler);
		return cfg;
	}
	
	
	
	
	public static Configuration getFolderCfg(String pageFolder)
			throws IOException {
		Configuration cfg =getCfg();
		cfg.setDirectoryForTemplateLoading(new File(pageFolder));
		
		return cfg;

	}

	public static void test() {
		Configuration cfg;
		try {
			cfg = FreeMarkerUtil
					.getFolderCfg("D:/workspace/eopnew/eop/WebContent/WEB-INF/classes/com/enation/app/shop/core/widget/goodscat");
			Template temp = cfg.getTemplate("GoodsCat.html");
			ByteOutputStream stream = new ByteOutputStream();

			Writer out = new OutputStreamWriter(stream, "UTF-8");
			temp.process(new HashMap(), out);

			out.flush();
			String html = stream.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException,
			TemplateException {
		Configuration cfg = FreeMarkerUtil
				.getFolderCfg("D:/workspace/eopnew/eop/WebContent/WEB-INF/classes/com/enation/app/shop/core/widget/goodscat");
		Template temp = cfg.getTemplate("GoodsCat.html");
		ByteOutputStream stream = new ByteOutputStream();

		Writer out = new OutputStreamWriter(stream, "UTF-8");
		temp.process(new HashMap(), out);

		out.flush();
		String html = stream.toString();
	}

}
