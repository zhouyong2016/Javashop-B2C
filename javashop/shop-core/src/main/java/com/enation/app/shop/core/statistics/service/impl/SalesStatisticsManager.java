package com.enation.app.shop.core.statistics.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.app.shop.core.statistics.model.DayAmount;
import com.enation.app.shop.core.statistics.model.MonthAmount;
import com.enation.app.shop.core.statistics.service.ISalesStatisticsManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：销售统计 Manager
 *  修改人：xulipeng
 *  修改时间：2015-09-24
 *  修改内容：制定初版
 */
@Service("salesStatisticsManager")
public class SalesStatisticsManager  implements ISalesStatisticsManager {

	@Autowired
	private IDaoSupport daoSupport;
	
	public List<MonthAmount> statisticsMonth_Amount() {
		SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy");
		String year = sdfInput.format(new Date());
		String sql = "";
		if(EopSetting.DBTYPE.equals("1")){//是mysql
			sql = "select sum(a.order_amount) as amount, Date_format(FROM_UNIXTIME(a.create_time / 1000),'%Y-%m') as mo from es_order a where Date_format(FROM_UNIXTIME(a.create_time / 1000),'%Y') = ?  group by mo";
		}else if(EopSetting.DBTYPE.equals("3")){//是mssql
			sql = "select sum(a.order_amount) as amount, substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,7) as mo from es_order a where substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,4) = ?  group by substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,7)";
		}else{//是oracle
			//注意：需先在oracle中建立function
			/*代码如下：
			create or replace function FROM_UNIXTIME(mydate IN number) return date is
  				Result date;
			begin
  				Result := TO_DATE('01011970','mmddyyyy')+1/24/60/60*(MYDATE);
  				return(Result);
			end FROM_UNIXTIME;
			*/
//			String createfunction = "create or replace function FROM_UNIXTIME(mydate IN number) return date is"
//  				 +" Result date;"
//  				 +" begin"
//  				 +" Result := TO_DATE('01011970','mmddyyyy')+1/24/60/60*(MYDATE);"
//  				 +" return(Result);"
//  				 +" end FROM_UNIXTIME;";
//			this.daoSupport.execute(createfunction);
			sql = "select sum(a.order_amount) as amount, to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy-mm') as mo from es_order a where to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy') = ?  group by to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy-mm')";
		}
		List<Map> list = this.daoSupport.queryForList(sql, year);
		List<MonthAmount> target = new ArrayList<MonthAmount>();
		List<String> monthList = getMonthList();
		for(String month:monthList){
			MonthAmount ma = new MonthAmount();
			ma.setMonth(month);
			ma.setAmount(new Double(0));
			for(Map mapdata:list){
				if(mapdata.get("mo").equals(month)){
					ma.setAmount(Double.valueOf(mapdata.get("amount").toString()));
				}
			}
			target.add(ma);
		}
		return target;
	}
	
	
	public List<MonthAmount> statisticsMonth_Amount(String monthinput) {
		String year = monthinput.substring(0,4);
		String sql = "";
		if("1".equals(EopSetting.DBTYPE)){//是mysql
			sql = "select sum(a.order_amount) as amount, Date_format(FROM_UNIXTIME(a.create_time / 1000),'%Y-%m') as mo from es_order a where a.status = " + OrderStatus.ORDER_COMPLETE + " and Date_format(FROM_UNIXTIME(a.create_time / 1000),'%Y') = ?  group by mo";
		}else if("2".equals(EopSetting.DBTYPE)){//是oracle
			sql = "select sum(a.order_amount) as amount, to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy-mm') as mo from es_order a where a.status = " + OrderStatus.ORDER_COMPLETE + " and to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy') = ?  group by to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy-mm')";
		}else if("3".equals(EopSetting.DBTYPE)){//SQLServer
			sql = "select sum(order_amount) as amount, substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,7) as mo from es_order where status = " + OrderStatus.ORDER_COMPLETE + " and substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,7) = ? group by substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,7)";
		}
		List<Map> list = this.daoSupport.queryForList(sql, year);
		List<MonthAmount> target = new ArrayList<MonthAmount>();
		List<String> monthList = getMonthList(monthinput);
		for(String month:monthList){
			MonthAmount ma = new MonthAmount();
			ma.setMonth(month);
			ma.setAmount(new Double(0));
			for(Map mapdata:list){
				if(mapdata.get("mo").equals(month)){
					ma.setAmount(Double.valueOf(mapdata.get("amount").toString()));
				}
			}
			target.add(ma);
		}
		return target;
	}
	
	
	public List<DayAmount> statisticsDay_Amount() {
		SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM");
		String year = sdfInput.format(new Date());
		String sql = "";
		if(EopSetting.DBTYPE.equals("1")){//是mysql
			sql = "select sum(a.order_amount) as amount, Date_format(FROM_UNIXTIME(a.create_time / 1000),'%Y-%m-%d') as mo from es_order a where a.status = " + OrderStatus.ORDER_COMPLETE + " and Date_format(FROM_UNIXTIME(a.create_time / 1000),'%Y-%m') = ?  group by mo";
		}else if(EopSetting.DBTYPE.equals("2")){
			sql = "select sum(a.order_amount) as amount, to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy-mm-dd') as mo from es_order a where a.status = " + OrderStatus.ORDER_COMPLETE + " and to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy-mm') = ?  group by to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy-mm-dd')";
		}else{
			sql = "select sum(a.order_amount) as amount, substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,10) as mo from es_order a where a.status = " + OrderStatus.ORDER_COMPLETE + " and substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,7) = ?  group by substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,10)";
		}
		List<Map> list = this.daoSupport.queryForList(sql, year);
		List<DayAmount> target = new ArrayList<DayAmount>();
		List<String> dayList = getDayList();
		for(String day:dayList){
			DayAmount da = new DayAmount();
			da.setDay(day);
			da.setAmount(new Double(0));
			for(Map mapdata:list){
				if(mapdata.get("mo").equals(day)){
					da.setAmount(Double.valueOf(mapdata.get("amount").toString()));
				}
			}
			target.add(da);
		}
		return target;
	}

	
	public List<DayAmount> statisticsDay_Amount(String monthinput) {
		String sql = "";
		if("1".equals(EopSetting.DBTYPE)){//是mysql
			sql = "select sum(a.order_amount) as amount, Date_format(FROM_UNIXTIME(a.create_time / 1000),'%Y-%m-%d') as mo from es_order a where a.status = " + OrderStatus.ORDER_COMPLETE + " and Date_format(FROM_UNIXTIME(a.create_time / 1000),'%Y-%m') = ?   group by mo";
		}else if("2".equals(EopSetting.DBTYPE)){//Oracle
			sql = "select sum(a.order_amount) as amount, to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy-mm-dd') as mo from es_order  a where a.status = " + OrderStatus.ORDER_COMPLETE + " and to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy-mm') = ?   group by to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(a.create_time / 1000),'yyyy-mm-dd')";
		}else if("3".equals(EopSetting.DBTYPE)){//SQLServer
			sql = "select sum(order_amount) as amount, substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,10) as mo from es_order a where status = " + OrderStatus.ORDER_COMPLETE + " and substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,7) = ? group by substring(convert(varchar(10),dateadd(ss,create_time/1000 + 28800,'1970-01-01'),120),1,10)";
		}
		List<Map> list = this.daoSupport.queryForList(sql, monthinput);
		List<DayAmount> target = new ArrayList<DayAmount>();
		List<String> dayList = getDayList(monthinput);
		for(String day:dayList){
			DayAmount da = new DayAmount();
			da.setDay(day);
			da.setAmount(new Double(0));
			for(Map mapdata:list){
				if(mapdata.get("mo").equals(day)){
					da.setAmount(Double.valueOf(mapdata.get("amount").toString()));
				}
			}
			target.add(da);
		}
		return target;
	}

	
	private static List<String> getMonthList(){
		List<String> monthList = new ArrayList<String>();
		SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy");
		String year = sdfInput.format(new Date());
		DecimalFormat df = new DecimalFormat("00");
		for(int i=1;i<=12;i++){
			monthList.add(year + "-" + df.format(i));
		}
		return monthList;
	}
	
