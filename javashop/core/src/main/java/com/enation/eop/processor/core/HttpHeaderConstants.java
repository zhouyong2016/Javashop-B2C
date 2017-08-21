package com.enation.eop.processor.core;

/**
 * Http头常量
 * 
 * @author kingapex
 * @date 2011-11-2 下午3:45:20
 * @version V1.0
 */
public interface HttpHeaderConstants {

	public static final String CONTEXT_TYPE_XML = "text/xml";
	public static final String CONTEXT_TYPE_JSON = "text/json";
	public static final String CONTEXT_TYPE_HTML = "text/html";
	public static final String CONTEXT_TYPE_JAVASCRIPT = "application/x-javascript";
	public static final String CONTEXT_TYPE_FLASH = "application/x-shockwave-flash";
	public static final String CONTEXT_TYPE_CSS = "text/css";
	public static final String CONTEXT_TYPE_JPG = "image/jpeg";
	public static final String CONTEXT_TYPE_GIF = "image/gif";
	public static final String CONTEXT_TYPE_PNG = "image/png";

	public static final int status_200 = 200;
	public static final int status_304 = 304;
	public static final int status_401 = 401;
	public static final int status_404 = 404;
	public static final int status_500 = 500;
	public static final int status_redirect = -1;
	public static final int status_do_original = -2;
}
