package com.enation.app.shop.core.goods.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.component.spec.service.ISpecValueManager;
import com.enation.app.shop.core.goods.model.SpecValue;
import com.enation.app.shop.core.goods.model.Specification;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 规格ation
 * @author kingapex
 *2010-3-7下午06:50:20
 */
@Scope("prototype")
@Controller 
@RequestMapping("/shop/admin/spec")
public class SpecController extends GridController{
	
	@Autowired
	private ISpecManager specManager;
	
	@Autowired
	private ISpecValueManager specValueManager;
	
	/**
	 * 检测规格是否被使用
	 * @param spec_id 规格Id数组,Integer[]
	 * @return json
	 * result 1.使用.0.未使用
	 */
	@ResponseBody
	@RequestMapping(value="/check-used")
	public Object checkUsed(Integer[] spec_id){
		try {
			Map map=new HashMap();
			if(this.specManager.checkUsed(spec_id)){
				map.put("result", 1);
				map.put("used",1);
			}else{
				map.put("result", 1);
				map.put("used",0);
			}
			return map;
			
		} catch (Exception e) {
			this.logger.error("检测规格使用情况出错",e);
			return JsonResultUtil.getErrorJson("检测规格使用情况出错");
			
		}
	}

	
	
	/**
	 * 检测某个规格值 是否被使用
	 * @param  valueid 规格Id
	 * @return json
	 * result 1.使用.0.未使用
	 */
	@ResponseBody
	@RequestMapping(value="/check-value-used")
	public JsonResult checkValueUsed(Integer valueid){
		
		boolean isused = this.specManager.checkUsed(valueid);
		
		if(isused){
			return JsonResultUtil.getSuccessJson("");
		}else{
			return JsonResultUtil.getErrorJson("");
		}
		
	}
	/**
	 * 跳转至规格列表页
	 * @return 规格列表页
	 */
	@RequestMapping(value="list")
	public ModelAndView list(){
		
		ModelAndView view=this.getGridModelAndView();
		view.setViewName("/shop/admin/spec/spec_list");
		return view;
	}
	
	/**
	 * 获取规格分页列表Json
	 * change by DMRain 2016-2-24
	 */
	@ResponseBody
	@RequestMapping(value="/list-json")
	public GridJsonResult listJson(){
		
		return JsonResultUtil.getGridJson(specManager.listSpec(this.getPage(), this.getPageSize()));
	}
	
	/**
	 * 跳转至添加规格页面
	 * @return 添加规格页面
	 */
	@RequestMapping(value="/add")
	public String add(){
		return "/shop/admin/spec/spec_input";
	}
	
	/**
	 * 保存规格
	 * @param spec 规格,Specification
	 * @param valueList 规格值,List
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="save-add")
	@SuppressWarnings("unchecked")
	public JsonResult saveAdd(Specification spec, @RequestParam(value = "valueIdArray", required = false) Integer[] valueIdArray,@RequestParam(value = "valueArray", required = false) String[] valueArray, @RequestParam(value = "imageArray", required = false) String[] imageArray){
		
		List valueList=fillSpecValueList(valueIdArray,valueArray,imageArray);
		if(spec.getSpec_type()==1){
			spec.setSpec_memo("type_color");
		}
		try {
			this.specManager.add(spec, valueList);
			return JsonResultUtil.getSuccessJson("规格添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("规格添加失败"+ e);
			return JsonResultUtil.getErrorJson("规格添加失败");
		}
	}
	

	/**
	 * 跳转至修改规格页面
	 * @param specId 规格Id,Integer
	 * @param specView 规格详细,Map
	 * @param valueList 规格值,List
	 * @return 修改规格页面
	 */
	@RequestMapping(value="edit")
	public ModelAndView edit(Integer specId){
		ModelAndView view=new ModelAndView();
		view.addObject("specView", this.specManager.get(specId));
		view.addObject("valueList", this.specValueManager.list(specId));
		view.setViewName("/shop/admin/spec/spec_edit");
		return view;
	}
	
	
	/**
	 * 保存修改规格
	 * @param spec 规格,Specification
	 * @param valueList 规格值,List
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="save-edit")
	public JsonResult saveEdit(Specification spec,@RequestParam(value = "valueIdArray", required = false) Integer[] valueIdArray,@RequestParam(value = "valueArray", required = false) String[] valueArray,@RequestParam(value = "imageArray", required = false) String[] imageArray){
		List valueList =fillSpecValueList(valueIdArray,valueArray,imageArray);
		try {
			this.specManager.edit(spec, valueList);
			return JsonResultUtil.getSuccessJson("规格修改成功");
		} catch (Exception e) {
			logger.error("规格修改失败"+e);
			return JsonResultUtil.getErrorJson("规格修改失败");
		}
	}
	
	/**
	 * 删除规格
	 * @param spec_id 规格Id数组,Integer[]
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="delete")
	public JsonResult delete(Integer[] spec_id){
		try {
			for (Integer valueid : spec_id) {
				boolean isused = this.specManager.checkUsed(valueid);
				if(isused){
					return JsonResultUtil.getErrorJson("规格已经被商品使用，不能删除");
				}
			}
			this.specManager.delete(spec_id);
			return JsonResultUtil.getSuccessJson("规格删除成功");
		} catch (Exception e) {
			logger.error("规格删除失败"+e);
			return JsonResultUtil.getErrorJson("规格删除失败");
		}
	}
	
	/**
	 * 整理规格List
	 * @return List<SpecValue>
	 */
	private List<SpecValue> fillSpecValueList(Integer[] valueIdArray,String[] valueArray,String[] imageArray){
		List valueList = new ArrayList<SpecValue>();
		
		if(valueArray!=null ){
			for(int i=0;i<valueArray.length;i++){
				String value =valueArray[i];
	
				SpecValue specValue = new SpecValue();
				specValue.setSpec_value_id(valueIdArray[i]);
				if(valueIdArray[i] ==0){
					specValue.setInherent_or_add(0);
				}
				specValue.setSpec_value(value);
				if( imageArray!=null&&imageArray.length!=0){
					String image = imageArray[i];
					if (image == null || image.equals("")) {
						image = "/shop/admin/spec/image/spec_def.gif";
					} else {
						image  =StaticResourcesUtil.convertToUrl(image);
					}
					specValue.setSpec_image(image);
				}else{
					specValue.setSpec_image( "/shop/admin/spec/image/spec_def.gif" );
				}
				valueList.add(specValue);
			}
		}
		return valueList;
	}
}
