package com.enation.app.shop.front.tag.decorate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.decorate.service.IShowCaseManager;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 
 * 橱窗商品列表获取标签
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Component
@Scope("prototype")
public class ShowCaseGoodsListTag extends BaseFreeMarkerTag{
	
	@Autowired
	private IShowCaseManager showCaseManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String goods_ids=params.get("goodsids").toString();
		if(!StringUtil.isEmpty(goods_ids)){
			List<Goods> list=this.showCaseManager.getSelectGoods(goods_ids);
//			Collections.shuffle(list);
			return list;
		}
		return new ArrayList();
	}

}
