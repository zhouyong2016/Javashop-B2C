package com.enation.app.shop.core.order.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.order.service.IAreaManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * @author lzf
 * @version v2.0,2016年2月23日 sylow 版本改造
 * @since v6.0
 */
@Controller 
@RequestMapping("/shop/admin/area")
public class AreaController {
	
	@Autowired
	private IRegionsManager regionsManager;
	@Autowired
	private IAreaManager areaManager;

	/**
	 * 获取地区列表html
	 * @param city_id 城市id
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/list-region", produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView list_region(int city_id){
		List regionList = regionsManager.listRegion(city_id);
		ModelAndView view = new ModelAndView();
		view.addObject("regionList", regionList);
		view.setViewName("/shop/admin/setting/site_area");
		
		return view;
	}
	
	/**
	 * 获取子地区
	 * @param regionid 地区id
	 * @return json格式的字符串
	 */
	@ResponseBody  
	@RequestMapping(value="/list-children", produces = MediaType.APPLICATION_JSON_VALUE)
	public String listChildren(int regionid){
		String s = this.regionsManager.getChildrenJson(regionid);
		String json = s.replace("local_name", "text").replace("p_region_id", "id");
		return json;
	}
	
	/**
	 * 通过地区id 获取地区列表
	 * @param regionid
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/get-regions-list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView getRegionsList(int regionid){
		List regionList = this.regionsManager.listChildren(regionid);
		ModelAndView view = new ModelAndView();
		view.addObject("regionList", regionList);
		view.setViewName("/shop/admin/setting/site_area");
		
		return view;
	}
	
	/**
	 * 删除地区
	 * @param id 地区id
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult delete(String id){
		try {
			this.areaManager.delete(id);
			return JsonResultUtil.getSuccessJson("删除成功");
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}
	
}
