package com.enation.app.cms.core.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.cms.core.model.DataCat;
import com.enation.app.cms.core.service.ArticleCatRuntimeException;
import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.app.cms.core.service.IDataModelManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

import net.sf.json.JSONArray;

/**
 * 文章类别管理
 * @author DMRain 2016年3月1日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */



@Controller
@Scope("prototype")
@RequestMapping("/cms/admin/cat")
public class DataCatController extends GridController{

	@Autowired
	private IDataCatManager dataCatManager;
	
	@Autowired
	private IDataModelManager dataModelManager;
	
	@RequestMapping(value="/list")
	public String list(){
		return "/cms/admin/cat/list";
	}
	
	@ResponseBody
	@RequestMapping(value="/list-json")
	public String listJson() {
		List catList = this.dataCatManager.listAllChildren(0);
		return JSONArray.fromObject(catList).toString();
	}
	
	@ResponseBody
	@RequestMapping(value="/list-json-help")
	public String listJsonHelp() {
		List catList = dataCatManager.listAllChildren(2);
		String catJson = JSONArray.fromObject(catList).toString();
		return catJson.replace("url", "html");
	}
	
	//到添加页面
	@RequestMapping(value="/add")
	public ModelAndView add(){
		ModelAndView view = new ModelAndView();
		view.addObject("isEdit", false);
		view.addObject("catList", this.dataCatManager.listAllChildren(0));
		view.addObject("modelList", this.dataModelManager.list());
		view.setViewName("/cms/admin/cat/add");
		return view;
	}
	
	//保存添加
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(DataCat cat){
		try{
			this.dataCatManager.add(cat);
			return JsonResultUtil.getSuccessJson("文章栏目添加成功");
		}catch(ArticleCatRuntimeException ex){
			return JsonResultUtil.getErrorJson("同级文章栏目不能同名");
		}
	}
	
	//到编辑页面
	@RequestMapping(value="/edit")
	public ModelAndView edit(int cat_id){
		ModelAndView view = new ModelAndView();
		view.addObject("isEdit", false);
		view.addObject("catList", this.dataCatManager.listAllChildren(0));
		view.addObject("modelList", this.dataModelManager.list());
		view.addObject("cat", dataCatManager.get(cat_id));
		view.setViewName("/cms/admin/cat/edit");
		return view;
	}
	
	//保存修改 
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(DataCat cat){
		try{
			this.dataCatManager.edit(cat);
			return JsonResultUtil.getSuccessJson("文章栏目修改成功");
		}catch(ArticleCatRuntimeException ex){
			return JsonResultUtil.getErrorJson("同级文章栏目不能同名"); 
		}
	}
	
	//删除
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(int cat_id){
		try {
			int res = this.dataCatManager.del(cat_id);
			if (res == 0) {
				return JsonResultUtil.getSuccessJson("删除成功");
			} else {
				return JsonResultUtil.getErrorJson("此类别下存在子类别或者文章不能删除!");
			}
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
			return JsonResultUtil.getErrorJson("类别删除出错");
		}
				
	}
	
	@ResponseBody
	@RequestMapping(value="/save-sort")
	public JsonResult saveSort(int[] cat_ids, int[] cat_sorts){
		try{
			this.dataCatManager.saveSort(cat_ids, cat_sorts);
			return JsonResultUtil.getSuccessJson("保存成功");
		}catch(RuntimeException  e){
			return JsonResultUtil.getErrorJson("保存失败");
		}
	}
	/**
	 * 检测名称是否重复
	 * @param cat_id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/check-repeat")
	public Integer checkRepeat(String name){
		return this.dataCatManager.getDataCat(name);
		 
	}
	
	
	/**
	 * 异步加载文章分类数
	 * @author xulipeng
	 * @param cat_id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/show-cat-tree")
	public String showCatTree(Integer cat_id){
		cat_id = cat_id==null?0:cat_id;
		List<DataCat> catList = dataCatManager.listAllChildren(cat_id);
		String catJson = JSONArray.fromObject(catList).toString();
		return catJson.replace("hasChildren", "isParent").replace("url", "html");
	}
	
//	/**
//	 * 用于异步显示分类树
//	 * @return
//	 */
//	@RequestMapping(value="/show-cat-tree")
//	public ModelAndView showCatTree(int cat_id){
//		ModelAndView view = new ModelAndView();
//		view.addObject("catList", dataCatManager.listAllChildren(cat_id));
//		view.setViewName("/cms/admin/cat/cat_tree");
//		return view;
//	}
	
}
