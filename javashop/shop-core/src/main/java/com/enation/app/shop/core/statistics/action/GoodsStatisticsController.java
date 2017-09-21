package com.enation.app.shop.core.statistics.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.statistics.service.IGoodsStatisticsManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;

import net.sf.json.JSONArray;

/**
 * 热卖商品统计
 * @author DMRain 2016年3月3日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/goodsStatis")
public class GoodsStatisticsController extends GridController{

	@Autowired
	private IGoodsStatisticsManager goodsStatisticsManager;

	@Autowired
	private IGoodsCatManager goodsCatManager;

	/**
	 * 跳转价格销量展示页
	 * @return
	 */
	@RequestMapping(value="/price-sales")
	public String priceSales(){
		return "/shop/admin/statistics/goodsanalysis/price_sales_list";
	}

	/**
	 * 读取价格销量统计的json
	 * @param cycle_type 周期模式 1为月，反之则为年
	 * @param year 年
	 * @param month	月
	 * @param cat_id 商品分类ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-price-sales-json")
	public Object getPriceSalesJson(Integer cycle_type, Integer year, Integer month, Integer cat_id){

		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		String minprices = request.getParameter("minprice");		//最小价格的数据
		String maxprices = request.getParameter("maxprice");		//最大价格的数据

		String [] minp = minprices.split(",");
		String [] maxp = maxprices.split(",");

		//把数据存放在list
		List list = new ArrayList();
		for(int i =0;i<minp.length;i++){
			Map map = new HashMap();
			if(!minp[i].equals("")){
				map.put("minprice", minp[i]);
				map.put("maxprice", maxp[i]);
				list.add(map);
			}
		}

		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}

		//当cat_id为空时，为其赋值0，目的是查询全部分类！ 修改人:DMRain 2015-12-07
		if(cat_id == null){
			cat_id = 0;
		}

		List data_list = new ArrayList();
		if(cycle_type.intValue()==1){

			int day = getDaysByYearMonth(year, month);
			long start_time = DateUtil.getDateline(year+"-"+month+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end_time = DateUtil.getDateline(year+"-"+month+"-"+day+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			//System.out.println(start_time+"___"+end_time);
			data_list = this.goodsStatisticsManager.getPriceSalesList(start_time, end_time, cat_id, list, null);

		}else{

			long start_time = DateUtil.getDateline(year+"-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end_time = DateUtil.getDateline(year+"-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss");  
			data_list = this.goodsStatisticsManager.getPriceSalesList(start_time, end_time, cat_id, list, null);
		}

		return JsonMessageUtil.getListJson(data_list);
	}

	/**
	 * 跳转热卖商品页
	 * @param cycle_type 周期模式 1为月，反之则为年
	 * @param year 年
	 * @param month	月
	 * @param cat_id 商品分类ID
	 * @param statis_type 统计模式  1为下单金额，反之为下单量
	 * @return
	 */
	@RequestMapping(value="/hot-goods")
	public ModelAndView hotgoods(Integer cycle_type, Integer year, Integer month, Integer cat_id, Integer statis_type){
		ModelAndView view = new ModelAndView();

		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		if(cat_id==null){
			cat_id=0;
		}

		view.addObject("cycle_type", cycle_type);
		view.addObject("year", year);
		view.addObject("month", month);

		if(statis_type==null || statis_type==1){	//下单金额

			Page moneyPage = getHotGoodsMoneyList(cycle_type, year, month, cat_id, null);
			List<Map> moneyList = (List) moneyPage.getResult();
			List money_list = new ArrayList();
			for (Map map : moneyList) {
				Map money_map = new HashMap();
				money_map.put("name", map.get("oiname"));
				money_map.put("y", map.get("t_money"));
				money_list.add(money_map);
			}
			view.addObject("money_json", JSONArray.fromObject(money_list).toString());
			view.setViewName("/shop/admin/statistics/goodsanalysis/hot_goods_money");
			return view;
		}else{

			Page numPage = getHotGoodsNumList(cycle_type, year, month, cat_id, null);
			List<Map> numList = (List) numPage.getResult();
			List num_list = new ArrayList();
			for (Map map : numList) {
				Map money_map = new HashMap();
				money_map.put("name", map.get("oiname"));
				money_map.put("y", map.get("t_num"));
				num_list.add(money_map);
			}

			view.addObject("num_json", JSONArray.fromObject(num_list).toString());
			view.setViewName("/shop/admin/statistics/goodsanalysis/hot_goods_num");
			return view;
		}
	}

	/**
	 * 获取热卖商品金额json
	 * @param cycle_type 周期模式 1为月，反之则为年
	 * @param year 年
	 * @param month	月
	 * @param cat_id 商品分类ID
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-hot-goods-money-json")
	public JsonResult getHotGoodsMoneyJson(Integer cycle_type, Integer year, Integer month, Integer cat_id){
		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		Page moneyPage = this.getHotGoodsMoneyList(cycle_type, year, month, cat_id, null);
		List<Map> moneyList = (List) moneyPage.getResult();
		List money_list = new ArrayList();
		for (Map map : moneyList) {
			Map money_map = new HashMap();
			money_map.put("name", map.get("oiname"));
			money_map.put("y", map.get("t_money"));
			money_list.add(money_map);
		}
		GridJsonResult gridJson= JsonResultUtil.getGridJson(moneyPage);//得到grid格式的json

		Map<String,Object> jsonMap=new HashMap<String,Object>();
		jsonMap.put("gridjson", gridJson);
		jsonMap.put("chartsjson", money_list);
		return JsonResultUtil.getObjectJson(jsonMap);
	}

	/**
	 * 获取热卖商品数量的grid-json和echarts-json
	 * @param cycle_type
	 * @param year
	 * @param month
	 * @param cat_id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-hot-goods-num-json")
	public JsonResult getHotGoodsNumJson(Integer cycle_type, Integer year, Integer month, Integer cat_id){
		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		Page numPage = this.getHotGoodsNumList(cycle_type, year, month, cat_id, null);
		GridJsonResult gridJson= JsonResultUtil.getGridJson(numPage);//得到grid格式的json

		Map<String,Object> jsonMap=new HashMap<String,Object>();
		jsonMap.put("gridjson", gridJson);

		List<Map> numList = (List) numPage.getResult();
		List num_list = new ArrayList();
		for (Map map : numList) {
			Map money_map = new HashMap();
			money_map.put("name", map.get("oiname"));
			money_map.put("y", map.get("t_num"));
			num_list.add(money_map);
		}
		jsonMap.put("chartsjson", num_list);

		return JsonResultUtil.getObjectJson(jsonMap);
	}
	/**
	 * 获取热卖商品金额的grid-json和echarts-json
	 * @param cycle_type
	 * @param year
	 * @param month
	 * @param cat_id
	 * @return
	 */

	@RequestMapping(value="/get-hot-goods-money")
	public String getHotGoodsMoney(Integer cycle_type, Integer year, Integer month, Integer cat_id){


		return "/shop/admin/statistics/goodsanalysis/get-hot-goods-money";

	}





	
	@RequestMapping(value="/get-hot-goods-num")
	public String getHotGoodsNum(Integer cycle_type, Integer year, Integer month, Integer cat_id){
		
		return "/shop/admin/statistics/goodsanalysis/get-hot-goods-num";
	}
	
	/**
	 * 商品销售明细
	 * @param cycle_type 周期模式 1为月，反之则为年
	 * @param year 年
	 * @param month	月
	 * @return
	 */
	@RequestMapping(value="/goods-sales-detail")
	public ModelAndView goodsSalesDetail(Integer cycle_type, Integer year, Integer month){
		ModelAndView view = new ModelAndView();

		if(cycle_type==null){
			cycle_type=1;
		}
		view.addObject("cycle_type", cycle_type);

		Calendar cal = Calendar.getInstance();

		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		view.addObject("year", year);

		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		view.addObject("month", month);

		view.setViewName("/shop/admin/statistics/goodsanalysis/goods_sales_detail");
		return view;
	}

	/**
	 * 商品销售明细的json
	 * @param cycle_type 周期模式 1为月，反之则为年
	 * @param year 年
	 * @param month	月
	 * @param cat_id 商品分类ID
	 * @param name 商品名称
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/goods-sales-detail-json")
	public GridJsonResult goodsSalesDetailJson(Integer cycle_type, Integer year, Integer month, Integer cat_id, String name){
		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}

		Page webpage = new Page();

		if(cycle_type.intValue()==1){

			int day = getDaysByYearMonth(year, month);
			long start_time = DateUtil.getDateline(year+"-"+month+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end_time = DateUtil.getDateline(year+"-"+month+"-"+day+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			webpage = this.goodsStatisticsManager.getgoodsSalesDetail(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, name, null);

		}else{

			long start_time = DateUtil.getDateline(year+"-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end_time = DateUtil.getDateline(year+"-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss");  
			webpage = this.goodsStatisticsManager.getgoodsSalesDetail(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, name, null);
		}
		return JsonResultUtil.getGridJson(webpage);
	}
	
	
	/**
	 * 商品收藏统计页
	 * @author xulipeng
	 * @return
	 */
	@RequestMapping(value="/goods-collect")
	public ModelAndView goodsCollect(){
		ModelAndView view = new ModelAndView();
		
		Page webPage = this.goodsStatisticsManager.getCollectList(1, 50);
		List list = (List) webPage.getResult();
		
		view.addObject("collectJosn", JSONArray.fromObject(list).toString());
		view.setViewName("/shop/admin/statistics/goodsanalysis/goods-collect");
		return view;
	}
	
	/**
	 * 商品收藏统计列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-collect-json")
	public GridJsonResult getCollectList(){
		Page webPage = this.goodsStatisticsManager.getCollectList(this.getPage(), this.getPageSize());
		return JsonResultUtil.getGridJson(webPage);
	}
	

	/**
	 * 热卖商品金额top list
	 * @param cycle_type
	 * @param year
	 * @param month
	 * @param cat_id
	 * @param map
	 * @return
	 */
	public Page getHotGoodsMoneyList(Integer cycle_type, int year,int month,Integer cat_id,Map map){
		List moneyList = new ArrayList();
		Page moneyPage = new Page();

		int rows = 50;

		if(cycle_type.intValue()==1){

			int day = getDaysByYearMonth(year, month);
			long start_time = DateUtil.getDateline(year+"-"+month+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end_time = DateUtil.getDateline(year+"-"+month+"-"+day+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			moneyPage = this.goodsStatisticsManager.getHotGoodsMoney(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, map);

		}else{

			long start_time = DateUtil.getDateline(year+"-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end_time = DateUtil.getDateline(year+"-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss");  

			moneyPage = this.goodsStatisticsManager.getHotGoodsMoney(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, map);
		}
		return moneyPage;
	}

	/**
	 * 热卖商品数量top list
	 * @param cycle_type
	 * @param year
	 * @param month
	 * @param cat_id
	 * @param map
	 * @return
	 */
	public Page getHotGoodsNumList(Integer cycle_type, int year,int month,Integer cat_id,Map map){
		List numList = new ArrayList();
		Page numPage = new Page();

		int rows = 50;
		if(cycle_type.intValue()==1){

			int day = getDaysByYearMonth(year, month);
			long start_time = DateUtil.getDateline(year+"-"+month+"-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end_time = DateUtil.getDateline(year+"-"+month+"-"+day+" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			numPage = this.goodsStatisticsManager.getHotGoodsNum(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, map);

		}else{

			long start_time = DateUtil.getDateline(year+"-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			long end_time = DateUtil.getDateline(year+"-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss");  

			numPage = this.goodsStatisticsManager.getHotGoodsNum(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, map);
		}
		return numPage;
	}
	
	


	//获取当前年月的最大的天数
	public int getDaysByYearMonth(int year, int month) {  
		Calendar a = Calendar.getInstance();  
		a.set(Calendar.YEAR, year);  
		a.set(Calendar.MONTH, month - 1);  
		a.set(Calendar.DATE, 1);  
		a.roll(Calendar.DATE, -1);  
		int maxDate = a.get(Calendar.DATE);  
		return maxDate;  
	}
	
	/**
	 * 跳转至修改价格区间页面
	 * @return
	 */
	@RequestMapping(value="/get_price_range_html")
	public String priceRange() {
		return "/shop/admin/statistics/goodsanalysis/price_range";
	}
}
