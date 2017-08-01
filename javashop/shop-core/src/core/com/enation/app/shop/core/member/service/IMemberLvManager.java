package com.enation.app.shop.core.member.service;

import java.util.List;

import com.enation.app.base.core.model.MemberLv;
import com.enation.framework.database.Page;



/**
 * 会员级别管理
 * @author kingapex
 *2010-4-30上午10:07:50
 */
public interface IMemberLvManager{
	
	 
	/**
	 * 获取缺省的会员级别 如果没有缺省的会员级别返回null
	 * @return
	 */
	public Integer getDefaultLv() ;
	
	
	
	
	/**
	 * 添加一个会员级别
	 * @param lv
	 */
	public void add(MemberLv lv);
	
	
	
	
	
	/**
	 * 编辑会员级别
	 * @param lv
	 */
	public void edit(MemberLv lv);
	
	
	
	
	
	/**
	 * 分页读取会员等级
	 * @param order
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page list(String order,int page,int pageSize);
	
	
	
	
	 /**
	  * 获取一个会员级别
	  * @param lv_id
	  * @return
	  */
	public MemberLv get(Integer lv_id);
	
	/**
	 * 根据积分获取合适的会员等级
	 * @param point
	 * @return
	 */
	public MemberLv getByPoint(int point);
	
	
	
	/**
	 * 删除会员级别
	 * @param id
	 */
	public void delete(Integer[] id);
	
	
	
	/**
	 * 读取所有级别列表
	 * @return
	 */
	public  List<MemberLv> list();
	
	
	/**
	 * 根据级别列表(,号分隔)读取会员级别
	 * @param ids(,号分隔的会员级别id字串)
	 * @return 会员级别列表
	 */
	public List<MemberLv> list(String ids);
	
	/**
	 * 获取大于指定积分数的下一个等级对象
	 * @param point
	 * @return
	 */
	public MemberLv getNextLv(int point);
	
	/**
	 * 根据等级获取商品分类折的扣列表
	 * @param lv_id
	 * @return
	 */
	public List getCatDiscountByLv(int lv_id);
	
	/**
	 * 根据等级获取该等级有折扣的类别列表
	 * @param lv_id
	 * @return
	 */
	public List getHaveCatDiscountByLv(int lv_id);
	
	/**
	 * 保存某等级的商品类别对应折扣
	 * @param cat_ids 与 discount是一一对应关系
	 * @param discounts 与catids是一一对应关系
	 * @param lv_id
	 */
	public void saveCatDiscountByLv(int[] cat_ids,int[] discounts,int lv_id);
	
	/**
	 * 根据类别获取所有等级的折扣
	 * @param cat_id
	 */
	public List getHaveLvDiscountByCat(int cat_id);
	
}