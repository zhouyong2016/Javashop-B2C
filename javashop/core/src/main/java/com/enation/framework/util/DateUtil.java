package com.enation.framework.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期相关的操作
 * @author Dawei
 *  
 */

public class DateUtil {
	public static final long ONE_DAY=86400;//一天的秒数
	 /**
	   * 当天的开始时间
	   * @return
	   */
	  public static long startOfTodDay() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    Date date=calendar.getTime();
	    return date.getTime()/1000;
	  }
	  /**
	   * 当天的结束时间
	   * @return
	   */
	  public static long endOfTodDay() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    Date date=calendar.getTime();
	    return date.getTime()/1000;
	  }
	  /**
	   * 昨天的开始时间
	   * @return
	   */
	  public static long startOfyesterday() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.add(Calendar.DATE, -1);
	    calendar.set(Calendar.MILLISECOND, 0);
	    Date date=calendar.getTime();
	    return date.getTime()/1000;
	  }
	  /**
	   * 昨天的结束时间
	   * @return
	   */
	  public static long endOfyesterday() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    calendar.add(Calendar.DATE, -1);
	    Date date=calendar.getTime();
	    return date.getTime()/1000;
	  }
	  /**
	   * 某天的开始时间
	   * @param dayUntilNow 距今多少天以前
	   * @return 时间戳
	   */
	  public static long startOfSomeDay(int dayUntilNow){
		  Calendar calendar = Calendar.getInstance();
		  calendar.set(Calendar.HOUR_OF_DAY, 0);
		  calendar.set(Calendar.MINUTE, 0);
		  calendar.set(Calendar.SECOND, 0);
		  calendar.set(Calendar.MILLISECOND, 0);
		  calendar.add(Calendar.DATE, -dayUntilNow);
		  Date date=calendar.getTime();
		  return date.getTime()/1000;
	  }
	  
	 /**
	  *  某天的年月日
	  * @param dayUntilNow 距今多少天以前
	  * @return 年月日map  key为  year month day
	  */
	 public static Map<String,Object> getYearMonthAndDay(int dayUntilNow){
		 Map<String,Object> map=new HashMap<String,Object>();
		 Calendar calendar=Calendar.getInstance();
		  calendar.set(Calendar.HOUR_OF_DAY, 0);
		  calendar.set(Calendar.MINUTE, 0);
		  calendar.set(Calendar.SECOND, 0);
		  calendar.set(Calendar.MILLISECOND, 0);
		  calendar.add(Calendar.DATE, -dayUntilNow);
		  map.put("year", calendar.get(Calendar.YEAR));
		  map.put("month", calendar.get(Calendar.MONTH)+1);
		  map.put("day", calendar.get(Calendar.DAY_OF_MONTH));
		  return map;
	 }
	/**
	 * 将一个字符串转换成日期格式
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date toDate(String date, String pattern) {
		if((""+date).equals("")){
			return null;
		}
		if(pattern == null){
			pattern = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date newDate = new Date();
		try {
			newDate = sdf.parse(date);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return newDate;
	}
	
	/**
	 * 把日期转换成字符串型
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toString(Date date, String pattern){
		if(date == null){
			return "";
		}
		if(pattern == null){
			pattern = "yyyy-MM-dd";
		}
		String dateString = "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			dateString = sdf.format(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dateString;
	}
	
	public static String toString(Long time,String pattern){
		if(time>0){
			if(time.toString().length()==10){
				time = time*1000;
			}
			Date date = new Date(time);
			String str  = DateUtil.toString(date, pattern);
			return str;
		}
		return "";
	}

	
	
	/**
	 * 获取上个月的开始结束时间
	 * @return
	 */
	public static Long[] getLastMonth() {
		   // 取得系统当前时间
		   Calendar cal = Calendar.getInstance();
		   int year = cal.get(Calendar.YEAR);
		   int month = cal.get(Calendar.MONTH) + 1;
		   
		   // 取得系统当前时间所在月第一天时间对象
		   cal.set(Calendar.DAY_OF_MONTH, 1);
		   
		   // 日期减一,取得上月最后一天时间对象
		   cal.add(Calendar.DAY_OF_MONTH, -1);
		   
		   // 输出上月最后一天日期
		   int day = cal.get(Calendar.DAY_OF_MONTH);

		   String months = "";
		   String days = "";

		   if (month > 1) {
		    month--;
		   } else {
		    year--;
		    month = 12;
		   }
		   if (!(String.valueOf(month).length() > 1)) {
		    months = "0" + month;
		   } else {
		    months = String.valueOf(month);
		   }
		   if (!(String.valueOf(day).length() > 1)) {
		    days = "0" + day;
		   } else {
		    days = String.valueOf(day);
		   }
		   String firstDay = "" + year + "-" + months + "-01";
		   String lastDay = "" + year + "-" + months + "-" + days;

		   Long[] lastMonth = new Long[2];
		   lastMonth[0] =DateUtil.getDateline(firstDay);
		   lastMonth[1] = DateUtil.getDateline(lastDay);

		 //  //System.out.println(lastMonth[0] + "||" + lastMonth[1]);
		   return lastMonth;
		}
	
	
	/**
	 * 获取当月的开始结束时间
	 * @return
	 */
	public static Long[] getCurrentMonth() {
		   // 取得系统当前时间
		   Calendar cal = Calendar.getInstance();
		   int year = cal.get(Calendar.YEAR);
		   int month = cal.get(Calendar.MONTH)+1 ;
		   // 输出下月第一天日期
		   int notMonth = cal.get(Calendar.MONTH)+2 ;
		   // 取得系统当前时间所在月第一天时间对象
		   cal.set(Calendar.DAY_OF_MONTH, 1);
		   
		   // 日期减一,取得上月最后一天时间对象
		   cal.add(Calendar.DAY_OF_MONTH, -1);
		   
		  

		   String months = "";
		   String nextMonths = "";


		   if (!(String.valueOf(month).length() > 1)) {
		    months = "0" + month;
		   } else {
		    months = String.valueOf(month);
		   }
		   if (!(String.valueOf(notMonth).length() > 1)) {
			   nextMonths = "0" + notMonth;
		   } else {
			   nextMonths = String.valueOf(notMonth);
		   }
		   String firstDay = "" + year + "-" + months + "-01";
		   String lastDay=	""+year+"-"+nextMonths+"-01";
		   Long[] currentMonth = new Long[2]; 
		   currentMonth[0] =DateUtil.getDateline(firstDay);
		   currentMonth[1] = DateUtil.getDateline(lastDay);

		 //  //System.out.println(lastMonth[0] + "||" + lastMonth[1]);
		   return currentMonth;
		}
		
 
	public static long getDateline(){
		return System.currentTimeMillis()/1000;
	}
	public static long getDateline(String date){
		return (long)(toDate(date, "yyyy-MM-dd").getTime()/1000);
	}
	public static long getDateHaveHour(String date){
		return (long)(toDate(date, "yyyy-MM-dd HH").getTime()/1000);
	}
	public static long getDateline(String date,String pattern){
		return (long)(toDate(date, pattern).getTime()/1000);
	}
	public static void main(String[] args){
		
		long starttime=startOfSomeDay(30);
		long endtime=endOfyesterday();
		System.out.println(starttime);
		System.out.println(endtime);
		Map<String,Object> map=getYearMonthAndDay(0);
		System.out.println(map.get("year")+"年"+map.get("month")+"月"+map.get("day")+"日");
		Map<String,Object> map2=getYearMonthAndDay(1);
		System.out.println(map2.get("year")+"年"+map2.get("month")+"月"+map2.get("day")+"日");
		/*	long d= 1319990400 ;
					d=d*1000;
			int line =getDateline("2011-10-31");
		
			//System.out.println( line +   "--"+toString(new Date(d), "yyyy-MM-dd"));
			//System.out.println(d);*/
		
//		int d1 =getDateline("2011-10-30");
//		int d2 =getDateline("2011-10-15");
//		
//		//System.out.println(d1);
//		//System.out.println(d2);
//		
//		int f = 15 *24*60*60;
//		
//		//System.out.println(d1-f);
		
		//System.out.println( new Date(1320205608000l));
		//System.out.println( DateUtil.toString( new Date(1320205608000l),"yyyy-MM-dd HH:mm:ss"));
	}
}
