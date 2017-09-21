package com.enation.app.shop.front.api.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.Regions;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.GridController;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.TestUtil;
import com.enation.framework.util.Validator;

import freemarker.template.TemplateModelException;
/**
 * 会员地址api
 * @author kingapex
 * @version 2.0,2016-02-18 wangxin v60版本改造
 */
@Controller
@RequestMapping("/api/shop/member-address")
@Scope("prototype")
public class MemberAddressApiController extends GridController{
	
	@Autowired
	private IMemberAddressManager memberAddressManager;

	

	
	/**
	 * 获取会员地址
	 * @param 无
	 * 
	 * @return json字串
	 * result  为1表示调用正确，0表示失败 ，int型
	 * data: 地址列表
	 * 
	 * {@link MemberAddress}
	 * 如果没有登录返回空数组
	 */
	@ResponseBody
	@RequestMapping(value="/list",produces = MediaType.APPLICATION_JSON_VALUE)
	public String list() {
		List<MemberAddress> addressList = null;
		MemberAddress defaultAddress = null;
		Member member = UserConext.getCurrentMember();
		if (member != null) {
			// 读取此会员的收货地址列表
			addressList = memberAddressManager.listAddress();
			defaultAddress = this.getDefaultAddress(addressList);
		} else {
			addressList = new ArrayList();
		}
		
		Map data = new HashMap();
		data.put("addressList", addressList);
		data.put("defaultAddress", defaultAddress);
		String json = JsonMessageUtil.getObjectJson(data);
		return json;
	}

	
	/**
	 * 添加一会员地址
	 * @param name：收货人姓名,String型，必填
	 * @param province:所在省,String型，必填
	 * @param province_id:所在省id,int型，参见：{@link Regions.region_id}，必填
	 * @param city：所在城市,String型，必填
	 * @param city_id: 所在城市id,int型，参见：{@link Regions.region_id}，必填
	 * @param region：所在地区,String型，必填
	 * @param region_id: 所在地区id,int型，参见：{@link Regions.region_id}	，必填
	 * @param addr：详细地址,String型 ，必填
	 * @param zip：邮编,String型 ，必填
	 * @param tel：固定电话,String型 ，手机和电话必填一项
	 * @param mobile：手机,String型 ，手机和电话必填一项
	 * @param def_addr：是否是默认地址,如果传递"1"则为默认地址，如果传递"0"为非默认地址    whj 14-03-07,修改(107-111行).
	 * @param remark：备注,String型,可选项
	 * @return json字串
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 * {@link MemberAddress}
	 */
	@ResponseBody
	@RequestMapping(value="/add",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult add() {
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			return JsonResultUtil.getErrorJson("无权访问此api[未登录或已超时]");				
		}

		if (memberAddressManager.addressCount(member.getMember_id()) >= 10) {
			return JsonResultUtil.getErrorJson("添加失败。原因：最多可以维护10个收货地址。");				
		
		} else {
			MemberAddress address = new MemberAddress();
			try {
				address = this.fillAddressFromReq(address);
				
				//检查是否选择地区  修改人：DMRain 2015-12-8
				if(address.getProvince_id() == 0 || address.getCity_id() == 0 || address.getRegion_id() == 0){
					return JsonResultUtil.getErrorJson("地区填写不完整");				

				}else{
					HttpServletRequest request = ThreadContextHolder.getHttpRequest();
					String def_addr = request.getParameter("def_addr");
					if ("1".equals(def_addr)){
						address.setDef_addr(Integer.valueOf(def_addr));       //应该是让当钱的member_address的addr_id的def_add值是1.如果是这个意思，那么执行顺序做了，应该是先变成0，然后再执行本句，为什么这么写也对呢
						memberAddressManager.updateAddressDefult();           //让member_address的def_addr值变成0.大事上一句话是啥意思。
					}
					
					memberAddressManager.addAddress(address);
					return JsonResultUtil.getSuccessJson("添加成功");

				}
			} catch (Exception e) {
				final Logger logger = Logger.getLogger(getClass());
				if (logger.isDebugEnabled()) {
					logger.error(e.getStackTrace());
				}
				return JsonResultUtil.getErrorJson("添加失败[" + e.getMessage() + "]");				

			}
		}
	}
	
