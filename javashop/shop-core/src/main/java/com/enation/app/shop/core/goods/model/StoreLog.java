package com.enation.app.shop.core.goods.model;

import java.io.SerializablePermission;

public class StoreLog {
	
	private Integer logid;
	private String goodsname;
	private Integer goodsid;
	private Integer productid;
	private Integer depot_type; //仓库类型 0网站1实体店
	private Integer op_type;   //操作类型 0进货 1实体店出货 2网店订单发货
	private Integer num;
	private String remark;
	private Long dateline;
	private Integer userid;
	private String username;
	private Integer depotid;
	private int enable_store;
	
	
	public Integer getLogid() {
		return logid;
	}
	public void setLogid(Integer logid) {
		this.logid = logid;
	}
	public String getGoodsname() {
		return goodsname;
	}
	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}
	public Integer getProductid() {
		return productid;
	}
	public void setProductid(Integer productid) {
		this.productid = productid;
	}
	
	public Integer getOp_type() {
		return op_type;
	}
	public void setOp_type(Integer op_type) {
		this.op_type = op_type;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getDepotid() {
		return depotid;
	}
	public void setDepotid(Integer depotid) {
		this.depotid = depotid;
	}
	public Integer getDepot_type() {
		return depot_type;
	}
	public void setDepot_type(Integer depot_type) {
		this.depot_type = depot_type;
	}
	public Integer getGoodsid() {
		return goodsid;
	}
	public void setGoodsid(Integer goodsid) {
		this.goodsid = goodsid;
	}
	public int getEnable_store() {
		return enable_store;
	}
	public void setEnable_store(int enable_store) {
		this.enable_store = enable_store;
	}
	public Long getDateline() {
		return dateline;
	}
	public void setDateline(Long dateline) {
		this.dateline = dateline;
	}
    
}
