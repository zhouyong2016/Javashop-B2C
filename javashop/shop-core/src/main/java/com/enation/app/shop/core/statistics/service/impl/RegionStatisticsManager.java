/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：会员统计实现类
 *  修改人：Kanon
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.statistics.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.app.shop.core.statistics.service.IRegionStatisticsManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;

/**
 * 区域分析
 * 
 * @author kanon
 * @version v1.0,2015-09-23
 * @since v4.0
 */
@Service("regionStatisticsManager")
public class RegionStatisticsManager implements IRegionStatisticsManager {
	
	@Autowired
	private IRegionsManager regionsManager;
	
	@Autowired
	private IDaoSupport daoSupport;

	/*
	 * (non-Javadoc)
	 * @see
	 * com.enation.app.shop.core.service.statistics.IRegionStatisticsManager
	 * #getRegionStatistics()
	 */
	@Override
	public String getRegionStatistics(Integer type,Integer cycle_type,Integer year,Integer month) {
		// 新建地区计划
		Map<String, String> myMap = new HashMap<String, String>(40);

		// 获取所有地区
		myMap = this.getReagionMap(myMap);

		// 返回数据列表
		List list = new ArrayList();

		// 地区统计JSON
		StringBuffer sb = new StringBuffer("[");

		// type 1.下单会员数、2.下单量、3.下单金额
		switch (type) {
			case 1:
				list = getRegionOrderMemberNum(cycle_type,year,month);
				break;
			case 2:
				list = this.getRegionOrderNum(cycle_type,year,month);
				break;
			default:
				list = this.getRegionOrderPrice(cycle_type,year,month);
				break;
		}

		String value = "";
		int num=1;
		// 循环地区值
		for (Object o : myMap.keySet()) {
			// 获取地区下单数量
			value = this.getRegionNum(list, o.toString());

			// 拼接JSON
			sb.append("{\"name\":\"").append(o.toString()).append("\",\"value\":").append(value).append("}");
			if(myMap.keySet().size()!=num){
				sb.append(",").append("\n");
			}
			num+=1;

		}
		return sb.append("]").toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.service.statistics.IRegionStatisticsManager
	 * #regionStatisticsList(java.lang.String, java.lang.String)
	 */
	@Override
	public List regionStatisticsList(Integer type, String sort,Integer cycle_type,Integer year,Integer month) {
		//判断周期模式是否为空 如果为空赋值为月周期、并且赋值当前月
		if(cycle_type==null){
			cycle_type=1;
			type=1;
			Calendar calendar=Calendar.getInstance();
			year=calendar.get(Calendar.YEAR);
			month=calendar.get(Calendar.MONTH)+1;
		}
		
		StringBuffer sql = new StringBuffer("select s.local_name,(SELECT COUNT(member_id) from es_member ");
		sql.append("WHERE disabled!=1 and member_id IN(SELECT o.member_id from es_order o WHERE o.ship_provinceid=s.region_id AND o.status="+ OrderStatus.ORDER_COMPLETE);
		
		//判断周期模式 1为月
		if(cycle_type==1){
			sql.append(" AND o.complete_time<"+getMaxvalType1(year,month)+" AND o.complete_time > "+getMinvalType1(year, month));
		}else{
			//如果周日为年
			sql.append(" AND o.complete_time<"+getMaxvalType0(year)+" AND o.complete_time > "+getMinvalType0(year));
		}
		
		sql.append(") ) member_num");
		sql.append(",(SELECT COUNT(o.order_id) from es_order o WHERE o.ship_provinceid=s.region_id AND o.status="+ OrderStatus.ORDER_COMPLETE);
		
		//判断周期模式 1为月
		if(cycle_type==1){
			sql.append(" AND o.complete_time<"+getMaxvalType1(year,month)+" AND o.complete_time > "+getMinvalType1(year, month));
		}else{
			//如果周日为年
			sql.append(" AND o.complete_time<"+getMaxvalType0(year)+" AND o.complete_time > "+getMinvalType0(year));
		}
		
		sql.append(	") order_num");
		sql.append(",(SELECT SUM(o.paymoney) from es_order o WHERE o.ship_provinceid=s.region_id AND o.status="+ OrderStatus.ORDER_COMPLETE);
		
		//判断周期模式 1为月
		if(cycle_type==1){
			sql.append(" AND o.complete_time<"+getMaxvalType1(year,month)+" AND o.complete_time > "+getMinvalType1(year, month));
		}else{
			//如果周日为年
			sql.append(" AND o.complete_time<"+getMaxvalType0(year)+" AND o.complete_time > "+getMinvalType0(year));
		}
		sql.append(") order_price");
		sql.append(" from es_regions s WHERE s.p_region_id=0 ");
		
		//判断排序方式
		switch(type){
		case 1:
			sql.append(" ORDER BY member_num ");
			break;
		case 2:
			sql.append(" ORDER BY order_num ");
			break;
		case 3:
			sql.append(" ORDER BY order_price ");
			break;
		}
		
		sql.append(sort);
		return this.daoSupport.queryForList(sql.toString());
	}
	
	/**
	 * 获取所有地区集合
	 * @param myMap 地区集合
	 * @return 地区集合
	 */
	private Map getReagionMap(Map myMap) {
		myMap.put("吉林", "cn-jl");
		myMap.put("天津", "cn-tj");
		myMap.put("安徽", "cn-ah");
		myMap.put("山东", "cn-sd");
		myMap.put("山西", "cn-sx");
		myMap.put("新疆", "cn-xj");
		myMap.put("河北", "cn-hb");
		myMap.put("河南", "cn-he");
		myMap.put("湖南", "cn-hn");
		myMap.put("甘肃", "cn-gs");
		myMap.put("福建", "cn-fj");
		myMap.put("贵州", "cn-gz");
		myMap.put("重庆", "cn-cq");
		myMap.put("江苏", "cn-js");
		myMap.put("湖北", "cn-hu");
		myMap.put("内蒙古", "cn-nm");
		myMap.put("广西", "cn-gx");
		myMap.put("黑龙江", "cn-hl");
		myMap.put("云南", "cn-yn");
		myMap.put("辽宁", "cn-ln");
		myMap.put("香港", "cn-6668");
		myMap.put("浙江", "cn-zj");
		myMap.put("上海", "cn-sh");
		myMap.put("北京", "cn-bj");
		myMap.put("广东", "cn-gd");
		myMap.put("澳门", "cn-3681");
		myMap.put("西藏", "cn-xz");
		myMap.put("陕西", "cn-sa");
		myMap.put("四川", "cn-sc");
		myMap.put("海南", "cn-ha");
		myMap.put("宁夏", "cn-nx");
		myMap.put("青海", "cn-qh");
		myMap.put("江西", "cn-jx");
		myMap.put("台湾", "tw-tw");
		myMap.put("南沙群岛", "cn-3664");
		return myMap;
	}

	/**
	 * 查询区域会员下单数量
	 * 
	 * @param myMap 地区集合
	 * @return 区域会员下单数量 JSON
	 */
	private List getRegionOrderMemberNum(Integer cycle_type,Integer year,Integer month) {
		// 拼装 区域会员下单数量SQL
		StringBuffer sql = new StringBuffer("select s.local_name,(SELECT COUNT(member_id) from es_member ");
		sql.append("WHERE disabled!=1 and member_id IN(SELECT o.member_id FROM es_order o WHERE o.ship_provinceid=s.region_id AND o.status="+ OrderStatus.ORDER_COMPLETE);
		
		//判断周期模式 1为月
		if(cycle_type==1){
			sql.append(" AND o.complete_time<"+getMaxvalType1(year,month)+" AND o.complete_time > "+getMinvalType1(year, month));
		}else{
			//如果周日为年
			sql.append(" AND o.complete_time<"+getMaxvalType0(year)+" AND o.complete_time > "+getMinvalType0(year));
		}
		
		sql.append(" ) ) num ");
		sql.append("FROM es_regions s WHERE s.p_region_id=0 ");
		// 获取所有地区的下单量的统计值
		return daoSupport.queryForList(sql.toString());

	}

	/**
	 * 查看区域下单数量
	 * 
	 * @param myMap
	 *            地区集合
	 * @return 区域下单数量 JSON
	 */
	private List getRegionOrderNum(Integer cycle_type,Integer year,Integer month) {
		StringBuffer sql = new StringBuffer("select s.local_name,(SELECT COUNT(o.order_id) FROM es_order o WHERE o.ship_provinceid=s.region_id AND o.status="+ OrderStatus.ORDER_COMPLETE);
		
		//判断周期模式 1为月
		if(cycle_type==1){
			sql.append(" AND o.complete_time<"+getMaxvalType1(year,month)+" AND o.complete_time > "+getMinvalType1(year, month));
		}else{
			//如果周期为年
			sql.append(" AND o.complete_time<"+getMaxvalType0(year)+" AND o.complete_time > "+getMinvalType0(year));
		}
		
		sql.append(" ) num FROM es_regions s WHERE s.p_region_id=0");
		
		// 获取所有地区的下单量的统计值
		return daoSupport.queryForList(sql.toString());
	}

	/**
	 * 获取区域下单价格
	 * 
	 * @param 地区集合
	 * @return 区域下单金额 JSON
	 */
	private List getRegionOrderPrice(Integer cycle_type,Integer year,Integer month) {
		StringBuffer sql = new StringBuffer("select s.local_name,(SELECT SUM(o.paymoney) from es_order o WHERE o.ship_provinceid=s.region_id AND o.status="+ OrderStatus.ORDER_COMPLETE);
		//判断周期模式 1为月
		if(cycle_type==1){
			sql.append(" AND o.complete_time<"+getMaxvalType1(year,month)+" AND o.complete_time > "+getMinvalType1(year, month));
		}else{
			//如果周日为年
			sql.append(" AND o.complete_time<"+getMaxvalType0(year)+" AND o.complete_time > "+getMinvalType0(year));
		}
		
		sql.append(") num FROM es_regions s WHERE s.p_region_id=0 ");
		// 获取所有地区的下单量的统计值
		return daoSupport.queryForList(sql.toString());
	}

	/**
	 * 获取下单地区的订单数量
	 * @param list 地区列表，包含数量
	 * @param local_name 地区名称
	 * @return 下单地区的订单数量
	 */
	private String getRegionNum(List<Map> list, String local_name) {
		String num = "0";
		for (Map map : list) {
			if (map.get("num") != null) {
				if (map.get("local_name").toString().equals(local_name)) {
					num = map.get("num").toString();
					break;
				}
			}
		}
		return num;
	}
	/**
	 * 获取当前月份的 unix时间戳  最大值
	 * @param month
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
	 * @param month
	 * @return
	 */
	private long getMinvalType1(int year,int month){
		 return DateUtil.getDateHaveHour(year+"-"+month+"-01 00"); 
	}
	/**
	 * 获取当前年份的  unix时间戳 最大值
	 * @param month
	 * @return
	 */
	private long getMaxvalType0(int year){ 
		 return DateUtil.getDateline(year+"-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 获取当前年份的 unix时间戳  最小值
	 * @param month
	 * @return
	 */
	private long getMinvalType0(int year){
		 return DateUtil.getDateline(year+"-01-01");
	}

}
