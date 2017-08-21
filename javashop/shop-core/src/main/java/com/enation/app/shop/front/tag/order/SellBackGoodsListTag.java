package com.enation.app.shop.front.tag.order; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 退货申请商品列表
 * @author fenlongli
 *
 */
@Component
public class SellBackGoodsListTag extends BaseFreeMarkerTag {
	
	@Autowired
	private ISellBackManager sellBackManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map<String,Object> map = new HashMap<String, Object>();
		Integer recid=Integer.parseInt(params.get("id").toString());
		List list = sellBackManager.getGoodsList(recid);
	 
		map.put("goodsList", list); 
		return map;
	}
	
	

	
	
}
