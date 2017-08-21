package com.enation.app.shop.core.statistics.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.core.statistics.service.IRegionStatisticsManager;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 区域统计
 * @author DMRain 2016年3月5日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@SuppressWarnings({ "rawtypes" })
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/regionOrderStatistics")
public class RegionOrderStatisticsController {

	@Autowired
	private IRegionStatisticsManager regionStatisticsManager;
	
	/**
	 * 区域分析页面
	 * @return 区域分析页面
	 */
	@RequestMapping(value="/region-list")
	public String regionList(){
		return "/shop/admin/statistics/sales/quyu";  
	}
	
	/**
	 * 区域分析JSON
	 * @param type 1.下单会员数、2.下单量、3.下单金额
	 * @param cycle_type 周期模式	1为月，反之则为年
	 * @param year 年
	 * @param month 月
	 * @param data 区域分析JSON
	 * @return 区域分析JSON
	 */
	@ResponseBody
	@RequestMapping(value="/region-type-list-json")
	public Object regionTypeListJson(String data, Integer type, Integer cycle_type, Integer year, Integer month){
		try {
			data = regionStatisticsManager.getRegionStatistics(type,cycle_type,year,month);
			return "{\"message\":"+data+"}";
		} catch (Exception e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("获取json失败！");
		}
	}
	
	/**
	 * 获取区域分析列表JSON
	 * @param type 1.下单会员数、2.下单量、3.下单金额
	 * @param sort 排序方式,正序、倒序
	 * @return 区域分析列表JSON
	 */
	@ResponseBody
	@RequestMapping(value="/region-list-json")
	public GridJsonResult regionListJson(Integer type, Integer cycle_type, Integer year, Integer month){
		
		List regionList = regionStatisticsManager.regionStatisticsList(type, " desc ",cycle_type,year,month);
		return JsonResultUtil.getGridJson(regionList);
		
	}
}
