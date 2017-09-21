package com.enation.app.shop.core.other.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.other.model.Activity;
import com.enation.app.shop.core.other.model.ActivityDetail;
import com.enation.app.shop.core.other.service.IActivityDetailManager;
import com.enation.app.shop.core.other.service.IActivityGiftManager;
import com.enation.app.shop.core.other.service.IActivityManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 促销活动管理
 * 2016-5-23
 * @author DMRain
 * @version 1.0
 */
@Scope("prototype")
@Controller 
@RequestMapping("/shop/admin/activity")
public class ActivityController extends GridController{

	@Autowired
	private IActivityManager activityManager;
	
	@Autowired
	private IActivityGiftManager activityGiftManager;
	
	@Autowired
	private IActivityDetailManager activityDetailManager;
	
	/**
	 * 获取促销活动分页列表
	 * @return
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(){
		ModelAndView view = this.getGridModelAndView();
		view.setViewName("/shop/admin/activity/activity_list");
		return view;
	}
	
	/**
	 * 获取促销活动分页列表json
	 * @param stype 搜索类型 0：普通搜索，1：高级搜索
	 * @param status 促销活动状态 1：进行中，2：未开始，3：已结束
	 * @param activity_name 促销活动名称
	 * @param activity_type 促销活动类型 1：普通促销，2：多级促销
	 * @param range_type 商品参加促销活动形式 1：全部参加，2：部分参加
	 * @param start_time 促销活动开始时间
	 * @param end_time 促销活动结束时间
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(Integer stype, Integer status, String activity_name, Integer activity_type,
			Integer range_type, String start_time, String end_time){
		Map actMap = new HashMap();
		actMap.put("stype", stype);
		actMap.put("status", status);
		actMap.put("activity_name", activity_name);
		actMap.put("activity_type", activity_type);
		actMap.put("range_type", range_type);
		actMap.put("start_time", start_time);
		actMap.put("end_time", end_time);
		
		Page webpage = this.activityManager.list(actMap, this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);
	}
	
	/**
	 * 跳转至新增促销活动页面
	 * @return
	 */
	@RequestMapping(value="/add")
	public ModelAndView add(){
		ModelAndView view = this.getGridModelAndView();
		view.addObject("giftList", this.activityGiftManager.getGiftList());
		view.addObject("bonusList", this.activityGiftManager.getBonusList());
		view.setViewName("/shop/admin/activity/activity_add");
		return view;
	}
	
