package com.enation.app.shop.core.goods.model;

import com.enation.eop.resource.model.AdminUser;

/**
 * 库存管理员
 * @author kingapex
 *
 */
public class DepotUser extends AdminUser {
	
	private Integer depotid;

	public Integer getDepotid() {
		return depotid;
	}

	public void setDepotid(Integer depotid) {
		this.depotid = depotid;
	}

 
	
}
