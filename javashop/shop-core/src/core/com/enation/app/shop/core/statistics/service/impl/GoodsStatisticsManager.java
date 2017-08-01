package com.enation.app.shop.core.statistics.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.statistics.service.IGoodsStatisticsManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 商品分析统计 接口实现体
 * @author xulipeng
 *
 */

@Service("goodsStatisticsManager")
public class GoodsStatisticsManager  implements IGoodsStatisticsManager {

	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IGoodsStatisticsManager#getPriceSalesList(int, int, int, java.util.List, java.util.Map)
	 */
	@Override
	public List getPriceSalesList(long startTime,long endTime, Integer catid,
			List<Map> list, Map map) {
		
		String cat_path = "";
		if(catid==null){
			cat_path = "1";
		}else if(catid.intValue()==0){
			cat_path = "0";
		}else{
			cat_path = this.daoSupport.queryForString("select gc.cat_path from es_goods_cat gc where gc.cat_id="+catid);
		}
		
		String when_sql =  this.getPriceInterval(list);
		String sql = "select count(0) as t_num , case "+when_sql+" as price_interval  from es_order_items oi  left join es_order o on oi.order_id=o.order_id "
				+ " where  oi.cat_id in (select gc.cat_id from es_goods_cat gc where gc.cat_path like '"+cat_path+"%') ";
		sql+=" and create_time >= ? and  create_time <=? ";
		sql+=" group by case "+when_sql;
		List<Map> data_list = this.daoSupport.queryForList(sql, startTime , endTime);
		
		List datalist = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			boolean flag = true;
			for (int j = 0; j < data_list.size(); j++) {
				Map m = data_list.get(j);
				if(m.get("price_interval").equals(i+1)){
					datalist.add(m.get("t_num"));
					flag=false;
				}
			}
			if(flag){
				datalist.add(0);
			}
		}

		
		return datalist;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IGoodsStatisticsManager#getHotGoodsMoney(long, long, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page getHotGoodsMoney(long startTime, long endTime, int page, int pageSize, Integer catid, Map map) {
		
		String cat_path = "";
		if(catid==null){
			cat_path= "1";
		}else if(catid.intValue()==0){
			cat_path = "0";
		}else{
			cat_path = this.daoSupport.queryForString("select gc.cat_path from es_goods_cat gc where gc.cat_id="+catid);
		}
		
		String sql = "select oi.goods_id,oi.name as oiname,SUM(oi.num) as t_num,SUM(oi.price*oi.num) as t_money from es_order_items oi left join es_order o on oi.order_id=o.order_id "
				+ "where oi.cat_id in (select gc.cat_id from es_goods_cat gc where gc.cat_path like '"+cat_path+"%') ";
		sql+=" and create_time >= ? and  create_time <=? ";
		sql+="GROUP BY oi.goods_id, oi.name ORDER BY SUM(oi.price*oi.num) desc";
		
		List list = this.daoSupport.queryForListPage(sql, page, pageSize, startTime,endTime);
		
		List t_list =  this.daoSupport.queryForList(sql, startTime,endTime);
		long totalSize = t_list.size();
		Page webPage = new Page(page, totalSize, pageSize, list);
		
		return webPage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IGoodsStatisticsManager#getHotGoodsNum(long, long, int, int, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page getHotGoodsNum(long startTime, long endTime, int page, int pageSize, Integer catid, Map map) {
		
		String cat_path = "";
		if(catid==null){
			cat_path= "1";
		}else if(catid.intValue()==0){
			cat_path = "0";
		}else{
			cat_path = this.daoSupport.queryForString("select gc.cat_path from es_goods_cat gc where gc.cat_id="+catid);
		}
		
		String sql = "select oi.goods_id,oi.name as oiname,SUM(oi.num) as t_num,SUM(oi.price*oi.num) as t_money from es_order_items oi left join es_order o on oi.order_id=o.order_id "
				+ "where oi.cat_id in (select gc.cat_id from es_goods_cat gc where gc.cat_path like '"+cat_path+"%') ";
		sql+=" and create_time >= ? and  create_time <=? ";
		sql+=" GROUP BY oi.goods_id, oi.name ORDER BY SUM(oi.num) desc";
		System.out.println(sql);
		List list = this.daoSupport.queryForListPage(sql, page, pageSize, startTime,endTime);
		
		List t_list =  this.daoSupport.queryForList(sql, startTime,endTime);
		long totalSize = t_list.size();
		Page webPage = new Page(page, totalSize, pageSize, list);
		
		return webPage;
	}
	
	public String getPriceInterval(List<Map> list){
		String sql ="";
		int i=1;
		for (Map map : list) {
			String minprice = (String) map.get("minprice");
			String maxprice = (String) map.get("maxprice");
			sql+=" when o.order_amount >= "+minprice +" and  o.order_amount <="+ maxprice +" then "+i++;
		}
		sql+=" else 0 end";
		return sql;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IGoodsStatisticsManager#getgoodsSalesDetail(long, long, int, int, java.lang.Integer, java.lang.String, java.util.Map)
	 */
	@Override
	public Page getgoodsSalesDetail(long startTime, long endTime, int page,
			int pageSize, Integer catid, String name, Map map) {
		
		String cat_path = "";
		if(catid==null || catid.intValue()==0){
			cat_path= "0";
		}else{
			cat_path = this.daoSupport.queryForString("select gc.cat_path from es_goods_cat gc where gc.cat_id="+catid);
		}
		
		String sql ="select oi.name as oiname,oi.product_id,count(oi.order_id) t_order_num,SUM(oi.num) t_goods_num,SUM(oi.num*oi.price) t_money "
				+ "from es_order_items oi left join es_order o on oi.order_id=o.order_id  "
				+ " where oi.cat_id in (select gc.cat_id from es_goods_cat gc where gc.cat_path like '"+cat_path+"%') ";
		sql+=" and o.create_time >= ? and  o.create_time <=? ";
		if(!StringUtil.isEmpty(name)){
			sql+=" and oi.name like '%"+name+"%' ";
		}
		sql+="GROUP BY oi.goods_id, oi.name, oi.product_id ORDER BY SUM(oi.num*oi.price) DESC";
		List list = this.daoSupport.queryForListPage(sql, page, pageSize, startTime,endTime);
		List t_list =  this.daoSupport.queryForList(sql, startTime,endTime);
		long totalSize = t_list.size();
		Page webPage = new Page(page, totalSize, pageSize, list);
		return webPage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.statistics.service.IGoodsStatisticsManager#getCollectList(int, int)
	 */
	@Override
	public Page getCollectList(int page, int pageSize) {
		String sql = "select count(f.member_id) y,f.goods_id,g.name,g.price from es_favorite f left join es_goods g on f.goods_id = g.goods_id  GROUP BY f.goods_id,g.name,g.price ORDER BY COUNT(f.member_id) desc ";
		List list =  this.daoSupport.queryForListPage(sql, page, pageSize);
		List totleList = this.daoSupport.queryForList(sql);
		Page webPage = new Page(page, totleList.size(), pageSize, list);
		return webPage;
	}
	

}
