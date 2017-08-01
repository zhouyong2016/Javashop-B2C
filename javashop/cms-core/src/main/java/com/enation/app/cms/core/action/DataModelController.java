package com.enation.app.cms.core.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.cms.core.model.DataModel;
import com.enation.app.cms.core.service.IDataFieldManager;
import com.enation.app.cms.core.service.IDataModelManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 模型管理
 * @author DMRain 2016年3月1日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/cms/admin/model")
public class DataModelController extends GridController{

	@Autowired
	private IDataModelManager dataModelManager;
	
	@Autowired
	private IDataFieldManager dataFieldManager;
	
	@RequestMapping(value="/list")
	public String list() {
		return "/cms/admin/model/model_list";
	}
	
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(){
		List modelList = this.dataModelManager.list();
		return JsonResultUtil.getGridJson(modelList);
	}
	
	@RequestMapping(value="/add")
	public String add(){
		return "/cms/admin/model/model_add";
	}
	
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(DataModel dataModel){
		try {
			if(dataModel.getBrief()!=null && dataModel.getBrief().length()>210){ 
				return JsonResultUtil.getErrorJson("备注太长");
			}
			if (this.dataModelManager.checkIfModelInUse(dataModel.getEnglish_name(), 0) == 0) {
				dataModel.setEnglish_name(dataModel.getEnglish_name());
				this.dataModelManager.add(dataModel);
				return JsonResultUtil.getSuccessJson("模型添加成功");
			} else {
				return JsonResultUtil.getErrorJson("表名已经存在");
			}
			
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("模型添加出现错误");
		}
	}
	
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer modelid){
		ModelAndView view = new ModelAndView();
		view.addObject("dataModel", this.dataModelManager.get(modelid));
		view.addObject("fieldList", this.dataFieldManager.list(modelid));
		view.setViewName("/cms/admin/model/model_edit");
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value="/file-list-json")
	public GridJsonResult fileListJson(Integer modelid){
		List fieldList = this.dataFieldManager.list(modelid);
		return JsonResultUtil.getGridJson(fieldList);
	}
	
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(DataModel dataModel){
		try {
			if(dataModel.getBrief()!=null && dataModel.getBrief().length()>210){ 
				return JsonResultUtil.getErrorJson("备注太长");
			}
			if (this.dataModelManager.checkIfModelInUse(dataModel.getEnglish_name(), dataModel.getModel_id()) == 0) {
//				String name = dataModel.getEnglish_name().substring(0, 4);
//				if (!name.equals("cms_") && !dataModel.getEnglish_name().equals("contact") 
//						&& !dataModel.getEnglish_name().equals("helpcenter") 
//						&& !dataModel.getEnglish_name().equals("hot_keyword")) {
//					dataModel.setEnglish_name("cms_" + dataModel.getEnglish_name());
//				}
				this.dataModelManager.edit(dataModel);
				return JsonResultUtil.getSuccessJson("模型修改成功");
			} else {
				return JsonResultUtil.getErrorJson("表名已经存在");
			}
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("模型修改出现错误");
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/check")
	public JsonResult check(Integer modelid){
		int result = this.dataModelManager.checkIfModelInUse(modelid);
		
		if (result > 0) {
			return JsonResultUtil.getErrorJson("模型已经被使用，请先删除对应的数据！");
		} else {
			try {
				this.dataModelManager.delete(modelid);
				return JsonResultUtil.getSuccessJson("删除成功");
			} catch (Exception e) {
				e.printStackTrace();
				this.logger.error(e.getMessage(), e);
				return JsonResultUtil.getErrorJson("删除失败");
			}
		}
	}
	
}
