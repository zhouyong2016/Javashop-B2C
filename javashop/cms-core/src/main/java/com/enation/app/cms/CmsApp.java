package com.enation.app.cms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.enation.app.cms.core.service.impl.cache.DataCatCacheProxy;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.App;
import com.enation.framework.cache.CacheFactory;

/**
 * CMS应用
 * @author kingapex
 *
 */
@Service("cms")
public class CmsApp extends App {
 

	public CmsApp() {
		super();
		tables.add("data_cat");
		tables.add("data_model");
		tables.add("data_field");
	}
	
 
	
	private static  String replaceTbName(String sql,String tbName ){
		
		Pattern p = Pattern.compile("CREATE TABLE `(.*?)`(.*)", 2 | Pattern.DOTALL);
		Matcher m = p.matcher(sql);
		if(m.find()){
			sql = m.replaceAll("CREATE TABLE `"+ tbName + "`$2");
		}
		return sql;
	}

	/**
	 * session失效事件
	 */
	public void sessionDestroyed(String seesionid,EopSite site) {
		//do noting
	}
 
	@Override
	public String dumpXml( ) {
		String xml = super.dumpXml( );
 
		return xml;
	}
	
	public String getId() {
		
		return "cms";
	}

	
	public String getName() {
		
		return "cms应用";
	}

	
	public String getNameSpace() {
		
		return "/cms";
	}

	
	public void install() {
		this.doInstall("file:com/enation/app/cms/cms.xml");
	}

	protected void cleanCache(){
		//清除挂件缓存
		CacheFactory.getCache(DataCatCacheProxy.cacheName).remove(DataCatCacheProxy.cacheName+"_"+ userid +"_"+siteid);
	}
	
 
	
 
}
