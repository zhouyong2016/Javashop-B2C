package com.enation.app.shop.front.tag.goods;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.service.IGoodsTagManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class GoodsRandomTag extends BaseFreeMarkerTag {

	private ISellBackManager sellBackManager;
	private IGoodsTagManager goodsTagManager;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		Integer tagid = (Integer) params.get("tagid");
		List list = goodsTagManager.getGoodsList(tagid);
		int totleNum = list.size();
		
		Random random  = new Random();
		int rdNum =  random.nextInt(totleNum);
		Map map = (Map) list.get(rdNum);
		
		return map;
	}
	
	
	public ISellBackManager getSellBackManager() {
		return sellBackManager;
	}
	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}


	public IGoodsTagManager getGoodsTagManager() {
		return goodsTagManager;
	}

	public void setGoodsTagManager(IGoodsTagManager goodsTagManager) {
		this.goodsTagManager = goodsTagManager;
	}

	
}
