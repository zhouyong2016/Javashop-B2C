package com.enation.app.shop.front.tag.goods.search;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.utils.UrlUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;
/**
 * 当前位置标签
 * @author lina
 * 2014-2-13下午15:57:41
 */
@Component
@Scope("prototype")
public class SearchNavTag extends BaseFreeMarkerTag {
	private IGoodsCatManager goodsCatManager;
	/**
	 * 当前位置标签
	 * @param 无
	 * @return 返回值Map型，其值如下：
	 * isSearch:是否为搜索  true是    false否
	 * cat:商品类别{@link Cat}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String url= request.getServletPath();
		Cat cat  = null;
		Map result = new HashMap();
		boolean isSearch = false;
		String catidstr = UrlUtils.getParamStringValue(url,"cat");
		if(!StringUtil.isEmpty(catidstr) && !"0".equals(catidstr)){
			Integer catid= Integer.valueOf(catidstr);
			cat = goodsCatManager.getById(catid); 
		}
		if(cat == null){
			isSearch = true;
		}
		result.put("isSearch", isSearch);
		result.put("cat", cat);
		return result;
	}
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

}
