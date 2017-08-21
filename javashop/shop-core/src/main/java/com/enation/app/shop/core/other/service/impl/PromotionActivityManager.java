package com.enation.app.shop.core.other.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.order.model.PromotionActivity;
import com.enation.app.shop.core.order.service.IPromotionActivityManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 促销活动
 * 
 * @author lzf<br/>
 *         2010-4-20下午04:56:34<br/>
 *         version 1.0
 */
@Deprecated
public class PromotionActivityManager  
		implements IPromotionActivityManager {

	@Autowired
	private IDaoSupport  daoSupport;
	
	public void add(PromotionActivity activity) {
		if(activity == null ) throw new  IllegalArgumentException("param activity is NULL");
		if(activity.getName() == null ) throw new  IllegalArgumentException("param activity.name is NULL");
		if(activity.getBegin_time() == null ) throw new  IllegalArgumentException("param activity.begin_time is NULL");
		if(activity.getEnd_time() == null ) throw new  IllegalArgumentException("param activity.end_time is NULL");
		this.daoSupport.insert("promotion_activity", activity);
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	
	public void delete(Integer[] idArray) {
		if (idArray != null && idArray.length > 0) {
			String ids = StringUtil.arrayToString(idArray, ",");
			this.daoSupport.execute(
					"delete from promotion_activity where id in (" + ids + ")");
			
			this.daoSupport.execute("delete from "+("es_pmt_member_lv")+" where pmt_id in(select pmt_id from "+("es_promotion")+" where pmta_id in(?))",ids);
			this.daoSupport.execute("delete from "+("es_pmt_goods")+" where pmt_id in(select pmt_id from "+("es_promotion")+" where pmta_id in(?))",ids);
			this.daoSupport.execute("delete from promotion where pmta_id in(?)", ids);
		}

	}

	
	public void edit(PromotionActivity activity) {
		if(activity.getId() == null ) throw new  IllegalArgumentException("param activity.id is NULL");
		if(activity.getName() == null ) throw new  IllegalArgumentException("param activity.name is NULL");
		if(activity.getBegin_time() == null ) throw new  IllegalArgumentException("param activity.begin_time is NULL");
		if(activity.getEnd_time() == null ) throw new  IllegalArgumentException("param activity.end_time is NULL");
		this.daoSupport.update("promotion_activity", activity, "id="
				+ activity.getId());

	}

	
	public PromotionActivity get(Integer id) {
		if(id == null ) throw new  IllegalArgumentException("param activity.id is NULL");
		PromotionActivity activity = this.daoSupport.queryForObject(
				"select * from promotion_activity where id = ?",
				PromotionActivity.class, id);
		if(activity == null) throw new ObjectNotFoundException("activity is NULL");
		return activity;
	}

	
	public Page list(int pageNo, int pageSize) {
		String sql = "select * from promotion_activity order by id desc";
		return this.daoSupport.queryForPage(sql, pageNo, pageSize);
	}

}
