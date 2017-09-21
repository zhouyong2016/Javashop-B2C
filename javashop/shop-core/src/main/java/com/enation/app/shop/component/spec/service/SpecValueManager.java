package com.enation.app.shop.component.spec.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.SpecValue;
import com.enation.app.shop.core.goods.model.mapper.SpecValueMapper;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.database.IDaoSupport;

/**
 * 规格值管理
 * @author kingapex
 *2010-3-7下午06:33:06
 *@author Kanon 2016-2-20;6.0版本改造
 */
@Service
public class SpecValueManager implements ISpecValueManager {

	@Autowired
	private IDaoSupport  daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecValueManager#add(com.enation.app.shop.core.goods.model.SpecValue)
	 */
	@Override
	public void add(SpecValue value) {
	   this.daoSupport.insert("es_spec_values",value);
	   int specValueId =this.daoSupport.getLastId("es_spec_values");
	   value.setSpec_value_id(specValueId);

	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecValueManager#update(com.enation.app.shop.core.goods.model.SpecValue)
	 */
	@Override
	public void update(SpecValue value) {
		this.daoSupport.update("es_spec_values", value, "spec_value_id="+ value.getSpec_value_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecValueManager#list(java.lang.Integer)
	 */
	@Override
	public List<SpecValue> list(Integer specId) {
		String sql ="select * from es_spec_values where inherent_or_add=0 and spec_id =?";
		List valueList = this.daoSupport.queryForList(sql, new SpecValueMapper() ,specId);
		return valueList;
	}
	

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecValueManager#get(java.lang.Integer)
	 */
	@Override
	public Map get(Integer valueId) {
		String sql ="select sv.*,s.spec_type from es_spec_values sv,es_specification s  where sv.spec_id=s.spec_id and sv.spec_value_id =?"; 
		Map temp = this.daoSupport.queryForMap(sql, valueId);
		String spec_image = (String)temp.get("spec_image");
		if(spec_image!=null){
			spec_image  =StaticResourcesUtil.convertToUrl(spec_image); 
		}
		temp.put("spec_image", spec_image);
		return temp;
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecValueManager#checkSpecValue(java.lang.String)
	 */
	@Override
	public int checkSpecValue(String specValue,String specImage) {
		String sql = "select count(0) from es_spec_values where spec_value= ? ";
		if(specImage!=""){
			sql += " and spec_image = '" + specImage+" ' ";
		}
		int count = this.daoSupport.queryForInt(sql, specValue);
		count = count > 0 ? 1 : 0;
		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecValueManager#delInherentSpec(java.lang.Integer)
	 */
	@Override
	public void delInherentSpec(Integer goodsId) {
		String sql = "DELETE from es_spec_values where inherent_or_add=1 and spec_value_id in (select spec_value_id from es_goods_spec where goods_id=?)";
		this.daoSupport.execute(sql, goodsId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecValueManager#selSpecValAndId(int, java.lang.String)
	 */
	@Override
	public int selSpecValAndId(int specId, String spec_value, String image) {
		String sql ="select spec_value_id from es_spec_values where spec_id=? and spec_value=?  ";
		if(image!=""){
			sql += " and spec_image = '" + image+" ' ";
		}
		return this.daoSupport.queryForInt(sql, specId,spec_value);
	}
}
