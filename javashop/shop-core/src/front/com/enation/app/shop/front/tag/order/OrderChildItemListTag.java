package com.enation.app.shop.front.tag.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 订单货物列表标签
 * @author 冯兴隆
 * 2015-07-22
 */
@Component
@Scope("prototype")
public class OrderChildItemListTag extends BaseFreeMarkerTag {
	private IOrderManager orderManager;

	
	/**
	 * 订单货物列表标签
	 * @param orderid:订单id，int型
	 * @return 订单货物列表 ,List<OrderItem>型
	 * {@link OrderItem}
	 */
	@Override
	public Object exec(Map params) throws TemplateModelException {
		Integer orderid  =(Integer)params.get("orderid");
		if(orderid==null){
			throw new TemplateModelException("必须传递orderid参数");
		}
		
		List<OrderItem> itemList  = orderManager.listGoodsItems(orderid);
		//冯兴隆 修改  添加套餐商品支持
		Map<String,Object> map = new HashMap<String,Object>();
		//map.put("itemList", itemList);
		List<Map> itemListNew = handleOrderItem(itemList);
		map.put("itemList",itemListNew );
		
		return map;
	}
	/**
	 * 处理OrderItem对象 为该对象增加is_pack属性 用于判断该商品是不是一个套餐
	 * 2015-07-19  冯兴隆  
	 * @param itemList
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked" })
	private List handleOrderItem(List<OrderItem> itemList){
		List list = new ArrayList();
		for(OrderItem oItem : itemList){
			List tempList = orderManager.listGoodsItemsByItemId(oItem.getItem_id());
			list.addAll(tempList);
		}
		return list;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
