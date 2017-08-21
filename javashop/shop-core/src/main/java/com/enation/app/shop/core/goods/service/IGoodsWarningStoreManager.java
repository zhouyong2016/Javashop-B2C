package com.enation.app.shop.core.goods.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.goods.model.WarnNum;
import com.enation.framework.database.Page;


/**
 * 
 * (商品预警库存管理) 
 * @author zjp
 * @version v1.0
 * @since v6.1
 * 2016年12月7日 下午5:01:26
 */
public interface IGoodsWarningStoreManager {
	
	/**
	 * 商品预警库存查询
	 * @param map
	 * @param page 当前页数
	 * @param pageSize 显示记录数
	 * @param other 关键字
	 * @param sort 商品id
	 * @param order 排序方式
	 * @return
	 */
	public Page listGoodsStore(Map map,int page,int pageSize,String other,String sort,String order);

	/**
	 * 获取某个预警商品库存维护的html
	 * @param goodsid
	 * @return
	 */
	public String getWarningStoreHtml(Integer goodsid);
}
