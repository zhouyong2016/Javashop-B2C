/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：会员统计实现类
 *  修改人：Kanon
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.statistics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.statistics.service.IGroupbuyStatisticsManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
@Service("groupbuyStatisticsManager")
public class GroupbuyStatisticsManager implements IGroupbuyStatisticsManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IGroupbuyStatisticsManager#groupbuyStatisticsList(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Page groupbuyStatisticsList(String keyword, Integer pageNo,Integer pageSize) {
		//获取团购统计分页列表
		StringBuffer sql=new StringBuffer("select gg.*,ga.start_time,ga.end_time from es_groupbuy_goods gg INNER JOIN es_groupbuy_active ga ON ga.act_id=gg.act_id");
		if(!StringUtil.isEmpty(keyword)){
			sql.append(" WHERE gb_name like'%").append(keyword).append("%'");
		}
		sql.append("  ORDER BY gb_id");
		
		return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}
}
