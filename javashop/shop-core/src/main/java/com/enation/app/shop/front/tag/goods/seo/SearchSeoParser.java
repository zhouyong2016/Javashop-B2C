package com.enation.app.shop.front.tag.goods.seo;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.plugin.search.SearchSelector;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.util.StringUtil;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

public class SearchSeoParser {

	private Map selectorMap;

	private Cat cat = null;

	private String tag = null;

	// 所有选中的属性
	private HashMap propMap = new HashMap();

	// 所有选中的除属性之外的字段
	private HashMap fieldMap = new HashMap();
	
	private String title = "";
	private String keywords = "";
	private String description = "";

	public SearchSeoParser(Map selectorMap, Cat cat, String tag) {
		this.selectorMap = selectorMap;
		this.cat = cat;
		this.tag = tag;
		init();
	}

	/**
	 * 初始化分析各种属性
	 */
	private void init() {
		Iterator iterator = selectorMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			if (key.equals("prop")) {
				Map multiSelector = (Map) (selectorMap.get(key));
				Iterator multiIterator = multiSelector.keySet().iterator();
				while (multiIterator.hasNext()) {
					String value = "";
					String multiKey = multiIterator.next().toString();
					List<SearchSelector> selectorList = (List<SearchSelector>) (multiSelector
							.get(multiKey));
					for (int i = 1; i < selectorList.size(); i++) {
						SearchSelector selector = (SearchSelector) selectorList
								.get(i);
						if (selector.getIsSelected()) {
							value = selector.getName();
							break;
						}
					}
					if (!StringUtil.isEmpty(value)) {
						if (!propMap.containsKey(multiKey)) {
							propMap.put(multiKey, value);
						}
					}
				}

			} else if (!key.equals("cat") && !key.equals("tag")) {
				String value = "";
				List<SearchSelector> selectorList = (List<SearchSelector>) (selectorMap
						.get(key));
				for (int i = 1; i < selectorList.size(); i++) {
					SearchSelector selector = (SearchSelector) selectorList
							.get(i);
					if (selector.getIsSelected()) {
						value = selector.getName();
						break;
					}
				}
				if (!StringUtil.isEmpty(value)) {
					if (!fieldMap.containsKey(key)) {
						fieldMap.put(key, value);
//						//System.out.println(key + ":" + value);
					}
				}
			}
		}

	}

	/**
	 * 解析挂件得到SEO标题、关键词和描述
	 * 
	 * @return
	 */
	public void parse() {
		try {
//			String path = SearchSeoParser.class.getResource("").toString();
//			if (path.toLowerCase().indexOf("file:") > -1) {
//				path = path.substring(6, path.length());
//			}
			
			String keyword = (String)fieldMap.get("keyword");
			
			EopSite site =EopSite.getInstance();
			Configuration cfg = new Configuration();
			cfg.setObjectWrapper(new DefaultObjectWrapper());	
			cfg.setDefaultEncoding("UTF-8");
			cfg.setLocale(java.util.Locale.CHINA);
			cfg.setEncoding(java.util.Locale.CHINA, "UTF-8");
			cfg.setClassForTemplateLoading(this.getClass(), "");
			
			
			HashMap data = new HashMap<String, Object>();
			data.put("cat", cat);
			data.put("tag", tag);
			data.put("propMap", propMap);
			data.put("fieldMap", fieldMap);
			data.put("sitename", site.getSitename());
			ByteOutputStream stream = new ByteOutputStream();
			Writer out = new OutputStreamWriter(stream);
			cfg.getTemplate("title.html").process(data, out);
			out.flush();
			title = StringUtil.replaceEnter(stream.toString());
			
			stream = new ByteOutputStream();
			out = new OutputStreamWriter(stream);
			cfg.getTemplate("keywords.html").process(data, out);
			out.flush();
			keywords = StringUtil.replaceEnter(stream.toString());
//			
			stream = new ByteOutputStream();
			out = new OutputStreamWriter(stream);
			cfg.getTemplate("description.html").process(data, out);
			out.flush();
			description = StringUtil.replaceEnter(stream.toString());
		} catch (Exception ex) { }
				
	}

	public String getTitle() {
		return title;
	}

	public String getKeywords() {
		return keywords;
	}

	public String getDescription() {
		return description;
	}

	

}
