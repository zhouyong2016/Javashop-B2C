package com.enation.app.shop.core.goods.action;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.core.goods.model.Attribute;
import com.enation.app.shop.core.goods.model.GoodsType;
import com.enation.app.shop.core.goods.model.support.GoodsTypeDTO;
import com.enation.app.shop.core.goods.model.support.ParamGroup;
import com.enation.app.shop.core.goods.model.support.TypeSaveState;
import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.app.shop.core.goods.service.IGoodsTypeManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 类型Controller 负责商品类型的添加和修改 <br/>
 * 负责类型插件相关业务管理
 * 
 * @author apexking
 * @author LiFenLong 2014-4-1;4.0版本改造
 * @author Kanon 2016-2-20;6.0版本改造
 */
@Controller 
@Scope("prototype")
@RequestMapping("/shop/admin/type")
public class TypeController extends GridController {

	@Autowired
	private IGoodsTypeManager goodsTypeManager;
	
	@Autowired
	private IBrandManager brandManager;
	
	@Autowired
	private ISpecManager specManager;

	static String GOODSTYPE_SESSION_KEY = "goods_type_in_session";

	static String GOODSTYPE_STATE_SESSION_KEY = "goods_type_state_in_session";

	static Map session =new HashMap(); 
	
	/**
	 * 查看类型名称是否存在
	 * @return json 
	 * return 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="checkname")
	public JsonResult checkname(GoodsTypeDTO goodsType){
		if(EopSetting.IS_DEMO_SITE){
			if(goodsType.getType_id()!=null && goodsType.getType_id()<=48){
				return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			}
		}
		
		 if(this.goodsTypeManager.checkname( goodsType.getName(),goodsType.getType_id() )){
			 return JsonResultUtil.getErrorJson("类型名称已存在");
		 }else{
			 this.goodsTypeManager.save(goodsType);
			 return JsonResultUtil.getSuccessJson("保存成功");
		 }
	}
	
	/**
	 * 跳转至类型列表
	 * @return 类型列表
	 */
	@RequestMapping(value="list")
	public String list() {
		return "/shop/admin/type/type_list";
	}
	
	/**
	 * 获取类型列表Json
	 * @author LiFenLong
	 * @return 类型列表Json
	 */
	@ResponseBody
	@RequestMapping(value="list-json")
	public GridJsonResult listJson(String keyword){
		Map map  = new HashMap();
		map.put("keyword", keyword);
		webpage = this.goodsTypeManager.pageType(this.getOrder(), this.getPage(),this.getPageSize(),map);
		return JsonResultUtil.getGridJson(webpage);
	}
	
	/**
	 * 跳转设置类型页面第一步
	 * @return 设置类型页面
	 */
	@RequestMapping(value="step1")
	public String step1() {
		return "/shop/admin/type/type_add_step1";
	}
	
	/**
	 * 跳转至设置类型页面第二步
	 * @return json
	 * @param 商品类型 GoodsTypeDTO
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="step2")
	public JsonResult step2(GoodsTypeDTO goodsType,int is_edit) {

		// *步骤的状态，存在session中，在每步进行更改这个状态*//
		TypeSaveState saveState = new TypeSaveState();
		this.session.put(GOODSTYPE_STATE_SESSION_KEY, saveState);

		GoodsType tempType = getTypeFromSession();
		if (tempType == null) {

			this.session.put(GOODSTYPE_SESSION_KEY, goodsType); // 用页面上收集的信息
		} else { // 用于编辑的时候，先从session取出从数据库里读取的类型信息，然后根据页面收集用户选择的情况改变session中的信息。

			if (is_edit == 1) {
				tempType.setHave_parm(goodsType.getHave_parm());
				tempType.setHave_prop(goodsType.getHave_prop());
				tempType.setJoin_brand(goodsType.getJoin_brand());
				tempType.setIs_physical(goodsType.getIs_physical());
				tempType.setHave_field(goodsType.getHave_field());
				tempType.setName(goodsType.getName());
				tempType.setJoin_spec(goodsType.getJoin_spec());
			} else {
				this.session.put(GOODSTYPE_SESSION_KEY, goodsType);
			}
		}

