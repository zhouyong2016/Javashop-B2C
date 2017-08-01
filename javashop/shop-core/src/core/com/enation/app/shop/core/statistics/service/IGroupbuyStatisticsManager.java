package com.enation.app.shop.core.statistics.service;

import com.enation.framework.database.Page;

/**
 * 团购统计
 * @author kanon
 *
 */
public interface IGroupbuyStatisticsManager {

	/**
	 * 获取团购统计分页列表
	 * @param keyword 关键字
	 * @param pageNo 分页页数
	 * @param pageSize 每页显示数量
	 * @return
	 */
	public Page groupbuyStatisticsList(String keyword,Integer pageNo,Integer pageSize);
}
