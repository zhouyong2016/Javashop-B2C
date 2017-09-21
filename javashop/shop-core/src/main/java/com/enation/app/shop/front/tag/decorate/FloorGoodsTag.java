package com.enation.app.shop.front.tag.decorate;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * 楼层商品获取标签
 * 
 * @author jianghongyan
 * @version 1.0.0,2016年6月20日
 * @since v6.1
 */
@Component
@Scope("prototype")
@SuppressWarnings(value = { "rawtypes" })
public class FloorGoodsTag extends BaseFreeMarkerTag {

	@Autowired
	private IGoodsManager goodsManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer goods_id = Integer.valueOf(params.get("goods_id").toString());
		Goods g = this.goodsManager.getGoods(goods_id);
		return  g;
	}

}
