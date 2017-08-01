package com.enation.app.shop.core.other.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.other.model.ActivityGift;
import com.enation.app.shop.core.other.service.IActivityGiftManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 促销活动管理接口实现类
 * 2016-5-24
 * @author DMRain
 * @version 1.0
 */
@Service("activityGiftManager")
public class ActivityGiftManager implements IActivityGiftManager{

	@Autowired
	private IDaoSupport daoSupport;
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IActivityGiftManager#list(java.lang.String, int, int)
	 */
	@Override
	public Page list(String keyword, int page, int pageSize) {
		String sql = "select * from es_activity_gift where disabled = 0";
		
		//判断搜索关键字是否为空
		if (keyword != null && !StringUtil.isEmpty(keyword)) {
			sql += " and gift_name like '%" + keyword + "%'";
		}
		
		sql += " order by create_time desc";
		
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IActivityGiftManager#add(com.enation.app.shop.core.other.model.ActivityGift)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="添加一个赠品名为${gift.gift_name}的赠品")
	public void add(ActivityGift gift) {
		this.daoSupport.insert("es_activity_gift", gift);
		
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IActivityGiftManager#edit(com.enation.app.shop.core.other.model.ActivityGift)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="修改赠品名为${gift.gift_name}的赠品信息")
	public void edit(ActivityGift gift) {
		this.daoSupport.update("es_activity_gift", gift, "gift_id="+gift.getGift_id());
		
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IActivityGiftManager#get(java.lang.Integer)
	 */
	@Override
	public ActivityGift get(Integer gift_id) {
		String sql = "select * from es_activity_gift where gift_id = ?";
		return this.daoSupport.queryForObject(sql, ActivityGift.class, gift_id);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IActivityGiftManager#delete(java.lang.Integer)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Log(type=LogType.GOODS,detail="删除赠品ID为${gift_id}的赠品信息")
	public void delete(Integer gift_id) {
		String sql = "update es_activity_gift set disabled = 1 where gift_id = ?";
		this.daoSupport.execute(sql, gift_id);
		
		//将已经结束的促销活动关联的赠品信息去除
		sql = "update es_activity_detail set is_send_gift = 0,gift_id = null where gift_id = ?";
		this.daoSupport.execute(sql, gift_id);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IActivityGiftManager#checkGiftInAct(java.lang.Integer)
	 */
	@Override
	public int checkGiftInAct(Integer gift_id) {
		String sql = "select count(ad.gift_id) from es_activity a left join es_activity_detail ad on a.activity_id = ad.activity_id where a.end_time > ? and ad.gift_id = ? and a.disabled = 0";
		int result = this.daoSupport.queryForInt(sql, DateUtil.getDateline(), gift_id);
		result = result > 0 ? 1 : 0;
		return result;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IActivityGiftManager#getGiftList()
	 */
	@Override
	public List getGiftList() {
		String sql = "select * from es_activity_gift where disabled = 0 order by create_time desc";
		return this.daoSupport.queryForList(sql);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IActivityGiftManager#getBonusList()
	 */
	@Override
	public List getBonusList() {
		String sql = "select * from es_bonus_type";
		return this.daoSupport.queryForList(sql);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.other.service.IActivityGiftManager#updateGiftStore(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="修改赠品名为${gift.gift_name}赠品库存数量")
	public void updateGiftStore(Integer gift_id, Integer type, Integer num) {
		String sql = "";
		
		//根据修改类型来修改赠品的库存
		if (type == 0) {
			sql = "update es_activity_gift set enable_store=(enable_store - " + num + ") where gift_id = ?";
		} else if (type == 1) {
			sql = "update es_activity_gift set enable_store=(enable_store + " + num + ") where gift_id = ?";
		} else if (type == 2) {
			sql = "update es_activity_gift set actual_store=(actual_store - " + num + ") where gift_id = ?";
		} else if (type == 3) {
			sql = "update es_activity_gift set actual_store=(actual_store + " + num + ") where gift_id = ?";
		}
		
		this.daoSupport.execute(sql, gift_id);
	}

}
