package com.enation.app.shop.core.statistics.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.statistics.model.Collect;
import com.enation.app.shop.core.statistics.service.IIndustryStatisticsManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;

/**
 * 行业统计
 * @author DMRain 2016年3月5日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/industryStatistics")
public class IndustryStatisticsController extends GridController{

	@Autowired
	private IIndustryStatisticsManager industryStatisticsManager;
	
	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	/**
	 * 显示统计主页面
	 * 
	 * @return page
	 */
	@RequestMapping(value="/show-page")
	public String showPage() {
		return "/shop/admin/statistics/industry/index";
	}
	
	/**
	 * 显示行业总览界面
	 * 
	 * @return page
	 */
	@RequestMapping(value="/show-collect")
	public ModelAndView showCollect(int cat_id) {
		ModelAndView view = new ModelAndView();
		List<Cat> cats = this.goodsCatManager.listAllChildren(0);
		view.addObject("cats", cats);
		
		if (cat_id == 0) {
			cat_id = cats.get(0).getCat_id();
		}
		view.addObject("cat_id", cat_id);
		view.addObject("pageSize", this.getPageSize());
		view.setViewName("/shop/admin/statistics/industry/collect");
		return view;
	}
	
	/**
	 * 总览的表格数据
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/collect-data")
	public GridJsonResult collectData(int cat_id){
		List<Collect> collects = new ArrayList<Collect>();
		List<Cat> cats = this.goodsCatManager.listAllChildren(0);
		// 如果没有选择。那么显示默认第一个
		if (cat_id == 0) {
			cat_id = cats.get(0).getCat_id();
		}
		try {
			collects = industryStatisticsManager.listCollect(cat_id,
					goodsCatManager.listAllChildren(cat_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JsonResultUtil.getGridJson(collects);

	}
	
	/**
	 * 获取价格 统计页面
	 * @param type 查询分类 1：月/2：年
	 * @return page
	 */
	@RequestMapping(value="/industry-price")
	public ModelAndView IndustryPrice(int type, int year, int month) {
		ModelAndView view = new ModelAndView();
		
		try {
			
			//如果第一次执行。没有时间参数的话，我们赋值现在的时间给查询条件
			if (type == 0) {
				type = 1;
				Date date = new Date();
				year = date.getYear() + 1900;
				month = date.getMonth() + 1;
			}
			
			List<Map> list = this.industryStatisticsManager.statistics_price(
					type, year, month);
			view.addObject(JsonMessageUtil.getListJson(list));
			StringBuffer tree = new StringBuffer();
			StringBuffer data = new StringBuffer();
			for (Map map : list) {
				for (Object key : map.keySet()) {
					tree.append("'" + key.toString() + "',");
					data.append("" + map.get(key) + ",");
				}
			}
			ThreadContextHolder.getHttpRequest().setAttribute("tree",
					tree.substring(0, tree.length() - 1));
			ThreadContextHolder.getHttpRequest().setAttribute("data",
					data.substring(0, data.length() - 1));
			if (type == 2) {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "");
			} else {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "-" + month);
			}
			
			view.addObject(JsonMessageUtil.getListJson(list));
			view.setViewName("/shop/admin/statistics/industry/industrystatistics_price");
			
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取数据出错", e);
			view.addObject(JsonResultUtil.getErrorJson("获取数据出错:" + e.getMessage()));
		}
		return view;
	}
	
	/**
	 * 获取商品统计页面
	 * @param type 查询分类 1：月/2：年
	 * @return page
	 */
	@RequestMapping(value="/industry-goods")
	public ModelAndView IndustryGoods(int type, int year, int month) {
		ModelAndView view = new ModelAndView();
		
		try {
			
			//如果第一次执行。没有时间参数的话，我们赋值现在的时间给查询条件
			if (type == 0) {
				type = 1;
				Date date = new Date();
				year = date.getYear() + 1900;
				month = date.getMonth() + 1;
			}
			
			List<Map> list = this.industryStatisticsManager.statistics_goods(
					type, year, month);
			view.addObject(JsonMessageUtil.getListJson(list));
			StringBuffer tree = new StringBuffer();
			StringBuffer data = new StringBuffer();
			for (Map map : list) {
				for (Object key : map.keySet()) {
					tree.append("'" + key.toString() + "',");
					data.append("" + map.get(key) + ",");
				}
			}
			ThreadContextHolder.getHttpRequest().setAttribute("tree",
					tree.substring(0, tree.length() - 1));
			ThreadContextHolder.getHttpRequest().setAttribute("data",
					data.substring(0, data.length() - 1));
			if (type == 2) {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "");
			} else {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "-" + month);
			}

			view.setViewName("/shop/admin/statistics/industry/industrystatistics_goods");
			
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取数据出错", e);
			view.addObject(JsonResultUtil.getErrorJson("获取数据出错:" + e.getMessage()));
		}
		return view;
	}
	
	/**
	 * 获取下单量统计
	 * @param type 查询分类 1：月/2：年
	 * @return page
	 */
	@RequestMapping(value="/industry-order")
	public ModelAndView IndustryOrder(int type, int year, int month) {
		ModelAndView view = new ModelAndView();
		
		try {
			
			//如果第一次执行。没有时间参数的话，我们赋值现在的时间给查询条件
			if (type == 0) {
				type = 1;
				Date date = new Date();
				year = date.getYear() + 1900;
				month = date.getMonth() + 1;
			}
			
			List<Map> list = this.industryStatisticsManager.statistics_order(
					type, year, month);
			view.addObject(JsonMessageUtil.getListJson(list));
			StringBuffer tree = new StringBuffer();
			StringBuffer data = new StringBuffer();
			for (Map map : list) {
				for (Object key : map.keySet()) {
					tree.append("'" + key.toString() + "',");
					data.append("" + map.get(key) + ",");
				}
			}
			ThreadContextHolder.getHttpRequest().setAttribute("tree",
					tree.substring(0, tree.length() - 1));
			ThreadContextHolder.getHttpRequest().setAttribute("data",
					data.substring(0, data.length() - 1));
			if (type == 2) {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "");
			} else {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "-" + month);
			}

			view.setViewName("/shop/admin/statistics/industry/industrystatistics_order");
			
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取数据出错", e);
			view.addObject(JsonResultUtil.getErrorJson("获取数据出错:" + e.getMessage()));
		}
		return view;
	}
	
}
