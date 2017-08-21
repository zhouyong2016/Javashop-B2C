package com.enation.app.base.core.service;

/**
 * 短链接Manager接口
 * @author Sylow
 * @version v1.0,2015-11-18
 * @since v1.0
 */
public interface IShortUrlManager {
	
	/**
	 * 生成一个短链接
	 * @param url 标准链接
	 * @return 短链接
	 */
	public String createShortUrl(String url);
	
	/**
	 * 根据短链接获得长链接
	 * @param shortUrl 短链接 （可带前缀 即：http:xxx/）
	 * @return 所对应的长链接
	 */
	public String getLongUrl(String shortUrl);
	
}
