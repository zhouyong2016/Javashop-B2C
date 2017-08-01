package com.enation.app.shop.core.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.DlyArea;
import com.enation.app.shop.core.order.service.IAreaManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;

 
/**
 * 地区manager实现类
 * @author Sylow
 * @version v2.0,2016年2月23日 版本改造
 * @since v6.0
 */
@Service("areaManager")
public class AreaManager implements IAreaManager{
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IAreaManager#delete(java.lang.String)
	 */
	@Override
	public void delete(String id) {
		if (id == null || id.equals("")) {
			return;
		}
		String sql = "delete from es_dly_area where area_id in (" + id + ")";
		this.daoSupport.execute(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IAreaManager#getAll()
	 */
	@Override
	public List getAll() {
		String sql = "select * from es_dly_area order by area_id desc";
		List list = this.daoSupport.queryForList(sql);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IAreaManager#pageArea(java.lang.String, int, int)
	 */
	@Override
	public Page pageArea(String order, int page, int pageSize) {
		order = order == null ? " area_id desc" : order;
		String sql = "select * from es_dly_area";
		sql += " order by  " + order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IAreaManager#saveAdd(java.lang.String)
	 */
	@Override
	public void saveAdd(String name) {
		DlyArea dlyArea = new DlyArea();
		dlyArea.setName(name);
		this.daoSupport.insert("es_dly_area", dlyArea);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IAreaManager#saveEdit(java.lang.Integer, java.lang.String)
	 */
	@Override
	public void saveEdit(Integer areaId, String name) {
		String sql = "update es_dly_area set name = ? where area_id=?";
		this.daoSupport.execute(sql,name,areaId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IAreaManager#getDlyAreaById(java.lang.Integer)
	 */
	@Override
	public DlyArea getDlyAreaById(Integer areaId) {
		String sql = "select * from es_dly_area where area_id=?";
		DlyArea a = this.daoSupport.queryForObject(sql, DlyArea.class, areaId);
		return a;
	}
    
}
