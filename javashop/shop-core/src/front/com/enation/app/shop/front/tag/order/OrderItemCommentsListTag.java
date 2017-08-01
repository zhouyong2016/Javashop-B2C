package com.enation.app.shop.front.tag.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.support.DaoSupport;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.member.model.MemberOrderItem;
import com.enation.app.shop.core.member.service.IMemberOrderItemManager;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.component.ComponentView;
import com.enation.framework.component.IComponentStartAble;
import com.enation.framework.component.IComponentStopAble;
import com.enation.framework.component.context.ComponentContext;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 
 * (订单评论货物列表标签) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2016年12月29日 上午10:20:45
 */
@Component
@Scope("prototype")
public class OrderItemCommentsListTag extends BaseFreeMarkerTag {
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IDaoSupport daoSupport;
	@Autowired
	private IMemberOrderItemManager memberOrderItemManager;
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

		List<OrderItem> itemList  =orderManager.listGoodsItems(orderid);
		//遍历订单项，查询没有评论过的商品列表
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		List list = new ArrayList();
		for(int i=0;i<itemList.size();i++){
			Integer order_id = itemList.get(i).getOrder_id();
			Integer product_id =itemList.get(i).getProduct_id();
		//	MemberOrderItem memberOrderItem = memberOrderItemManager.getMemberOrderItem(order_id, product_id, 0);
			List<MemberOrderItem> memberOrderItemList = memberOrderItemManager.getMemberOrderItemList(order_id, product_id, 0);
			if(memberOrderItemList!=null&&memberOrderItemList.size()==1){
				orderItemList.add(itemList.get(i));
			}
			if(memberOrderItemList!=null&&memberOrderItemList.size()>1){	
				MemberOrderItem memberOrderItem = memberOrderItemList.get(0);
				Integer productid = memberOrderItem.getProduct_id();
				if(!list.contains(productid)){
					orderItemList.add(itemList.get(i));
					list.add(productid);
				}
			}
		}
		return orderItemList;
	}
	
}
