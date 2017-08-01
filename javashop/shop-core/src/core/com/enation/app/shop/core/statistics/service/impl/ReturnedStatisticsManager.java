/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：会员统计实现类
 *  修改人：Kanon
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.statistics.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.model.SellBackStatus;
import com.enation.app.shop.core.statistics.service.IReturnedStatisticsManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;
@Service("returnedStatisticsManager")
public class ReturnedStatisticsManager implements IReturnedStatisticsManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IReturnedStatisticsManager#statisticsMonth_Amount(long, long)
	 */
	@Override
	public List<Map> statisticsMonth_Amount(long year,long month){
		String condition_sql = createSql(1, year+"-"+month);
		String sql =   "select count(0) as t_num,SUM(alltotal_pay) as t_money, case "+ condition_sql +" as month  from es_sellback_list where tradestatus=? group by case "+condition_sql;
		List list = this.daoSupport.queryForList(sql,SellBackStatus.refund.getValue()); 
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IReturnedStatisticsManager#statisticsYear_Amount(int)
	 */
	@Override
	public List<Map> statisticsYear_Amount(int year) {
		String condition_sql = createSqlByYear(1, year+"");
		String sql =  "select count(0) as t_num,SUM(alltotal_pay) as t_money, case "+ condition_sql +" as month  from es_sellback_list where tradestatus=? group by case "+condition_sql;
		List list = this.daoSupport.queryForList(sql,SellBackStatus.refund.getValue());
		return list;
	}
	
	/**
	 * 创建SQL语句
	 * @param type 1.按照月查询(查询出此月每天的下单金额)
	 * @param date 时间，选中的某一月
	 */
	private  String createSql(int type,String date){
		StringBuffer sql =new StringBuffer();
	  
		for(int i=1;i<=29;i++){
			  String day = "0"+i;
			  day= day.substring( day.length()-2,day.length());
			  String day_date= date+"-"+day;
			  long start = DateUtil.getDateline(  day_date+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
			  long end = DateUtil.getDateline(  day_date+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			  sql.append(" when regtime >= "+start +" and   regtime <="+ end +" then "+i );
		 }
		
		 sql.append(" else 0 end");
		 return sql.toString();
	}
	
	/**
	 * 创建SQL语句
	 * @param type 1.按照年查询(查询出此年每月的下单金额)
	 * @param date 时间，选中的某一年
	 */
	private  String createSqlByYear(int type,String date){
		StringBuffer sql =new StringBuffer();
		for(int i=1;i<=12;i++){
			String day = "0"+i;
			day = day.substring( day.length()-2,day.length());
			String day_date = date+"-"+day;
			long start = DateUtil.getDateline(day_date+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end = DateUtil.getDateline(day_date+"-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" when regtime >= "+start +" and  regtime <="+ end +" then "+i );
		 }
		 sql.append(" else 0 end");
		 return sql.toString();
	}
	
}
