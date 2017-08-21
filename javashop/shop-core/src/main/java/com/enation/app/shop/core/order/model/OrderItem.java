package com.enation.app.shop.core.order.model;

import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.database.DynamicField;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;
import com.enation.framework.util.StringUtil;

/**
 * 
 * @author kingapex 2010-4-6下午04:11:42
 */
public class OrderItem extends DynamicField implements java.io.Serializable {

	private static final long serialVersionUID = 8531790664258169824L;

	private Integer item_id;
	private Integer order_id;
	private Integer goods_id;
	private Integer product_id;

	private Integer num;
	private Integer ship_num;
	private String name;
	private String sn;
	private String image;
	private Integer store; // 对应货品的库存
	private String addon;
	private Integer cat_id; // 此商品的分类
	private Double price;
	private int gainedpoint;
	private int state;// 订单货物状态
	private String change_goods_name;// 换到的货物名称
	private Integer change_goods_id;// 换到的货物ID

	private String unit;

	private String depotId; // 库房Id 2014-6-8 李奋龙

	/** 商品类型 2016-02-10 add_by_Sylow */
	private int goods_type;

	private String other;

	private String exchange;//add by jianghongyan 积分相关信息
	
	private Integer exchange_point;//add by jianghongyan 订单项消费积分
	
	private Integer snapshot_id;//商品快照 2016-12-5 张继平
	private Integer snapshot_switch;//快照是否显示 1true 0 false 2016-12-5 张继平
	
	@NotDbField
	public Integer getExchange_point() {
		return exchange_point;
	}

	public void setExchange_point(Integer exchange_point) {
		this.exchange_point = exchange_point;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getChange_goods_name() {
		return change_goods_name;
	}

	public void setChange_goods_name(String change_goods_name) {
		this.change_goods_name = change_goods_name;
	}

	public Integer getChange_goods_id() {
		return change_goods_id;
	}

	public void setChange_goods_id(Integer change_goods_id) {
		this.change_goods_id = change_goods_id;
	}

	public Integer getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}

	@PrimaryKeyField
	public Integer getItem_id() {
		return item_id;
	}

	public void setItem_id(Integer item_id) {
		this.item_id = item_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
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

	public int getGainedpoint() {
		return gainedpoint;
	}

	public void setGainedpoint(int gainedpoint) {
		this.gainedpoint = gainedpoint;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@NotDbField
	public Integer getStore() {
		return store;
	}

	public void setStore(Integer store) {
		this.store = store;
	}

	public String getAddon() {
		return addon;
	}

	public void setAddon(String addon) {
		this.addon = addon;
	}

	public int getCat_id() {
		return cat_id;
	}

	public void setCat_id(int cat_id) {
		this.cat_id = cat_id;
	}

	@NotDbField
	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getImage() {

		if (!StringUtil.isEmpty(image)) {
			image = StaticResourcesUtil.convertToUrl(image);
		}

		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getDepotId() {
		return depotId;
	}

	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}

	public int getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(int goods_type) {
		this.goods_type = goods_type;
	}

	public Integer getSnapshot_id() {
		return snapshot_id;
	}

	public void setSnapshot_id(Integer snapshot_id) {
		this.snapshot_id = snapshot_id;
	}
	@NotDbField
	public Integer getSnapshot_switch() {
		return snapshot_switch;
	}

	public void setSnapshot_switch(Integer snapshot_switch) {
		this.snapshot_switch = snapshot_switch;
	}

	
}