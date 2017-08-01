/**
 * 
 */
package com.enation.app.base.component.plugin;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.setting.IOnSettingInputShow;
import com.enation.app.base.core.plugin.setting.IOnSettingSaveEnvent;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 站点设置插件
 * @author kingapex
 *2015-3-12
 */
@Component
public class SiteSettingPlugin extends AutoRegisterPlugin implements
		IOnSettingInputShow,IOnSettingSaveEnvent {

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.plugin.setting.IOnSettingInputShow#onShow()
	 */
	@Override
	public String onShow() {
		
		return "site-setting";
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.plugin.setting.IOnSettingInputShow#getSettingGroupName()
	 */
	@Override
	public String getSettingGroupName() {
		
		return EopSite.SITE_SETTING_KEY;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.plugin.setting.IOnSettingInputShow#getTabName()
	 */
	@Override
	public String getTabName() {
		
		return "站点设置";
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.plugin.setting.IOnSettingInputShow#getOrder()
	 */
	@Override
	public int getOrder() {
		
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.enation.app.base.core.plugin.setting.IOnSettingSaveEnvent#onSave()
	 */
	@Override
	public void onSave() {
		 EopSite.reload();
		
	}

	public static void main(String[] args) {
		//毫秒数是到1970-01-01 08:00:00 的证明
		long d1970  = DateUtil.toDate("1970-01-01 08:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
		System.out.println("毫秒数是到1970-01-01 08:00:00 的证明:"+d1970);
				
				
		//调整这两个日期做测试
		long now  = DateUtil.toDate("2015-01-31 59:59:00", "yyyy-MM-dd HH:mm:ss").getTime();
		long end  = DateUtil.toDate("2015-02-01 00:00:00", "yyyy-MM-dd HH:mm:ss").getTime();
		
		//运算的程序开始
		long cha= end-now;
		
		
		Date date  =new Date(cha);
		String str  = DateUtil.toString(date, "yyyy-MM-dd HH:MM:ss");
		System.out.println("两个日期毫秒差，得到的时间："+str);
		
        Calendar ca = Calendar.getInstance();  
        ca.setTime(date);
        int year = ca.get(Calendar.YEAR);
		 
        int month = ca.get(Calendar.MONTH);
        
        int day = ca.get(Calendar.DAY_OF_MONTH);
        
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        
        
        int minute = ca.get(Calendar.MINUTE);
        
        int second = ca.get(Calendar.SECOND);
        
        System.out.println("年月日分别减去1970 01 01 08 00 就是相差的年月日:");

        System.out.println("年:"+(year-1970));
        System.out.println("月:"+(month));
        System.out.println("日:"+(day-1));
        System.out.println("时:"+(hour-8));
        System.out.println("分:"+minute);
        System.out.println("秒:"+second);


	}
	
}
