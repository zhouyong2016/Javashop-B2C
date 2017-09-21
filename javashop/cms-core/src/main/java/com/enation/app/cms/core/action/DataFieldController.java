package com.enation.app.cms.core.action;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.plugin.ArticlePluginBundle;
import com.enation.app.cms.core.service.IDataFieldManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 模型字段管理
 * @author DMRain 2016年3月1日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/cms/admin/field")
public class DataFieldController extends GridController{

	@Autowired
	private IDataFieldManager dataFieldManager;
	
	@Autowired
	private ArticlePluginBundle articlePluginBundle;
	
	@RequestMapping(value="/add")
	public ModelAndView add(Integer modelid){
		ModelAndView view = new ModelAndView();
		view.addObject("isEdit", false);
		view.addObject("modelid", modelid);
		view.addObject("fieldPluginList", this.articlePluginBundle.getFieldPlugins());
		view.setViewName("/cms/admin/model/field_add");
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(DataField dataField){
		try{
			//验证字段名
			String englistName = dataField.getEnglish_name();
			Pattern pattern = Pattern.compile("[a-zA-Z_]+");
			Matcher matcher = pattern.matcher(englistName);
			boolean result = matcher.matches();
			
			if (result) {
				result = !"_".equals(englistName);
				if (result) {
					result = 16 > englistName.length();
				}
			}
			
			if (result) {
				Integer fieldid = this.dataFieldManager.add(dataField);
				return JsonResultUtil.getSuccessJson("字段添加成功");
			} else {
				return JsonResultUtil.getErrorJson("字段名格式错误");
			}
		}catch(RuntimeException e){
			return JsonResultUtil.getErrorJson("字段添加出错"+e.getMessage());
		}
	}
	
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer fieldid, Integer modelid){
		ModelAndView view = new ModelAndView();
		view.addObject("isEdit", true);
		view.addObject("modelid", modelid);
		view.addObject("dataField", this.dataFieldManager.get(fieldid));
		view.addObject("fieldPluginList", this.articlePluginBundle.getFieldPlugins());
		view.setViewName("/cms/admin/model/field_edit");
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(DataField dataField){
		try{
			 this.dataFieldManager.edit(dataField);
			 return JsonResultUtil.getSuccessJson("字段修改成功");
		}catch(RuntimeException e){
			return JsonResultUtil.getErrorJson("字段修改出错");
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer field_id){
		try{
			this.dataFieldManager.delete(field_id);
			return JsonResultUtil.getSuccessJson("字段删除成功");
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			return JsonResultUtil.getErrorJson("字段删除出错");
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/save-sort")
	public String saveSort(Integer[] ids, Integer[] sorts){
		try{
			this.dataFieldManager.saveSort(ids, sorts);
			return "{result:1,message:'排序更新成功'}";
		}catch(RuntimeException e){
			this.logger.error(e.getMessage(), e);
			return "{result:0,message:'"+e.getMessage()+"'}";
			
		}		
	}
}
