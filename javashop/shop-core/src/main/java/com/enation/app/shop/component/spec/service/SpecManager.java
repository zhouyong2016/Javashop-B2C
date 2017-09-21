package com.enation.app.shop.component.spec.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import com.enation.app.shop.core.goods.model.mapper.SpecValueMapper;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;
import com.enation.app.shop.core.goods.model.SpecValue;
import com.enation.app.shop.core.goods.model.Specification;

/**
 * 规格管理
 * @author kingapex
 *2010-3-7上午11:19:20
 *@author Kanon 2015-2-20;6.0版本改造
 */
@Service("specManager")
public class SpecManager  implements ISpecManager {
	@Autowired
	private ISpecValueManager specValueManager;
	
	@Autowired
	private IDaoSupport  daoSupport;


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecManager#add(com.enation.app.shop.core.goods.model.Specification, java.util.List)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	@Log(type=LogType.GOODS,detail="添加了一个规格名为${spec.spec_name}的规格")
	public void add(Specification spec, List<SpecValue> valueList) {
		
		this.daoSupport.insert("es_specification", spec);
		Integer specId= this.daoSupport.getLastId("es_specification");
		if(valueList!=null){
			for(SpecValue value : valueList){
				value.setSpec_id(specId);
				value.setSpec_type(spec.getSpec_type());
				String path  = value.getSpec_image();
				if(path!=null){
					if(path.indexOf("http")>-1){
						String static_server_domain= SystemSetting.getStatic_server_domain();
						path = path.replaceAll(static_server_domain, EopSetting.FILE_STORE_PREFIX);
					}else{
						path="/"+EopSetting.PRODUCT+path;
					}
					
					
				}
				value.setSpec_image(path);
				specValueManager.add(value);
			}
		}
		
		
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecManager#checkUsed(java.lang.Integer[])
	 */
	@Override
	public boolean checkUsed(Integer[] idArray){
		if(idArray==null) return false;
		
		String idStr = StringUtil.arrayToString( idArray,",");
		String sql  ="select count(0)  from  es_goods_spec where spec_id in (-1,"+idStr+")";
		
		int count  = this.daoSupport.queryForInt(sql);
		if(count>0)
			return true;
		else
			return false;
	} 
	
	

	/*
	 * (non-Javadoc)
	 * @see com.enation.javashop.component.spec.service.ISpecManager#checkUsed(java.lang.Integer)
	 */
	@Override
	public boolean checkUsed(Integer valueid) {
		 String sql  ="select count(0) from es_goods_spec where spec_value_id=?";
		 
		return this.daoSupport.queryForInt(sql, valueid)>0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecManager#delete(java.lang.Integer[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	@Log(type=LogType.GOODS,detail="删除了一个规格")
	public void delete(Integer[] idArray) {
		
		String idStr = StringUtil.arrayToString( idArray,",");
		String sql ="delete from es_specification where spec_id in (-1,"+idStr+")";
		this.daoSupport.execute(sql);
		
		sql="delete from es_spec_values where spec_id in (-1,"+idStr+")";
		this.daoSupport.execute(sql);
		
		sql="delete from es_goods_spec where spec_id in (-1,"+idStr+")";
		this.daoSupport.execute(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecManager#edit(com.enation.app.shop.core.goods.model.Specification, java.util.List)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	@Log(type=LogType.GOODS,detail="修改了规格名为${spec.spec_name}的规格信息")
	public void edit(Specification spec, List<SpecValue> valueList) {
		
		//不存在规格值 ，直接全部删除
		if(valueList==null || valueList.isEmpty()){
			
			String sql ="delete from es_spec_values where spec_id=?";
			this.daoSupport.execute(sql, spec.getSpec_id());
			this.daoSupport.update("es_specification", spec, "spec_id="+spec.getSpec_id());
			
		}else{ //删除该删除的，添加该添加的，更新该更新的
			
			
			String valuidstr="";
			for(SpecValue value:valueList){
				int valueid = value.getSpec_value_id();
				if(!StringUtil.isEmpty(valuidstr)){
					valuidstr+=",";
				}
				valuidstr+=valueid;
				
			}
			String sql ="delete from es_spec_values where  spec_id=? and spec_value_id not in("+valuidstr+")"; //删除不存在的规格值id
			this.daoSupport.execute(sql, spec.getSpec_id());
			this.daoSupport.update("es_specification", spec, "spec_id="+spec.getSpec_id());
			for(SpecValue value : valueList){
				value.setSpec_id(spec.getSpec_id());
				value.setSpec_type(spec.getSpec_type());
				String path  = value.getSpec_image();
				if(path!=null){
					String static_server_domain= SystemSetting.getStatic_server_domain();
					path = path.replaceAll(static_server_domain, EopSetting.FILE_STORE_PREFIX);
				}
				value.setSpec_image(path);			
				if(value.getSpec_value_id().intValue()==0){ //id为0，新增的，添加
					specValueManager.add(value);
				}else{
					specValueManager.update(value);
				}
				
			}	
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecManager#list()
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public List list() {
		String sql ="select * from es_specification order by spec_id desc";
		return this.daoSupport.queryForList(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecManager#listSpecAndValue()
	 */
	@Override
	public List<Specification> listSpecAndValue(){
		
		String sql ="select * from es_specification";
		List<Specification> specList= this.daoSupport.queryForList(sql,Specification.class);
		
		sql ="select * from es_spec_values order by spec_id";
		List valueList=   this.daoSupport.queryForList(sql, new SpecValueMapper() );
		for(Specification spec :specList){
			List<SpecValue> newList =  new ArrayList<SpecValue>();
			for(SpecValue value:(List<SpecValue>)valueList){
				if(value.getSpec_id().intValue() == spec.getSpec_id().intValue()){
					newList.add(value);
				}
			}
			spec.setValueList(newList);
		}
		return specList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecManager#listSpecAndValueByType(int)
	 */
	@Override
	public List<Specification> listSpecAndValueByType(int goodsTypeId,int goodsId){

		String sql ="select s.* from es_specification s inner join es_type_spec ts on s.spec_id=ts.spec_id where ts.type_id=?";
		List<Specification> specList= this.daoSupport.queryForList(sql,Specification.class, goodsTypeId);
//		sql ="select * from es_spec_values where spec_id in (select spec_id from es_type_spec where type_id=?) order by spec_value_id";
//		List valueList= this.daoSupport.queryForList(sql, new SpecValueMapper(), goodsTypeId);
		List valueList=null;
		if(goodsId == 0){
			sql ="select * from es_spec_values where inherent_or_add=0 and spec_id in (select spec_id from es_type_spec where type_id=?) order by spec_value_id";
			valueList = this.daoSupport.queryForList(sql, new SpecValueMapper(), goodsTypeId);
		}else{
			sql ="select * from es_spec_values where spec_value_id in (select spec_value_id from es_goods_spec where goods_id =?) OR inherent_or_add=0 and spec_id in (select spec_id from es_type_spec where type_id=?) order by spec_value_id";
			valueList = this.daoSupport.queryForList(sql, new SpecValueMapper(), goodsId,goodsTypeId);
		}
		   
		for(Specification spec :specList){
			List<SpecValue> newList =  new ArrayList<SpecValue>();
			for(SpecValue value:(List<SpecValue>)valueList){
				if(value.getSpec_id().intValue() == spec.getSpec_id().intValue()){
					newList.add(value);
				}
			}
			spec.setValueList(newList);
		}		
		return specList;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecManager#get(java.lang.Integer)
	 */
	@Override
	public Map get(Integer spec_id){
		String sql ="select * from es_specification where spec_id=?";
		return this.daoSupport.queryForMap(sql, spec_id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.javashop.component.spec.service.ISpecManager#getProSpecList(int)
	 */
	@Override
	public List getProSpecList(int productid) {
		String sql ="select s.spec_name name ,sv.spec_value value  from  es_specification s ,es_spec_values sv ,es_goods_spec gs where gs.product_id=? and gs.spec_id=s.spec_id and gs.spec_value_id = sv.spec_value_id";
		return  this.daoSupport.queryForList(sql, productid);
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.spec.service.ISpecManager#listSpec(int, int)
	 */
	@Override
	public Page listSpec(int page, int pageSize) {
		String sql = "select * from es_specification order by spec_id desc";
		Page webPage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webPage;
	}

}
