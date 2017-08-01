package com.enation.app.shop.core.order.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 货运明细
 * @author kingapex
 *2010-4-8上午09:57:52
 */
public class DeliveryItem {
 
	private Integer    item_id;    
	private int order_itemid;//订单货物项id 20110924新增，是数据库字段
	private Integer delivery_id;  
	private Integer goods_id;
	private Integer product_id;
	private String sn;                  
	private String name;                 
	private Integer  num;
	private Integer itemtype;  //货物类型
	private Integer depotId;  //发货仓库Id 2014-6-8 李奋龙
	
	@PrimaryKeyField
	public Integer getItem_id() {
		return item_id;
	}
	public void setItem_id(Integer itemId) {
		item_id = itemId;
	}
	public Integer getDelivery_id() {
		return delivery_id;
	}
	public void setDelivery_id(Integer deliveryId) {
		delivery_id = deliveryId;
	}
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer productId) {
		product_id = productId;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
 
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goodsId) {
		goods_id = goodsId;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Integer getItemtype() {
		return itemtype;
	}
	public void setItemtype(Integer itemtype) {
		this.itemtype = itemtype;
	}
	public int getOrder_itemid() {
		return order_itemid;
	}
	public void setOrder_itemid(int order_itemid) {
		this.order_itemid = order_itemid;
	}
	public Integer getDepotId() {
		return depotId;
	}
	public void setDepotId(Integer depotId) {
		this.depotId = depotId;
	}

}
