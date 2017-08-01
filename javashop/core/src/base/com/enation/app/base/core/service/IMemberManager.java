package com.enation.app.base.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.framework.database.Page;

/**
 * 会员管理接口
 * @author kingapex
 *2010-4-30上午10:07:35
 */
public interface IMemberManager {
	
	/**
	 * 添加会员
	 * 
	 * @param member
	 * @return 0：用户名已存在，1：添加成功
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int add(Member member);
	
	
	/**
	 * 会员注册 
	 * @param member
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int register(Member member);

	
	
	/**
	 * 某个会员邮件注册验证成功
	 * 此方法会更新为验证成功，并激发验证成功事件
	 * @param 会员实体
	 *  
	 */
	public void checkEmailSuccess(Member member);
	
	
	
	
	/**
	 * 检测用户名是否存在
	 * 
	 * @param name
	 * @return 存在返回1，否则返回0
	 */
	public int checkname(String name);
	
	/**
	 * 检测邮箱是否存在
	 * 
	 * @param name
	 * @return 存在返回1，否则返回0
	 */
	public int checkemail(String email);

	/**
	 * 修改会员信息
	 * 
	 * @param member
	 * @return
	 */
	public Member edit(Member member);

	/**
	 * 根据会员id获取会员信息
	 * 
	 * @param member_id
	 * @return
	 */
	public Member get(Integer member_id);
	
	/**
	 * 根据会员id获取会员信息
	 * 
	 * @param member_id
	 * @return
	 */
	public Map getMember(Integer member_id);
	/**
	 * 根据会员id获取会员信息
	 * 
	 * @param member_id
	 * @return
	 */
	public Member getMemberByMemberId(Integer member_id);

	/**
	 * 删除会员
	 * 
	 * @param id
	 */
	public void delete(Integer[] id);

	/**
	 * 根据用户名称取用户信息
	 * 
	 * @param uname
	 * @return 如果没有找到返回null
	 */
	public Member getMemberByUname(String uname);
	
	/**
	 * 根据邮箱取用户信息
	 * @param email
	 * @return
	 */
	public Member getMemberByEmail(String email);

	/**
	 * 根据手机取用户信息
	 * @param mobile
	 * @return
	 */
	public Member getMemberByMobile(String mobile);
	
	
	/**
	 * 修改当前登录会员的密码
	 * 
	 * @param password
	 */
	public void updatePassword(String password);
	
	
	
	/**
	 * 更新某用户的密码
	 * @param memberid
	 * @param password
	 */
	public void updatePassword(Integer memberid,String password);
	
	/**
	 * 找回密码使用code
	 * @param code
	 */
	public void updateFindCode(Integer memberid,String code);
	
	
	/**
	 * 增加预存款
	 */
	public void addMoney(Integer memberid,Double num);
	
	
	
	/**
	 * 减少预存款
	 * @param memberid
	 * @param num
	 */
	public void cutMoney(Integer memberid,Double num);
	
	
	
	
	/**
	 * 会员登录 
	 * @param username 用户名
	 * @param password 密码
	 * @return 1:成功, 0：失败
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public int login(String username,String password);
	
	/**
	 * 会员登录(用于微信登陆)
	 * @param username 用户名
	 * @return 1:成功, 0：失败
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int login(String username);
	/**
	 * 会员登录 
	 * @param username 用户名
	 * @param password 密码
	 * @return 1:成功, 0：失败
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public int loginWithCookie(String username, String password);
	
	/**
	 * 会员注销
	 */
	public void logout();
	
	
	
	/**
	 * 管理员以会员身份登录
	 * @param username 要登录的会员名称
	 * @return 0登录失败，无此会员
	 * @throws  RuntimeException 无权操作
	 */
	public int loginbysys(String username);
	
	
	/**
	 * 更新某个会员的等级
	 * @param memberid
	 * @param lvid
	 */
	public void updateLv(int memberid,int lvid);
	
	/**
	 * 会员搜索
	 * @param keyword
	 * @param lvid
	 * @return
	 */
	public List<Member> search(Map memberMap);

	/**
	 * 会员搜索 带分页
	 * @param memberMap
	 * @param page
	 * @param pageSize
	 * @param other
	 * @return
	 */
	public Page searchMember(Map memberMap,Integer page,Integer pageSize,String other,String order);
	
	/**
	 * 会员回收站 带分页
	 * @param memberMap
	 * @param page
	 * @param pageSize
	 * @param other
	 * @return
	 */
	public Page searchMemberRecycle(Integer page,Integer pageSize);
	
	/**
	 * 会员搜索 无店铺会员
	 * @param memberMap
	 * @param page
	 * @param pageSize
	 * @param other
	 * @return
	 */
	public Page searchMemberNoShop(Map memberMap,Integer page,Integer pageSize,String other,String order);
	
	/**
	 * 检测手机号
	 * @param phone
	 * @return
	 */
	public int checkMobile(String phone);


	public Member getDisabled(Integer member_id);


	/**
	 * 修改用户信息是检测email是否存在
	 * @param email
	 * @param member_id 
	 * @return
	 */
	public boolean checkemailInEdit(String email, Integer member_id);


	public Member loginByMobile(String mobile);


	/**
	 * 获取指定商品ID归属于哪个店铺会员
	 * @param goodsId
	 * @return
	 */
	public Member getByGoodsId(int goodsId);
	
	/**
	 * 修改会员手机号
	 * @author add_by DMRain 2016-7-8
	 * @param member_id 会员id
	 * @param mobile 手机号
	 */
	public void changeMobile(Integer member_id, String mobile);
	/**
	 * 恢复会员
	 * 
	 * @param id
	 */
	public void regain(Integer[] member_id);


	/**
	 * 检测除该会员自己之外的手机号是否有重复 
	 * @param mobile 手机号
	 * @param member_id 当前会员id
	 * @return 0：没有记录 可以修改 else int :有记录 不能修改
	 */
	public int checkMobileExceptSelf(String mobile, Integer member_id);

	/**
	 * 充值增加会员积分
	 * @param memberid 会员id
	 * @param value 增加的积分
	 */
	public void addPoint(int memberid, int value);
	
	/**
	 * 修改会员头像
	 * @param member_id
	 * @param img			fs地址
	 */
	public void editMemberImg(Integer member_id,String img);
	
}