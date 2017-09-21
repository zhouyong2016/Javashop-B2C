package com.enation.app.shop.core.decorate.action;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.upload.IUploader;
import com.enation.app.base.core.upload.UploadFacatory;
import com.enation.app.shop.core.decorate.model.Floor;
import com.enation.app.shop.core.decorate.model.Style;
import com.enation.app.shop.core.decorate.service.IFloorManager;
import com.enation.app.shop.core.decorate.service.IStyleManager;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.eop.processor.facade.ThemePathGeterFactory;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonResultUtil;

/**
 * 
 * 后台楼层管理
 * @author    jianghongyan
 * @version   6.1.1,2016年6月20日
 * @since     v6.1.1
 */
@Controller
@RequestMapping("/core/admin/floor")
@SuppressWarnings(value={"rawtypes"})
public class FloorController extends GridController{

	@Autowired
	private IFloorManager floorManager;
	
	@Autowired
	private IStyleManager styleManager;
	
	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	protected final Logger logger = Logger.getLogger(getClass());
	/**
	 * 跳转主页管理
	 * @return url
	 */
	@RequestMapping(value="list")
	public ModelAndView list(){
		ModelAndView view=new ModelAndView();
		//view.addObject("ctx", ThreadContextHolder.getHttpRequest().getContextPath());
		Integer pageid=this.daoSupport.queryForInt("select id from es_page where name=?", "PC首页");
		view.addObject("pageid", pageid);
		view.setViewName("/floor/admin/indexpage/list");
		return view;
	}
	
	
	
	/**
	 * 异步加载分类列表json数据
	 * @return 符合combotree定义格式的json串
	 */
	@ResponseBody
	@RequestMapping(value="/get-list-by-parentid-json")
	public GridJsonResult getlistByParentidJson(Integer parentid,Integer pageid){
		List<Map> floorList = this.floorManager.getListChildren(parentid,pageid);
		return JsonResultUtil.getGridJson(floorList);
	}

	/**
	 * 添加楼层
	 * @param id 楼层id
	 * @return spring ModelAndView
	 */
	@RequestMapping(value="add-floor")
	public ModelAndView addFloor(Integer id){
		ModelAndView view=new ModelAndView();
		view.addObject("pageid",id);
		view.addObject("actionName","save-add.do");
		view.setViewName("/floor/admin/indexpage/floor-input");
		return view;
	}


