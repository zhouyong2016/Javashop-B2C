package com.enation.app.shop.core.goods.service.batchimport.impl;

import java.util.Map;

import org.w3c.dom.Element;

import com.enation.app.shop.core.goods.model.ImportDataSource;
import com.enation.app.shop.core.goods.service.batchimport.IGoodsDataImporter;

/**
 * 商品字段导入器
 * @author kingapex
 *
 */
public class GoodsFieldImporter implements IGoodsDataImporter{

	
	public void imported(Object value,Element node, ImportDataSource importConfig,Map goods) {
		String fieldname = node.getAttribute("fieldname");
		if(importConfig.isNewGoods())
			goods.put(fieldname, value);
	}
 

	
	
}
