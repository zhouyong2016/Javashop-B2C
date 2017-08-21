package com.enation.app.shop.core.goods.service;

 
/**
 * 
 * 品牌应用manager
 * @author	Chopper
 * @version	v1.0, 2016-1-7 下午4:37:13
 * @since
 */
public interface IBrandsTagManager {

	/**
	 * 删除一个引用
	 * @param tag_id
	 * @param rel_id
	 */
	public void del(int tag_id,int rel_id); 
	
	/**
	 * 添加一个引用
	 * @param tag_id
	 * @param brand_id
	 */
	public void add(int tag_id,int[] brand_id);
	
	
	/**
	 * 排序保存
	 * @param tag_id
	 * @param rel_id
	 * @param order
	 */
	public void saveOrder(int tag_id,int[] rel_id,int[] order);
	
	
}
