package com.enation.eop.resource.model;

import java.io.Serializable;
import java.util.Map;

import com.enation.app.base.core.service.ISettingService;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * 站点信息<br>
 * 此类为长驻内存式缓存，需要通过getInstance 获取实例（单例模式）<br>
 * 可通过此类获取站点的常用信息<br>
 * @author kingapex
 *
 */
public class EopSite implements Serializable {
	
	
	/**
	 * 禁止被外部类new
	 * 强制使用getInstance方法获取实例
	 */
	private EopSite(){}
	
	
	
	private static final long serialVersionUID = 7525130003L;
	
	
	//在设置表中的group(key),
	public static final String SITE_SETTING_KEY="site_seting";
	
	// SEO   
	private  String sitename;
	private  String title;
	private  String keywords;
	private  String descript;
	        
	 
	private  int siteon;// 网站是否开启,0:on default
	private  String closereson; //关闭原因
            
	private  Integer themeid; //前台模板id
	private  String themepath; //前台模板路径
	private  Integer adminthemeid; //后台模板id

	private  String logofile; //logo文件路径，fs:开头或http://开头，需要用<@image指令解析
           
	//内存常驻缓存
	private static EopSite mySelf=null;
	
	
	/**
	 * 获取本类实例
	 * @return
	 */
	public static EopSite getInstance(){
		
		if(mySelf!=null){
			//System.out.println("load site from cache ");
			return mySelf;
		}
		
		reload();
		
		return mySelf;
	}
	
	
	/**
	 * 由数据库中重新加载信息至实例<br>
	 * 当站点信息改变时，应该先更新以 SITE_SETTING_KEY 为key的配置，再调用此方法以刷新缓存<br>
	 */
	public  static void reload(){
		if( EopSetting.INSTALL_LOCK.toUpperCase().equals("NO") ){
			 return;
		 }
		if(mySelf==null){
			mySelf = new EopSite();
		}
		
		ISettingService settingService= SpringContextHolder.getBean("settingService");
		Map siteSetting = settingService.getSetting(SITE_SETTING_KEY);
		
		mySelf.sitename  = (String)siteSetting.get("sitename");
		mySelf.title  = (String)siteSetting.get("title");
		mySelf.keywords  = (String)siteSetting.get("keywords");
		mySelf.descript  = (String)siteSetting.get("descript");
		
		mySelf.siteon  = toInt((String)siteSetting.get("siteon"));
		
		mySelf.closereson  = (String)siteSetting.get("closereson");
		 
		mySelf.themeid  = toInt((String)siteSetting.get("themeid"));
		mySelf.themepath  = (String)siteSetting.get("themepath");
		mySelf.adminthemeid  = toInt((String)siteSetting.get("adminthemeid"));
		mySelf.logofile  = (String)siteSetting.get("logofile");
		 
	}
	
	
	/**
	 * 将一个字串转为int 
	 * @param value
	 * @return 如果为空，返回1
	 */
	private static int toInt(String value){
		if(!StringUtil.isEmpty(value)){
			return StringUtil.toInt(value,1);
		}else{
			return 1;
		}
	}

	
	public String getSitename() {
		return sitename;
	}

	public String getTitle() {
		return title;
	}

	public String getKeywords() {
		return keywords;
	}

	public String getDescript() {
		return descript;
	}

	public int getSiteon() {
		return siteon;
	}

	public String getClosereson() {
		return closereson;
	}

	public Integer getThemeid() {
		return themeid;
	}

	public String getThemepath() {
		return themepath;
	}

	public Integer getAdminthemeid() {
		return adminthemeid;
	}

	public String getLogofile() {
		return logofile;
	}

}