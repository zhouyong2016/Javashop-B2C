package com.enation.app.shop.core.member.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.member.model.MemberAddress;

/**
 * 会员中心-接收地址
 * @author lzf<br/>
 * 2010-3-17 下午02:49:23<br/>
 * version 1.0<br/>
 */
public interface IMemberAddressManager {
	
	/**
	 * 列表接收地址
	 * @return
	 */
	public List<MemberAddress> listAddress();
	
	/**
	 * 取得地址详细信息
	 * @param addr_id
	 * @return
	 */
	public MemberAddress getAddress(int addr_id);
	/**
	 * 添加接收地址
	 * @param address 接收地址
	 * @return 接收地址Id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public int addAddress(MemberAddress address);
	/**
	 * 修改接收地址
	 * @param address
	 */
	public void updateAddress(MemberAddress address);
	/**
	 * 修改会员默认收货地址
	 */
	public void updateAddressDefult();
	/**
	 * 修改会员默认收货地址
	 * @param addr_id 收货地址Id
	 */
	public void addressDefult(String addr_id);
	/**
	 * 删除会员默认收货地址
	 * @param addr_id 收货地址Id
	 */
	public void deleteAddress(int addr_id);
	/**
	 * 显示会员有杜少的收货地址
	 * @param member_id 会员Id
	 * @return
	 */
	public int addressCount(int member_id);
	/**
	 * 获取会员的默认收货地址
	 * @param memberid 会员Id
	 * @return MemberAddress
	 */
	public MemberAddress getMemberDefault(Integer memberid);
	
	
	/**
	 * 设置会员默认收货地址
	 * @param memberid
	 * @param addr_id
	 */
	public void updateMemberAddress(Integer memberid,Integer addr_id);
	
	
	/**
	 * 获取会员默认地区id<br>
	 * 如果用户已经登陆则试着读取其保存过的默认地址<br>
	 * 如果没有找到其保存过的地址则根据ip 找到相应的地区id<br>
	 * 如果没有登陆也根据ip找到相应的地区
	 * @return
	 */
	public Integer getMemberDefaultRegionId();
	
	/**
	 * 获取会员所有收货地址集合(此方法应用于后台-会员修改页面-他的收货地址)
	 * add by DMRain 2016-4-27
	 * @param member_id 会员ID
	 * @return
	 */
	public List<MemberAddress> listAddress(Integer member_id);
}
