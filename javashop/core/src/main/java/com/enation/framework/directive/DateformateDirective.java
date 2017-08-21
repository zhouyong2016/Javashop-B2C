package com.enation.framework.directive;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.enation.framework.util.DateUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class DateformateDirective implements TemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		Date date=new Date();
		String timeStr = params.get("time").toString();
		String pattern = params.get("pattern").toString();
		if(timeStr.equals("now")){ //输出当前日期
		 
		}else if(timeStr.equals("tomorrow")){//输出明天
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, +1);
			date= new Date(cal.getTimeInMillis());
		}else if(timeStr.equals("yesterday")){//输出昨天
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			date= new Date(cal.getTimeInMillis());
		}else if(timeStr.equals("nextmonth")){//输出下个月的今天
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, +1);
			date= new Date(cal.getTimeInMillis());
		}else if(timeStr.equals("prevmonth")){//输出上个月的今天
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			date= new Date(cal.getTimeInMillis());
		}else{
			long time = Long.valueOf(timeStr)*1000;
			 date = new Date(time);
			
		}
		
		String str  = DateUtil.toString(date, pattern);
		env.getOut().write(str);
 

	}
	public static void main(String[] args) {
		long num =System.currentTimeMillis()  +(30* 24* 60*60*1000);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, +1);
		long num2 =cal.getTimeInMillis();
		String date =DateUtil.toString(new Date( num2), "yyyy-MM-dd");
	}
}
