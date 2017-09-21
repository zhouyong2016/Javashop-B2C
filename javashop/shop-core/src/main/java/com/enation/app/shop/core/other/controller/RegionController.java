package com.enation.app.shop.core.other.controller;

import java.util.List;

import net.sf.json.JSONArray;

import org.apache.poi.ss.util.SSCellRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 地区管理
 * 
 * @author lzf<br/>
 *         2010-4-22下午12:52:49<br/>
 *         version 1.0
 * @author Kanon  2015-2-26;6.0版本改造
 */
@Scope("prototype")
@Controller
@RequestMapping("/shop/admin/region")
public class RegionController  {
	
	@Autowired
	private IRegionsManager regionsManager;
	
	
	/**
	 * 跳转至地区管理列表页
	 * @return 地区管理列表页
	 */
	@RequestMapping("/list")
	public String list(){
		return "/shop/admin/regions/regions";
	}
	/**
	 * 获取地区管理列表Json
	 * @param listRegion 地区列表,List
	 * @return 获取地区管理列表Json
	 */
	@ResponseBody
	@RequestMapping("/list-children")
	public Object listChildren(Integer parentid){
		List list  = regionsManager.listChildrenAsyn(parentid);
		String s = JSONArray.fromObject(list).toString();
		return s.replace("local_name", "text").replace("p_region_id", "parent_id").replace("region_id", "id");
	}
	
 
	/**
	 * 跳转至添加地区页
	 * @return 添加地区页
	 */
	@RequestMapping("/add")
	public String add(){
		return "/shop/admin/regions/region_add";
	}
	/**
	 * 添加地区
	 * @param regions 地区,Regions
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping("/save-add")
	public JsonResult saveAdd( Regions regions){
		try{
			regionsManager.add(regions);
			//如果没有邮编，set 空值 非 null值
			if(regions.getZipcode()==null){
				regions.setZipcode("");
			}
			return JsonResultUtil.getSuccessJson("地区添加成功");
		}catch(Exception e){
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("地区添加失败");
		}
	}
	/**
	 * 添加子地区
	 * @param regions 地区,Regions
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping("/save-add-children")
	public JsonResult saveAddchildren( Regions regions){
		try{
			regionsManager.add(regions);
			return JsonResultUtil.getSuccessJson("子地区添加成功");
		}catch(Exception e){
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("子地区添加失败");
		}
	}
	/**
	 * 跳转至会员修改页
	 * @param region_id 地区Id
	 * @return 会员修改页
	 */
	@RequestMapping("/edit")
	public ModelAndView edit(Integer region_id){	
		ModelAndView view=new ModelAndView();
		view.addObject("regions", regionsManager.get(region_id));
		view.setViewName("/shop/admin/regions/region_edit");
		return view;	
	}
	/**
	 * 子地区
	 * @param region_id 地区Id
	 * @return
	 */
	@RequestMapping("/children")
	public ModelAndView children(Integer region_id){
		ModelAndView view=new ModelAndView();
		view.addObject("regions", regionsManager.get(region_id));
		view.setViewName("/shop/admin/regions/region_children");
		return view;	
	}
	/**
	 * 修改地区
	 * @param regions 地区,Regions
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping("/save-edit")
	public JsonResult saveEdit( Regions regions){
		try{
			regionsManager.update(regions);
			return JsonResultUtil.getSuccessJson("修改成功");
			
		}catch(Exception e){
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("修改失败");
		}
	}
	/**
	 * 删除地区
	 * @param region_id 地区Id,Integer
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public JsonResult delete(Integer region_id){
		try {
			this.regionsManager.delete(region_id);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}
	/**
	 * 初始化地区
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping("/reset")
	public JsonResult reset(){
		try {
			this.regionsManager.reset();
			
			return JsonResultUtil.getSuccessJson("初始化地区成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			
			return JsonResultUtil.getErrorJson("初始化地区失败");
		}
	}
	/**
	 * 获取子地区
	 * @return 子地区Json
	 */
	@ResponseBody
	@SuppressWarnings("rawtypes")
	@RequestMapping("/get-children")
	public String getChildren(Integer regionid){
		List list = this.regionsManager.listChildrenByid(regionid);
		return JSONArray.fromObject(list).toString();
		
		
	}

	
}
