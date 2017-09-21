package com.enation.app.shop.core.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.Logi;
import com.enation.app.shop.core.order.service.ILogiManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;
 
/**
 * 物流公司管理类
 * @author LiFenLong 2014-4-2;4.0改版修改delete方法参数为integer[]
 * @version2.0 wangxin 6.0 改造
 */
@Service("logiManager")
public class LogiManager implements ILogiManager{
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ILogiManager#delete(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.SETTING,detail="删除物流公司")
	public void delete(Integer[] logi_id) {
		
		String id =StringUtil.implode(",", logi_id);
		if(id==null || id.equals("")){return ;}
		String sql = "delete from es_logi_company where id in (" + id + ")";
		this.daoSupport.execute(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ILogiManager#getLogiById(java.lang.Integer)
	 */
	@Override
	public Logi getLogiById(Integer id) {
		String sql  = "select * from es_logi_company where id=?";
		Logi a =  this.daoSupport.queryForObject(sql, Logi.class, id);
		return a;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ILogiManager#pageLogi(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page pageLogi(String order, Integer page, Integer pageSize) {
		order = order == null ? " id desc" : order;
		String sql = "select * from es_logi_company";
		sql += " order by  " + order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ILogiManager#saveAdd(com.enation.app.shop.core.order.model.Logi)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="添加了一个名为${logi.name}的物流公司")
	public void saveAdd(Logi logi) {
		this.daoSupport.insert("es_logi_company", logi);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ILogiManager#saveEdit(com.enation.app.shop.core.order.model.Logi)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="修改了名为${logi.name}的物流公司信息")
	public void saveEdit(Logi logi ) {
		this.daoSupport.update("es_logi_company", logi, "id="+logi.getId());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.ILogiManager#list()
	 */
	@Override
	public List list() {
		String sql = "select * from es_logi_company";
		return this.daoSupport.queryForList(sql);
	}

	@Override
	public Logi getLogiByCode(String code) {
		// TODO Auto-generated method stub
		String sql  = "select * from es_logi_company where code=?";
		Logi a =  this.daoSupport.queryForObject(sql, Logi.class, code);
		return a;
	}

	@Override
	public Logi getLogiByName(String name) {
		// TODO Auto-generated method stub
		String sql  = "select * from es_logi_company where name=?";
		Logi a =  this.daoSupport.queryForObject(sql, Logi.class, name);
		return a;
	}
}
