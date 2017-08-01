package com.enation.app.cms.core.tag;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;

import freemarker.template.TemplateModelException;

/**
 * CMS数据详细标签 
 * @author kingapex
 *2013-10-23上午11:10:41
 */
@Component
@Scope("prototype")
public class DataDetailTag extends BaseFreeMarkerTag {
	
	protected IDataManager dataManager;
	
	/**
	 * 根据文章id和文章分类读取文章内容
	 * @param catid:文章所在分类,int型
	 * @param id:文章id,int型
	 * @return 文章的详细数据Map,map的key为文章模型中定义的字段名
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer catid=(Integer)params.get("catid");
		Integer articleid=(Integer)params.get("id");
		
		if(catid==null||articleid==null){           //当参数是通过标签得到，而不是通过ul地址烂得到参数
			Integer[] ids = this.parseId();
			Integer aid = ids[0];
			Integer cid = ids[1];
			
			if( articleid==null){
				articleid=aid;
			}
			if( articleid==null){
				throw new RuntimeException("id参数未能正确传递");
			}
			
			if(catid==null){
				catid= cid;
			}
			
			if(catid==null){
				throw new RuntimeException("catid参数未能正确传递");
			}
		}
		
		Map data = this.dataManager.get(articleid, catid, true);
		if(data==null){
			throw new UrlNotFoundException();
		}
		return data;
	}
	public IDataManager getDataManager() {
		return dataManager;
	}
	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}
	
	protected Integer[] parseId() {
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String url = RequestUtil.getRequestUrl(httpRequest);
		String pattern = "/(.*)-(\\d+)-(\\d+).html(.*)";
		String page = null;
		String catid = null;
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			page = m.replaceAll("$3");
			catid = m.replaceAll("$2");
			return new Integer[] { Integer.valueOf("" + page), Integer.valueOf("" + catid) };
		}
		return null;
	}
}
