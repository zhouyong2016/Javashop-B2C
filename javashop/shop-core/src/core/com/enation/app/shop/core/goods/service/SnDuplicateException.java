package com.enation.app.shop.core.goods.service;


/**
 * 货号重复异常
 * @author kingapex
 *2012-5-16下午9:54:49
 */
public class SnDuplicateException extends RuntimeException {
	 
	public SnDuplicateException(String sn){
		super("货号["+ sn +"]重复");
	}
	
}
