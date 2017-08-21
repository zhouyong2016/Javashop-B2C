package com.enation.app.shop.core.goods.model.support;

import java.util.List;

import com.enation.app.shop.core.goods.model.Attribute;
import com.enation.app.shop.core.goods.model.GoodsType;


public class GoodsTypeDTO extends GoodsType {
	
	private List<Attribute> propList;
	private ParamGroup[] paramGroups;
	private List brandList;
	private List specList;
	
	public ParamGroup[] getParamGroups() {
		return paramGroups;
	}
	public void setParamGroups(ParamGroup[] paramGroups) {
		this.paramGroups = paramGroups;
	}
	public List<Attribute> getPropList() {
		return propList;
	}
	public void setPropList(List<Attribute> propList) {
		this.propList = propList;
	}
	public List getBrandList() {
		return brandList;
	}
	public void setBrandList(List brandList) {
		this.brandList = brandList;
	}

	public List getSpecList() {
		return specList;
	}

	public void setSpecList(List specList) {
		this.specList = specList;
	}
}