	/**
	 * 修改收货地址
	 * @param  addr_id：要修改的收货地址id,int型，必填
	 * @param  name：收货人姓名,String型，必填
	 * @param province:所在省,String型，必填
	 * @param province_id:所在省id,int型，参见：{@link Regions.region_id}，必填
	 * @param city：所在城市,String型，必填
	 * @param city_id: 所在城市id,int型，参见：{@link Regions.region_id}，必填
	 * @param region：所在地区,String型，必填
	 * @param region_id: 所在地区id,int型，参见：{@link Regions.region_id}	，必填
	 * @param addr：详细地址,String型 ，必填
	 * @param zip：邮编,String型 ，必填
	 * @param tel：固定电话,String型 ，手机和电话必填一项
	 * @param mobile：手机,String型 ，手机和电话必填一项
	 * @param def_addr：是否是默认地址,Integer型 ,可选项，如果传递"1"则为默认地址，如果传递"0"为非默认地址    whj 14-03-07,修改(158-161行).
	 * @param remark：备注,String型,可选项
	 * @return json字串
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	@ResponseBody
	@RequestMapping(value="/edit",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult edit() {
		Member member = UserConext.getCurrentMember();

		if (member == null) {
			return JsonResultUtil.getErrorJson("无权访问此api[未登录或已超时]");				
		}
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String addr_id = request.getParameter("addr_id"); 
		MemberAddress address = memberAddressManager.getAddress(StringUtil.toInt(addr_id,0));
		
		//查看是否是会员自己的地址，权限控制
		if(!member.getMember_id().equals(address.getMember_id())){
			return JsonResultUtil.getErrorJson("您没有操作权限");	
		}
		
		try {
			address = this.fillAddressFromReq(address);
			
			//检查是否选择地区  修改人：DMRain 2015-12-8
			
			if(address.getProvince_id() == 0 || address.getCity_id() == 0 || (address.getRegion_id() != null && address.getRegion_id() == 0)||(address.getTown_id() != null && address.getTown_id() == 0)){
				return JsonResultUtil.getErrorJson("地区填写不完整");				

			}else{
				
				
				
				String def_addr = request.getParameter("def_addr");
				if ("1".equals(def_addr)){
					address.setDef_addr(Integer.valueOf(def_addr));
					memberAddressManager.updateAddressDefult(); 
				}
				
				if ("0".equals(def_addr)){
					address.setDef_addr(Integer.valueOf(def_addr));
				}
				
				
				memberAddressManager.updateAddress(address);
				return JsonResultUtil.getSuccessJson("修改成功");

			}
		} catch (Exception e) {
			final Logger logger = Logger.getLogger(getClass());
			if (logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			return JsonResultUtil.getErrorJson("修改失败[" + e.getMessage() + "]");				

		}
	}

	/**
	 * 设置当前地址为默认地址
	 */
	@ResponseBody
	@RequestMapping(value="/isdefaddr",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult isdefaddr() {
		try {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String addr_id = request.getParameter("addr_id");
		memberAddressManager.updateAddressDefult(); 
		memberAddressManager.addressDefult(addr_id); 
		return JsonResultUtil.getSuccessJson("修改成功");

		}
		catch (Exception e) {
			final Logger logger = Logger.getLogger(getClass());
			if (logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			return JsonResultUtil.getErrorJson("修改失败[" + e.getMessage() + "]");				

		}
	}
	
	
	
	/**
	 * 删除一个收货地址
	 * @param addr_id ：要删除的收货地址id,int型
	 * result  为1表示添加成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	@ResponseBody
	@RequestMapping(value="/delete",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult delete() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String addr_id = request.getParameter("addr_id");
		
		//查看是否是会员自己的地址，权限控制
		Member member = UserConext.getCurrentMember();

		if (member == null) {
			return JsonResultUtil.getErrorJson("无权访问此api[未登录或已超时]");				
		}
		MemberAddress address = memberAddressManager.getAddress(StringUtil.toInt(addr_id,0));
		
		if( address==null || !member.getMember_id().equals(address.getMember_id())){
			return JsonResultUtil.getErrorJson("您没有操作权限");	
		}
		
		try {
			memberAddressManager.deleteAddress(Integer.valueOf(addr_id));
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			final Logger logger = Logger.getLogger(getClass());
			if (logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			return JsonResultUtil.getErrorJson("删除失败[" + e.getMessage() + "]");				

		}
	}
	
	
	
	
	/**
	 * 不需要验证，结算时直接添加
	 */
	@ResponseBody
	@RequestMapping(value="/add-new-address",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult addNewAddress() {
		Member member = UserConext.getCurrentMember();
		
		if (memberAddressManager.addressCount(member.getMember_id()) >= 10) {
			return JsonResultUtil.getErrorJson("添加失败。原因：最多可以维护10个收货地址。");				
		
		} else {
			
			MemberAddress address = new MemberAddress();
			try {
				address = this.createAddress();
				
				int addressid =this.memberAddressManager.addAddress(address);
				address.setAddr_id(addressid);
				return JsonResultUtil.getObjectJson(address);				
			} catch (Exception e) {
				TestUtil.print(e);
				final Logger logger = Logger.getLogger(getClass());
				logger.error("前台添加地址错误", e);
			}
			return JsonResultUtil.getErrorJson("添加失败");
			
		}
		
		
	}
	
	
	/**
	 * 结算页修改地址
	 * whj -2015-12-14 
	 * @param addr_id 修改地址ID
	 * @param def_addr 是否为默认，1为默认，0为不默认。
	 * @return json ,1是成功，0为错误。
	 */
	
	@ResponseBody
	@RequestMapping(value="/edit-new-address",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult editNewAddress(Integer addr_id) {
		Member member = UserConext.getCurrentMember();

		if (member == null) {
			return JsonResultUtil.getErrorJson("无权访问此api[未登录或已超时]");				
		}
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String addrId = request.getParameter("addr_id");
		MemberAddress address = memberAddressManager.getAddress(Integer.valueOf(addr_id));
		
		//查看是否是会员自己的地址，权限控制
		if( address==null || !member.getMember_id().equals(address.getMember_id())){
			return JsonResultUtil.getErrorJson("您没有操作权限");	
		}
		try {
			address = this.createAddress();
			int checkoutAddrId = Integer.parseInt(addrId);
			address.setAddr_id(checkoutAddrId);
			//检查是否选择地区  修改人：DMRain 2015-12-8
			if(address.getProvince_id() == 0 || address.getCity_id() == 0 || address.getRegion_id() == 0){
				return JsonResultUtil.getErrorJson("地区填写不完整");				

			}else{
				String def_addr = request.getParameter("def_addr");
				if ("1".equals(def_addr)){
					address.setDef_addr(Integer.valueOf(def_addr));
					memberAddressManager.updateAddressDefult(); 
				}
				
				if ("0".equals(def_addr)){
					address.setDef_addr(Integer.valueOf(def_addr));
				}
				
				memberAddressManager.updateAddress(address);
				return JsonResultUtil.getSuccessJson("修改成功");

			}
		} catch (Exception e) {
			final Logger logger = Logger.getLogger(getClass());
			if (logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			return JsonResultUtil.getErrorJson("修改失败[" + e.getMessage() + "]");				
		}
	}
	

	
	
	
	/**
	 * 设置会员默认收货地址
	 * @param member 店铺会员,StoreMember
	 * @param addrid 收货地址Id,Integer
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	@ResponseBody
	@RequestMapping(value="/set-def-address",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult setDefAddress(Integer addr_id){
		try {
			Member member = UserConext.getCurrentMember();
			if(member==null){
				throw new TemplateModelException("未登录不能使用此标签[ConsigneeListTag]");
			}
			this.memberAddressManager.updateMemberAddress(member.getMember_id(),addr_id);
			return JsonResultUtil.getSuccessJson("设置成功");

		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("修改失败");				

		}
	}

	/************以下方法非api，不需要书写api文档*************/
	
	
	/**
	 * 设置默认地址
	 * @param addr_id:要设置为默认收货地址的id,int型
	 * result  为1表示设置功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	@ResponseBody
	@RequestMapping(value="/defaddr",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult defaddr() {
		Member member = UserConext.getCurrentMember();

		if (member == null) {
			return JsonResultUtil.getErrorJson("无权访问此api[未登录或已超时]");				
		}
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String addr_id = request.getParameter("addr_id");
		MemberAddress address = memberAddressManager.getAddress(Integer.valueOf(addr_id));
		address.setDef_addr(1);
		try {
			memberAddressManager.updateAddressDefult();
			memberAddressManager.updateAddress(address);
			return JsonResultUtil.getSuccessJson("设置成功");

		} catch (Exception e) {
			final Logger logger = Logger.getLogger(getClass());
			if (logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			return JsonResultUtil.getErrorJson("设置失败[" + e.getMessage() + "]");				

		}
	}
	

	
	
	/**
	 * 获取会员的默认收货地址
	 * @param 无
	*/
	private MemberAddress getDefaultAddress(List<MemberAddress> addressList) {
		if (addressList != null && !addressList.isEmpty()) {
			for (MemberAddress address : addressList) {
				if (address.getDef_addr() != null && address.getDef_addr().intValue() == 1) {
					address.setDef_addr(1);
					return address;
				}
			}

			MemberAddress defAddress = addressList.get(0);
			defAddress.setDef_addr(1);
			return defAddress;
		}
		return null;
	}
	
	
	/**
	 * 从request中填充address信息
	 * @param address
	 * @return
	 */
	private MemberAddress fillAddressFromReq(MemberAddress address) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String def_addr = request.getParameter("def_addr");
		if ("yes".equals(def_addr)){
			address.setDef_addr(Integer.valueOf(def_addr));
		}
		String name = request.getParameter("name");
		address.setName(name);
		if(StringUtil.isEmpty(name)){
			throw new RuntimeException("姓名不能为空！");
		}
		Pattern p = Pattern.compile("^([0-9A-Za-z一-龥]|-|_){0,20}$");
		Matcher m = p.matcher(name);
		if (!m.matches()) {
			throw new RuntimeException("收货人格式不正确！");
		}

		String tel = request.getParameter("tel");
		address.setTel(tel);

 		String mobile = request.getParameter("mobile");
		address.setMobile(mobile);
		if (StringUtil.isEmpty(mobile)) {
			throw new RuntimeException("手机必须填写！");
		}else if(!StringUtil.isEmpty(mobile)&& Validator.isMobile(mobile)==false){
			throw new RuntimeException("手机格式不对！");
		}

		//省级
		String province_id = request.getParameter("province_id");
		if (StringUtil.isEmpty(province_id)) {
			throw new RuntimeException("请选择地区中的省！");
		}
		address.setProvince_id(Integer.valueOf(province_id));

		//市级
		String city_id = request.getParameter("city_id");
		if (StringUtil.isEmpty(city_id)) {
			throw new RuntimeException("请选择地区中的市！");
		}
		address.setCity_id(Integer.valueOf(city_id));

		//县级
		String region_id = request.getParameter("region_id");
		address.setRegion_id(StringUtil.toInt(region_id,-1));
		if (region_id == null || region_id.equals("")) {
			address.setRegion("");
		}else{
			String region = request.getParameter("region");
			address.setRegion(region);
		}

		//镇级
		String town_id = request.getParameter("town_id");
		address.setTown_id(StringUtil.toInt(town_id,-1));
		if(town_id==null || town_id.equals("")){
			address.setTown("");
		}else{
			String town = request.getParameter("town");
			address.setTown(town);
		}
		
		String province = request.getParameter("province");
		address.setProvince(province);

		String city = request.getParameter("city");
		address.setCity(city);
		
		String zip = request.getParameter("zip");
		address.setZip(zip);

		String addr = request.getParameter("addr");
		if (StringUtil.isEmpty(addr)) {
			throw new RuntimeException("地址不能为空！");
		}
		/*	Comment by Liuzy 校验导至 4-2401 即4单元2401室这样的写法不能通过		
		Pattern p1 = Pattern.compile("^[0-9A-Za-z一-]{0,50}$");
		Matcher m1 = p1.matcher(addr);
		if(!m1.matches()){
			throw new RuntimeException("地址格式不正确！");
			
		}*/
		address.setAddr(addr);

//		String zip = request.getParameter("zip");
//		if (zip == null || zip.equals("")) {
//			throw new RuntimeException("邮编不能为空！");
//		}
//		address.setZip(zip);

		String remark = request.getParameter("remark");
		address.setRemark(remark);
		
		String shipAddressName = request.getParameter("shipAddressName");
		address.setShipAddressName(shipAddressName);

		return address;
	}
	private static boolean isPhone(String str) {   
        Pattern p = null;  
        Matcher m = null;  
        boolean b = false;   
        p = Pattern.compile("^0?(13[0-9]|15[0-9]|18[0-9]|14[0-9]|17[0-9])[0-9]{8}$"); // 验证手机号  
        m = p.matcher(str);  
        b = m.matches();   
        return b;  
    } 
	private static boolean isMobile(String str) {   
        Pattern p1 = null,p2 = null;  
        Matcher m = null;  
        boolean b = false;    
        p1 = Pattern.compile("0\\d{2,3}-\\d{7,8}");       // 验证带区号的        whj  2015-05-22修改对带区号的固定电话的验证，验证格式为“0467-8888888” 
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");     // 验证没有区号的  
        if(str.length() >9)  
        {   m = p1.matcher(str);  
            b = m.matches();    
        }else{  
            m = p2.matcher(str);  
            b = m.matches();   
        }    
        return b;  
    }
	/**
	 * 创建收货地址
	 * @param shipName 收货人名称
	 * @param shipTel 收货人电话
	 * @param shipMobile 收货人手机号
	 * @param province_id 收货——省Id
	 * @param city_id 收货——城市Id
	 * @param region_id 收货——区Id
	 * @param province 收货——省
	 * @param city 收货——城市
	 * @param region 收货——区
	 * @param shipAddr 详细地址
	 * @param shipZip 收货邮编
	 * @return 收货地址
	 */
	private MemberAddress createAddress() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String def_addr = request.getParameter("def_addr");
	
		MemberAddress address = new MemberAddress();
		
		//设置是否是默认地址
		if ("1".equals(def_addr)){
			address.setDef_addr(Integer.valueOf(def_addr));
		}
		String name = request.getParameter("shipName");
		if(StringUtil.isEmpty(name)){
			throw new RuntimeException("收货人错误");
		}
		address.setName(name);

		String tel = request.getParameter("shipTel");
		address.setTel(tel);

		String mobile = request.getParameter("shipMobile");
		if(StringUtil.isEmpty(mobile)){
			throw new RuntimeException("收货人手机号错误");
		}
		address.setMobile(mobile);

		
		//转换数字应使用StringUtil
		String province_id = request.getParameter("province_id");
		if(StringUtil.isEmpty(province_id)){
			throw new RuntimeException("收货地址错误");
		}
		
		address.setProvince_id(StringUtil.toInt(province_id, null));
		

		String city_id = request.getParameter("city_id");
		if(StringUtil.isEmpty(city_id)){
			throw new RuntimeException("收获地址错误");
		}
		
		address.setCity_id(StringUtil.toInt(city_id,null));
		

		String region_id = request.getParameter("region_id");
		address.setRegion_id(StringUtil.toInt(region_id,null));
		
				
		String town_id = request.getParameter("town_id");
		address.setTown_id(StringUtil.toInt(town_id,null));

		String province = request.getParameter("province");
		address.setProvince(province);

		String city = request.getParameter("city");
		address.setCity(city);

		String region = request.getParameter("region");
		address.setRegion(region);
		
		String town = request.getParameter("town");
		address.setTown(town);

		String addr = request.getParameter("shipAddr");
		if(StringUtil.isEmpty(addr)){
			throw new RuntimeException("详细地址错误");
		}
		address.setAddr(addr);

		String zip = request.getParameter("shipZip");
		address.setZip(zip);
		
		String shipAddressName = request.getParameter("shipAddressName");
		address.setShipAddressName(shipAddressName);

		return address;
	}
}
