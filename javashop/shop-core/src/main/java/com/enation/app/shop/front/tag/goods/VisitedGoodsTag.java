package com.enation.app.shop.front.tag.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.directive.ImageUrlDirectiveModel;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 浏览历史列表标签
 * @author whj
 *2014-02-13下午73:13:00
 */
@Component
@Scope("prototype")
public class VisitedGoodsTag extends BaseFreeMarkerTag{

	/**
	 * @param 不需要输出参数，
	 * @return 返回所有浏览历史的列表 ，List<Map>型
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpSession sessionContext = ThreadContextHolder.getSession();
		List<Map> visitedGoods = (List<Map>)sessionContext.getAttribute("visitedGoods");
		if(visitedGoods==null) visitedGoods = new ArrayList<Map>();
		Map result = new HashMap();
		result.put("visitedGoods", visitedGoods);
		result.put("GoodsPic",new  ImageUrlDirectiveModel());
		return result;
	}

}
