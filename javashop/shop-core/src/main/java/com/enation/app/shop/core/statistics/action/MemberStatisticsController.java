package com.enation.app.shop.core.statistics.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.core.statistics.service.IMemberStatisticsManager;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;

/**
 * 会员统计
 * @author DMRain 2016年3月3日 版本改造
 * @version v2.0 改为spring mvc
 * @since v6.0
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/memberStatistics")
public class MemberStatisticsController {
	
	protected Logger logger;
	
	@Autowired
	private IMemberStatisticsManager memberStatisticsManager;

	/**
	 * 获得会员分析页
	 * 
	 * @return result name
	 */
	@RequestMapping(value="/member-analysis-html")
	public String memberAnalysisHtml() {
		return "/shop/admin/statistics/member/member_analysis";
	}
	
	/**
	 * 获取下单量统计页
	 * @return result name
	 */
	@RequestMapping(value="/order-num-statistics-html")
	public String orderNumStatisticsHtml(){
		return "/shop/admin/statistics/member/order_num_statistics";
	}
	
	/**
	 * 获取下单商品件数统计页
	 * @return result name
	 */
	@RequestMapping(value="/goods-num-statistics-html")
	public String goodsNumStatisticsHtml(){
		
		return "/shop/admin/statistics/member/goods_num_statistics";
	}
	

	/**
	 * 获取下单金额统计页
	 * @return result name
	 */
	@RequestMapping(value="/order-price-statistics-html")
	public String orderPriceStatisticsHtml(){
		
		return "/shop/admin/statistics/member/order_price_statistics";
	}

	
	/**
	 * 获取购买分析html页
	 * @return result name
	 */
	@RequestMapping(value="/buy-analysis-html")
	public String buyAnalysisHtml(){
		return "/shop/admin/statistics/member/buy_analysis";
	}
	
	/**
	 * 获得新增会员
	 * @return result name
	 */
	@RequestMapping(value="/add-member-num-html")
	public String addMemberNumHtml(){
		
		return "/shop/admin/statistics/member/add_member";
	}
	
	/**
	 * 获取会员下单排行
	 * 
	 * @author Sylow
	 * @param top_num
	 *            排名名次 <b>必填</b>
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否则失败
	 */
	@ResponseBody
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/get-order-num-top")
	public Object getOrderNumTop(String start_date,String end_date) {
		try {
			/**
			 * 排名名次 默认15
			 */
			Integer top_num = 15;
			
			
			String startDateStamp = "";		//开始时间戳
			String endDateStamp = "";		//结束时间戳
			
			// 1.判断并赋值
			if (start_date != null && !"".equals(start_date)) {
				startDateStamp = String.valueOf(DateUtil.getDateline(start_date, "yyyy-MM-dd HH:mm:ss"));
			}
			if (end_date != null && !"".equals(end_date)) {
				endDateStamp = String.valueOf(DateUtil.getDateline(end_date, "yyyy-MM-dd HH:mm:ss"));
			}
			
			Map map = new HashMap();
			map.put("start_date", startDateStamp);
			
			// 2.获取数据
			List<Map<String, Object>> list = this.memberStatisticsManager.getOrderNumTop(top_num, startDateStamp, endDateStamp);
			
			return JsonMessageUtil.getListJson(list);

		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("获取用户下单排行出错", e);
			return JsonResultUtil.getErrorJson("获取用户下单排行出错:" + e.getMessage());
		}
	}

	/**
	 * 获取会员下单商品排行
	 * 
	 * @author Sylow
	 * @param top_num
	 *            排名名次 <b>必填</b>
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否者失败
	 */
	@ResponseBody
	@RequestMapping("/get-goods-num-top")
	public Object getGoodsNumTop(String start_date,String end_date) {
		try {

			/**
			 * 排名名次 默认15
			 */
			Integer top_num = 15;
			
			String startDateStamp = "";		//开始时间戳
			String endDateStamp = "";		//结束时间戳
			
			// 1.判断并赋值
			if (start_date != null && !"".equals(start_date)) {
				startDateStamp = String.valueOf(DateUtil.getDateline(start_date, "yyyy-MM-dd HH:mm:ss"));
			}
			if (end_date != null && !"".equals(end_date)) {
				endDateStamp = String.valueOf(DateUtil.getDateline(end_date, "yyyy-MM-dd HH:mm:ss"));
			}
			
			// 2.获取数据
			List<Map<String, Object>> list = this.memberStatisticsManager.getGoodsNumTop(top_num, startDateStamp, endDateStamp);

			return JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户下单商品数量排行出错", e);
			return JsonResultUtil.getErrorJson("获取用户下单商品数量排行出错:" + e.getMessage());
		}
	}
	
	/**
	 * 获取会员下单总金额排行
	 * 
	 * @author Sylow
	 * @param top_num
	 *            排名名次 <b>必填</b>
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否者失败
	 */
	@ResponseBody
	@RequestMapping("/get-order-price-top")
	public Object getOrderPriceTop(String start_date,String end_date) {
		try {
			
			/**
			 * 排名名次 默认15
			 */
			Integer top_num = 15;
			
			String startDateStamp = "";		//开始时间戳
			String endDateStamp = "";		//结束时间戳
			
			// 1.判断并赋值
			if (start_date != null && !"".equals(start_date)) {
				startDateStamp = String.valueOf(DateUtil.getDateline(start_date, "yyyy-MM-dd HH:mm:ss"));
			}
			if (end_date != null && !"".equals(end_date)) {
				endDateStamp = String.valueOf(DateUtil.getDateline(end_date, "yyyy-MM-dd HH:mm:ss"));
			}
			
			// 2.获取数据
			List<Map<String, Object>> list = this.memberStatisticsManager.getOrderPriceTop(top_num, startDateStamp, endDateStamp);
			return JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户下单总额排行出错", e);
			
			return JsonResultUtil.getErrorJson("获取用户下单总额排行出错:" + e.getMessage());
		}
	}
	
	/**
	 * 获取客单价分布数据
	 * 
	 * @author Sylow
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否者失败
	 */
	@ResponseBody
	@RequestMapping("/get-order-price-dis")
	public Object getOrderPriceDis(String start_date,String end_date, Integer[] sections) {
		try {
			
			String startDateStamp = "";		//开始时间戳
			String endDateStamp = "";		//结束时间戳
			
			// 1.判断并赋值
			if (start_date != null && !"".equals(start_date)) {
				startDateStamp = String.valueOf(DateUtil.getDateline(start_date, "yyyy-MM-dd HH:mm:ss"));
			}
			if (end_date != null && !"".equals(end_date)) {
				endDateStamp = String.valueOf(DateUtil.getDateline(end_date, "yyyy-MM-dd HH:mm:ss"));
			}
			
			// 2.获取数据
			List<Map<String, Object>> list = this.memberStatisticsManager.getOrderPriceDis(Arrays.asList(sections), startDateStamp, endDateStamp);
			return JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			this.logger.error("获取用户客单价分布数据出错", e);
			return JsonResultUtil.getErrorJson("获取用户客单价分布数据出错:" + e.getMessage());
		}
	}
	
	/**
	 * 获取用户购买频次数据
	 * @author Sylow
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否者失败
	 */
	@ResponseBody
	@RequestMapping("/get-buy-fre")
	public Object getBuyFre(String start_date,String end_date) {
		try {
			
			String startDateStamp = "";		//开始时间戳
			String endDateStamp = "";		//结束时间戳
			
			// 1.判断并赋值
			if (start_date != null && !"".equals(start_date)) {
				startDateStamp = String.valueOf(DateUtil.getDateline(start_date, "yyyy-MM-dd HH:mm:ss"));
			}
			if (end_date != null && !"".equals(end_date)) {
				endDateStamp = String.valueOf(DateUtil.getDateline(end_date, "yyyy-MM-dd HH:mm:ss"));
			}
			
			// 2.获取数据
			List<Map<String, Object>> list = this.memberStatisticsManager.getBuyFre(startDateStamp, endDateStamp);
			
			return JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户购买频次出错", e);
			return JsonResultUtil.getErrorJson("获取用户购买频次出错:" + e.getMessage());
		}
	}
	
	/**
	 * 获取用户购买时段分布数据
	 * @author Sylow
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否者失败
	 */
	@ResponseBody
	@RequestMapping("/get-buy-time-dis")
	public Object getBuyTimeDis(String start_date,String end_date) {
		try {
			
			String startDateStamp = "";		//开始时间戳
			String endDateStamp = "";		//结束时间戳
			
			// 1.判断并赋值
			if (start_date != null && !"".equals(start_date)) {
				startDateStamp = String.valueOf(DateUtil.getDateline(start_date, "yyyy-MM-dd HH:mm:ss"));
			}
			if (end_date != null && !"".equals(end_date)) {
				endDateStamp = String.valueOf(DateUtil.getDateline(end_date, "yyyy-MM-dd HH:mm:ss"));
			}
			
			// 2.获取数据
			List<Map<String, Object>> list = this.memberStatisticsManager.getBuyTimeDis(startDateStamp, endDateStamp);
			return JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户购买时段分布出错", e);
			return JsonResultUtil.getErrorJson("获取用户购买时段分布出错:" + e.getMessage());
		}
	}


	/**
	 * 获得新增会员
	 * 
	 * @author whj
	 * @param start_date 开始时间
	 * @param lastStart_date 上月开始时间
	 * @param type 搜索类型  如果是2，按年搜索，
	 * @return Json格式的字符串 result = 1 代表成功 否则失败
	 */
	@ResponseBody
	@RequestMapping("/get-add-member-num")
	public Object getAddMemberNum(String start_date,String end_date , String lastStart_date,String lastEnd_date,Integer type) {
		try {
			
			String startDateStamp = "";		//本月开始时间戳
			String endDateStamp = "";		//本月结束时间戳
			String lastStartDateStamp = "";		//本月开始时间戳
			String lastEndDateStamp = "";		//本月结束时间戳
			
			// 1.判断并赋值
			if (start_date != null && !"".equals(start_date)) {
				startDateStamp = String.valueOf(DateUtil.getDateline(start_date,"yyyy-MM-dd HH:mm:ss"));
			}
			if (end_date != null && !"".equals(end_date)) {
				endDateStamp = String.valueOf(DateUtil.getDateline(end_date,"yyyy-MM-dd HH:mm:ss"));
			}
			
			if (lastStart_date != null && !"".equals(lastStart_date)) {
				lastStartDateStamp = String.valueOf(DateUtil.getDateline(lastStart_date,"yyyy-MM-dd HH:mm:ss"));
			}
			if (lastEnd_date != null && !"".equals(lastEnd_date)) {
				lastEndDateStamp = String.valueOf(DateUtil.getDateline(lastEnd_date,"yyyy-MM-dd HH:mm:ss"));
			}
			
			if(type==2){
				// 2.获取本月数据
				List<Map<String, Object>> list = this.memberStatisticsManager.getAddYearMemberNum(startDateStamp, endDateStamp);

				// 3.获取上月数据
				List<Map<String, Object>> lastList = this.memberStatisticsManager.getLastAddYearMemberNum(lastStartDateStamp, lastEndDateStamp);
				//4.放到map中
				Map result = new HashMap();
				result.put("list", list);
				result.put("lastList", lastList);
				return JsonMessageUtil.getObjectJson(result);
			}else{
				// 2.获取本月数据
				List<Map<String, Object>> list = this.memberStatisticsManager.getAddMemberNum(startDateStamp, endDateStamp);

				// 3.获取上月数据
				List<Map<String, Object>> lastList = this.memberStatisticsManager.getAddMemberNum(lastStartDateStamp, lastEndDateStamp);
				//4.放到map中
				Map result = new HashMap();
				result.put("list", list);
				result.put("lastList", lastList);
				return JsonMessageUtil.getObjectJson(result);
			}
			
			

		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户数据出错", e);
			return JsonResultUtil.getErrorJson("获取用户数据出错:" + e.getMessage());
		}
	}
	/**
	 * 获取价格区间页
	 * @return result name
	 */
	@RequestMapping(value="/get-price-range-html")
	public String getPriceRangeHtml(){
		
		return "/shop/admin/statistics/member/price_range";
	}
}
