package com.enation.app.shop.core.goods.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.service.IDepotMonitorManager;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
@Service("depotMonitorManager")
public class DepotMonitorManager  implements IDepotMonitorManager {
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IGoodsCatManager goodsCatManager;

	@Override
	public List getTaskList() {
		String sql = "select  count(0)  num, depotid,d.name from  es_depot  d left join es_goods_depot  gd on d.id = gd.depotid left join es_goods  g on gd.goodsid=g.goods_id   where  gd.iscmpl=0 and g.disabled=0 group by depotid";
		List list = this.daoSupport.queryForList(sql);
		////System.out.println(sql);
		return list;
	}

	@Override
	public List getSendList() {
		String sql = "select count(0) num,o.depotid,d.name  from es_depot  d  left join es_order  o on o.depotid=d.id  where o.status=4 group by depotid";
		List list = this.daoSupport.queryForList(sql);
		return list;
	}
	
	@Override
	public int getTotalByStatus(int status){
		String sql = "select count(0) from es_order  where status=?";
		return this.daoSupport.queryForInt(sql,status);
	}
	@Override
	public List depotidDepotByGoodsid(int goodsid,int catid){
		String tablename = "product_store";//货品库存--
	 
		if(tablename=="")
			return null;
		
		String sql = "select d.name, d.name,sum(s.store) num from es_depot d left join es_product_store s on d.id=s.depotid where s.goodsid="+goodsid+" group by depotid";
		////System.out.println(sql);
		List list = this.daoSupport.queryForList(sql);
		return list; 
	}
	@Override
	public List searchOrderSalesAmout(long startDate,long endDate){
		//TODO 这句语句是专为mysql准备的，在oracle及mssql中无法执行
		//TODO 测试我不太清楚逻辑是在哪的，没有测试
		//lzf edit 20120524
		String sql = "";
		if(EopSetting.DBTYPE.equals("1")){//是mysql
			sql = "select FROM_UNIXTIME(sale_cmpl_time,'%Y-%m-%d') as isdate,sum(order_amount) as amount  from es_order where  sale_cmpl=1 ";
		}else if(EopSetting.DBTYPE.equals("3")){//是mssql
			sql = "select substring(convert(varchar(10),dateadd(ss,sale_cmpl_time + 28800,'1970-01-01'),120),1,10) as isdate,sum(order_amount) as amount  from es_order where  sale_cmpl=1 ";
		}else{//是oracle
			sql = "select to_char(TO_DATE('01011970','mmddyyyy')+1/24/60/60*(sale_cmpl_time),'yyyy-mm-dd') as isdate,sum(order_amount) as amount  from es_order where  sale_cmpl=1 ";
		}
		if(startDate>0&&endDate>0){
			sql+=" and sale_cmpl_time>="+startDate;
			sql+=" and sale_cmpl_time<"+endDate;
		}
		sql+=" group by isdate";
//		//System.out.println(sql);
		RowMapper mapper = new RowMapper() {
			
			@Override
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				Date  isdate  = rs.getDate("isdate");
			    Double amount = rs.getDouble("amount");
			    Map map = new HashMap();
			    map.put("isdate", isdate);
			    map.put("amount", amount);
				return map;
			}
		};
		List list = this.daoSupport.queryForList(sql,mapper);
		return list;
	}
	@Override
	public List searchOrderSaleNumber(long startDate,long endDate,int catid){
		String sql = "select * from (select g.goods_id, g.name,sum(oi.num) as total   from es_order_items  oi left join es_goods g on oi.goods_id=g.goods_id left join es_order o on oi.order_id=o.order_id where o.sale_cmpl=1";
				
		if(startDate>0&&endDate>0){
			sql+=" and o.sale_cmpl_time>="+startDate;
			sql+=" and o.sale_cmpl_time<"+endDate;
		}
		if(catid>0){
			Cat cat  =this.goodsCatManager.getById(catid);
			sql += " and  g.cat_id in(";
			sql += "select c.cat_id from es_goods_cat c where c.cat_path like '" + cat.getCat_path() + "%')  ";
		}
		sql+=" group by oi.goods_id) t order by t.total desc";
//		//System.out.println(sql);
		
		List list = this.daoSupport.queryForList(sql);
		return list;
	}
	@Override
	public Page searchStoreLog(long startDate,long endDate,int depotid,int depotType,int opType,int page,int pageSize){
		String sql ="select g.name,d.name as depotname,sl.*  from es_store_log sl left join es_goods  g on sl.goodsid=g.goods_id left join es_depot  d on sl.depotid=d.id where 1=1 ";
		if(startDate>0&&endDate>0){
			sql+=" and sl.dateline>="+startDate;
			sql+=" and sl.dateline<"+endDate;
		}
		if(depotid>0){
			sql+=" and sl.depotid="+depotid;
		}
		if(depotType!=-1){
			sql+=" and sl.depot_type="+depotType;
		}
		if(opType!=-1){
			sql+=" and sl.op_type="+opType;
		}
		sql+=" order by dateline desc";
		Page webpage  = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
		
	}

 


}
