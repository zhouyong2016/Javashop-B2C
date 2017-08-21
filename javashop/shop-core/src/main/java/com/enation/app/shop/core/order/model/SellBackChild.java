package com.enation.app.shop.core.order.model;

/**
 * 退货商品子项 （整箱商品内的 单个商品项）
 * @author FengXingLong
 * 2015-07-15
 */
public class SellBackChild {
    private Integer id;
    private Integer order_id;	//订单id
    private Integer goods_id;	//商品id
    private Integer parent_id;	//所属整箱商品id
    private Integer return_num;	//退货数量
    private Integer storage_num;//已退货数量
   
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	public Integer getParent_id() {
		return parent_id;
	}
	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}
	public Integer getReturn_num() {
		return return_num;
	}
	public void setReturn_num(Integer return_num) {
		this.return_num = return_num;
	}
	public Integer getStorage_num() {
		return storage_num;
	}
	public void setStorage_num(Integer storage_num) {
		this.storage_num = storage_num;
	}
	   
	   
}
