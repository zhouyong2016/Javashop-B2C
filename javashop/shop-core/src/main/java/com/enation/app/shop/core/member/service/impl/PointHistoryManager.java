package com.enation.app.shop.core.member.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.model.PointHistory;
import com.enation.app.shop.core.member.service.IPointHistoryManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;

/**
 * 会员积分日志
 * 
 * @author lzf<br/>
 * version 2.0  2016-2-20 6.0版本升级改造  wangxin
 */
@Service("pointHistoryManager")
public class PointHistoryManager implements IPointHistoryManager {
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IPointHistoryManager#pagePointHistory(int, int, int)
	 */
	@Override
	public Page pagePointHistory(int pageNo, int pageSize,int pointType) {
		Member member = UserConext.getCurrentMember();

		String sql = "select * from es_point_history where member_id = ? and point_type=? order by time desc";

		Page webpage = this.daoSupport.queryForPage(sql, pageNo, pageSize,
				member.getMember_id(),pointType);
		return webpage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IPointHistoryManager#pagePointHistory(int, int)
	 */
	@Override
	public Page pagePointHistory(int pageNo, int pageSize) {
		Member member = UserConext.getCurrentMember();

		String sql = "select * from es_point_history where member_id = ? order by time desc";

		Page webpage = this.daoSupport.queryForPage(sql, pageNo, pageSize,
				member.getMember_id());
		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IPointHistoryManager#getConsumePoint(int)
	 */
	@Override
	public Long getConsumePoint(int pointType) {
		Member member = UserConext.getCurrentMember();
		Long result = this.daoSupport
				.queryForLong(
						"select SUM(point) from es_point_history where  member_id = ?  and  type=0  and point_type=?",
						member.getMember_id(),pointType);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IPointHistoryManager#getGainedPoint(int)
	 */
	@Override
	public Long getGainedPoint(int pointType) {
		Member member = UserConext.getCurrentMember();
		Long result = this.daoSupport.queryForLong(
						"select SUM(point) from es_point_history where    member_id = ? and  type=1  and point_type=?",
						member.getMember_id(),pointType);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IPointHistoryManager#addPointHistory(com.enation.app.shop.core.member.model.PointHistory)
	 */
	@Override
	public void addPointHistory(PointHistory pointHistory) {
		this.daoSupport.insert("es_point_history", pointHistory);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IPointHistoryManager#listPointHistory(int, int)
	 */
	@Override
	public List<PointHistory> listPointHistory(int member_id,int pointtype) {
		String sql = "select * from es_point_history where member_id = ? and point_type = ? order by time desc";
		List list = this.daoSupport.queryForList(sql,PointHistory.class, member_id, pointtype);
		return list;
	}
	
	/**
	 * 冻结积分列表(当前会员）
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IPointHistoryManager#pagePointFreeze(int, int)
	 */
	@Override
	public Page pagePointFreeze(int pageNo, int pageSize){
		Member member = UserConext.getCurrentMember();
		String sql = "select * from es_freeze_point where memberid = ? order by id desc";
		Page webpage = this.daoSupport.queryForPage(sql, pageNo, pageSize,
				member.getMember_id());
		return webpage;
	}

}
