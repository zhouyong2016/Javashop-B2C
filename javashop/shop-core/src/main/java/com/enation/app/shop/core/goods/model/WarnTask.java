package com.enation.app.shop.core.goods.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class WarnTask implements Serializable {

    private Integer id;
    private Integer catid;   
    private Integer goodsid;
    private Integer depotid;    
    private Integer flag;       
    private String sphere;
    private String cylinder;
    private String productids;
    @PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCatid() {
		return catid;
	}
	public void setCatid(Integer catid) {
		this.catid = catid;
	}
	public Integer getGoodsid() {
		return goodsid;
	}
	public void setGoodsid(Integer goodsid) {
		this.goodsid = goodsid;
	}
	public Integer getDepotid() {
		return depotid;
	}
	public void setDepotid(Integer depotid) {
		this.depotid = depotid;
	}
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public String getSphere() {
		return sphere;
	}
	public void setSphere(String sphere) {
		this.sphere = sphere;
	}
	public String getCylinder() {
		return cylinder;
	}
	public void setCylinder(String cylinder) {
		this.cylinder = cylinder;
	}
	public String getProductids() {
		return productids;
	}
	public void setProductids(String productids) {
		this.productids = productids;
	}
    
    
    
}
