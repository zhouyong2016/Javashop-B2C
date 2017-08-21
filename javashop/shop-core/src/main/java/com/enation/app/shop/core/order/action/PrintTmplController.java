package com.enation.app.shop.core.order.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.order.model.PrintTmpl;
import com.enation.app.shop.core.order.service.ILogiManager;
import com.enation.app.shop.core.order.service.IPrintTmplManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 打印模板
 * 
 * @author lzf<br/>
 *         2010-5-4上午11:10:46<br/>
 *         version 1.0
 * @author LiFenLong 2014-4-1;4.0版本改造         
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/print-tmpl")
public class PrintTmplController extends GridController {
	
	@Autowired
	private IPrintTmplManager printTmplManager;
	
	@Autowired
	private ILogiManager logiManager;
	/**
	 * 跳转至 快递单模板列表页
	 * @return 快递单模板列表页
	 */
	@RequestMapping(value="list")
	public ModelAndView list(){
		ModelAndView view=getGridModelAndView();
		view.setViewName("/shop/admin/printTmpl/list");
		return view;
	}
	
	/**
	 * 快递单模板列表Json
	 * @param list 快递单模板列表
	 * @return 快递单模板列表Json
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(){
		List list = printTmplManager.list();
		return JsonResultUtil.getGridJson(list);
	}
	/**
	 * 跳转至快递单添加页
	 * @param logiList 配送方式列表,List
	 * @return 快递单添加页
	 */
	@RequestMapping(value="/add")
	public ModelAndView add(){
		ModelAndView view=new ModelAndView();
		view.addObject("logiList", this.logiManager.list());
		view.setViewName("/shop/admin/printTmpl/add");
		return view;
	}
	
	/**
	 * 保存快递单
	 * @param printTmpl 快递单,printTmpl
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(PrintTmpl printTmpl){
		try {
			if(printTmplManager.check(printTmpl.getPrt_tmpl_title())){
				return JsonResultUtil.getSuccessJson("已经存在此快递单模板");
			}else{
				printTmplManager.add(printTmpl);
				return JsonResultUtil.getSuccessJson("模板添加成功");
			}
		} catch (Exception e) {
			logger.error("模板添加失败", e);
			return JsonResultUtil.getErrorJson("模板添加失败");
		}
	}
	
	/**
	 * 跳转至修改快递单
	 * @param logiList 配送方式列表,List
	 * @param prt_tmplId 快递单Id,Integer
	 * @param printTmpl 快递单,printTmpl
	 * @return 修改快递单
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer prt_tmplId){
		ModelAndView view=new ModelAndView();
		PrintTmpl tmp=printTmplManager.get(prt_tmplId);
		view.addObject("logiList", this.logiManager.list());
		view.addObject("prt_tmplId", prt_tmplId);
		if(tmp != null && tmp.getBgimage().startsWith("fs")){
			tmp.setBgimage(StaticResourcesUtil.convertToUrl(tmp.getBgimage()));
		}
		view.addObject("printTmpl", tmp);
		view.setViewName("/shop/admin/printTmpl/edit");
		return view;
	}
	
	/**
	 * 保存修改快递单
	 * @param printTmpl 快递单,printTmpl
	 * @return json 
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(PrintTmpl printTmpl){
		try {
			printTmplManager.edit(printTmpl);
			
			return JsonResultUtil.getSuccessJson("模板修改成功");
		} catch (Exception e) {
			logger.error("模板修改失败", e);
			return JsonResultUtil.getErrorJson("模板修改失败");
		}
	}
	
	/**
	 * 删除快递单
	 * @param prt_tmpl_id 快递单Id,Integer
	 * @return json 
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer[] prt_tmpl_id){
		if(EopSetting.IS_DEMO_SITE){
			
			return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
		}
		try {
			this.printTmplManager.clean(prt_tmpl_id);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (Exception e) {
			logger.error("模板删除失败", e);
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}
}
