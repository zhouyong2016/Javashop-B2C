package com.enation.app.shop.core.goods.model.support;

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.PluginTab;

//商品编辑数据dto
public class GoodsEditDTO {

	private Map goods; // 编辑的商品数据
	private List<PluginTab> tabs;

	public Map getGoods() {
		return goods;
	}

	public void setGoods(Map goods) {
		this.goods = goods;
	}

	public List<PluginTab> getTabs() {
		return tabs;
	}

	public void setTabs(List<PluginTab> tabs) {
		this.tabs = tabs;
	}



}
