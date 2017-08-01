package com.enation.app.shop.component.bonus.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 优惠券类型管理
 * 
 * @author DMRain 2016年3月14日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/bonus-type")
public class BonusTypeController extends GridController {

	@Autowired
	private IBonusTypeManager bonusTypeManager;

	@RequestMapping(value = "/list")
	public ModelAndView list() {
		ModelAndView view = new ModelAndView();
		view.addObject("pageSize", this.getPageSize());
		view.setViewName("/shop/admin/bonus/bonus_type_list");
		return view;
	}

	@ResponseBody
	@RequestMapping(value = "/list-json")
	public GridJsonResult listJson() {
		this.webpage = this.bonusTypeManager.list(this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);
	}

	@RequestMapping(value = "/add")
	public String add() {
		return "/shop/admin/bonus/bonus_type_add";
	}

	/**
	 * 添加优惠券
	 * @param bonusType
	 * @param useTimeStart
	 * @param useTimeEnd
	 * @param sendTimeStart
	 * @param sendTimeEnd
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/save-add")
	public JsonResult saveAdd(BonusType bonusType, String useTimeStart, String useTimeEnd, String sendTimeStart,
			String sendTimeEnd) {

		if (StringUtil.isEmpty(bonusType.getRecognition())) {
			return JsonResultUtil.getErrorJson("请输入优惠券识别码");
		}

		//检查优惠券识别码是否重复
		if (this.bonusTypeManager.checkRecognition(bonusType.getRecognition(), 0) == 1) {
			return JsonResultUtil.getErrorJson("优惠券识别码重复");
		}
		
		if (StringUtil.isEmpty(bonusType.getType_name())) {
			return JsonResultUtil.getErrorJson("请输入类型名称");
		}

		if (bonusType.getType_money() == null) {
			return JsonResultUtil.getErrorJson("请输入金额");
		}

		if (StringUtil.isEmpty(useTimeStart)) {
			return JsonResultUtil.getErrorJson("请输入使用起始日期");
		}
		
		bonusType.setUse_start_date(DateUtil.getDateline(useTimeStart+" 00:00:00","yyyy-MM-dd HH:mm:ss"));

		if (StringUtil.isEmpty(useTimeEnd)) {
			return JsonResultUtil.getErrorJson("请输入使用结束日期");
		}
		bonusType.setUse_end_date(DateUtil.getDateline(useTimeEnd+" 23:59:59","yyyy-MM-dd HH:mm:ss"));

		if (DateUtil.getDateline(useTimeEnd+" 23:59:59","yyyy-MM-dd HH:mm:ss") <= DateUtil.getDateline(useTimeStart+" 00:00:00","yyyy-MM-dd HH:mm:ss")) {
			return JsonResultUtil.getErrorJson("结束日期不能小于起始日期");
		}

		if (!StringUtil.isEmpty(sendTimeStart)) {
			bonusType.setSend_start_date(DateUtil.getDateline(sendTimeStart+" 00:00:00","yyyy-MM-dd HH:mm:ss"));
		}

		if (!StringUtil.isEmpty(sendTimeEnd)) {
			bonusType.setSend_end_date(DateUtil.getDateline(sendTimeEnd+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
		}

		bonusType.setBelong(1);
		try {
			this.bonusTypeManager.add(bonusType);
			return JsonResultUtil.getSuccessJson("保存优惠券类型成功");
		} catch (Throwable e) {
			this.logger.error("保存优惠券类型出错", e);
			return JsonResultUtil.getErrorJson("保存优惠券类型出错" + e.getMessage());
		}

	}

	@RequestMapping(value = "/edit")
	public ModelAndView edit(Integer typeid) {
		ModelAndView view = new ModelAndView();
		view.addObject("bonusType", this.bonusTypeManager.get(typeid));
		view.setViewName("/shop/admin/bonus/bonus_type_edit");
		return view;
	}

	@ResponseBody
	@RequestMapping(value = "/save-edit")
	public JsonResult saveEdit(BonusType bonusType, String useTimeStart, String useTimeEnd, String sendTimeStart,
			String sendTimeEnd) {

		if (StringUtil.isEmpty(bonusType.getRecognition())) {
			return JsonResultUtil.getErrorJson("请输入优惠券识别码");
		}

		//检查优惠券识别码是否重复
		if (this.bonusTypeManager.checkRecognition(bonusType.getRecognition(), bonusType.getType_id()) == 1) {
			return JsonResultUtil.getErrorJson("优惠券识别码重复");
		}
		
		if (StringUtil.isEmpty(bonusType.getType_name())) {
			return JsonResultUtil.getErrorJson("请输入类型名称");
		}

		if (bonusType.getType_money() == null) {
			return JsonResultUtil.getErrorJson("请输入金额");
		}

		if (StringUtil.isEmpty(useTimeStart)) {
			return JsonResultUtil.getErrorJson("请输入使用起始日期");
		}
		bonusType.setUse_start_date(DateUtil.getDateline(useTimeStart+" 00:00:00","yyyy-MM-dd HH:mm:ss"));

		if (StringUtil.isEmpty(useTimeEnd)) {
			return JsonResultUtil.getErrorJson("请输入使用结束日期");
		}
		bonusType.setUse_end_date(DateUtil.getDateline(useTimeEnd+" 23:59:59","yyyy-MM-dd HH:mm:ss"));

		if (DateUtil.getDateline(useTimeEnd+" 23:59:59","yyyy-MM-dd HH:mm:ss") <= DateUtil.getDateline(useTimeStart+" 00:00:00","yyyy-MM-dd HH:mm:ss")) {
			return JsonResultUtil.getErrorJson("结束日期不能小于起始日期");
		}

		if (!StringUtil.isEmpty(sendTimeStart)) {
			bonusType.setSend_start_date(DateUtil.getDateline(sendTimeStart+" 00:00:00","yyyy-MM-dd HH:mm:ss"));
		}

		if (!StringUtil.isEmpty(sendTimeEnd)) {
			bonusType.setSend_end_date(DateUtil.getDateline(sendTimeEnd+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
		}

		bonusType.setBelong(1);
		try {
			this.bonusTypeManager.update(bonusType);
			return JsonResultUtil.getSuccessJson("保存优惠券类型成功");
		} catch (Throwable e) {
			this.logger.error("保存优惠券类型出错", e);
			return JsonResultUtil.getErrorJson("保存优惠券类型出错" + e.getMessage());
		}

	}
	
	/**
	 * 删除一个优惠券
	 * add by DMRain 2016-5-30
	 * @param type_id 优惠券ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delete")
	public JsonResult delete(Integer type_id){
		
		try {
			
			//检查促销活动是否已经关联了促销活动
			if (this.bonusTypeManager.checkForActivity(type_id) == 1) {
				return JsonResultUtil.getErrorJson("此优惠券已经关联了促销活动，不可删除");
			} else {
				
				//检查促销活动是否已经发放
				if (this.bonusTypeManager.checkIsSend(type_id) == 1) {
					return JsonResultUtil.getErrorJson("此优惠券已经发放，不可删除");
				} else {
					this.bonusTypeManager.del(type_id);
					return JsonResultUtil.getSuccessJson("删除优惠券类型成功");
				}
			}
			
		} catch (Throwable e) {
			this.logger.error("删除优惠券类型出错", e);
			return JsonResultUtil.getErrorJson("删除优惠券类型出错"+e.getMessage());
		}
		
	}
	
//批量删除暂时注释掉 DMRain 2016-5-30	
//	@ResponseBody
//	@RequestMapping(value = "/delete")
//	public JsonResult delete(Integer[] type_id){
//		
//		try {
//			this.bonusTypeManager.delete(type_id);
//			return JsonResultUtil.getSuccessJson("删除优惠券类型成功");
//		} catch (Throwable e) {
//			this.logger.error("删除优惠券类型出错", e);
//			return JsonResultUtil.getErrorJson("删除优惠券类型出错"+e.getMessage());
//		}
//		
//	}
	
}
