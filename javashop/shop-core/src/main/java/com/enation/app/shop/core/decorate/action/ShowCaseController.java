package com.enation.app.shop.core.decorate.action;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.decorate.model.ShowCase;
import com.enation.app.shop.core.decorate.service.IShowCaseManager;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 
 * 橱窗管理controller
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Controller
@RequestMapping("/core/admin/showcase")
public class ShowCaseController extends GridController{
	
	@Autowired
	private IShowCaseManager showCaseManager;

	protected final Logger logger = Logger.getLogger(getClass());
	/**
	 * 跳转橱窗列表
	 * @return
	 */
	@RequestMapping(value="list")
	public ModelAndView list(){
		ModelAndView view=getGridModelAndView();
		view.setViewName("/floor/admin/showcase/list");
		return view;
	}
	
	/**
	 * 获取所有橱窗信息
	 * @return 
	 */
	@ResponseBody
	@RequestMapping(value="list-json")
	public GridJsonResult listJson(){
		List list=this.showCaseManager.listAll();
		return JsonResultUtil.getGridJson(list);
	}
	
	/**
	 * 跳转添加橱窗页面
	 * @return
	 */
	@RequestMapping(value="add-showcase")
	public ModelAndView addShowCase(){
		ModelAndView view=new ModelAndView();
		view.addObject("actionName", "save-showcase.do");
		view.setViewName("/floor/admin/showcase/add-showcase");
		return view;
	}
	
	/**
	 * 保存橱窗信息
	 * @param showcase 橱窗实体
	 * @return 处理结果
	 */
	@ResponseBody
	@RequestMapping(value="save-showcase")
	public JsonResult saveShowCase(ShowCase showcase){
		try {
			this.showCaseManager.saveShowCase(showcase);
			return JsonResultUtil.getSuccessJson("保存成功");
		} catch (RuntimeException e) {
			this.logger.error("保存橱窗信息失败",e);
			return JsonResultUtil.getErrorJson("保存失败");
		}
	}
	
	/**
	 * 保存橱窗排序
	 * @param id 橱窗id
	 * @param sort 顺序
	 * @return 处理结果
	 */
	@ResponseBody
	@RequestMapping(value="save-sort")
	public JsonResult saveSort(Integer[] showcase_ids,Integer[] showcase_sorts){
		try {
			this.showCaseManager.saveSort(showcase_ids,showcase_sorts);
			return JsonResultUtil.getSuccessJson("保存排序成功");
		} catch (RuntimeException e) {
			this.logger.error("保存橱窗排序失败",e);
			return JsonResultUtil.getErrorJson("保存排序失败");
		}
	}
	
	/**
	 * 保存显示状态
	 * @param id 橱窗id
	 * @param is_display 是否显示  0:显示 1:不显示
	 * @return 处理结果
	 */
	@ResponseBody
	@RequestMapping(value="save-display")
	public JsonResult saveDisplay(Integer id,Integer is_display){
		try {
			this.showCaseManager.saveDisplay(id,is_display);
			return JsonResultUtil.getSuccessJson("保存成功");
		} catch (RuntimeException e) {
			this.logger.error("保存橱窗显示状态失败",e);
			return JsonResultUtil.getErrorJson("保存失败");
		}
	}
	
	/**
	 * 跳转编辑橱窗
	 * @param id 橱窗id
	 * @return 
	 */
	@RequestMapping(value="edit-showcase")
	public ModelAndView editShowCase(Integer id){
		ModelAndView view=new ModelAndView();
		ShowCase showCase=this.showCaseManager.getShowCaseById(id);
		view.addObject("showcase", showCase);
		view.addObject("actionName","save-edit.do");
		view.setViewName("/floor/admin/showcase/edit-showcase");
		return view;
	}
	
	/**
	 * 保存橱窗编辑
	 * @param showCase 橱窗实体
	 * @return 处理结果
	 */
	@ResponseBody
	@RequestMapping(value="save-edit")
	public JsonResult saveEdit(ShowCase showCase){
		try {
			this.showCaseManager.saveEdit(showCase);
			return JsonResultUtil.getSuccessJson("修改成功");
		} catch (RuntimeException e) {
			this.logger.error("修改橱窗信息失败",e);
			return JsonResultUtil.getErrorJson("修改失败");
		}
	}
	
	/**
	 * 删除橱窗信息
	 * @param id 橱窗id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="delete")
	public JsonResult delete(Integer id){
		try {
			if (EopSetting.IS_DEMO_SITE) {
				if (id <= 5) {
					return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				}
			}
			this.showCaseManager.delete(id);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			this.logger.error("删除橱窗信息失败",e);
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}
	
	/**
	 * 跳转橱窗商品添加
	 * @return
	 */
	@RequestMapping(value="add-showcase-goods")
	public String addShowCaseGoods(){
		return "/floor/admin/showcase/add-showcase-goods";
	}
	
	/**
	 * 跳转橱窗商品编辑
	 * @param id 橱窗id
	 * @return
	 */
	@RequestMapping(value="edit-showcase-goods")
	public  ModelAndView  editShowCaseGoods(Integer id){
		ModelAndView view=new ModelAndView();
		view.addObject("showcaseid", id);
		view.setViewName("/floor/admin/showcase/edit-showcase-goods");
		return view;
	}
	
	/**
	 * 获取已选择商品
	 * @param id 橱窗id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="get-selected-goods")
	public GridJsonResult getSelectGoods(Integer id){
		ShowCase showCase=this.showCaseManager.getShowCaseById(id);
		List<Goods> list=this.showCaseManager.getSelectGoods(showCase.getContent());
		return JsonResultUtil.getGridJson(list);
	}
	/**
	 * 获取已选择商品Map集合
	 * @param id 橱窗id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="get-selected-goods-map")
	public GridJsonResult getSelectGoodsMap(Integer id){
		ShowCase showCase=this.showCaseManager.getShowCaseById(id);
		List<Map> list=this.showCaseManager.getSelectGoodsMap(showCase.getContent());
		return JsonResultUtil.getGridJson(list);
	}
	
}
