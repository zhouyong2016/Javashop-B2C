package com.enation.app.shop.core.order.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.order.model.Logi;
import com.enation.app.shop.core.order.service.ILogiManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.JsonResultUtil;
/**
 * 物流公司Controller
 * @author LiFenLong 2014-4-2;4.0改版
 * @version2.0 wangxin 2016-2-28 6.0版本改造 
 */

@Controller 
@Scope("prototype")
@RequestMapping("/shop/admin/logi")
public class LogiController extends GridController {
	@Autowired
	private IDaoSupport daoSupport;
	@Autowired
	private ILogiManager logiManager;

	/**
	 * 跳转至物流公司添加页面
	 * @return 物流公司添加页面
	 */
	@RequestMapping(value="/add-logi")
	public String add_logi(){
		return "/shop/admin/setting/logi_add";
	}
	/**
	 * 跳转至物流公司修改页面
	 * @return 流公司修改页面
	 */
	@RequestMapping(value="/edit-logi")
	public ModelAndView edit_logi(Integer cid){
		ModelAndView view = new ModelAndView();
		view.addObject("logi",this.logiManager.getLogiById(cid));
		view.setViewName("/shop/admin/setting/logi_edit");
		return view;
	}
	/**
	 * 跳转至物流公司列表
	 * @return 物流公司列表
	 */
	@RequestMapping(value="/list-logi")
	public String list_logi(){
		return "/shop/admin/setting/logi_list";
	}
	/**
	 * 获取物流公司列表Json
	 * @author LiFenLong
	 * @param order 排序,String
	 * @return 物流公司列表Json
	 */
	@ResponseBody
	@RequestMapping(value="/list-logi-json")
	public GridJsonResult list_logiJson(String order){
		this.webpage = this.logiManager.pageLogi(order, this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);
	}
	/**
	 * 删除物流公司
	 * @param id,物流公司Id
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer[] id){
		try {
			// 如果 剩余的物流公司总类数量小于等于 要删除的 物流公司总类数量，那么制造一个错误，让这个程序提出一个异常前台删除失败
			if (id.length >= this.daoSupport.queryForInt(
					"select count(0) from es_logi_company")) {
				return JsonResultUtil.getErrorJson("不能删除全部物流公司");
			}
			this.logiManager.delete(id);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException e) {
			logger.error("物流公司删除失败", e);
			return JsonResultUtil.getErrorJson("快递公司删除失败");
		}
	}
	/**
	 * 添加物流公司
	 * @param code 物流公司代码,String
	 * @param name 物流公司名称,String
	 * @param logi 物流公司,Logi
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(String code,String name){
		try {
			Logi logi = new Logi();
			logi.setCode(code);
			logi.setName(name);
			Logi logicode = logiManager.getLogiByCode(code);
			Logi loginame = logiManager.getLogiByName(name);
			if(loginame != null){
				return JsonResultUtil.getErrorJson("快递公司名称不能相同");
			}
			if(logicode != null){
				return JsonResultUtil.getErrorJson("快递公司代码不能相同");
			}else{
				logiManager.saveAdd(logi);
				return JsonResultUtil.getSuccessJson("添加成功");
			}
		} catch (Exception e) {
			logger.error("物流公司添加失败", e);
			return JsonResultUtil.getErrorJson("快递公司添加失败");			
		}
	}
	/**
	 * 修改物流公司
	 * @param cid 物流公司Id,Integer
	 * @param code 物流公司代码,String
	 * @param name 物流公司名称,String
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(String code,String name,Integer cid){
		try {
			Logi logi = new Logi();
			logi.setId(cid);
			logi.setCode(code);
			logi.setName(name);
			Logi logicode = logiManager.getLogiByCode(code);
			Logi loginame = logiManager.getLogiByName(name);
			if(loginame != null && loginame.getId() != cid){
				return JsonResultUtil.getErrorJson("快递公司名称不能相同");
			}
			if(logicode != null && logicode.getId() != cid){
				return JsonResultUtil.getErrorJson("快递公司代码不能相同");
			}else{
				logiManager.saveEdit(logi);
				return JsonResultUtil.getSuccessJson("修改成功");
			}
		} catch (Exception e) {
			logger.error("物流公司修改失败", e);
			return JsonResultUtil.getErrorJson("快递公司修改失败");			
		}
	}

}