	/**
	 * 保存显示状态
	 * @param id 楼层id
	 * @param is_display 显示状态 0为显示 1为隐藏
	 * @return 处理结果 JsonResult
	 */
	@ResponseBody
	@RequestMapping(value="/save-display")
	public JsonResult saveSort(Integer id,Integer is_display){
		if(id==null){
			return JsonResultUtil.getErrorJson("id不能为空");
		}
		if(is_display==null){
			return JsonResultUtil.getErrorJson("显示不能为空");
		}

		try {
			List list=this.floorManager.saveDisplay(id,is_display);
			JsonResult result=new JsonResult();
			result.setResult(1);
			result.setMessage("保存成功");
			result.setData(list);
			return result;
		} catch (RuntimeException e) {
			this.logger.error("保存楼层显示状态失败",e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}

	/**
	 * 保存标题
	 * @param floor_id 楼层id
	 * @param title 楼层标题
	 * @return 处理结果JsonResult
	 */
	@ResponseBody
	@RequestMapping(value="/save-title")
	public JsonResult saveTitle(Integer floor_id,String title){
		if(floor_id==null){
			return JsonResultUtil.getErrorJson("id不能为空");
		}
		if(title==null){
			return JsonResultUtil.getErrorJson("标题不能为空");
		}

		try {
			this.floorManager.saveTitle(floor_id,title);
			return JsonResultUtil.getSuccessJson("保存标题成功");
		} catch (RuntimeException e) {
			this.logger.error("保存楼层标题失败",e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
	
	/**
	 * 弹出添加子楼层dialog
	 * @param floorid 当前楼层id
	 * @return Spring ModelAndView
	 */

	@RequestMapping(value="/add-children")
	public ModelAndView addChildren(Integer floorid){
		ModelAndView view=new ModelAndView();
		Floor floor=this.floorManager.getFloorById(floorid);
		view.addObject("floor", floor);
		view.addObject("pageid", floor.getPage_id());
		view.addObject("is_b2c","b2c".equals(EopSetting.PRODUCT));
		view.setViewName("/floor/admin/indexpage/floor-add-children");
		return view;
	}


	/**
	 * 获取分类列表
	 * @param pageid 页面id 
	 * @return 符合combotree格式的json串
	 */
	@ResponseBody
	@RequestMapping(value="/add-list-json")
	public String addlistJson(Integer pageid,Integer id) {
		if(id==null){
			id=0;
		}
		List addlist = this.floorManager.getListChildren(id, pageid);

		String s = JSONArray.fromObject(addlist).toString();
		return s.replace("title", "text");
	}

	/**
	 * 保存添加的楼层
	 * @param floor 楼层对象
	 * @param file 楼层logo
	 * @return 处理结果JsonResult
	 */
	@ResponseBody
	@RequestMapping(value="save-add")
	public JsonResult saveAdd(Floor floor,@RequestParam(value = "file", required = false) MultipartFile file){
		try {
			if(file!=null){
				if(!FileUtil.isAllowUpImg(file.getOriginalFilename())){
					return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				}else{
					IUploader uploader=UploadFacatory.getUploaer();
					String logoPath=uploader.upload(file.getInputStream(), "floor", file.getOriginalFilename());
					floor.setLogo(logoPath);
				}
			}
			//判断时候选择主分类
			if(floor.getCat_id()==null){
				return JsonResultUtil.getErrorJson("请选择主分类");
			}
			this.floorManager.save(floor);
			
			List<Map> catList= this.goodsCatManager.getListChildren(Integer.parseInt(floor.getCat_id()));
			int totleSize = catList.size()<5?catList.size():5;
			for (int i = 0; i < totleSize; i++) {
				Floor childFloor = new Floor();
				Map map = catList.get(i);
				String name = (String) map.get("name");
				childFloor.setTitle(name);
				childFloor.setSort(i);
				childFloor.setParent_id(floor.getId());
				childFloor.setPage_id(floor.getPage_id());
				childFloor.setStyle("style1");
				childFloor.setIs_display(0);
				
				this.floorManager.save(childFloor);
			}
			
			
			return JsonResultUtil.getSuccessJson("楼层添加成功");
		} catch (RuntimeException e) {
			this.logger.error("添加楼层失败",e);
			return JsonResultUtil.getErrorJson("楼层添加失败:"+e.getMessage());
		} catch (IOException e) {
			this.logger.error("添加楼层失败",e);
			return JsonResultUtil.getErrorJson("楼层添加失败:"+e.getMessage());
		}
	}

	/**
	 * 删除楼层 
	 * @param floor_id 要删除的楼层id
	 * @return 处理结果JsonResult
	 */
	@ResponseBody
	@RequestMapping(value="delete",produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult delete(Integer floor_id){

		try {
			if(EopSetting.IS_DEMO_SITE ){//附加条件
				if("b2c".equals(EopSetting.PRODUCT)){
					if(floor_id<=12){
						return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
					}
				}
				
				if("b2b2c".equals(EopSetting.PRODUCT)){
					if(floor_id<=20){
						return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
					}
				}
			}
			this.floorManager.delete(floor_id);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException ex) {
			this.logger.error("删除楼层失败",ex);
			return JsonResultUtil.getErrorJson("非法操作!");
		}
	}

	/**
	 * 保存排序
	 * @param floor_id 楼层id
	 * @param sort 楼层的排序
	 * @return 处理结果JsonResult
	 */
	@ResponseBody
	@RequestMapping(value="save-sort")
	public JsonResult savaSort(Integer[] floor_ids,Integer[] floor_sorts){
		try {
			this.floorManager.saveSort(floor_ids, floor_sorts);
			return JsonResultUtil.getSuccessJson("保存成功");
		} catch (Exception e) {
			this.logger.error("添加楼层排序失败",e);
			return JsonResultUtil.getErrorJson("保存失败");
		}
		
	}


	/**
	 * 跳转编辑页面
	 * @param floorid 楼层id
	 * @return spring
	 */
	@RequestMapping(value="edit")
	public ModelAndView edit(Integer floorid){
		ModelAndView view=new ModelAndView();
		Floor floor=this.floorManager.getFloorById(floorid);
		view.addObject("floor", floor);
		view.addObject("pageid", floor.getPage_id());
		view.addObject("is_b2c","b2c".equals(EopSetting.PRODUCT));
		view.setViewName("/floor/admin/indexpage/floor-edit");
		return view;
	}

	/**
	 * 保存编辑
	 * @param floor 楼层对象
	 * @param file  楼层logo
	 * @return 处理结果JsonResult
	 */
	@ResponseBody
	@RequestMapping(value="save-edit")
	public JsonResult saveEdit(Floor floor,@RequestParam(value = "file", required = false) MultipartFile file){
		try {
			//上传文件
			if (file!=null) {

				if(!FileUtil.isAllowUpImg(file.getOriginalFilename())){
					return JsonResultUtil.getErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				}else{
					IUploader uploader=UploadFacatory.getUploaer();
					String logoPath=uploader.upload(file.getInputStream(), "floor", file.getOriginalFilename());
					floor.setLogo(logoPath);
				}
			}
			//保存楼层信息
			this.floorManager.update(floor);
			return JsonResultUtil.getSuccessJson("楼层修改成功");
		} catch (RuntimeException e) {
			this.logger.error("修改楼层失败",e);
			return JsonResultUtil.getErrorJson("楼层修改失败:"+e.getMessage());
		} catch (IOException e) {
			this.logger.error("修改楼层失败",e);
			return JsonResultUtil.getErrorJson("楼层修改失败:"+e.getMessage());
		}
	}
	
	
	/**
	 * 跳转模板编辑页面
	 * @param floor_id 楼层id
	 * @return spring ModelAndView
	 */
	@RequestMapping(value="edit-template")
	public ModelAndView editTemPlate(Integer floor_id,Integer page_id){
		ModelAndView view=new ModelAndView();
		try {
			view.addObject("floor_id", floor_id);
			if(page_id==null){
				page_id=1;
			}
			Style style=this.styleManager.getStyleByFloorId(floor_id,page_id);
			view.addObject("stylepath", style.getPath());
			view.addObject("pageid",page_id);
		} catch (Exception e) {
			view.addObject("error", e.getMessage());
		}
		String themePath=ThemePathGeterFactory.getThemePathGeter().getThemespath(null);
		view.addObject("themes_path","/themes/" + themePath);
		view.setViewName("/floor/admin/style/edit-template");
		return view;
	}
}
