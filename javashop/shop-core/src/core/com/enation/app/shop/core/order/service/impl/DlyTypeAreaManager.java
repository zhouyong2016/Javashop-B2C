package com.enation.app.shop.core.order.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import com.enation.app.shop.core.order.service.IDlyTypeAreaManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;

public class DlyTypeAreaManager   implements
		IDlyTypeAreaManager {
	@Autowired
	private IDaoSupport daoSupport;
	@Override
	public Map listAllByRegion(String regionid) {
		String sql = "select dta.type_id,dta.area_id_group,dta.area_name_group,dta.has_cod,dta.config,dt.name,dt.is_same,dt.config as dt_config  from es_dly_type_area dta left join es_dly_type dt on dta.type_id = dt.type_id";
		List<Map> typeAreaList = null;
		if (regionid != null) {
			typeAreaList = this.daoSupport.queryForList(sql, new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int arg1)
						throws SQLException {
					Map map = new HashMap();
					map.put("type_id", rs.getInt("type_id"));
					map.put("area_name_group", rs.getString("area_name_group"));
					map.put("area_id_group", rs.getString("area_id_group"));
					map.put("has_cod", rs.getInt("has_cod"));
					map.put("dta_config",
							JSONObject.fromObject(rs.getString("config")));
					map.put("name", rs.getString("name"));
					map.put("is_same", rs.getInt("is_same"));
					map.put("config",
							JSONObject.fromObject(rs.getString("dt_config")));
					return map;
				}
			});
			if(typeAreaList != null &&  typeAreaList.size() != 0){
				Map area = null;
				for (Map map : typeAreaList) {
					if(map.get("area_id_group").toString().indexOf(","+regionid.trim()+",") != -1 ){
						int cod = this.queryByrdgion(regionid);
						map.put("has_cod", cod);
						area = map;
						break;
					}else{
						continue;
					}
				}
				if(area == null){
					return this.queryOtherRegions(1,regionid);
				}else{
					return area;
				}
				
			}else{
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public int queryByrdgion(String regionid) {
		int cod = 0;
		if(!StringUtil.isEmpty(regionid)){
			cod = this.daoSupport.queryForInt("select cod from regions where region_id = "+regionid);
		}
		return cod;
	}
	
	
	public Map queryOtherRegions(Integer type_id,String regionid){
		String sql = "select * from dly_type where type_id = "+type_id;
		List<Map> typeAreaList  = this.daoSupport.queryForList(sql, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int arg1)
					throws SQLException {
				Map map = new HashMap();
				map.put("type_id", rs.getInt("type_id"));
				map.put("area_name_group", null);
				map.put("area_id_group", null);
				map.put("has_cod", rs.getInt("has_cod"));
				map.put("dta_config",	JSONObject.fromObject(rs.getString("config")));
				map.put("name", rs.getString("name"));
				map.put("is_same", rs.getInt("is_same"));
				return map;
			}
		});
		Map area = null;
		if(typeAreaList != null && !typeAreaList.isEmpty()){
			area = typeAreaList.get(0);
			int cod = this.queryByrdgion(regionid);
			area.put("has_cod", cod);
		}
		 return area;
	}
}
