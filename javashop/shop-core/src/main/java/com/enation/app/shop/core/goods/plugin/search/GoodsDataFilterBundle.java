package com.enation.app.shop.core.goods.plugin.search;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.enation.framework.plugin.AutoRegisterPluginsBundle;
@Service("goodsDataFilterBundle")
public class GoodsDataFilterBundle extends AutoRegisterPluginsBundle {

	public String getName() {
		 
		return "商品数据过滤插件桩";
	}
	
	
	public List getPluginList(){
		
		return this.getPlugins();
	}
 

	public void filterGoodsData(List<Map> goodsList){
		List<IGoodsDataFilter > filterList  = getPluginList();
		
		if(filterList== null ) return ;
		
		for(IGoodsDataFilter filter:filterList){
			((IGoodsDataFilter)filter).filter(goodsList);
		}
	}
	 
}
