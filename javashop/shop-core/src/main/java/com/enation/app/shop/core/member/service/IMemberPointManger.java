package com.enation.app.shop.core.member.service;

import java.util.Map;

import com.enation.app.base.core.model.Member;
 
/**
 * 会员积分管理接口
 * @author kingapex
 *
 */
public interface IMemberPointManger {

	/**
	 * 获取积分项常量
	 */
	String TYPE_REGISTER= "register";
	String TYPE_EMIAL_CHECK="email_check";
	String TYPE_FINISH_PROFILE = "finish_profile";
	String TYPE_BUYGOODS ="buygoods";
	String TYPE_ONLINEPAY="onlinepay";
	String TYPE_LOGIN="login";
	String TYPE_COMMENT="comment";
	String TYPE_COMMENT_IMG="comment_img";
	String TYPE_FIRST_COMMENT = "first_comment";
	String TYPE_REGISTER_LINK="register_link";
	String TYPE_GOODS_POINT="goods_point";
	
	 
	/**
	 * 检测某项是否获取积分
	 * @param itemname
	 * @return
	 */
	public boolean checkIsOpen(String itemname);
	
	/**
	 * 获取某项的获取积分值
	 * @param itemname
	 * @return
	 */
	public int getItemPoint(String itemname);
	
	/**
	 * 为会员增加积分
	 * @param point
	 * @param itemname
	 * @param pointType 新增的会员积分类型 0为等级积分，1为消费积分
	 */
	public void add(Member member,int point,String itemname,Integer relatedId, int mp,int pointType);
	
 

	/**
	 * 使用消费积分 
	 * @param memberid
	 * @param point
	 * @param reson
	 * @param relatedId
	 */
	public void useMarketPoint(int memberid,int point,String reson,Integer relatedId);
	
	
	/**
	 * 得到积分价格
	 * @param point
	 * @return
	 */
	public Double pointToPrice(int point);
	
	
	/**
	 * 将价格兑换为积分
	 * @param price
	 * @return
	 */
	public int priceToPoint(Double price);
	
	/**
	 * 添加会员冻结积分
	 * @author add_by DMRain 
	 * @date 2016-7-25
	 * @param map 冻结积分map
	 */
	public void addFreezePoint(Map map);
	
	/**
	 * 删除会员冻结积分
	 * @author add_by DMRain 
	 * @date 2016-7-25
	 * @param member_id 会员id
	 * @param order_id 订单id
	 */
	public void delFreezePoint(Integer member_id, Integer order_id);
	
}
