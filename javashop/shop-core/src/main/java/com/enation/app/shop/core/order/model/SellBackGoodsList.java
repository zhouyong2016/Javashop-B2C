package com.enation.app.shop.core.order.model;

/**
 * 退货商品表<br>
 * 此表基本冗余es_order_items表<br>
 * 冗余信息的目的是为了查询、显示方便。<br>
 * 在es_order_items表中的基本信息属于快照是不是会变化的。<br>
 * @author lina
 *2013-11-10上午10:19:58
 */
public class SellBackGoodsList {
	
	private Integer id;
	private Integer recid;//退货单id
	private Integer goods_id;
	private Integer ship_num;//发货数量
	private Double price;//销售价格
	private Integer return_num;//退货数量
	private Integer storage_num;//入库数量
	private String goods_remark;//退货商品备注
	private Integer product_id; 
	private int return_type;
	
	/**
	 * 对应es_order_items表中的item id
	 * add by kingapex 2015-11-21
	 */
	private int item_id; 
	
	
	/**
	 * 规格字段，对应es_order_items表中的addon字段
	 *  add by kingapex 2015-11-21
	 */
	private String spec_json;
	
	
	/**
	 * 商品名称，对应es_order_items表的name字段
	 */
	private String goods_name;
	
	
	/**
	 * 商品sn，对应es_order_items表的sn字段
	 */
	private String goods_sn;
	
	
	/**
	 * 商品图片，对应es_order_items表的image字段
	 */
	private String goods_image; 
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRecid() {
		return recid;
	}
	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	public Integer getShip_num() {
		return ship_num;
	}
	public void setShip_num(Integer ship_num) {
		this.ship_num = ship_num;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
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
	public String getGoods_remark() {
		return goods_remark;
	}
	public void setGoods_remark(String goods_remark) {
		this.goods_remark = goods_remark;
	}
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	} 
	public int getItem_id() {
		return item_id;
	}
	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}
	public String getSpec_json() {
		return spec_json;
	}
	public void setSpec_json(String spec_json) {
		this.spec_json = spec_json;
	}
	public String getGoods_name() {
		return goods_name;
	}
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	public String getGoods_sn() {
		return goods_sn;
	}
	public void setGoods_sn(String goods_sn) {
		this.goods_sn = goods_sn;
	}
	public String getGoods_image() {
		return goods_image;
	}
	public void setGoods_image(String goods_image) {
		this.goods_image = goods_image;
	}
	public int getReturn_type() {
		return return_type;
	}
	public void setReturn_type(int return_type) {
		this.return_type = return_type;
	}
	
	
	
}
