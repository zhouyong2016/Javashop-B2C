package com.enation.app.shop.core.decorate.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.decorate.DecorateUtil;
import com.enation.app.shop.core.decorate.model.Floor;
import com.enation.app.shop.core.decorate.service.IFloorManager;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.service.IBrandManager;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

/**
 * 
 * 模板后台展示
 * @author    jianghongyan
 * @version   1.0.0,2016年6月20日
 * @since     v6.1
 */
@Controller
@RequestMapping("/core/admin/templateshow")
@SuppressWarnings(value={"rawtypes"})
public class TemplateShowController {
	@Autowired
	private IDaoSupport daoSupport;
	@Autowired
	private IBrandManager brandManager;
	@Autowired
	private IGoodsManager goodsManager;
	@Autowired
	private IFloorManager floorManager;
	
	/**
	 * 获取全部品牌
	 * @return 品牌信息字符串
	 */
	@ResponseBody
	@RequestMapping(value="list-brand")
	public String listBrand(){
		String sql="select * from es_brand";
		List list=this.daoSupport.queryForList(sql);
		String s=JSONArray.fromObject(list).toString();
		return s.replace("name","text").replace("brand_id", "id");
	}
	

	
	/**
	 * 跳转商品列表页
	 * @param json 查询条件
	 * @param batch 是否为批量添加 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("list-goods")
	public ModelAndView listGoods(String json,Integer batch){
		ModelAndView view=new ModelAndView();
		Map<String,Object> goodsMap=new HashMap<String,Object>();
		goodsMap.put("stype", 0);
		Map<String,Object> jsonMap=JsonUtil.toMap(json);
		for(Map.Entry<String, Object> entry:jsonMap.entrySet()){
			String key=entry.getKey().toString();
			String value=entry.getValue().toString();
			
			if(value!=null&&(!StringUtil.isEmpty(value))){
				if(StringUtils.isNumeric(value)){
					goodsMap.put(key, Integer.parseInt(value));
				}else{
					goodsMap.put(key, value);
				}
			}
		}
		Integer pageSize=10;
		if(goodsMap.get("goods_num")!=null){
			pageSize=Integer.valueOf(goodsMap.get("goods_num").toString());
		}
		Integer pageno=DecorateUtil.getPage();
		Page page=null;
		if(goodsMap.get("sort")==null||goodsMap.get("order")==null){
			page=this.goodsManager.searchGoods(goodsMap, pageno, pageSize, null, null, null);
		}else{
			page=this.goodsManager.searchGoods(goodsMap, pageno, pageSize, null, goodsMap.get("sort").toString(), goodsMap.get("order").toString());
		}
		List<Map> result = (List<Map>) page.getResult();
		if(result!=null){
			for(Map g : result){
				if(((String)g.get("thumbnail")).startsWith("fs:")){
					g.put("thumbnail", StaticResourcesUtil.convertToUrl((String)g.get("thumbnail")));
				}
			}
		}
		page.setCurrentPageNo(pageno);
		view.addObject("page", page);
		view.addObject("batch",batch);
		view.setViewName("/floor/admin/edit/goods-list-show");
		return view;
	}
	
	/**
	 * 获取已选择的商品
	 * @param floor_id 楼层id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("get-selected-goods")
	public GridJsonResult getSelectedGoods(Integer floor_id){
		Floor floor=this.floorManager.getFloorById(floor_id);
		String goods_ids=floor.getGoods_ids();
		Map<String,Object> goods_id_map=new HashMap<String, Object>();
		if(!StringUtil.isEmpty(goods_ids)){
			goods_id_map=JsonUtil.toMap(goods_ids);
		}
		List goodsList=this.floorManager.getGoodsListByGoods_ids(goods_id_map);
		return JsonResultUtil.getGridJson(goodsList);
	}

	/**
	 * 获取已选择的品牌
	 * @param floor_id 楼层id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("get-selected-brand")
	public String getSelectedBrand(Integer floor_id){
		Floor floor=this.floorManager.getFloorById(floor_id);
		String brand_ids=floor.getBrand_ids();
		Map<String,Object> brand_id_map=new HashMap<String, Object>();
		if(!StringUtil.isEmpty(brand_ids)){
			brand_id_map=JsonUtil.toMap(brand_ids);
		}
		List brandList=this.floorManager.getBrandListByBrandIds(brand_id_map);
		return JSONArray.fromObject(brandList).toString();
	}
	
	/**
	 * 获取已选择的广告
	 * @param floor_id 楼层id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("get-selected-adv")
	public String getSelectedAdv(Integer floor_id,String position){
		Floor floor=this.floorManager.getFloorById(floor_id);
		String adv_ids=floor.getAdv_ids();
		Map<String,Object> adv_id_map=new HashMap<String, Object>();
		if(!StringUtil.isEmpty(adv_ids)){
			adv_id_map=JsonUtil.toMap(adv_ids);
		}
		Map<String,Object> adv_id_map2=(Map<String,Object>)adv_id_map.get(position);
		if(adv_id_map2==null||adv_id_map2.size()==0){
			return "";
		}
		List advList=this.floorManager.getAdvListByAids(adv_id_map2);
		return JSONArray.fromObject(advList).toString();
	}
}