		String result = getResult(goodsType.getType_id());
		if (result == null) {
			//this.renderText("参数不正确！");
		}
		return JsonResultUtil.getSuccessJson("添加成功");
	}

	/**
	 * 编辑类型
	 * @param typeId 商品类型Id
	 * @return
	 */
	@RequestMapping(value="edit")
	public ModelAndView edit(Integer typeId) {
		ModelAndView view=new ModelAndView();
		view.setViewName("/shop/admin/type/type_edit_step1");
		view.addObject("goodsType", this.goodsTypeManager.get(typeId));
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value="edit-json")
	public String editJson(Integer typeId){
		String json="";
		GoodsTypeDTO goodsType = this.goodsTypeManager.get(typeId);
		session.put(GOODSTYPE_SESSION_KEY, goodsType);
		if(goodsType.getProps()!=null){
			JSONArray jsonar=JSONArray.fromObject(goodsType.getProps());
			Object[] objar= jsonar.toArray();
			int i=0;
			for (Object object : objar) {
				JSONObject obj= (JSONObject)object;
				obj.put("id", i);
				i++;
			}
			json =(JSONArray.fromObject(objar).toString());
		}
		if(json == null || json.equals("")){
			json="[]";
		}
		return json;
	}

	@RequestMapping(value="edit-other")
	public ModelAndView editOther(Integer typeId, Integer otherType){
		ModelAndView view=new ModelAndView();
		view.addObject("goodsType", goodsTypeManager.get(typeId));
		if(otherType==2){
			GoodsTypeDTO goodsType = this.goodsTypeManager.get(typeId);
			view.addObject("proplist", goodsType.getPropList());
			
			view.setViewName("/shop/admin/type/type_props");
			return view;
		}
		if(otherType==3){
			view.setViewName("/shop/admin/type/type_params");
			return view;
		}
		if(otherType==4){
			view.addObject("brandlist", brandManager.list());
			view.setViewName("/shop/admin/type/type_brand");
			return view;
		}
		if(otherType == 5){
			view.addObject("specList", this.specManager.list());
			view.setViewName("/shop/admin/type/type_spec");
			return view;
		}
		return null;
	}
	
	@RequestMapping(value="param-item")
	public String paramItem(){
		return "/shop/admin/type/param_input_item";
	}
	
	
	/**
	 * 保存参数信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-params")
	public JsonResult saveParams(String paramnum, String[] groupnames, String[] paramnames,Integer typeId) {
		String[] paramnums = new String[] {};
		if (paramnum != null&&!StringUtil.isEmpty(paramnum)) {
			if (paramnum.indexOf(",-1") >= 0) {// 检查是否有删除的参数组
				paramnum = paramnum.replaceAll(",-1", "");
			}
			paramnums = paramnum.split(",");
		}

		String params = this.goodsTypeManager.getParamString(paramnums,groupnames, paramnames, null);
		GoodsType prop= this.goodsTypeManager.getById(typeId);
		prop.setParams(params);
		this.goodsTypeManager.save(prop);
		return JsonResultUtil.getSuccessJson("保存成功");
		
	}

	/**
	 * 保存属性信息
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ResponseBody
	@RequestMapping(value="save-props")
	public JsonResult saveProps(Integer typeId) throws UnsupportedEncodingException {
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		//设置请求编码
		req.setCharacterEncoding("UTF-8");
		
		//接收参数
		String [] name = req.getParameterValues("name");
		String [] type = req.getParameterValues("type");
		String [] options = req.getParameterValues("options");
		String [] unit = req.getParameterValues("unit");
		String [] required = req.getParameterValues("required");
		String [] datatype = req.getParameterValues("datatype");
		
		try {
			GoodsType inprop= this.goodsTypeManager.getById(typeId);
			
			List<Attribute> list = new ArrayList<Attribute>();
			if(name!=null){
				for (int i = 0; i < name.length; i++) {
					Attribute attribute = new Attribute();
					attribute.setName(name[i]);
					
					//判断类型不能为空
					if (StringUtil.isEmpty(type[i])) {
						return JsonResultUtil.getErrorJson("类型不能为空");
					}
					attribute.setType(Integer.parseInt(type[i]+"") );
					attribute.setOptions(options[i].replace("，", ","));
					attribute.setUnit(unit[i]);
					attribute.setRequired(Integer.parseInt(required[i]+""));
					attribute.setDatatype(datatype[i]);
					list.add(attribute);
				}
				
			}
			
			
			String str=JSONArray.fromObject(list).toString();
			inprop.setProps(str);
			this.goodsTypeManager.save(inprop);
			if(name==null){
				return JsonResultUtil.getErrorJson("没有可保存的记录");
			}else{
				return JsonResultUtil.getSuccessJson("保存成功");
			}
			
		} catch (Exception e) {
			this.logger.error("操作失败：", e);
			return JsonResultUtil.getErrorJson("操作失败");
			
		}

	}

	/**.
	 * 保存品牌数据
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-brand")
	public JsonResult saveBrand(Integer typeId,Integer[] chhoose_brands) {

		GoodsType prop= this.goodsTypeManager.getById(typeId);
		prop.setBrand_ids(chhoose_brands);

		this.goodsTypeManager.saveTypeBrand(prop);
		return JsonResultUtil.getSuccessJson("保存成功");

	}

	/**.
	 * 保存关联的规格数据
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/save-spec")
	public JsonResult saveSpec(Integer typeId,Integer[] choose_specs) {

		GoodsType prop= this.goodsTypeManager.getById(typeId);
		prop.setSpec_ids(choose_specs);

		this.goodsTypeManager.saveTypeSpec(prop);
		return JsonResultUtil.getSuccessJson("保存成功");
	}
	
	/**
	 * 将商品类型放入回收站
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="delete")
	public JsonResult delete(Integer[] type_id) {
		try {
			if(EopSetting.IS_DEMO_SITE){
				for(Integer tid:type_id){
					if(tid<=48){
						return JsonResultUtil.getErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
					}
				}
			}
			
			int result  = goodsTypeManager.delete(type_id);
			if(result ==1){
				return JsonResultUtil.getSuccessJson("删除成功");
			}else{
				return JsonResultUtil.getErrorJson("此类型存在与之关联的商品或类别，不能删除。");
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("删除失败");
		}
	}

	/**
	 * 清空商品类型
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="clean")
	public JsonResult clean(Integer[] type_id) {
		try {
			goodsTypeManager.clean(type_id);
			return JsonResultUtil.getSuccessJson("清除成功");
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("清除失败");
		}
	}

	/**
	 * 从回收站中还原商品类型
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="revert")
	public JsonResult revert(Integer[] type_id) {
		try {
			goodsTypeManager.revert(type_id);
			return JsonResultUtil.getSuccessJson("还原成功");
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("还原失败");
		}
	}


	// 读取某个类型下的商品属性定义并形成输入html
	// 被ajax抓取用
	public String disPropsInput(Integer typeId) {
		List attrList = this.goodsTypeManager.getAttrListByTypeId(typeId);
		attrList =attrList==null || attrList.isEmpty() ?null:attrList;
		return "/com/enation/app/shop/plugin/standard/type/props_input"; 
	}

	//
	// 读取某个类型下的商品参数定义并形成输入html
	// 被ajax抓取用
	public String disParamsInput(Integer typeId) {
		ParamGroup[] paramAr = this.goodsTypeManager.getParamArByTypeId(typeId);
		return "/com/enation/app/shop/plugin/standard/type/params_input";
	}


	//添加或修改商品时异步读取品牌列表
	public String listBrand(Integer typeId){
		List brandlist = this.goodsTypeManager.listByTypeId(typeId);
		return "/com/enation/app/shop/plugin/standard/type/brand_input";
	}	

	private GoodsTypeDTO getTypeFromSession() {
		Object obj = this.session.get(GOODSTYPE_SESSION_KEY);

		if (obj == null) {
			// this.renderText("参数不正确");
			return null;
		}

		GoodsTypeDTO tempType = (GoodsTypeDTO) obj;

		return tempType;
	}

	/**
	 * 当前流程的保存状态
	 * 
	 * @return
	 */
	private TypeSaveState getSaveStateFromSession() {

		// *从session中取出收集的类型数据*//
		Object obj = this.session.get(GOODSTYPE_STATE_SESSION_KEY);
		if (obj == null) {
			//this.renderText("参数不正确");
			return null;
		}
		TypeSaveState tempType = (TypeSaveState) obj;
		return tempType;
	}

	/**
	 * 根据流程状态以及在第一步时定义的
	 * 
	 * @return
	 */
	private String getResult(Integer typeId) {

		GoodsType tempType = getTypeFromSession();
		GoodsTypeDTO goodsType = getTypeFromSession();
		TypeSaveState saveState = getSaveStateFromSession();
		String result = null;

		if (tempType.getHave_prop() != 0 && saveState.getDo_save_props() == 0) { // 用户选择了此类型有属性，同时还没有保存过
			result = "add_props";
		} else if (tempType.getHave_parm() != 0
				&& saveState.getDo_save_params() == 0) { // 用户选择了此类型有参数，同时还没有保存过
			result = "add_parms";
		} else if (tempType.getJoin_brand() != 0
				&& saveState.getDo_save_brand() == 0) { // 用户选择了此类型有品牌，同时还没有保存过
			List brandlist= this.brandManager.list();
			result = "join_brand";
		} else {

			//result = save(typeId);
		}

		return result;
	}
	
}