	private static List<String> getMonthList(String monthinput){
		List<String> monthList = new ArrayList<String>();
		String year = monthinput.substring(0,4);
		DecimalFormat df = new DecimalFormat("00");
		for(int i=1;i<=12;i++){
			monthList.add(year + "-" + df.format(i));
		}
		return monthList;
	}
	
	private static List<String> getDayList(){
		List<String> dayList = new ArrayList<String>();
		Date date = new Date();
		Calendar cal =Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM");
		String str_month = sdfInput.format(date);
		DecimalFormat df = new DecimalFormat("00");
		int count = days(year, month);
		for(int i=1;i<=count;i++){
			dayList.add(str_month + "-" + df.format(i));
		}
		return dayList;
	}
	
	private static List<String> getDayList(String monthinput){
		List<String> dayList = new ArrayList<String>();
		
		Date date =DateUtil.toDate(monthinput+"-01", "yyyy-MM-dd");// new Date(monthinput + "-01");
		 
		Calendar cal =Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		String str_month = monthinput;
		DecimalFormat df = new DecimalFormat("00");
		int count = days(year, month);
		for(int i=1;i<=count;i++){
			dayList.add(str_month + "-" + df.format(i));
		}
		return dayList;
	}
	
	public static int days(int year,int month){
		int days = 0;
		if(month!=2){
			switch(month){
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:days = 31 ;break;
			case 4:
			case 6:
			case 9:
			case 11:days = 30;

			}
		}else{
			if(year%4==0 && year%100!=0 || year%400==0)
				days = 29;
			else  
				days = 28;
		}
		return days;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISalesStatisticsManager#orderStatByPayment()
	 */
	public List<Map> orderStatByPayment(){
		String sql ="select count(0) num,sum(order_amount) amount,max(payment_name) payment_name from es_order where disabled=0 group by shipping_id";
		return this.daoSupport.queryForList(sql);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISalesStatisticsManager#orderStatByShip()
	 */
	public List<Map> orderStatByShip(){
		String sql ="select count(0) num,sum(order_amount) amount,max(shipping_type) shipping_type from es_order where disabled=0 group by shipping_id";
		return this.daoSupport.queryForList(sql);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISalesStatisticsManager#statisticsYear_Amount(java.lang.Integer, int)
	 */
	@Override
	public List<Map> statisticsYear_Amount(Integer status, int year) {
		String condition_sql = createSqlByYear(1, year+"");
		String sql =  "select count(0) as t_num,SUM(need_pay_money) as t_money, case "+ condition_sql +" as month  from es_order o where 1=1 ";
				
		if( status!=null && status.intValue()!=0 && status.intValue()!=99 ){
			sql += " and o.status="+status;
		}
		
		sql +=  " group by case "+condition_sql;
		List list = this.daoSupport.queryForList(sql);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISalesStatisticsManager#statisticsMonth_Amount(java.lang.Integer, long, long)
	 */
	@Override
	public List<Map> statisticsMonth_Amount(Integer status, int year,int month) {
		String condition_sql = createSql(1, year,month);
		String sql =  "select count(0) as t_num,SUM(need_pay_money) as t_money, case "+ condition_sql +" as month  from es_order o where 1=1";
		
		if( status!=null && status.intValue()!=99 ){
			sql += " and o.status="+status;
		}
		
		sql+= " group by case "+condition_sql;
		List list = this.daoSupport.queryForList(sql);
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IStatisticsManager#hotGoodsTop_Money()
	 */
	@Override
	public List<Map> hotGoodsTop_Money() {
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(i.price*i.num) as t_price,i.`name`,c.`name` from es_order_items i left join es_order o on i.order_id=o.order_id left join es_goods_cat c on c.cat_id = i.cat_id ");
		sql.append("");
		sql.append(" GROUP BY i.goods_id  ORDER BY t_price DESC LIMIT 0,50");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IStatisticsManager#hotGoodsTop_Num()
	 */
	@Override
	public List<Map> hotGoodsTop_Num() {
		return null;
	}
	
	
	
	/**
	 * 创建SQL语句
	 * @param type 1.按照月查询(查询出此月每天的下单金额)
	 * @param date
	 */
	public static String createSql(int type,int year,int month){
		StringBuffer sql =new StringBuffer();
		String date = year+"-"+month;
		int day = getDaysByYearMonth(year, month);
		for(int i=1;i<=day;i++){
			String day_date= date+"-"+i;
			long start = DateUtil.getDateline(  day_date+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end = DateUtil.getDateline(  day_date+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" when create_time >= "+start +" and   create_time <="+ end +" then "+i );
		 }
		
		 sql.append(" else 0 end");
		 return sql.toString();
  }
	
	/**
	 * 创建SQL语句
	 * @param type 1.按照年查询(查询出此年每月的下单金额)
	 * @param date
	 */
	public static String createSqlByYear(int type,String date){
		StringBuffer sql =new StringBuffer();
		for(int i=1;i<=12;i++){
			String day = "0"+i;
			day = day.substring( day.length()-2,day.length());
			String day_date = date+"-"+day;
			long start = DateUtil.getDateline(day_date+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end = DateUtil.getDateline(day_date+"-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" when create_time >= "+start +" and  create_time <="+ end +" then "+i );
		 }
		 sql.append(" else 0 end");
		 return sql.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISalesStatisticsManager#getSalesIncome(long, long, int, int, java.util.Map)
	 */
	@Override
	public Page getSalesIncome(int year, int month, int page, int pageSize,
			Map map) {
		
		String  date = year+"-"+month;
		long start = DateUtil.getDateline(date+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
		long end = DateUtil.getDateline(date+"-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
		
		
		String sql = "select oi.goods_id,oi.name,oi.price,SUM(oi.num) t_num,SUM(oi.num*oi.price) t_price from es_order_items oi "
				+ " left join es_order o on oi.order_id=o.order_id "
				+ " where o.create_time >=? and  o.create_time <=? and ship_num >0 group by oi.goods_id,oi.name,oi.price order by SUM(oi.num*oi.price) desc";
		List list = this.daoSupport.queryForListPage(sql, page, pageSize, start, end );
		
		Page salesPage= new Page(0, daoSupport.queryForList(sql, start, end).size(), pageSize, list);
		
		return salesPage;
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISalesStatisticsManager#getReceivables(long, long, java.util.Map)
	 */
	@Override
	public Double getReceivables(int year, int month, Map parames) {
		
		String  date = year+"-"+month;
		long start = DateUtil.getDateline(date+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
		long end = DateUtil.getDateline(date+"-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
		
		String sql = "select SUM(o.need_pay_money) as receivables from es_order o where create_time >=? and  create_time <=?";
		//String sql = "select SUM(pl.money) as receivables from es_payment_logs pl where pl.pay_date >=? and pl.pay_date <=?";
		Map map = this.daoSupport.queryForMap(sql, start,end);
		Double receivables = 0.0;
		if(map!=null){
			receivables = StringUtil.toDouble(map.get("receivables"), false);
		}
		return receivables;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ISalesStatisticsManager#getRefund(long, long, java.util.Map)
	 */
	@Override
	public Double getRefund(int year, int month, Map parames) {
		
		String  date = year+"-"+month;
		long start = DateUtil.getDateline(date+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
		long end = DateUtil.getDateline(date+"-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
		
		String sql = "select SUM(sl.alltotal_pay) as refund from es_sellback_list sl  where sl.regtime >=? and sl.regtime <=?";
		Map map = this.daoSupport.queryForMap(sql, start,end);
		Double refund = 0.0;
		if(map!=null){
			refund = StringUtil.toDouble(map.get("refund"),false);
		}
		return refund;
	}

	
	//获取当前年月的最大的天数
	public static int getDaysByYearMonth(int year, int month) {  
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.statistics.service.ISalesStatisticsManager#getPaid(int, int, java.util.Map)
	 */
	@Override
	public Double getPaid(int year, int month, Map parames) {
		String  date = year+"-"+month;
		long start = DateUtil.getDateline(date+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
		long end = DateUtil.getDateline(date+"-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
		
		String sql = "select SUM(o.need_pay_money) as receivables from es_order o where create_time >=? and  create_time <=? "
				+ " and pay_status="+OrderStatus.PAY_YES
				+ " and status!="+OrderStatus.ORDER_CANCELLATION
				+ " and status!="+OrderStatus.ORDER_MAINTENANCE +" ";
		Map map = this.daoSupport.queryForMap(sql, start,end);
		Double paid = 0.0;
		if(map!=null){
			paid = StringUtil.toDouble(map.get("receivables"), false);
		}
		return paid;
	}
}
