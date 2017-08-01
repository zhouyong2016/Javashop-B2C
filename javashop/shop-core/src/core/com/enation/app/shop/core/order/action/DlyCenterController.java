package com.enation.app.shop.core.order.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.order.model.DlyCenter;
import com.enation.app.shop.core.order.service.IDlyCenterManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;
/**
 * 发货信息 Action
 * @author LiFenLong 2014-4-1;4.0版本改造   
 * @author Kanon 2016-3-1;6.0版本改造
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/dly-center")
public class DlyCenterController extends GridController {
	
	@Autowired
	private IDlyCenterManager dlyCenterManager;
	
	@Autowired
	private IRegionsManager regionsManager;
	
	
	/**
	 * 显示发货信息添加页
	 * @author xulipeng
	 * @param provinceList 省列表,List
	 * @return 发货信息添加页
	 * 2014年4月1日17:29:02
	 */
	@RequestMapping(value="/add")
	public ModelAndView add(){
		ModelAndView view =new ModelAndView();
		view.setViewName("/shop/admin/dlyCenter/add");
		view.addObject("provinceList", regionsManager.listProvince());
		return view;
	}
	/**
	 * 显示发货信息修改页
	 * @param dlyCenterId 发货信息Id,Integer
	 * @param listProvince 省列表,List
	 * @param cityList 城市列表,List
	 * @param regionList 地区列表,List
	 * @return 发货信息修改页
	 */
	@RequestMapping("/edit")
	public ModelAndView edit(Integer dlyCenterId){
		
		ModelAndView view =new ModelAndView();
		
		DlyCenter dlyCenter = dlyCenterManager.get(dlyCenterId);
		
		view.addObject("dlyCenter", dlyCenter);
		
		List provinceList = this.regionsManager.listProvince();
		
		view.addObject("provinceList", provinceList);
		
		if (dlyCenter.getProvince_id() != null) {
			List cityList = this.regionsManager.listCity(dlyCenter.getProvince_id());
			view.addObject("cityList", cityList);
		}
		if (dlyCenter.getCity_id() != null) {
			List regionList = this.regionsManager.listRegion(dlyCenter.getCity_id());
			view.addObject("regionList", regionList);
		}
		view.setViewName("/shop/admin/dlyCenter/edit");
		return view;
	}
	
	/**
	 * 跳转发货信息列表页
	 * @return 发货信息列表页
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(){
		ModelAndView view=getGridModelAndView();
		view.setViewName("/shop/admin/dlyCenter/list");
		return view;
	}
	/**
	 * 获取发货信息列表json
	 * @param list 发货信息列表
	 * @return 获取发货信息列表json
	 */
	@ResponseBody
	@RequestMapping(value="list-json")
	public GridJsonResult listJson(){
		
		return JsonResultUtil.getGridJson(dlyCenterManager.list());
	}
	/**
	 * 批量删除发货信息
	 * @param dly_center_id 发货信息Id
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	@ResponseBody
	@RequestMapping("/delete")
	public JsonResult delete(Integer[] dly_center_id){
		try {
			
			//获取发货中心总数 修改人：DMRain 2015-12-07
			int total = this.dlyCenterManager.getAllDlyNum();
			
			//如果要删除的发货中心数量大于或等于发货中心总数，就给出提示信息，否则就可以正常删除！
			if (dly_center_id.length >= total) {
				return JsonResultUtil.getErrorJson("不可全部删除，至少保留一条作为默认发货点");
			} else {
				this.dlyCenterManager.delete(dly_center_id);
				return JsonResultUtil.getSuccessJson("发货信息删除成功");
			}
			
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			return JsonResultUtil.getErrorJson("发货信息删除失败"+e.getMessage());

		}
	}
	
	/**
	 * 删除一条发货信息
	 * add by DMRain 2016-5-9
	 * @param dly_center_id 发货信息Id
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	@ResponseBody
	@RequestMapping("/del")
	public JsonResult del(Integer dly_center_id){
		try {
			
			//判断删除的发货点是否为默认发货点
			if (this.dlyCenterManager.get(dly_center_id).getChoose().equals("true")) {
				return JsonResultUtil.getErrorJson("默认发货点不允许删除");
			} else {
				//获取发货中心总数 修改人：DMRain 2015-12-07
				int total = this.dlyCenterManager.getAllDlyNum();
				
				//如果要删除的发货中心数量大于或等于发货中心总数，就给出提示信息，否则就可以正常删除！
				if (total == 1) {
					return JsonResultUtil.getErrorJson("不可全部删除，至少保留一条作为默认发货点");
				} else {
					this.dlyCenterManager.del(dly_center_id);
					return JsonResultUtil.getSuccessJson("发货信息删除成功");
				}
			}
			
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			return JsonResultUtil.getErrorJson("发货信息删除失败"+e.getMessage());

		}
	}
	
	/**
	 * 添加发货信息
	 * @author xulipeng
	 * 2014年4月1日17:28:35
	 * @param province 省,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @param dlyCenter 发货信息对象,DlyCenter
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public JsonResult saveAdd(DlyCenter dlyCenter){
		try{
			
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			
			
			dlyCenter.setProvince(province);
			dlyCenter.setCity(city);
			dlyCenter.setRegion(region);
			
			if(!StringUtil.isEmpty(province_id)){
				dlyCenter.setProvince_id( StringUtil.toInt(province_id,true));
			}
			
			if(!StringUtil.isEmpty(city_id)){
				dlyCenter.setCity_id(StringUtil.toInt(city_id,true));
			}
			
			if(!StringUtil.isEmpty(province_id)){
				dlyCenter.setRegion_id(StringUtil.toInt(region_id,true));
			}
			
			dlyCenterManager.add(dlyCenter);
			return JsonResultUtil.getSuccessJson("发货信息添加成功");
			
		}catch(Exception e){
			logger.error("发货信息添加失败", e);
			return JsonResultUtil.getErrorJson("发货信息添加失败");
		}
	}
	/**
	 * 修改发货信息
	 * @author xulipeng
	 * @param province 省,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @param dlyCenter 发货信息对象,DlyCenter
	 * @return json
	 * result 1.操作成功。0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(DlyCenter dlyCenter){
		try{
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			
			
			dlyCenter.setProvince(province);
			dlyCenter.setCity(city);
			dlyCenter.setRegion(region);
			
			if(!StringUtil.isEmpty(province_id)){
				dlyCenter.setProvince_id( StringUtil.toInt(province_id,true));
			}
			
			if(!StringUtil.isEmpty(city_id)){
				dlyCenter.setCity_id(StringUtil.toInt(city_id,true));
			}
			
			if(!StringUtil.isEmpty(province_id)){
				dlyCenter.setRegion_id(StringUtil.toInt(region_id,true));
			}
			
			dlyCenterManager.edit(dlyCenter);
			return JsonResultUtil.getSuccessJson("发货信息修改成功");
			
		}catch(Exception e){
			logger.error("发货信息修改失败", e);
			return JsonResultUtil.getSuccessJson("发货信息修改失败");
		}
	}
}