	/**
	 * 保存新增的促销活动信息
	 * @param activity 促销活动信息
	 * @param detail 促销活动优惠详细
	 * @param startTime 促销活动开始时间
	 * @param endTime 促销活动结束时间
	 * @param goods_ids 参加促销活动的商品ID组
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(Activity activity, ActivityDetail detail, String startTime, String endTime, Integer[] goods_ids){
		try {
			
			//促销活动名称不能为空
			if (StringUtil.isEmpty(activity.getActivity_name())) {
				return JsonResultUtil.getErrorJson("请填写促销活动名称");
			}
			
			//促销活动的生效日期不能为空
			if (StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
				return JsonResultUtil.getErrorJson("请填写促销活动生效时间");
			}
			
			Long start_time = DateUtil.getDateline(startTime, "yyyy-MM-dd HH:mm:ss");
			Long end_time = DateUtil.getDateline(endTime, "yyyy-MM-dd HH:mm:ss");
			
			//促销活动结束时间必须要大于开始时间
			if (end_time < start_time) {
				return JsonResultUtil.getErrorJson("开始时间不能大于结束时间");
			}
			
			//判断促销活动在同一时间段是否重复添加
			if (this.activityManager.checkActByDate(start_time, end_time, 0) == 1) {
				return JsonResultUtil.getErrorJson("同一时间段不可重复添加促销活动");
			} 
			
			
			//促销活动的优惠门槛不能为空也不能为0
			if (detail.getFull_money() == null || detail.getFull_money() == 0) {
				return JsonResultUtil.getErrorJson("优惠门槛不能为空或不能为0");
			}
			
			//促销活动的优惠方式不能全部为空，至少要选择一项
			if (detail.getIs_full_minus() == null && detail.getIs_send_point() == null && detail.getIs_free_ship() == null 
					&& detail.getIs_send_gift() == null && detail.getIs_send_bonus() == null) {
				return JsonResultUtil.getErrorJson("请选择优惠方式");
			}
			
			//如果促销活动优惠详细是否包含满减不为空
			if (detail.getIs_full_minus() != null) {
				//如果促销活动包含了满减现金活动
				if (detail.getIs_full_minus() == 1) {
					//减少的现金不能为空也不能为0
					if (detail.getMinus_value() == null || detail.getMinus_value() == 0) {
						return JsonResultUtil.getErrorJson("减少的现金不能为空或不能为0");
					}
					
					//减少的现金必须小于优惠门槛
					if (detail.getMinus_value() > detail.getFull_money()) {
						return JsonResultUtil.getErrorJson("减少的现金不能多于" + detail.getFull_money() + "元");
					}
				}
			}
			
			//如果促销活动优惠详细是否包含满送积分不为空
			if (detail.getIs_send_point() != null) {
				//如果促销活动包含了满送积分活动
				if (detail.getIs_send_point() == 1) {
					//赠送的积分不能为空也不能为0
					if (detail.getPoint_value() == null || detail.getPoint_value() == 0) {
						return JsonResultUtil.getErrorJson("赠送的积分不能为空或不能为0");
					}
				}
			}
			
			//如果促销活动优惠详细是否包含满送赠品不为空
			if (detail.getIs_send_gift() != null) {
				//如果促销活动包含了满送赠品活动
				if (detail.getIs_send_gift() == 1) {
					//赠品id不能为0
					if (detail.getGift_id() == 0) {
						return JsonResultUtil.getErrorJson("请选择赠品");
					}
				}
			}
			
			//如果促销活动优惠详细是否包含满送优惠券不为空
			if (detail.getIs_send_bonus() != null) {
				//如果促销活动包含了满送优惠券活动
				if (detail.getIs_send_bonus() == 1) {
					//优惠券id不能为0
					if (detail.getBonus_id() == 0) {
						return JsonResultUtil.getErrorJson("请选择优惠券");
					}
				}
			}
			
			//如果选择的是部分商品参加活动，那么必须要选择商品，也就是goods_ids不能为空
			if (activity.getRange_type() == 2 && goods_ids == null) {
				return JsonResultUtil.getErrorJson("请选择要参加促销活动的商品");
			}
			
			activity.setStart_time(start_time);
			activity.setEnd_time(end_time);
			activity.setDisabled(0);
			this.activityManager.add(activity, detail, goods_ids);
			return JsonResultUtil.getSuccessJson("促销活动新增成功");
			
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("添加失败：", e);
			return JsonResultUtil.getErrorJson("新增促销活动失败");
		}
	}
	
	/**
	 * 跳转至编辑促销活动页面
	 * @return
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer activity_id){
		ModelAndView view = this.getGridModelAndView();
		view.addObject("activity", this.activityManager.get(activity_id));
		view.addObject("detailList", this.activityDetailManager.listDetail(activity_id));
		view.addObject("giftList", this.activityGiftManager.getGiftList());
		view.addObject("bonusList", this.activityGiftManager.getBonusList());
		view.setViewName("/shop/admin/activity/activity_edit");
		return view;
	}
	
	/**
	 * 保存修改的促销活动信息
	 * @param activity 促销活动信息
	 * @param detail 促销活动优惠详细信息
	 * @param startTime 促销活动开始时间
	 * @param endTime 促销活动结束时间
	 * @param goods_ids 参加促销活动的商品ID组
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(Activity activity, ActivityDetail detail, String startTime, String endTime, Integer[] goods_ids){
		try {
			
			//促销活动名称不能为空
			if (StringUtil.isEmpty(activity.getActivity_name())) {
				return JsonResultUtil.getErrorJson("请填写促销活动名称");
			}
			
			//促销活动的生效日期不能为空
			if (StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
				return JsonResultUtil.getErrorJson("请填写促销活动生效时间");
			}
			
			Long start_time = DateUtil.getDateline(startTime, "yyyy-MM-dd HH:mm:ss");
			Long end_time = DateUtil.getDateline(endTime, "yyyy-MM-dd HH:mm:ss");
			
			//促销活动结束时间必须要大于开始时间
			if (end_time < start_time) {
				return JsonResultUtil.getErrorJson("开始时间不能大于结束时间");
			}
			
			//判断促销活动在同一时间段是否重复添加
			if (this.activityManager.checkActByDate(start_time, end_time, activity.getActivity_id()) == 1) {
				return JsonResultUtil.getErrorJson("同一时间段不可重复添加促销活动");
			} 
			
			//促销活动的优惠门槛不能为空也不能为0
			if (detail.getFull_money() == null || detail.getFull_money() == 0) {
				return JsonResultUtil.getErrorJson("优惠门槛不能为空或不能为0");
			}
			
			//促销活动的优惠方式不能全部为空，至少要选择一项
			if (detail.getIs_full_minus() == null && detail.getIs_send_point() == null && detail.getIs_free_ship() == null 
					&& detail.getIs_send_gift() == null && detail.getIs_send_bonus() == null) {
				return JsonResultUtil.getErrorJson("请选择优惠方式");
			}
			
			//如果促销活动优惠详细是否包含满减不为空
			if (detail.getIs_full_minus() != null) {
				//如果促销活动包含了满减现金活动
				if (detail.getIs_full_minus() == 1) {
					//减少的现金不能为空也不能为0
					if (detail.getMinus_value() == null || detail.getMinus_value() == 0) {
						return JsonResultUtil.getErrorJson("减少的现金不能为空或不能为0");
					}
					
					//减少的现金必须小于优惠门槛
					if (detail.getMinus_value() > detail.getFull_money()) {
						return JsonResultUtil.getErrorJson("减少的现金不能多于" + detail.getFull_money() + "元");
					}
				}
			}
			
			//如果促销活动优惠详细是否包含满送积分不为空
			if (detail.getIs_send_point() != null) {
				//如果促销活动包含了满送积分活动
				if (detail.getIs_send_point() == 1) {
					//赠送的积分不能为空也不能为0
					if (detail.getPoint_value() == null || detail.getPoint_value() == 0) {
						return JsonResultUtil.getErrorJson("赠送的积分不能为空或不能为0");
					}
				}
			}
			
			//如果促销活动优惠详细是否包含满送赠品不为空
			if (detail.getIs_send_gift() != null) {
				//如果促销活动包含了满送赠品活动
				if (detail.getIs_send_gift() == 1) {
					//赠品id不能为0
					if (detail.getGift_id() == 0) {
						return JsonResultUtil.getErrorJson("请选择赠品");
					}
				}
			}
			
			//如果促销活动优惠详细是否包含满送优惠券不为空
			if (detail.getIs_send_bonus() != null) {
				//如果促销活动包含了满送优惠券活动
				if (detail.getIs_send_bonus() == 1) {
					//优惠券id不能为0
					if (detail.getBonus_id() == 0) {
						return JsonResultUtil.getErrorJson("请选择优惠券");
					}
				}
			}
			
			//如果选择的是部分商品参加活动，那么必须要选择商品，也就是goods_ids不能为空
			if (activity.getRange_type() == 2 && goods_ids == null) {
				return JsonResultUtil.getErrorJson("请选择要参加促销活动的商品");
			}
			
			activity.setStart_time(start_time);
			activity.setEnd_time(end_time);
			
			this.activityManager.edit(activity, detail, goods_ids);
			return JsonResultUtil.getSuccessJson("促销活动修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("修改失败：", e);
			return JsonResultUtil.getErrorJson("修改促销活动失败");
		}
	}
	
	/**
	 * 删除促销活动
	 * @param activity_id 促销活动ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer activity_id){
		try {
			this.activityManager.delete(activity_id);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			this.logger.error("删除失败：", e);
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}
	
	/**
	 * 获取所有有效并且正在上架销售的商品分页列表json
	 * @param keyword 搜索关键字
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-goods-json")
	public GridJsonResult listGoodsJson(String keyword, Integer activity_id){
		Page webpage = this.activityManager.listGoods(keyword, activity_id, this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);
	}
	/**
	 * 获取所有有效并且正在上架销售的商品列表json
	 * @param keyword 搜索关键字
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-goods-json-map")
	public GridJsonResult listGoodsJsonMap(String keyword, Integer activity_id){
		List<Map> list = this.activityManager.listGoods(keyword, activity_id);
		return JsonResultUtil.getGridJson(list);
	}
}
