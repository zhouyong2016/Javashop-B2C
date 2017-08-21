package com.enation.app.shop.component.search.plugin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.plugin.search.IGoodsFrontSearchFilter;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 关键字搜索过滤器
 * @author kingapex
 *
 */
@Component
public class KeywordSearchFilter extends AutoRegisterPlugin implements
		IGoodsFrontSearchFilter {
	

	@Override
	public void createSelectorList(Map mainmap ,Cat cat) {
 
	}
	
 
	public void filter(StringBuffer sql, Cat cat) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String keyword =  request.getParameter("keyword");
		if (!StringUtil.isEmpty(keyword)) {
			sql.append(" and ( g.name like '%");
			sql.append(keyword);
			sql.append("%')");
		}
	}
	
	public static void main(String [] args){
		String keyword = "测试的  123   0000   11   22";
		String[] keys  = keyword.split("\\s");
		for(String key:keys);
			//System.out.println(key);
	}
	
	
	public String getFilterId() {
		
		return "keyword";
	}

	
	public String getAuthor() {
		
		return "kingapex";
	}

	
	public String getId() {
		
		return "keywordSearchFilter";
	}

	
	public String getName() {
		
		return "关键字搜索过滤器";
	}

	
	public String getType() {
		
		return "searchFilter";
	}

	
	public String getVersion() {
		
		return "1.0";
	}

	
	public void perform(Object... params) {
		

	}
	public void register() {
		

	}



}
