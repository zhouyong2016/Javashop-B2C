package com.enation.app.shop.component.bonus.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.DateUtil;


/**
 * 红包类型管理
 * @author kingapex
 *2013-8-13下午3:10:21
 */
@Service("bonusTypeManager")
public class BonusTypeManager implements IBonusTypeManager {
	
	@Autowired
	private IDaoSupport daoSupport;

	
	@Override
	@Log(type=LogType.GOODS,detail="新添加一个名为${bronusType.type_name}的优惠券")
	public void add(BonusType bronusType) { 
		this.daoSupport.insert("es_bonus_type", bronusType);

	}

	@Override
	@Log(type=LogType.GOODS,detail="修改一个名为${bronusType.type_name}的优惠券信息")
	public void update(BonusType bronusType) {
		this.daoSupport.update("es_bonus_type", bronusType," type_id="+bronusType.getType_id());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer[] bonusTypeId) {
		
		for(int typeid:bonusTypeId){
			this.daoSupport.execute("delete from es_member_bonus where bonus_type_id=?", typeid);
			this.daoSupport.execute("delete from es_bonus_type where type_id=?",typeid);
		}
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.bonus.service.IBonusTypeManager#del(java.lang.Integer)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="删除ID为${bonusTypeId}的优惠券活动")
	public void del(Integer bonusTypeId) {
		this.daoSupport.execute("delete from es_bonus_type where type_id = ?", bonusTypeId);
		
		//将已经结束的促销活动关联的优惠券信息去除 by_DMRain 2016-6-16
		String sql = "update es_activity_detail set is_send_bonus = 0,bonus_id = null where bonus_id = ?";
		this.daoSupport.execute(sql, bonusTypeId);
	}
	
	@Override
	public Page list(int page, int pageSize) {
		String sql = "select * from es_bonus_type order by type_id desc";
		return this.daoSupport.queryForPage(sql, page, pageSize, BonusType.class);
	}

	@Override
	public BonusType get(int typeid) {
		String sql ="select * from es_bonus_type  where type_id =?";
		return (BonusType) this.daoSupport.queryForObject(sql, BonusType.class, typeid);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.bonus.service.IBonusTypeManager#checkIsSend(java.lang.Integer)
	 */
	@Override
	public int checkIsSend(Integer type_id) {
		String sql = "select create_num from es_bonus_type where type_id = ?";
		int result = this.daoSupport.queryForInt(sql, type_id);
		result = result > 0 ? 1 : 0;
		return result;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.bonus.service.IBonusTypeManager#checkForActivity(java.lang.Integer)
	 */
	@Override
	public int checkForActivity(Integer type_id) {
		String sql = "select count(0) from es_activity a inner join es_activity_detail ad on a.activity_id = ad.activity_id "
				+ "where a.disabled = 0 and a.end_time > ? and ad.bonus_id = ?";
		int result = this.daoSupport.queryForInt(sql, DateUtil.getDateline(), type_id);
		result = result > 0 ? 1 : 0;
		return result;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.component.bonus.service.IBonusTypeManager#checkRecognition(java.lang.String, java.lang.Integer)
	 */
	@Override
	public int checkRecognition(String recognition, Integer type_id) {
		String sql = "select count(0) from es_bonus_type where recognition = ?";
		
		//如果优惠券ID不等于0
		if (type_id != 0) {
			sql += " and type_id != " + type_id + "";
		}
		
		int result = this.daoSupport.queryForInt(sql, recognition);
		result = result > 0 ? 1 : 0;
		return result;
	}


}
