package com.enation.app.shop.core.member.service;

import java.util.List;

import com.enation.app.shop.core.member.model.PointHistory;
import com.enation.framework.database.Page;

/**
 * 会员积分历史管理
 * 
 * @author lzf<br/>
 *         2010-3-22 上午11:23:00<br/>
 *         version 1.0<br/>
 */
public interface IPointHistoryManager {

	/**
	 * 积分历史列表(当前会员）
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param 积分类型 
	 * @return
	 */
	public Page pagePointHistory(int pageNo, int pageSize,int pointType);
	
	
	
	/**
	 * 积分历史列表
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page pagePointHistory(int pageNo, int pageSize);
	
	
	
	/**
	 * 列表指定会员 的积分日志
	 * @param member_id
	 * @param pointtype 积分类型：
	 * @return
	 */
	public List<PointHistory> listPointHistory(int member_id,int pointtype);

	/**
	 * 累计消费积分
	 * 
	 * @return
	 */
	public Long getConsumePoint(int pointType);

	/**
	 * 累计获得积分
	 * 
	 * @return
	 */
	public Long getGainedPoint(int pointType);

	/**
	 * 新增日志
	 * 
	 * @param pointHistory
	 */
	public void addPointHistory(PointHistory pointHistory);
	
	
	/**
	 * 冻结积分列表(当前会员）
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page pagePointFreeze(int pageNo, int pageSize);

}
