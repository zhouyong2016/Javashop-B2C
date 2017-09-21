package com.enation.app.cms.core.action;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.cms.core.model.DataCat;
import com.enation.app.cms.core.model.DataField;
import com.enation.app.cms.core.plugin.ArticlePluginBundle;
import com.enation.app.cms.core.service.IDataCatManager;
import com.enation.app.cms.core.service.IDataFieldManager;
import com.enation.app.cms.core.service.IDataManager;
import com.enation.app.cms.core.service.IDataModelManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 后台文章管理
 * @author DMRain 2016年3月1日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/cms/admin/data")
public class DataController extends GridController{

	@Autowired
	private IDataFieldManager dataFieldManager;
	
	@Autowired
	private IDataCatManager dataCatManager;
	
	@Autowired
	private IDataManager dataManager;
	
	@Autowired
	private ArticlePluginBundle articlePluginBundle;
	
	@Autowired
	private IDataModelManager dataModelManager;
	/**
	 * 跳转至帮助中心页面
	 * @return
	 */
	@RequestMapping(value="/help-list")
	public String helplist() { 
		return "/cms/admin/article/help_list";
	}

	
	
	/**
	 * 跳转至修改文章分类页面
	 * @return
	 */
	@RequestMapping(value="/help-edit")
	public ModelAndView helpEdit() { 
		ModelAndView view = new ModelAndView();
		view.addObject("modelList", this.dataModelManager.list());
		view.setViewName("/cms/admin/article/help_edit");
		return view;
	}
	
	/**
	 * 跳转至管理文章分类页面
	 * @return
	 */
	@RequestMapping(value="/manage-cat")
	public ModelAndView manageCat() { 
		ModelAndView view = new ModelAndView();
		view.addObject("modelList", this.dataModelManager.list());
		view.setViewName("/cms/admin/cat/manage_cat");
		return view;
	}
	
	/**
	 * 跳转至热门关键字列表页面
	 * @param catid 文章分类id
	 * @return
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Integer catid) {
		ModelAndView view = new ModelAndView();
		view.addObject("catid", catid);
		
		DataCat cat = this.dataCatManager.get(catid);
		view.addObject("cat", cat);
		
		view.addObject("fieldList", this.dataFieldManager.listIsShow(cat.getModel_id()));
		view.addObject("pageSize", this.getPageSize());
		view.setViewName("/cms/admin/article/list");
		return view;
	}
	
	/**
	 * 获取文章分页列表json
	 * @param searchText 搜索关键字
	 * @param searchField 搜索标题
	 * @param catid 文章分类id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(String searchText,String searchField, Integer catid){
		catid = (catid==null?1:catid);
		String term = null;
		if (!StringUtil.isEmpty(searchText)) {
			term = "and " + searchField + " like '%" + searchText + "%'";
		}
		this.webpage = this.dataManager.listAll(catid, term, null, false, this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);
	}
	
	/**
	 * 跳转至添加文章
	 * @param catid 文章分类id
	 * @return
	 */
	@RequestMapping(value="/add")
	public ModelAndView add(Integer catid) {
		ModelAndView view = new ModelAndView();
		view.addObject("catid", catid);
		
		DataCat cat = this.dataCatManager.get(catid);
		view.addObject("cat", cat);
		
		view.addObject("isEdit", false);
		view.addObject("catList", this.dataCatManager.listAllChildren(catid));
		view.addObject("modelid", cat.getModel_id());
		
		List<DataField> fieldList = this.dataFieldManager.listByCatId(catid);
		
		for (DataField field : fieldList) {
			field.setInputHtml(this.articlePluginBundle.onDisplay(field, null));
		}
		
		view.addObject("fieldList", fieldList);
		view.setViewName("/cms/admin/article/input");
		return view;
	}
	
	/**
	 * 保存添加文章
	 * @param modelid 模型id
	 * @param catid 分类id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(Integer modelid, Integer catid) {
		try {
			this.dataManager.add(modelid, catid);
			return JsonResultUtil.getSuccessJson("添加成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("添加失败");
		}
	}
	
	/**
	 * 跳转至文章修改页面
	 * @param catid 分类id
	 * @param dataid 数据id
	 * @return
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit(Integer catid, Integer dataid) {
		ModelAndView view = new ModelAndView();
		view.addObject("catid", catid);
		view.addObject("dataid", dataid);
		view.addObject("isEdit", true);
		
		Map article = this.dataManager.get(dataid, catid, false);
		view.addObject("article", article);
		
		if (owner(article.get("site_code"))) {
			DataCat cat = this.dataCatManager.get(catid);
			view.addObject("cat", cat);
			view.addObject("catList", this.dataCatManager.listAllChildren(0));
			view.addObject("modelid", cat.getModel_id());
			
			List<DataField> fieldList = dataFieldManager.listByCatId(catid);
			for (DataField field : fieldList) {
				field.setInputHtml(articlePluginBundle.onDisplay(field, article.get(field.getEnglish_name())));
			}
			
			view.addObject("fieldList", fieldList);
			view.setViewName("/cms/admin/article/input");
			return view;
		} else {
			view.addObject("JsonResult", "{'result':0,'message':'非本站内容，不能编辑！'}");
			return view;
		}
	}
	
	/**
	 * 保存修改文章
	 * @param modelid 模型id
	 * @param catid 分类id
	 * @param dataid 数据id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(Integer modelid, Integer catid, Integer dataid) {
		try {
			this.dataManager.edit(modelid, catid, dataid);
			return JsonResultUtil.getSuccessJson("修改成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("修改失败");
		}
	}
	
	/**
	 * 文章排序
	 * @param dataids
	 * @param sorts
	 * @param catid
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/update-sort")
	public JsonResult updateSort(Integer[] dataids, Integer[] sorts, Integer catid) {
		try {
			this.dataManager.updateSort(dataids, sorts, catid);
			return JsonResultUtil.getSuccessJson("修改排序成功");
		} catch (Exception e) {
			this.logger.error(e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
	
	/**
	 * 删除文章
	 * @param catid
	 * @param dataid
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete(Integer catid, Integer dataid) {
		
		Map article = this.dataManager.get(dataid, catid, false);
		
		if (article.get("sys_lock") != null && article.get("sys_lock").toString().equals("1")) {
			return JsonResultUtil.getErrorJson("不能删除！");
		} else {
			if (owner(article.get("site_code"))) {
				this.dataManager.delete(catid, dataid);
				return JsonResultUtil.getSuccessJson("删除成功");
			} else {
				return JsonResultUtil.getErrorJson("非本站内容，不能删除！");
			}
		}
	}
	
	private boolean owner(Object site_code) {
		return true;
	}
	
	
	
	//------------以下被注释的三个方法已经被废弃，不用了-----------------------------
	
	/*public String implist() {
		Integer sitecode = 100000;
		if (siteid != null) {
			MultiSite site = this.multiSiteManager.get(siteid);
			sitecode = site.getCode();
		}
		this.webpage = this.dataManager
				.list(catid, this.getPage(), 5, sitecode);
		cat = this.dataCatManager.get(catid);
		fieldList = this.dataFieldManager.listIsShow(cat.getModel_id());
		return "implist";
	}*/
	
	/*public String importdata() {
		this.dataManager.importdata(catid, dataids);
		this.json = "{result:0}";
		return DataAction.JSON_MESSAGE;
	}*/
	
	/*public String dlgList() {
		this.webpage = this.dataManager.listAll(catid, null, this.getPage(), 15);
		cat = this.dataCatManager.get(catid);
		fieldList = this.dataFieldManager.listIsShow(cat.getModel_id());
		return "dlglist";
	}*/
	
}
