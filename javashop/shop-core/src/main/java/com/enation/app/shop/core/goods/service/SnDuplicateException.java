package com.enation.app.shop.core.goods.service;


/**
 * 货号重复异常
 * @author kingapex
 *2012-5-16下午9:54:49
 */
public class SnDuplicateException extends RuntimeException {
	 
	private static final long serialVersionUID = -5860514618486825874L;

	public SnDuplicateException(String sn){
		super("商品编号["+ sn +"]重复");
	}
	
}
