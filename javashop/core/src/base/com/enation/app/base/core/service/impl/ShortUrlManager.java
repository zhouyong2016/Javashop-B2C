package com.enation.app.base.core.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IShortUrlManager;
import com.enation.app.base.core.util.ShortUrlGenerator;
import com.enation.framework.database.IDaoSupport;

/**
 * 短链接manager实现类
 * @author Sylow
 * @see com.enation.app.distribution.core.service.IShortUrlManager
 */
@Component
public class ShortUrlManager  implements IShortUrlManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.distribution.core.service.IShortUrlManager#getShortUrl(java.lang.String)
	 */
	@Override
	public String createShortUrl(String url) {
		String shortUrl = "";
		String checkSql = "SELECT count(id) FROM es_short_url WHERE val = ?";
		int num = this.daoSupport.queryForInt(checkSql, url);
		
		//如果没有生成过
		if(num == 0) {
			String[] shortUrls = ShortUrlGenerator.getShortUrl(url);

			//检测是否存在
			for(String tempUrl : shortUrls) {
				String sql = "SELECT count(id) FROM es_short_url WHERE short_key = '" + tempUrl + "'";
				num = this.daoSupport.queryForInt(sql);
				
				if(num == 0) {
					shortUrl = tempUrl;
					Map map = new HashMap();
					map.put("short_key", shortUrl);
					map.put("val", url);
					this.daoSupport.insert("es_short_url", map);
					break;
				}
			}
			
			// 如果生成的短链接都存在了
			if("".equals(shortUrl)) {
				throw new RuntimeException("短链接生成重复");
			}
		} else {
			String getSql = "SELECT * FROM es_short_url WHERE val = ?";
			Map map = this.daoSupport.queryForMap(getSql, url);
			shortUrl = map.get("short_key").toString();
		}
		
		
		return shortUrl;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.distribution.core.service.IShortUrlManager#getLongUrl(java.lang.String)
	 */
	@Override
	public String getLongUrl(String shortUrl) {
		
		String longUrl = "";
		String key =  ShortUrlGenerator.getShortKey(shortUrl);
		String checkSql = "SELECT count(id) FROM es_short_url WHERE short_key = '" + key + "'";
		int num = this.daoSupport.queryForInt(checkSql);
		
		if(num != 0) {
			String sql = "SELECT * FROM es_short_url WHERE short_key = '" + key + "'";
			Map map = this.daoSupport.queryForMap(sql);
			
			//如果存在该链接
			if(map != null) {
				longUrl = map.get("val").toString();
			}
		}
		
		return longUrl;
	}

}
