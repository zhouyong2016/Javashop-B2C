package com.enation.app.shop.component.depot.plugin.goods;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.goods.plugin.IGoodsAfterAddEvent;
import com.enation.app.shop.core.goods.plugin.IGoodsAfterEditEvent;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
public class GoodsModifedUpdateCachePlugin extends AutoRegisterPlugin implements
		IGoodsAfterAddEvent, IGoodsAfterEditEvent {
	
	public void updateCache(Map goods){
		int catid = Integer.valueOf(goods.get("cat_id").toString());
		int goodsid= Integer.valueOf(goods.get("goods_id").toString());
		
		String link ="";
	 
			link="/goods-"+goodsid+".html";
	 
//		HttpCacheManager.updateUrlModified(link);
//		HttpCacheManager.updateUriModified("/search-(.*).html");
	}

	
	@Override
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
		updateCache(goods);
	}

	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {
		updateCache(goods);
	}

}
