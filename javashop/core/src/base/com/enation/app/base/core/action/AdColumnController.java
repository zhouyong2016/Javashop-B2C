package com.enation.app.base.core.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 后台广告位管理
 * @author DMRain 2016年2月20日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/core/admin/adColumn")
public class AdColumnController extends GridController{

	@Autowired
	private IAdColumnManager adColumnManager;
	
	/**
	 * 跳转至广告位列表
	 * @return 广告位列表
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(){
		ModelAndView view = new ModelAndView();
		view.addObject("pageSize", this.getPageSize());
		view.setViewName("/core/admin/adv/adc_list");
		return view;
	}
	
	/**
	 * 获取广告位分页列表json
	 * @return 广告位分页列表JSON
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(String cname) {
		//获取广告位分页列表
		this.webpage = this.adColumnManager.pageAdvPos(cname,this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);
	}
	
	/**
	 * 跳转至添加广告位
	 * @return 添加广告位页面
	 */
	@RequestMapping(value="/add")
	public String add(){
		return "/core/admin/adv/adc_input";
	}
	
	/**
	 * 检查是否包含px 如果没有重新赋值
	 * @param adColumn
	 * @return
	 */
	private AdColumn checkpx(AdColumn adColumn){
		if(!adColumn.getHeight().contains("px")){
			adColumn.setHeight(adColumn.getHeight()+"px");
		}
		if(!adColumn.getWidth().contains("px")){
			adColumn.setWidth(adColumn.getWidth()+"px");
		}
		return adColumn;
	}
	/**
	 * 添加广告位
	 * @param adColumn 广告位
	 * @return 添加状态
	 */
	@ResponseBody
	@RequestMapping(value="/add-save")
	public JsonResult addSave(AdColumn adColumn) {
		try {
			adColumn=this.checkpx(adColumn);
			this.adColumnManager.addAdvc(adColumn);
			return JsonResultUtil.getSuccessJson("广告位添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("添加广告位出错",e);
			return JsonResultUtil.getErrorJson("添加失败"+e.getMessage());
		}
	}
	
	/**
	 * 跳转至修改广告位页面
	 * @param ac_id 广告位Id
	 * @return 修改广告位页面
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Long ac_id){
		ModelAndView view = new ModelAndView();
		view.addObject("adColumn", this.adColumnManager.getADcolumnDetail(ac_id));
		view.setViewName("/core/admin/adv/adc_edit");
		return view;
	}
	
	/**
	 * 修改广告位
	 * @param adColumn 广告位
	 * @return 修改广告位状态
	 */
	@ResponseBody
	@RequestMapping(value="/edit-save")
	public JsonResult editSave(AdColumn adColumn){
		try {
			adColumn=this.checkpx(adColumn);
			this.adColumnManager.updateAdvc(adColumn);
			return JsonResultUtil.getSuccessJson("修改广告位成功");
		} catch (Exception e) {
			this.logger.error("修改广告位出错",e);
			return JsonResultUtil.getErrorJson("修改失败"+e.getMessage());
		}
	}
	
	/**
	 * 删除广告位
	 * @param acid 广告位ID
	 * @return 广告位删除状态
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer[] acid){
		//是否为演示站点
		if (EopSetting.IS_DEMO_SITE) {
			for (Integer id : acid) {
				if (id <= 21) {
					return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				}
			}
		}
		
		//删除广告位
		try {
			this.adColumnManager.delAdcs(acid);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.logger.error("删除广告位出错",e);
			return JsonResultUtil.getErrorJson("删除失败"+e.getMessage());
		}
	}
	
}
