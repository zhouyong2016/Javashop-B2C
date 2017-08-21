package com.enation.app.shop.front.tag.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.support.DaoSupport;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.Order;
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
 * 订单货物列表标签
 * @author kingapex
 *2013-7-28下午3:54:32
 */
@Component
@Scope("prototype")
public class OrderItemListTag extends BaseFreeMarkerTag {
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IDaoSupport daoSupport;
	
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
		//遍历订单项，判断商品是否更新，如果商品更新则显示快照按钮
		List<OrderItem> itemList;
		Order order = orderManager.get(orderid);
		if(order.getItems_json().length()==2){
			List<Order> childOrders = orderManager.getChildOrders(orderid);
			itemList = new ArrayList<OrderItem>();
			if(childOrders!=null&&childOrders.size()>0){
				for(Order corder : childOrders){
					List<OrderItem> temlist = orderManager.listGoodsItems(corder.getOrder_id());
					itemList.addAll(temlist);
				}
			}
		}else{
			itemList =orderManager.listGoodsItems(orderid);
		}
		for (OrderItem orderItem : itemList) {
			Integer goods_id = orderItem.getGoods_id();
			String sql = "select * from es_goods where goods_id=?";
			Map map = daoSupport.queryForMap(sql, goods_id);
			Object last_modify = map.get("last_modify");
			Integer snapshot_id = orderItem.getSnapshot_id();
			sql = "select * from es_goods_snapshot where snapshot_id=?";
			List list = daoSupport.queryForList(sql, snapshot_id);
			//判断组建是否启用
			ComponentView componentView= this.getComponentView("orderCoreComponent");
			if(componentView.getEnable_state() == 1){
				if(list.size()!=0){
					Map map_snapshot = (Map) list.get(0);
					Object  last_modify_snapshot= map_snapshot.get("last_modify");
					if(!last_modify_snapshot.equals(last_modify)){				
						orderItem.setSnapshot_switch(1);
						orderItem.setSnapshot_id(snapshot_id);
					}else{
						orderItem.setSnapshot_switch(0);
					}
				}
			}
		}
		return itemList;
	}
	/**
	 * 获取组件列表
	 * @return 组件列表
	 */
	private List<ComponentView> getDbList() {
		String sql = "select * from es_component ";
		return this.daoSupport.queryForList(sql, ComponentView.class);
	}
	/**
	 * 
	 * @param componentid 组件
	 * @return 组件视图
	 */
	private ComponentView getComponentView(String componentid) {
		List<ComponentView> componentList = ComponentContext.getComponents();
		for (ComponentView componentView : componentList) {
			if (componentView.getComponentid().equals(componentid)) {
				return componentView;
			}
		}
		return null;
	}
}
