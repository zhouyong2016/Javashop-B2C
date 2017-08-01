package com.enation.app.shop.core.order.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.order.model.DlyType;
import com.enation.app.shop.core.order.model.support.DlyTypeConfig;
import com.enation.app.shop.core.order.model.support.TypeAreaConfig;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.app.shop.core.order.service.ILogiManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;
 
/**
 *  配送方式管理
 * @author kingapex
 * @version v2.0,2016年3月1日 sylow 版本改造
 * @since v6.0
 */
@Controller 
@RequestMapping("/shop/admin/dly-type")
public class DlyTypeController extends GridController {
	
	@Autowired
	private IDlyTypeManager dlyTypeManager;
	@Autowired
	private ILogiManager logiManager;
	
	/**
	 * 添加配送方式
	 * @param logiList 物流公司列表,list
	 * @return 配送方式添加页
	 */
	@ResponseBody  
	@RequestMapping(value="/add-type", produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView add_type(){
		List logiList =this.logiManager.list();
		ModelAndView view = new ModelAndView();
		view.addObject("logiList", logiList);
		view.setViewName("/shop/admin/setting/dly_type_add");
		return view;
	}
	
	/**
	 * 修改配送方式 
	 * @param isEdit 是否为修改,Boolean
	 * @param typeId 配送方式Id,Integer
	 * @param type 配送方式,DlyType
	 * @param arealistsize 地区数量，Integer
	 * @param logiList 物流公司列表,List
	 * @return 配送方式修改页
	 */
	@ResponseBody  
	@RequestMapping(value="/edit", produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView edit(int typeId){
		boolean isEdit= true;
		DlyType type = this.dlyTypeManager.getDlyTypeById(typeId);
		int arealistsize = 0;
		if(type.getTypeAreaList()!=null){
			arealistsize = type.getTypeAreaList().size();
		}
		List logiList =this.logiManager.list();
		
		ModelAndView view = new ModelAndView();
		view.addObject("type", type);
		view.addObject("isEdit", isEdit);
		view.addObject("arealistsize", arealistsize);
		view.addObject("logiList", logiList);
		view.setViewName("/shop/admin/setting/dly_type_edit");
		return view;
	}
	
	/**
	 * 物流公司列表
	 * @param logiList 物流公司列表,List
	 * @return 配送方式列表页
	 */
	@ResponseBody  
	@RequestMapping(value="/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView list(){
		List logiList = this.logiManager.list();
		ModelAndView view = new ModelAndView();
		view.addObject("logiList", logiList);
		view.setViewName("/shop/admin/setting/dly_type_list");
		return view;
	}
	
	/**
	 * 跳转至配送公式验证页面
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/check-exp", produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView checkExp(){
		ModelAndView view = new ModelAndView();
		view.setViewName("/shop/admin/setting/check_exp");
		return view;
	}
	
	/**
	 * 获取配送方式列表json
	 * @author LiFenLong
	 * @param pageNo 页码,Integer
	 * @param PageSize 每页数量,Integer
	 * @return 配送方式列表json
	 */
	@ResponseBody  
	@RequestMapping(value="/list-json", produces = MediaType.APPLICATION_JSON_VALUE)
	public GridJsonResult listJson(){
		Page webpage = this.dlyTypeManager.pageDlyType(this.getPage(), this.getPageSize());
		
		return JsonResultUtil.getGridJson(webpage);
	}
	
	/**
	 * 保存配送方式
	 * @param type 配送方式,DlyType
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody  
	@RequestMapping(value="/save-add", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult saveAdd(DlyType type, int firstunit, int continueunit, Double[] firstprice, Double[] continueprice, Integer[] useexp, 
				String[] expressions, Integer defAreaFee, Integer areacount) {
		//int tid = 0;
		try {
			DlyType dlyType=this.dlyTypeManager.getDlyTypeByName(type.getName(), type.getIs_same());
			if(dlyType==null){
				
				if(type.getIs_same().intValue()==1){
					this.saveSame(false, firstunit, continueunit, firstprice, continueprice, useexp, type, expressions);
				}
				if(type.getIs_same().intValue()==0){
					this.saveDiff(false,firstunit, continueunit, firstprice, continueprice, useexp,type, expressions,
							defAreaFee, areacount);
				}
				return JsonResultUtil.getSuccessJson("配送方式添加成功");
			}else{
				return JsonResultUtil.getErrorJson("配送方式名称不能相同,添加失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("配送方式添加失败",e);
			return JsonResultUtil.getErrorJson("配送方式添加失败"+e.getMessage());
		}
	}
	
	
	/**
	 * 修改配送方式
	 * @param type 配送方式,DlyType
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody  
	@RequestMapping(value="/save-edit", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult saveEdit(DlyType type, int firstunit, int continueunit, Double[] firstprice, Double[] continueprice, Integer[] useexp, 
			String[] expressions, Integer defAreaFee, Integer areacount) {
 
		try {
			if(type.getIs_same().intValue()==1){
				this.saveSame(true, firstunit, continueunit, firstprice, continueprice, useexp, type, expressions);
			}
			
			if(type.getIs_same().intValue()==0){
				this.saveDiff(true,firstunit, continueunit, firstprice, continueprice, useexp,type, expressions,
						defAreaFee, areacount);
			}
			
			return JsonResultUtil.getSuccessJson("配送方式修改成功");
		} catch (Exception e) {
			logger.error("配送方式修改失败", e);

			return JsonResultUtil.getErrorJson("配送方式修改失败"+e.getMessage());
		}
	}
	
	/**
	 * 删除配送方式
	 * @param type_id 配送方式Id数组,Integer[]
	 * @return json
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody  
	@RequestMapping(value="/delete", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult delete(Integer[] type_id) {
		try {
			//获取配送方式记录总数
			int total = this.dlyTypeManager.getDlyTotal();
			
			//如果要删除的配送方式数量大于或等于配送方式记录总数，则出现提示消息，反之则可以删除
			//修改人：DMRain 2015-12-08
			if (type_id.length >= total) {
				return JsonResultUtil.getErrorJson("不可全部删除，至少保留一条作为默认配送方式！");
			} else {
				this.dlyTypeManager.delete(type_id);
				return JsonResultUtil.getSuccessJson("删除成功");
			}
		} catch (Exception e) {
			this.logger.error("删除失败：", e);
			return JsonResultUtil.getErrorJson("删除失败！");
		}
	}
	
	
	/**
	 * 添加统一式配置
	 * @param config 配送方式配置,DlyTypeConfig
	 * @param isUpdate 是否为修改,boolean
	 * @param firstunit 首重,Integer
	 * @param continueunit 续重,Integer
	 * @param firstprice 首重费用,Double[]
	 * @param continueprice 续重费用,Double[]
	 * @param useexp 是否使用公式,Integer[]
	 * @return tid 添加后配送方式Id
	 */
	private Integer saveSame(boolean isUpdate, int firstunit, int continueunit, Double[] firstprice, Double[] continueprice, Integer[] useexp,DlyType type, String[] expressions){
		
		DlyTypeConfig config = new DlyTypeConfig();
		config.setFirstunit(firstunit); //首重 
		config.setContinueunit(continueunit); //续重 
	
		config.setFirstprice(firstprice[0]); //首重费用
		config.setContinueprice(continueprice[0]); //续重费用
		if(useexp[0]==null){
			useexp[0]=0;
		}
		//启用公式
		if( useexp[0].intValue() ==1 ){
			config.setExpression(expressions[0]);
			config.setUseexp(1);
		}else{
			config.setUseexp(0);
		}
		
		type.setHas_cod(0);
		config.setHave_cod(type.getHas_cod());
		int tid=0;
		if(isUpdate){
			this.dlyTypeManager.edit(type, config);
		}else{
			tid=this.dlyTypeManager.add(type, config);
		}
		
		return tid;
	}
	
	/**
	 * 地区设置
	 * @param config 配送方式配置,DlyTypeConfig
	 * @param isUpdate 是否为修改,boolean
	 * @param firstunit 首重,Integer
	 * @param continueunit 续重,Integer
	 * @param firstprice 首重费用,Double[]
	 * @param continueprice 续重费用,Double[]
	 * @param defAreaFee 默认费用设置,Integer[]
	 * @param useexp 是否使用公式,Integer[]
	 * @param areacount 地区,Integer[]
	 */
	private void saveDiff(boolean isUpdate, int firstunit, int continueunit, Double[] firstprice, Double[] continueprice, Integer[] useexp,DlyType type, String[] expressions
				, Integer defAreaFee, Integer areacount){
		DlyTypeConfig config = new DlyTypeConfig();
		
		config.setFirstunit(firstunit); //首重 
		config.setContinueunit(continueunit); //续重 		
		config.setDefAreaFee(defAreaFee);//默认费用设置
		config.setFirstprice(firstprice[1]);
		config.setContinueprice(continueprice[1]);
		//启用默认费用配置,费用数组第一个元素
		if(defAreaFee!=null &&defAreaFee.intValue()==1){
			config.setFirstprice(firstprice[1]);
			config.setContinueprice(continueprice[1]);
			if(useexp[1]==null){
				useexp[1]=0;
			}
			if( useexp[1].intValue() ==1 ){
				config.setExpression(expressions[1]);
				config.setUseexp(1);
			}else{
				config.setUseexp(0);
			}
			
		}
		if(areacount==null){
			areacount=0;
		}
		TypeAreaConfig[] configArray= new TypeAreaConfig[areacount+1];
		//int price_index=0;
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		for(int i=1;i<=areacount;i++){
			if(request.getParameter("areas"+i)!=null){
				String totle_areas = request.getParameter("totle_areas"+i);
				String totle_regions = request.getParameter("totle_regions"+i);
				
				TypeAreaConfig areaConfig = new TypeAreaConfig();
				
				//首重和续重使用统一的设置
				areaConfig.setContinueunit(config.getContinueunit());
				areaConfig.setFirstunit(config.getFirstunit());
				
				String [] areass = request.getParameterValues("areas"+i);
				areaConfig.setUseexp(Integer.parseInt(request.getParameter("useexp"+i)));
				areaConfig.setAreaId(totle_areas+totle_regions);
				areaConfig.setAreaName(areass[0]);
				
				//启用公式
				if(Integer.parseInt(request.getParameter("useexp"+i))==1){
					areaConfig.setExpression(StringUtil.arrayToString(request.getParameterValues("expressions"+i), ","));
					areaConfig.setUseexp(1);
				}else{
					String firstpriceStr = StringUtil.arrayToString(request.getParameterValues("firstprice"+i), ",");
					String continuepriceStr =  StringUtil.arrayToString(request.getParameterValues("continueprice"+i), ",");
					areaConfig.setFirstprice(Double.parseDouble(firstpriceStr));
					areaConfig.setContinueprice(Double.parseDouble(continuepriceStr));
					config.setUseexp(0);
				}
				//areaConfig.setHave_cod(this.has_cod[i]);
				configArray[i]=areaConfig;
			}
		}
		
		if(isUpdate){
			this.dlyTypeManager.edit(type, config, configArray);
		}else{
			this.dlyTypeManager.add(type, config, configArray);
		}
	}
	

}
