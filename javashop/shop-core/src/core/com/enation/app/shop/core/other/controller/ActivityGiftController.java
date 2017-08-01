package com.enation.app.shop.core.other.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.other.model.ActivityGift;
import com.enation.app.shop.core.other.service.IActivityGiftManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 促销活动管理
 * 2016-5-24
 * @author DMRain
 * @version 1.0
 */
@Scope("prototype")
@Controller 
@RequestMapping("/shop/admin/gift")
public class ActivityGiftController extends GridController{

	@Autowired
	private IActivityGiftManager activityGiftManager;
	
	/**
	 * 促销活动分页列表
	 * @return
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(){
		ModelAndView view = this.getGridModelAndView();
		view.setViewName("/shop/admin/gift/gift_list");
		return view;
	}
	
	/**
	 * 获取促销活动分页列表json
	 * @param keyword 搜素关键字
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(String keyword){
		Page webpage = this.activityGiftManager.list(keyword, this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);
	}
	
	/**
	 * 跳转至赠品添加页面
	 * @return
	 */
	@RequestMapping(value="/add")
	public String add() {
		return "/shop/admin/gift/gift_add";
	}
	
	/**
	 * 保存添加赠品信息
	 * @param gift 赠品信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(ActivityGift gift){
		try {
			
			//赠品名称不能为空
			if (StringUtil.isEmpty(gift.getGift_name())) {
				return JsonResultUtil.getErrorJson("请填写赠品名称");
			}
			
			//赠品价格不能为空也不能为0
			if (gift.getGift_price() == null || gift.getGift_price() == 0) {
				return JsonResultUtil.getErrorJson("赠品价格不能为空也不能为0");
			}
			
			//赠品价格不能为空也不能为0
			if (gift.getActual_store() == null || gift.getActual_store() == 0) {
				return JsonResultUtil.getErrorJson("赠品库存不能为空也不能为0");
			}
			
			//判断是否上传了赠品图片
			if (StringUtil.isEmpty(gift.getGift_img())) {
				return JsonResultUtil.getErrorJson("请上传赠品图片");
			}
			
			//转化图片路径
			String url = this.transformPath(gift.getGift_img());
			
			//将原图片路径替换
			gift.setGift_img(url);
			
			//设置赠品可用库存(添加时可用库存=实际库存)
			gift.setEnable_store(gift.getActual_store());
			
			//添加赠品创建时间
			gift.setCreate_time(DateUtil.getDateline());
			
			//设置赠品类型(1.0版本赠品类型默认为0：普通赠品)
			gift.setGift_type(0);
			
			//默认不禁用
			gift.setDisabled(0);
			
			this.activityGiftManager.add(gift);
			return JsonResultUtil.getSuccessJson("添加成功");
			
		} catch (Exception e) {
			this.logger.error("添加失败：", e);
			return JsonResultUtil.getErrorJson("添加失败");
		}
	}
	
	/**
	 * 跳转至修改赠品信息页面
	 * @param gift_id 赠品ID
	 * @return
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer gift_id){
		ModelAndView view = this.getGridModelAndView();
		ActivityGift gift = this.activityGiftManager.get(gift_id);
		gift.setGift_img(StaticResourcesUtil.convertToUrl(gift.getGift_img()));
		view.addObject("gift", gift);
		view.setViewName("/shop/admin/gift/gift_edit");
		return view;
	}
	
	/**
	 * 保存修改赠品信息
	 * @param gift 赠品信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(ActivityGift gift){
		try {
			
			//赠品名称不能为空
			if (StringUtil.isEmpty(gift.getGift_name())) {
				return JsonResultUtil.getErrorJson("请填写赠品名称");
			}
			
			//赠品价格不能为空也不能为0
			if (gift.getGift_price() == null || gift.getGift_price() == 0) {
				return JsonResultUtil.getErrorJson("赠品价格不能为空也不能为0");
			}
			
			//赠品价格不能为空也不能为0
			if (gift.getActual_store() == null || gift.getActual_store() == 0) {
				return JsonResultUtil.getErrorJson("赠品库存不能为空也不能为0");
			}
			
			//判断是否上传了赠品图片
			if (StringUtil.isEmpty(gift.getGift_img())) {
				return JsonResultUtil.getErrorJson("请上传赠品图片");
			}
			
			//转化图片路径
			String url = this.transformPath(gift.getGift_img());
			
			//将原图片路径替换
			gift.setGift_img(url);
			
			//获取原赠品信息
			ActivityGift activityGift = this.activityGiftManager.get(gift.getGift_id());
			
			//获取赠品原实际库存和原可用库存的差
			int differ = activityGift.getActual_store() - activityGift.getEnable_store();
			
			//设置赠品修改后的可用库存
			gift.setEnable_store(gift.getActual_store() - differ);
			
			this.activityGiftManager.edit(gift);
			return JsonResultUtil.getSuccessJson("修改成功");
			
		} catch (Exception e) {
			this.logger.error("修改失败：", e);
			return JsonResultUtil.getErrorJson("修改失败");
		}
	}
	
	/**
	 * 删除赠品信息
	 * @param gift_id 赠品ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer gift_id){
		try {
			int result = this.activityGiftManager.checkGiftInAct(gift_id);
			
			//如果此赠品没有参与促销活动，可以删除，如过参与了，不可删除
			if (result == 1) {
				return JsonResultUtil.getErrorJson("此赠品已经参与了促销活动，不可删除！");
			} else {
				this.activityGiftManager.delete(gift_id);
				return JsonResultUtil.getSuccessJson("删除成功");
			}
		} catch (Exception e) {
			this.logger.error("删除失败：", e);
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}
	
	/**
	 * 页面中传递过来的图片地址为:http://<staticserver>/<image path>
	 * 如:http://static.enationsoft.com/attachment/goods/1.jpg
	 * 存在库中的为fs:/attachment/goods/1.jpg 生成fs式路径
	 * @param path 原地址路径
	 * @return
	 */
	private String transformPath(String path) {
		String static_server_domain= SystemSetting.getStatic_server_domain();

		String regx =static_server_domain;
		path = path.replace(regx, EopSetting.FILE_STORE_PREFIX);
		return path;
	}
}
