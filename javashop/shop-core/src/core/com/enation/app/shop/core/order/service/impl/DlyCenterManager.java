package com.enation.app.shop.core.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.DlyCenter;
import com.enation.app.shop.core.order.service.IDlyCenterManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

/**
 * 发货中心
 * @author lzf<br/>
 * 2010-4-30上午10:14:35<br/>
 * version 1.0
 */
@Service("dlyCenterManager")
public class DlyCenterManager implements IDlyCenterManager {

	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyCenterManager#add(com.enation.app.shop.core.order.model.DlyCenter)
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="添加一个名为${dlyCenter.name}的发货中心")
	public void add(DlyCenter dlyCenter) {
		if(dlyCenter.getChoose().equals("true")){
			this.daoSupport.execute("update es_dly_center set choose='false' where choose='true'");
		}
		this.daoSupport.insert("es_dly_center", dlyCenter);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyCenterManager#delete(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="批量删除发货中心")
	public void delete(Integer[] id) {
		if(id== null  || id.length==0  ) return ;
		String ids = StringUtil.arrayToString(id, ",");
		this.daoSupport.execute("update es_dly_center set disabled = 'true' where dly_center_id in (" + ids + ")");

	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyCenterManager#del(java.lang.Integer)
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="删除一条ID为${dly_center_id}发货中心信息")
	public void del(Integer dly_center_id) {
		String sql = "update es_dly_center set disabled = 'true' where dly_center_id = ?";
		this.daoSupport.execute(sql, dly_center_id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyCenterManager#edit(com.enation.app.shop.core.order.model.DlyCenter)
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="修改了名为${dlyCenter.name}的发货中心信息")
	public void edit(DlyCenter dlyCenter) {
		if(dlyCenter.getChoose().equals("true")){
			this.daoSupport.execute("update es_dly_center set choose='false' where choose='true'");
		}
		this.daoSupport.update("es_dly_center", dlyCenter, "dly_center_id = " + dlyCenter.getDly_center_id());

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyCenterManager#list()
	 */
	@Override
	public List<DlyCenter> list() {
		return this.daoSupport.queryForList("select * from es_dly_center where disabled = 'false' order by dly_center_id desc", DlyCenter.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyCenterManager#get(java.lang.Integer)
	 */
	@Override
	public DlyCenter get(Integer dlyCenterId) {
		return this.daoSupport.queryForObject("select * from es_dly_center where dly_center_id = ?", DlyCenter.class, dlyCenterId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.order.service.IDlyCenterManager#getAllDlyNum()
	 */
	@Override
	public int getAllDlyNum() {
		String sql = "select count(0) from es_dly_center where disabled = 'false'";
		int total = this.daoSupport.queryForInt(sql);
		return total;
	}


}
