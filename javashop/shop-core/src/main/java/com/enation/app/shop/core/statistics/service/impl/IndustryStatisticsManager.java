/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：销售统计接口
 *  修改人：liushuai
 *  修改时间：2015-09-24
 *  修改内容：制定初版
 *  
 */
package com.enation.app.shop.core.statistics.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.app.shop.core.statistics.model.Collect;
import com.enation.app.shop.core.statistics.service.IIndustryStatisticsManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单管理的实现类 
 * @author liushuai 
 */
@Service("industryStatisticsManager")
public class IndustryStatisticsManager  implements
		IIndustryStatisticsManager {

	@Autowired
	private IDaoSupport daoSupport;
	
	// 获取所有的 根节点菜单 sql
	private	String treeRootSql = "select gc.cat_id,gc.name from es_goods_cat gc where gc.parent_id = 0";
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IIndustryStatisticsManager#statistics_price(int, int, int)
	 */
	@Override
	public List<Map> statistics_price(int type, int year, int month) {
		// cat_id 类型 name 名称
		List<Map> treeRoot = (List) this.daoSupport.queryForList(treeRootSql);

		List<Map> result = new ArrayList<Map>();
		Map tMap = new HashMap();
		for (int i = 0; i < treeRoot.size(); i++) {
			tMap = new HashMap();
			if (type == 1) {
				// 月份统计
				String temSql = "select SUM(oi.price) sump from es_order_items oi left join es_order od on oi.order_id = od.order_id where  oi.cat_id in (select cat_id from es_goods_cat gc where gc.cat_path like '0|"
						+ treeRoot.get(i).get("cat_id") + "%') and od.create_time BETWEEN "+this.getMinvalType1(year, month)+" and "+this.getMaxvalType1(year, month) +" and od.disabled = 0 AND ((od.payment_id = 3  AND od.status = "
				+ OrderStatus.ORDER_COMPLETE
				+ ") OR ( od.pay_status = "
				+ OrderStatus.PAY_YES
				+ "))";
				tMap.put(treeRoot.get(i).get("name"),
						this.daoSupport.queryForMap(temSql).get("sump"));

			} else {
				// 年份统计
				String temSql = "select SUM(oi.price) sump from es_order_items oi left join es_order od on oi.order_id = od.order_id where  oi.cat_id in (select cat_id from es_goods_cat gc where gc.cat_path like '0|"
						+ treeRoot.get(i).get("cat_id") + "%') and od.create_time BETWEEN "+this.getMinvalType0(year)+" and "+this.getMaxvalType0(year)+" and od.disabled = 0 AND ((od.payment_id = 3  AND od.status = "
				+ OrderStatus.ORDER_COMPLETE
				+ ") OR ( od.pay_status = "
				+ OrderStatus.PAY_YES
				+ "))"; 
				tMap.put(treeRoot.get(i).get("name"),
						this.daoSupport.queryForMap(temSql).get("sump"));
			}
			result.add(tMap);
		}
		return result;
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IIndustryStatisticsManager#statistics_order(int, int, int)
	 */
	@Override
	public List<Map> statistics_order(int type, int year, int month) {
		// cat_id 类型 name 名称
		List<Map> treeRoot = (List) this.daoSupport.queryForList(treeRootSql);

		List<Map> result = new ArrayList<Map>();
		Map tMap = new HashMap();
		for (int i = 0; i < treeRoot.size(); i++) {
			tMap = new HashMap();
			if (type == 1) {
				// 月份统计
				String temSql = "select count(od.order_id) sump from  es_order od left join es_order_items oi on oi.order_id = od.order_id where  oi.cat_id in (select cat_id from es_goods_cat gc where gc.cat_path like '0|"
						+ treeRoot.get(i).get("cat_id") + "%') and od.create_time BETWEEN "+this.getMinvalType1(year, month)+" and "+this.getMaxvalType1(year, month) +" and od.disabled = 0 AND ((od.payment_id = 3  AND od.status = "
								+ OrderStatus.ORDER_COMPLETE
								+ ")	OR ( od.pay_status = "
								+ OrderStatus.PAY_YES
								+ "))"; 
				tMap.put(treeRoot.get(i).get("name"),
						this.daoSupport.queryForInt(temSql));

			} else {
				// 年份统计
				String temSql = "select count(od.order_id) sump from es_order od left join es_order_items oi on oi.order_id = od.order_id where  oi.cat_id in (select cat_id from es_goods_cat gc where gc.cat_path like '0|"
						+ treeRoot.get(i).get("cat_id") + "%') and od.create_time BETWEEN "+this.getMinvalType0(year)+" and "+this.getMaxvalType0(year)+" and od.disabled = 0 AND ((od.payment_id = 3  AND od.status = "
								+ OrderStatus.ORDER_COMPLETE
								+ ")	OR ( od.pay_status = "
								+ OrderStatus.PAY_YES
								+ "))"; 
				tMap.put(treeRoot.get(i).get("name"),
						this.daoSupport.queryForInt(temSql));

			}
			result.add(tMap);
		}
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IIndustryStatisticsManager#statistics_goods(int, int, int)
	 */
	@Override
	public List<Map> statistics_goods(int type, int year, int month) {
		// cat_id 类型 name 名称
		List<Map> treeRoot = (List) this.daoSupport.queryForList(treeRootSql);

		List<Map> result = new ArrayList<Map>();
		Map tMap = new HashMap();
		for (int i = 0; i < treeRoot.size(); i++) {
			tMap = new HashMap();
			if (type == 1) {
				// 月份统计
				String temSql = "select count(oi.price) sump from es_order_items oi left join es_order od on oi.order_id = od.order_id where  oi.cat_id in (select cat_id from es_goods_cat gc where gc.cat_path like '0|"
						+ treeRoot.get(i).get("cat_id") + "%') and od.create_time BETWEEN "+this.getMinvalType1(year, month)+" and "+this.getMaxvalType1(year, month)+" and od.disabled = 0 AND ((od.payment_id = 3  AND od.status = "
								+ OrderStatus.ORDER_COMPLETE
								+ ")	OR ( od.pay_status = "
								+ OrderStatus.PAY_YES
								+ "))"; 
				tMap.put(treeRoot.get(i).get("name"),
						this.daoSupport.queryForInt(temSql));

			} else {
				// 年份统计
				String temSql = "select count(oi.price) sump from es_order_items oi left join es_order od on oi.order_id = od.order_id where  oi.cat_id in (select cat_id from es_goods_cat gc where gc.cat_path like '0|"
						+ treeRoot.get(i).get("cat_id") + "%') and od.create_time BETWEEN "+this.getMinvalType0(year)+" and "+this.getMaxvalType0(year)+" and od.disabled = 0 AND ((od.payment_id = 3  AND od.status = "
								+ OrderStatus.ORDER_COMPLETE
								+ ")	OR ( od.pay_status = "
								+ OrderStatus.PAY_YES
								+ "))"; 
				tMap.put(treeRoot.get(i).get("name"),
						this.daoSupport.queryForInt(temSql));

			}
			result.add(tMap);
		}
		return result;
	} 

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IIndustryStatisticsManager#getAllRootMenu()
	 */
	@Override
	public List<Map> getAllRootMenu() { 
		return this.daoSupport.queryForList(treeRootSql);
	} 
	 
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IIndustryStatisticsManager#listCollect(int, java.util.List)
	 */
	@Override
	public List<Collect> listCollect(int cat_id,List<Cat> cats) { 
		List<Collect> collects = new ArrayList<Collect>(); 
		//展示的是近三十天的属性。所以先获取两个时间戳
		Date now = new Date();  
		Long endDate = DateUtil.getDateline();
		Long startDate = DateUtil.getDateline(this.getLastMonth(now), null);
		Collect collect = null;
		for (Cat cat : cats) { 
			//这里查询到的有一个是父类所以要判定
			if(cat.getCat_id()==cat_id){
				continue;
			}
			
			collect = new Collect();
			//名称
			collect.setName(cat.getName());
			//平均价格
			String avgSql = "select AVG(gs.price) as avg from es_goods gs where cat_id in (select cat_id from es_goods_cat gc where gc.cat_path like '%|"	+ cat.getCat_id() + "|%') ";
			try {
				collect.setAvgPrice(Double.parseDouble(this.daoSupport.queryForMap(avgSql).get("avg").toString()));
			} catch (NumberFormatException e1) { 
			}
			//有销量商品数
			String salesGoodsSql = "select count(0)from (select oi.goods_id from es_order_items oi " +
					"left join es_order od on oi.order_id = od.order_id where oi.cat_id in " +
					"(select cat_id from es_goods_cat gc where gc.cat_path like '%|"
					+ cat.getCat_id() + "|%') and od.create_time BETWEEN "+startDate+" and "+endDate+""
					+" and od.disabled = 0 AND ((od.payment_id = 3  AND od.status = "
					+ OrderStatus.ORDER_COMPLETE
					+ ")	OR ( od.pay_status = "
					+ OrderStatus.PAY_YES
					+ "))"+" GROUP BY oi.goods_id) tt";
			try {
				collect.setSalesGoodsNum(this.daoSupport.queryForInt(salesGoodsSql));
			} catch (Exception e) { 
			}
			//无销量商品数 / 总商品数
			String residueSql = "select count(0) from es_goods g where g.cat_id in (select cat_id from es_goods_cat gc where gc.cat_path like '%|"	+ cat.getCat_id() + "|%')";
			try {
				collect.setCountGoods(this.daoSupport.queryForInt(residueSql));
			} catch (Exception e) { 
			}
			try {
				collect.setResidue(collect.getCountGoods()-collect.getSalesGoodsNum());
			} catch (Exception e) { 
			}
			//销售总额
			String countSql = "select sum(oi.price) as zonge,count(0) as zongs from es_order_items oi left join es_order od on oi.order_id = od.order_id where  oi.cat_id in (select cat_id from es_goods_cat gc where gc.cat_path like '%|"
					+ cat.getCat_id() + "|%') and od.create_time BETWEEN "+startDate+" and "+endDate +" and od.disabled = 0 AND ((od.payment_id = 3  AND od.status = "
							+ OrderStatus.ORDER_COMPLETE
							+ ")	OR ( od.pay_status = "
							+ OrderStatus.PAY_YES
							+ "))";
			Map map = this.daoSupport.queryForMap(countSql);
			try {
				collect.setSaleroom(StringUtil.toDouble(map.get("zonge"),false));
				collect.setSales(StringUtil.toInt(map.get("zongs"),false));
			} catch (NumberFormatException e) { 
			}
			
			collects.add(collect);
		}
		return collects;
	}
	
	
	/**
	 * 获取上个月的同一时间
	 * @param 当前时间
	 * @return
	 */
	private String getLastMonth(Date now) { 
		// date getYear 获取到的年份 是从1990年算起所以需要加 1990 
		GregorianCalendar calendar = new GregorianCalendar(now.getYear()+1900,
				now.getMonth(), now.getDate()); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 定义日期显示格式
		calendar.add(Calendar.MONTH, -1);// 获取上个月月份
		return (sdf.format(calendar.getTime()));
	}
	
	/**
	 * 获取当前月份的 unix时间戳  最大值
	 * @param 年份
	 * @param 月份
	 * @return
	 */
	private long getMaxvalType1(int year,int month){ 
		//如果是 12月 那么明年1月的零时作为结束时间
		if(month==12){ 
			return DateUtil.getDateHaveHour((year+1)+"-01-01 00"); 
		}
		//否则 下个月 0时
		return DateUtil.getDateHaveHour(year+"-"+(month+1)+"-01 00"); 
	}
	/**
	 * 获取当前月份的  unix时间戳  最小值
	 * @param 年份
	 * @param 月份
	 * @return
	 */
	private long getMinvalType1(int year,int month){
		 return DateUtil.getDateHaveHour(year+"-"+month+"-01 00"); 
	}
	
	
	/**
	 * 获取当前年份的  unix时间戳 最大值
	 * @param 年份
	 * @return
	 */
	private long getMaxvalType0(int year){ 
		 return DateUtil.getDateline(year+"-12-31 23:59:59" , "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 获取当前年份的 unix时间戳  最小值
	 * @param 年份
	 * @return
	 */
	private long getMinvalType0(int year){
		 return DateUtil.getDateline(year+"-01-01");
	}


 
}