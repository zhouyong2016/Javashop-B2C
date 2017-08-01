package com.enation.app.shop.core.member.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.Regions;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.plugin.MemberPluginBundle;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.ip.IPSeeker;



/**
 * 会员中心-收货地址
 * 
 * @author lzf<br/>
 * @version 2.0,2016-02-18 wangxin v60版本改造
 */
@Service("memberAddressManager")
public class MemberAddressManager implements IMemberAddressManager {

	@Autowired
	private IDaoSupport daoSupport;
	@Autowired
	private MemberPluginBundle memberPluginBundle;
	
	private static final String USER_REGION_ID_KEY="user_region_id_key";
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#addAddress(com.enation.app.shop.core.member.model.MemberAddress)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int addAddress(MemberAddress address) {
		Member member = UserConext.getCurrentMember();
		address.setMember_id(member.getMember_id());
		MemberAddress defAddr = this.getMemberDefault(member.getMember_id());
		
		//如果没有默认地址，则此地址置为默认地址
		if(defAddr==null){
			address.setDef_addr(1);
		}else{
			//不是第一个，且设置为默认地址了，则更新其它地址为非默认地址
			if(address.getDef_addr()==1){
				daoSupport.execute( "update es_member_address set def_addr = 0 where member_id = ?",member.getMember_id());
			}
		}
		
		
		this.daoSupport.insert("es_member_address", address);
		int addressid  = this.daoSupport.getLastId("es_member_address");
		address.setAddr_id(addressid);
		memberPluginBundle.onAddressAdd(address);
		return addressid;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#deleteAddress(int)
	 */
	@Override
	public void deleteAddress(int addr_id) {
		this.daoSupport.execute(
				"update es_member_address set isDel = 1 where addr_id = ?", addr_id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#getAddress(int)
	 */
	@Override
	public MemberAddress getAddress(int addr_id) {
		MemberAddress address = this.daoSupport.queryForObject(
				"select * from es_member_address where addr_id = ?",
				MemberAddress.class, addr_id);
		return address;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#listAddress()
	 */
	@Override
	public List<MemberAddress> listAddress() {
		Member member = UserConext.getCurrentMember();
		List<MemberAddress> list = this.daoSupport.queryForList(
				"select * from es_member_address where member_id = ? and isDel = 0 order by def_addr desc", MemberAddress.class,  member.getMember_id());
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#updateAddress(com.enation.app.shop.core.member.model.MemberAddress)
	 */
	@Override
	public void updateAddress(MemberAddress address) {
		this.daoSupport.update("es_member_address", address, "addr_id="+ address.getAddr_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#updateAddressDefult()
	 */
	@Override
	public void updateAddressDefult() {
		Member member = UserConext.getCurrentMember();
		this.daoSupport.execute(
				"update es_member_address set def_addr = 0 where member_id = ?", member.getMember_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#addressDefult(java.lang.String)
	 */
	@Override
	public void addressDefult(String addr_id) {
		this.daoSupport.execute(
				"update es_member_address set def_addr = 1 where addr_id = ?",addr_id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#addressCount(int)
	 */
	@Override
	public int addressCount(int member_id) {
		return daoSupport.queryForInt("select count(*) from es_member_address where member_id=? and isDel = 0", member_id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#getMemberDefault(java.lang.Integer)
	 */
	@Override
	public MemberAddress getMemberDefault(Integer memberid) {
		String sql = "select * from es_member_address where member_id=? and def_addr=1 and isDel = 0";
		List<MemberAddress> addressList  = this.daoSupport.queryForList(sql, MemberAddress.class, memberid);
		MemberAddress address=null;
		if(!addressList.isEmpty()){
			address=addressList.get(0);
		}
		return address;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#getMemberDefaultRegionId()
	 */
	@Override
	public Integer getMemberDefaultRegionId() {
		
		Integer regionid =(Integer)ThreadContextHolder.getSession().getAttribute(USER_REGION_ID_KEY);
		
		if(regionid!=null) {
			return regionid;
		}
		
		Member member =UserConext.getCurrentMember();
		if(member!=null){
			MemberAddress address=this.getMemberDefault(member.getMember_id());
			
			//会员没有默认地址用ip获取
			if(address==null){
				regionid=this.getRegionIdByIp();
			}else{
				regionid =address.getTown_id();
				if(regionid==null){
					regionid =address.getRegion_id();
				}
				if(regionid==null){
					regionid =address.getCity_id();
				}
				if(regionid==null){
					regionid =address.getProvince_id();
				}
				
			}
		}else{
			
			//会员没登陆用获取
			regionid =this.getRegionIdByIp();
		}
		//将地区id压入session
		ThreadContextHolder.getSession().setAttribute(USER_REGION_ID_KEY, regionid);
		return regionid;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#updateMemberAddress(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void updateMemberAddress(Integer memberid,Integer addr_id) {
		this.daoSupport.execute("update es_member_address set def_addr=0 where member_id=?", memberid);
		this.daoSupport.execute("update es_member_address set def_addr=1 where addr_id=?", addr_id);
	}
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberAddressManager#listAddress(java.lang.Integer)
	 */
	@Override
	public List<MemberAddress> listAddress(Integer member_id) {
		List<MemberAddress> list = this.daoSupport.queryForList("select * from es_member_address where member_id = ? and isDel = 0", MemberAddress.class, member_id);
		return list;
	}
	
	/**
	 * 根据ip得到省的id
	 * @return 如果根据ip没找到，则默认是北京
	 */
	private int getRegionIdByIp(){
		String ip = getRemoteHost();
		IPSeeker ipSeeker=  IPSeeker.getInstance(); 
		String country=  ipSeeker.getIPLocation(ip).getCountry();
		country=this.getDiqu(country);
		List list = this.daoSupport.queryForList("select * from es_regions where local_name like '%"+country+"%' order by region_path asc", Regions.class  );
		
		if(list== null || list.isEmpty()) {
			 return 1;
		}
		Regions region= (Regions)list.get(0);
		return region.getRegion_id();
		
	}

	
	/**
	 * 从地区出提取出省
	 * @param country
	 * @return
	 */
	private  String getDiqu(String  country){
		
		int pos = country.indexOf("省");
		if(pos==-1){
			pos = country.indexOf("市");
		}
		
		if(pos==-1){
			pos = country.indexOf("区");
		}
		if(pos==-1){
			return "北京";
		}
		country = country.substring(0,  pos );
		
		//默认为北京
		if(StringUtil.isEmpty(country)){
			country="北京";
		}
		return country;
	}
	
 
	/**
	 * 
	 * @return
	 */
	private String getRemoteHost(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
	}

	
}
