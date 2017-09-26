package com.enation.app.shop.core.member.action;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.member.model.PointHistory;
import com.enation.app.shop.core.member.plugin.MemberPluginBundle;
import com.enation.app.shop.core.member.service.IAdvanceLogsManager;
import com.enation.app.shop.core.member.service.IMemberCommentManager;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.member.service.IPointHistoryManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.TestUtil;
/**
 * 会员管理Action
 * 
 * @author LiFenLong 2014-4-1;4.0版本改造
 *
 */

@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/member")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MemberController extends GridController {

	@Autowired
	private IMemberManager memberManager;

	@Autowired
	private IMemberLvManager memberLvManager;

	@Autowired
	private IRegionsManager regionsManager;

	@Autowired
	private IPointHistoryManager pointHistoryManager;

	@Autowired
	private IAdvanceLogsManager advanceLogsManager;

	@Autowired
	private IMemberCommentManager memberCommentManager;

	@Autowired
	private MemberPluginBundle memberPluginBundle;



	/**
	 * 跳转至添加会员等级页面
	 * @return 添加会员等级页面
	 */
	@RequestMapping(value = "/add-lv")
	public String addLv() {
		return "/shop/admin/member/lv_add";
	}

	/**
	 * 跳转至修改会员等级页面
	 * @param lvId 会员等级Id
	 * @param lv 会员等级
	 * @return 修改会员等级页面
	 */
	@RequestMapping(value = "/edit-lv")
	public ModelAndView editLv(Integer lvId) {
		ModelAndView view = new ModelAndView();
		view.addObject("lv", memberLvManager.get(lvId));
		view.setViewName("/shop/admin/member/lv_edit");
		return view;
	}

	/**
	 * 跳转至会员等级列表
	 * @return 会员等级列表
	 */
	@RequestMapping(value = "/list-lv")
	public ModelAndView listLv() {

		ModelAndView view = getGridModelAndView();
		view.setViewName("/shop/admin/member/lv_list");
		return view;
	}

	/**
	 * 跳转至会员回收站页面
	 * @return 会员回收站列表
	 */
	@RequestMapping(value = "/list-recycle")
	public ModelAndView listRecycle() {

		ModelAndView view = getGridModelAndView();
		view.setViewName("/shop/admin/member/recycle_list");
		return view;
	}

	/**
	 * 获取会员等级列表Json
	 * @return 会员等级列表Json
	 */
	@ResponseBody
	@RequestMapping(value = "list-lv-json")
	public GridJsonResult listLvJson() {

		return JsonResultUtil.getGridJson(memberLvManager.list(this.getSort(), this.getPage(), this.getPageSize()));
	}

	/**
	 * 获取会员回收站列表Json
	 * @return 回收站列表Json
	 */
	@ResponseBody
	@RequestMapping(value = "list-recycle-json")
	public GridJsonResult listRecycleJson() {
		return JsonResultUtil.getGridJson(memberManager.searchMemberRecycle(this.getPage(), this.getPageSize()));
	}

	/**
	 * 添加会员等级
	 * @param lv 会员等级,MemberLv
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-add-lv")
	public JsonResult saveAddLv(MemberLv lv) {
		try {
			memberLvManager.add(lv);
			return JsonResultUtil.getSuccessJson("会员等级添加成功");
		} catch (Exception e) {
			logger.error("会员等级添加失败：", e);
			return JsonResultUtil.getErrorJson("会员等级添加失败");
		}
		
	}

	/**
	 * 修改会员等级
	 * @param lv 会员等级,MemberLv
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-edit-lv")
	public JsonResult saveEditLv(MemberLv lv) {

		try {
			memberLvManager.edit(lv);
			return JsonResultUtil.getSuccessJson("会员等级修改成功");
		} catch (Exception e) {
			logger.error("会员等级修改失败：", e);
			return JsonResultUtil.getErrorJson("会员等级修改失败");
		}
	}

	/**
	 * 删除会员等级
	 * @param lv_id,会员等级Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/delete-lv")
	public JsonResult deletelv(Integer[] lv_id) {
		try {
			this.memberLvManager.delete(lv_id);
			return JsonResultUtil.getSuccessJson("会员等级删除成功");
		} catch (RuntimeException e) {
			logger.error("会员等级删除失败：", e);
			return JsonResultUtil.getErrorJson("会员等级删除失败");
		}
	}

	/**
	 * 恢复会员信息
	 * @param member_id,会员Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/recycle-regain-member")
	public JsonResult recycleRegainMember(Integer[] member_id) {
		try {
			this.memberManager.regain(member_id);
			for (int i = 0; i < member_id.length; i++) {
				Member member = this.memberManager.getMemberByMemberId(member_id[i]);
				memberPluginBundle.onRecycle(member);
			}
			return JsonResultUtil.getSuccessJson("会员信息恢复成功");
		} catch (RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("会员信息恢复出错", e);
			return JsonResultUtil.getErrorJson("会员信息恢复出错：" + e.getMessage());
		}
	}

	/**
	 * 跳转至添加会员页面
	 * @param lvlist 会员等级列表,List
	 * @return 添加会员页面
	 */
	@RequestMapping(value = "/add-member")
	public ModelAndView addMember() {
		ModelAndView view = new ModelAndView();
		Integer is_lvmember = 0;
		// 判定是否是b2b2c
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			// 如果为0，则隐藏会员等级相关内容
			is_lvmember = 0;
		} else {
			is_lvmember = 1;
		}
		view.addObject("is_lvmember", is_lvmember);
		view.addObject("lvlist", this.memberLvManager.list());
		view.addObject("provinceList", this.regionsManager.listProvince());
		view.setViewName("/shop/admin/member/member_add");
		return view;
	}

	/**
	 * 跳转至修改会员页面
	 * @param memberId 会员Id,Integer
	 * @param member 会员,Member
	 * @param lvlist 会员等级列表,List
	 * @return 修改会员页面
	 */
	@RequestMapping(value = "/edit-member")
	public ModelAndView editMember(Integer memberId) {
		ModelAndView view = new ModelAndView();
		Integer is_lvmember=0;
		// 判定是否是b2b2c
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			//如果为0，则隐藏会员等级相关内容
			is_lvmember=0;
		}else{
			is_lvmember=1;
		}
		view.addObject("is_lvmember", is_lvmember);
		view.addObject("member", memberManager.get(memberId));
		view.addObject("lvlist", memberLvManager.list());
		view.setViewName("/shop/admin/member/member_edit");
		return view;
	}

	/**
	 * 跳转至会员列表
	 * @param lvlist 会员等级列表,List
	 * @return 会员列表
	 */
	@RequestMapping(value = "/member-list")
	public ModelAndView memberList() {
		ModelAndView view = getGridModelAndView();
		Integer is_lvmember=0;
		// 判定是否是b2b2c
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			//如果为0，则隐藏会员等级相关
			is_lvmember=0;
		}else{
			is_lvmember=1;
		}
		view.addObject("is_lvmember", is_lvmember);
		view.addObject("lvlist", this.memberLvManager.list());
		view.setViewName("/shop/admin/member/member_list");
		return view;
	}

	/**
	 * 跳转至会员列表
	 * @param lvlist 会员等级列表,List
	 * @return 会员列表
	 */
	@RequestMapping(value = "/member-list-store")
	public ModelAndView memberListStore() {
		ModelAndView view = getGridModelAndView();
		Integer is_lvmember=0;
		// 判定是否是b2b2c
		if (EopSetting.PRODUCT.equals("b2b2c")) {
			//如果为0，则隐藏会员等级相关
			is_lvmember=0;
		}else{
			is_lvmember=1;
		}
		view.addObject("is_lvmember", is_lvmember);
		view.addObject("lvlist", this.memberLvManager.list());
		view.setViewName("/shop/admin/member/member_list_store");
		return view;

	}

	/**
	 * 获取会员列表Json
	 * @param stype 搜索类型,Integer
	 * @param keyword 搜索关键字,String
	 * @param uname 会员名称,String
	 * @param mobile 联系方式,String
	 * @param lvId 会员等级,Integer
	 * @param email 邮箱,String
	 * @param sex 性别,Integer
	 * @param start_time 注册开始时间,String
	 * @param end_time 注册最后时间,String
	 * @param province_id 省份Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @return 会员列表Json
	 */

	@ResponseBody
	@RequestMapping(value = "/member-list-json")
	public GridJsonResult memberListJson(String uname, @RequestParam(value = "mobile", required = false) String mobile,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "sex", required = false) Integer sex,
			@RequestParam(value = "lvId", required = false) Integer lvId,
			@RequestParam(value = "start_time", required = false) String start_time,
			@RequestParam(value = "end_time", required = false) String end_time,
			@RequestParam(value = "stype", required = false) Integer stype,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "province_id", required = false) Integer province_id,
			@RequestParam(value = "city_id", required = false) Integer city_id,
			@RequestParam(value = "region_id", required = false) Integer region_id,
			@RequestParam(value = "isshopkeeper", required = false) Integer isshopkeeper) {

		Map memberMap = new HashMap();
		memberMap.put("stype", stype);
		memberMap.put("keyword", keyword);
		memberMap.put("uname", uname);
		memberMap.put("mobile", mobile);
		memberMap.put("lvId", lvId);
		memberMap.put("email", email);
		memberMap.put("sex", sex);
		memberMap.put("start_time", start_time);
		memberMap.put("end_time", end_time);
		memberMap.put("province_id", province_id);
		memberMap.put("city_id", city_id);
		memberMap.put("region_id", region_id);
		
		if (isshopkeeper != null && isshopkeeper == 1) {
			return JsonResultUtil.getGridJson(memberManager.searchMemberNoShop(memberMap, this.getPage(),
					this.getPageSize(), this.getSort(), this.getOrder()));
		} else {

			return JsonResultUtil.getGridJson(memberManager.searchMember(memberMap, this.getPage(), this.getPageSize(),
					this.getSort(), this.getOrder()));
		}

	}

	/**
	 * 修改会员
	 * @param birthday 生日,String
	 * @param oldMember 修改前会员,Member
	 * @param member 修改后会员,Member
	 * @param province 省份,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省份Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @return result result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-edit-member")
	public JsonResult saveEditMember(Member member, String mybirthday) {
		long birth = DateUtil.getDateline(mybirthday); // 将生日日期转换为秒数
		
		// 如果生日日期小于当前日期，则允许修改操作，否则不允许 修改人：DMRain 2015-12-17
		if (birth < DateUtil.getDateline()) {
			if (validEmail(member.getEmail())) {
				if (validEmailInDb(member.getEmail(), member.getMember_id())) {
					//检测手机号码是否存在
					if (StringUtil.isEmpty(member.getMobile())
							||this.memberManager.checkMobileExceptSelf(member.getMobile(),member.getMember_id()) == 0) {
						try {
							//如果会员固定电话不为空并且格式不正确
							if (!StringUtil.isEmpty(member.getTel()) && !this.isTel(member.getTel())) {
								return JsonResultUtil.getErrorJson("固定电话格式不正确");
							}
							
							member.setBirthday(birth);
							Member mb = this.memberManager.get(member.getMember_id());
							if (!StringUtil.isEmpty(member.getPassword())) {
								member.setPassword(StringUtil.md5(member.getPassword()));
							} else {
								member.setPassword(mb.getPassword());
							}
							member.setInfo_full(mb.getInfo_full());
							this.memberManager.edit(member);
							return JsonResultUtil.getSuccessJson("修改成功");
						} catch (Exception e) {
							return JsonResultUtil.getErrorJson("修改失败");
						}
					} else {
						return JsonResultUtil.getErrorJson("手机号码已存在");
					}
				} else {
					return JsonResultUtil.getErrorJson("此email已经有人使用");
				}
			} else {
				return JsonResultUtil.getErrorJson("email非法！");
			}
		} else {
			return JsonResultUtil.getErrorJson("生日不可以大于当前日期！");
		}
	}

	/**
	 * 删除会员
	 * @param member_id 会员Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/delete")
	public JsonResult delete(Integer[] member_id) {
		try {
			this.memberManager.delete(member_id);
			for (int i = 0; i < member_id.length; i++) {
				Member member = this.memberManager.getMemberByMemberId(member_id[i]);
				memberPluginBundle.onDeleteMember(member);
			}
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("会员信息删除出错", e);
			return JsonResultUtil.getErrorJson("删除失败" + e.getMessage());
		}
	}

	/**
	 * 跳转至会员详细页面
	 * @param memberId 会员Id,Integer
	 * @param member 会员,Member
	 * @param pluginTabs tab列表,List<Map>
	 * @param pluginHtmls tab页Html内容,List<Map>
	 * @return 会员详细页面
	 */
	@ResponseBody
	@RequestMapping(value = "/detail")
	public ModelAndView detail(Integer memberId) {
		ModelAndView view = new ModelAndView();
        
		Member member = memberManager.get(memberId);
		view.addObject("member", member);
		view.addObject("pluginTabs", memberPluginBundle.getDetailHtml(member));
		view.setViewName("/shop/admin/member/member_detail");
		return view;
	}

	/**
	 * 保存添加会员
	 * @author xulipeng
	 * @param member 会员,Member
	 * @param province 省份,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省份Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @param birthday 生日,String
	 * @return result 2014年4月1日18:22:50
	 */
	@ResponseBody
	@RequestMapping(value = "/save-member")
	public Object saveMember(Member member, String mybirthday) {

		// 判断会员是否为空
		if (member != null) {
			long birth = DateUtil.getDateline(mybirthday); // 将生日日期转换为秒数

			// 如果生日日期小于当前日期，则允许修改操作，否则不允许 修改人：DMRain 2015-12-17
			if (birth < DateUtil.getDateline()) {
				member.setBirthday(birth);
			}
			if (StringUtil.isEmpty(member.getUname())) {
				return JsonResultUtil.getErrorJson("用户名为空!");
			}

			int result = memberManager.checkname(member.getUname());

			if (result == 1) {
				return JsonResultUtil.getErrorJson("用户名已存在");
			}
			if (this.memberManager.checkemail(member.getEmail()) == 1) {
				return JsonResultUtil.getErrorJson("邮箱已存在");
			}

			//如果会员固定电话不为空并且格式不正确
			if (!StringUtil.isEmpty(member.getTel()) && !this.isTel(member.getTel())) {
				return JsonResultUtil.getErrorJson("固定电话格式不正确");
			}

			// 检查手机号码是否存在
			if ((!StringUtil.isEmpty(member.getMobile()))&&this.memberManager.checkMobile(member.getMobile()) == 1) {
				return JsonResultUtil.getErrorJson("手机号码已存在");
			}

			if (validEmail(member.getEmail())) {
				member.setRegtime(DateUtil.getDateline());// lzf add
				memberManager.add(member);

				Map map = new HashMap();
				map.put("result", 1);
				map.put("message", "保存会员成功");
				map.put("id", member.getMember_id());
				return map;
			} else {
				Map map = new HashMap();
				map.put("result", 0);
				map.put("message", "保存会员失败,email不合法!");
				map.put("id", member.getMember_id());
				return map;
			}
		}
		return null;
	}

	@ResponseBody
	@RequestMapping("/edit-remark")
	public JsonResult editRemark() {

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String modify_memo = request.getParameter("modify_memo");
		int memberid = StringUtil.toInt(request.getParameter("memberid"), true);
		Member member = this.memberManager.get(memberid);
		member.setRemark(modify_memo);
		try {
			memberManager.edit(member);
			return JsonResultUtil.getSuccessJson("会员备注修改成功");
		} catch (Exception e) {
			this.logger.error("修改会员备注", e);
			return JsonResultUtil.getErrorJson("会员备注修改失败");
		}

	}

	@RequestMapping(value = "/edit-point")
	public ModelAndView editPoint(Integer memberId) {

		ModelAndView view = new ModelAndView();

		view.addObject("member", memberManager.get(memberId));
		view.setViewName("editPoint");
		return view;
	}

	@ResponseBody
	@RequestMapping(value = "/edit-save-point")
	public JsonResult editSavePoint(Integer memberId, int point) {
		Member member = this.memberManager.get(memberId);
		Integer points = member.getPoint() + point;
		if(points < 0){
			member.setPoint(0);
		}else{
			member.setPoint(points);
		}
		PointHistory pointHistory = new PointHistory();
		pointHistory.setMember_id(memberId);
		pointHistory.setOperator("管理员");
		pointHistory.setPoint(point);
		pointHistory.setReason("管理员手工修改");
		pointHistory.setTime(DateUtil.getDateline());
		pointHistory.setMp(0);
		try {
			memberManager.edit(member);
			pointHistoryManager.addPointHistory(pointHistory);

			return JsonResultUtil.getSuccessJson("会员积分修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("修改失败");
		}
	}

	@RequestMapping(value = "/point-log")
	public ModelAndView pointLog(Integer memberId, int pointtype) {

		ModelAndView view = new ModelAndView();

		view.addObject("listPointHistory", pointHistoryManager.listPointHistory(memberId, pointtype));
		view.addObject("member", memberManager.get(memberId));
		view.setViewName("pointLog");

		return view;
	}

	@RequestMapping(value = "/advance")
	public ModelAndView advance(Integer memberId) {

		ModelAndView view = new ModelAndView();

		view.addObject("listAdvanceLogs", advanceLogsManager.listAdvanceLogsByMemberId(memberId));
		view.addObject("member", memberManager.get(memberId));
		view.setViewName("advance");

		return view;
	}

	@RequestMapping(value = "/comments")
	public ModelAndView comments(Integer memberId, String object_type) {

		ModelAndView view = new ModelAndView();
		Page page = memberCommentManager.getMemberComments(1, 100, StringUtil.toInt(object_type), memberId);

		view.addObject("page", memberCommentManager.getMemberComments(1, 100, StringUtil.toInt(object_type), memberId));

		view.setViewName("comments");

		if (page != null) {
			view.addObject("listComments", page.getResult());
		}
		return view;
	}

	/**
	 * 获取订单状态的json
	 * @param OrderStatus 订单状态
	 * @return
	 */
	private Map getStatusJson() {
		Map orderStatus = new HashMap();
		orderStatus.put("" + OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));
		orderStatus.put("" + OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));
		orderStatus.put("" + OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));
		orderStatus.put("" + OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));
		orderStatus.put("" + OrderStatus.ORDER_CANCELLATION,
				OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));
		orderStatus.put("" + OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
		return orderStatus;
	}

	private boolean validEmail(String email) {
		// 验证email,如果email不合法不允许修改
		String emailstr = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		Pattern p = Pattern.compile(emailstr);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	private boolean validEmailInDb(String email, Integer member_id) {
		// 验证email,如果email不合法不允许修改
		// String
		// emailstr="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		// Pattern p = Pattern.compile(emailstr);
		// Matcher m = p.matcher(email);
		// return m.matches();
		// return this.memberManager.getMemberByEmail(email)==null?true:false;
		return this.memberManager.checkemailInEdit(email, member_id);
	}
	
	/** 
	  * 固定电话号码验证 
	  * @author	duanmingyu
	  * @date	2017-9-26
	  * @param  str 
	  * @return 验证通过返回true 
	  */  
	 private boolean isTel(final String str) {  
	     Pattern p1 = null, p2 = null;  
	     Matcher m = null;  
	     boolean b = false;  
	     p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的  
	     p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的  
	     if (str.length() > 9) {  
	        m = p1.matcher(str);  
	        b = m.matches();  
	     } else {  
	         m = p2.matcher(str);  
	        b = m.matches();  
	     }  
	     return b;  
	 }
}
