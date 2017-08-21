package com.enation.app.shop.core.decorate.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.app.shop.core.decorate.model.Floor;
import com.enation.app.shop.core.decorate.model.FloorProps;
import com.enation.app.shop.core.decorate.service.IFloorManager;
import com.enation.app.shop.core.goods.model.Brand;
import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.JsonResult;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

/**
 * 
 * 模板管理
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Controller
@RequestMapping("/core/admin/template")
@SuppressWarnings(value={"rawtypes"})
public class TemplateController {

	@Autowired
	private IGoodsCatManager goodsCatManager;
	@Autowired
	private IFloorManager floorManager;

	@Autowired
	private IBrandManager brandManager;

	@Autowired
	private IAdvManager advManager;

	@Autowired
	private IDaoSupport daoSupport;

	@Autowired
	private IGoodsManager goodsManager;

	protected final Logger logger = Logger.getLogger(getClass());
	/**
	 * 弹出编辑导航分类dialog
	 * @param data_id 导航分类id
	 * @param floor_id 楼层id
	 * @return spring ModelAndView
	 */
	@RequestMapping(value="edit-guid-cat")
	public ModelAndView editGuidCat(Integer data_id,Integer floor_id){
		ModelAndView view=new ModelAndView();
		if(data_id!=null){
			List<Cat> childrenCat =this.goodsCatManager.listAllChildren(data_id);
			view.addObject("guid_cat", childrenCat);
			view.addObject("guid_cat_id",data_id);
		}
		view.addObject("floor_id", floor_id);
		view.setViewName("/floor/admin/edit/edit-guid-cat");
		return view;
	}

	/**
	 * 保存导航分类
	 * @param cat_id 分类id
	 * @param floor_id 楼层id
	 * @return 处理结果JsonResult
	 */
	@ResponseBody
	@RequestMapping(value="save-guid-cat")
	public JsonResult savaGuidCat(Integer cat_id,Integer floor_id){
		try {
			this.floorManager.saveGuidCat(cat_id,floor_id);
			return JsonResultUtil.getSuccessJson("保存成功");
		} catch (RuntimeException e) {
			this.logger.error("保存楼层导航分类失败",e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}

	/**
	 * 跳转编辑品牌
	 * @param floor_id 楼层id
	 * @return
	 */
	@RequestMapping(value="edit-brand")
	public ModelAndView editBrand(Integer floor_id){
		ModelAndView view= new ModelAndView();
		String brand_Json=this.floorManager.getBrandIds(floor_id);
		if(!StringUtil.isEmpty(brand_Json)){
			List<Brand> brandList  =floorManager.listBrands(brand_Json);
			view.addObject("brandList", brandList);
		}
		view.addObject("floor_id", floor_id);
		view.setViewName("/floor/admin/edit/edit-brand");
		return view;
	}



	/**
	 * 保存品牌
	 * @param floor_id  楼层id
	 * @param brand_ids 品牌id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-brand")
	public JsonResult saveBrand(Integer floor_id,@RequestParam(value="brand_ids[]",required=false)Integer[] brand_ids){
		try {
			this.floorManager.saveBrandIds(floor_id,brand_ids);
			return JsonResultUtil.getSuccessJson("保存品牌成功");
		} catch (Exception e) {
			this.logger.error("保存楼层品牌失败",e);
			return JsonResultUtil.getErrorJson("保存品牌失败");
		}
	}

	/**
	 * 弹出编辑分类dialog
	 * @param data_id 分类id
	 * @param floor_id 楼层id
	 * @return spring ModelAndView
	 */
	@RequestMapping(value="edit-cat")
	public ModelAndView editCat(Integer data_id,Integer floor_id){
		ModelAndView view= new ModelAndView();
		if(data_id!=null){
			List<Cat> list=this.goodsCatManager.listAllChildren(data_id);
			view.addObject("catList", list);
			view.addObject("cat_id", data_id);
		}
		view.addObject("floor_id", floor_id);
		view.setViewName("/floor/admin/edit/edit-cat");
		return view;
	}


	/**
	 * 保存分类
	 * @param floor_id 楼层id
	 * @param cat_id 分类id
	 * @return 处理结果JsonResult
	 */
	@ResponseBody
	@RequestMapping(value="save-cat")
	public JsonResult saveCat(Integer floor_id,Integer cat_id){
		try {
			this.floorManager.saveCatId(floor_id,cat_id);
			return JsonResultUtil.getSuccessJson("保存左侧分类成功");
		} catch (Exception e) {
			this.logger.error("保存楼层分类失败",e);
			return JsonResultUtil.getErrorJson("保存左侧分类失败");
		}
	}

	/**
	 * 跳转广告编辑
	 * @param floor_id 楼层id
	 * @return 
	 */
	@RequestMapping(value="edit-adv")
	public ModelAndView editAdv(Integer floor_id,String index){
		ModelAndView view= new ModelAndView();
		List advList = this.floorManager.getAllAdvList();
		advList = advList == null ? new ArrayList<Adv>():advList;
		String advIds=this.floorManager.getAdvIds(floor_id);
		if(!StringUtil.isEmpty(advIds)){
			Map<String,Object> aid_map1=JsonUtil.toMap(advIds);
			Map<String,Object> aid_map=(Map<String, Object>) aid_map1.get(index);
			if(aid_map!=null&&aid_map.size()!=0){
				List<Integer> list=new ArrayList<Integer>();
				for(Map.Entry<String, Object> entry:aid_map.entrySet()){
					list.add(Integer.valueOf(entry.getValue().toString()));
				}
				view.addObject("aid_List", list);
			}
		}
		view.addObject("advList", advList);
		view.addObject("floor_id", floor_id);
		view.addObject("index",index);
		view.setViewName("/floor/admin/edit/edit-adv");
		return view;
	}

	/**
	 * 保存广告
	 * @param floor_id 楼层id
	 * @param adv_ids 广告id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="save-adv")
	public JsonResult saveAdv(Integer floor_id,@RequestParam(value="adv_ids[]",required=false)Integer[] adv_ids,String position){
		try {
			this.floorManager.saveAdvId(floor_id,adv_ids,position);
			return JsonResultUtil.getSuccessJson("保存楼层广告成功");
		} catch (Exception e) {
			this.logger.error("保存楼层广告失败",e);
			return JsonResultUtil.getErrorJson("保存楼层广告失败");
		}


	}



	/**
	 * 编辑单个商品
	 * @param data_id 商品id
	 * @param floor_id 楼层id
	 * @param index 商品位置索引
	 * @return
	 */
	@RequestMapping("edit-each-goods")
	public ModelAndView editEachGoods(Integer data_id,Integer floor_id,Integer index){
		ModelAndView view=new ModelAndView();
		Goods goods=this.goodsManager.getGoods(data_id);
		if(goods!=null){
			if(goods.getThumbnail().startsWith("fs:")){
				goods.setThumbnail(StaticResourcesUtil.convertToUrl(goods.getThumbnail()));
			}
			view.addObject("goods", goods);
		}
		view.addObject("index", index);
		view.addObject("floor_id", floor_id);
		view.setViewName("/floor/admin/edit/edit-each-goods");
		return view;
	}
	/**
	 * 保存单个商品
	 * @param new_goods_id 新商品id
	 * @param floor_id 楼层id
	 * @param index 商品位置索引
	 * @return
	 */
	@ResponseBody
	@RequestMapping("save-each-goods")
	public JsonResult saveEachGoods(Integer new_goods_id,Integer floor_id,Integer index){
		try {
			if(new_goods_id==null||new_goods_id==0){
				return JsonResultUtil.getErrorJson("请选择商品");
			}
			this.floorManager.saveEachGoods(new_goods_id,floor_id,index);
			return JsonResultUtil.getSuccessJson("保存成功");
		} catch (Exception e) {
			this.logger.error("保存楼层单个商品失败",e);
			return JsonResultUtil.getErrorJson("保存失败");
		}
	}
	/**
	 * 跳转批量编辑商品
	 * @param floor_id 楼层id
	 * @return
	 */
	@RequestMapping("edit-batch-goods")
	public ModelAndView editBatchGoods(Integer floor_id){
		ModelAndView view=new ModelAndView();
		Floor floor=this.floorManager.getFloorById(floor_id);
		if(!StringUtil.isEmpty(floor.getProps())){
			Map<String,Object> props=JsonUtil.toMap(floor.getProps());
			props.put("stype", 0);
			view.addObject("props", props);
		}
//		if(!StringUtil.isEmpty(floor.getGoods_ids())){//无用 暂时不去掉
//			Map<String,Object> goods_ids_map=JsonUtil.toMap(floor.getGoods_ids());
//			List list=this.floorManager.getGoodsListByGoods_ids(goods_ids_map);
//			view.addObject("goodsList", list);
//		}
		view.addObject("pageid",floor.getPage_id());
		view.addObject("floor_id", floor_id);
		view.addObject("parent_floor",floor.getParent_id());
		//		view.addObject("")添加已选
		view.setViewName("/floor/admin/edit/edit-batch-goods");
		return view;
	}

	/**
	 * 批量保存商品
	 * @param floor_id 楼层id
	 * @param goods_ids 商品id
	 * @param props 查询条件属性
	 * @return
	 */
	@ResponseBody
	@RequestMapping("save-batch-goods")
	public JsonResult saveBatchGoods(Integer floor_id,@RequestParam("goods_ids[]")Integer[] goods_ids,FloorProps props){
		try {
			this.floorManager.saveProps(props,floor_id);
			this.floorManager.saveBatchGoods(goods_ids,floor_id);
			return JsonResultUtil.getSuccessJson("保存成功");
		} catch (Exception e) {
			this.logger.error("保存楼层批量商品失败",e);
			return JsonResultUtil.getErrorJson("保存失败");
		}
	}

	/**
	 * 根据id获取品牌
	 * @param id  品牌id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("get-brand")
	public JsonResult getBrand(Integer id){
		Brand brand=this.floorManager.getBrand(id);
		return JsonResultUtil.getObjectJson(brand);
	}

}
