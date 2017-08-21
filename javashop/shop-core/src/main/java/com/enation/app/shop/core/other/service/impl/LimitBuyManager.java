package com.enation.app.shop.core.other.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.other.model.LimitBuy;
import com.enation.app.shop.core.other.model.LimitBuyGoods;
import com.enation.app.shop.core.other.service.ILimitBuyManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;


@Deprecated
public class LimitBuyManager implements ILimitBuyManager {
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IDaoSupport  daoSupport;
	@Transactional(propagation = Propagation.REQUIRED)  
	public void add(LimitBuy limitBuy) {
		List<LimitBuyGoods> limitBuyGoodsList = limitBuy.getLimitBuyGoodsList();
	
		limitBuy.setAdd_time(DateUtil.getDateline());
		this.daoSupport.insert("limitbuy", limitBuy);
		Integer limitBuyId = this.daoSupport.getLastId("limitbuy");
		this.addGoods(limitBuyGoodsList, limitBuyId);
	
	}
	
	private void addGoods(List<LimitBuyGoods> limitBuyGoodsList,Integer limitBuyId){
		if(limitBuyGoodsList == null || limitBuyGoodsList.isEmpty()) throw new RuntimeException("添加限时购买的商品列表不能为空");
		for(LimitBuyGoods limitBuyGoods :limitBuyGoodsList){
			this.daoSupport.execute("insert into limitbuy_goods (limitbuyid,goodsid,price)values(?,?,?)", limitBuyId,limitBuyGoods.getGoodsid(),limitBuyGoods.getPrice());
			
			this.updateGoodsLimit(limitBuyId, 1);
		}
	}
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer id) {
		
		this.updateGoodsLimit(id, 0);
		this.daoSupport.execute("delete from limitbuy_goods where limitbuyid=?", id);
		this.daoSupport.execute("delete from limitbuy where id=?", id);
		

	}

	@Transactional(propagation = Propagation.REQUIRED)  
	public void edit(LimitBuy limitBuy) {
		List<LimitBuyGoods> limitBuyGoodsList = limitBuy.getLimitBuyGoodsList();
		this.daoSupport.update("limitbuy", limitBuy,"id="+limitBuy.getId());
		Integer limitBuyId = limitBuy.getId();
		this.daoSupport.execute("delete from limitbuy_goods where limitbuyid=?", limitBuyId);
		
		this.updateGoodsLimit(limitBuyId, 0);
		this.addGoods(limitBuyGoodsList, limitBuyId);

	}

	/**
	 * 更新商品是否是限时购买
	 * @param limitid
	 * @param islimit
	 */
	private void updateGoodsLimit(Integer limitid,int islimit){
		this.daoSupport.execute("update "+("es_goods")+" set islimit=? where goods_id in(select goodsid from "+ ("es_limitbuy_goods") +" where limitbuyid=?)",islimit ,limitid);
	}
	
	
	public Page list(int pageNo, int pageSize) {
		String sql ="select * from limitbuy order by add_time desc";
		return this.daoSupport.queryForPage(sql, pageNo, pageSize, LimitBuy.class);
	}

	
	public List<LimitBuy> listEnable() {
		long now  = DateUtil.getDateline();
		//首先读出可用的限时抢购列表
		String sql ="select * from limitbuy where start_time<? and end_time>? order by add_time desc";
		List<LimitBuy> limitBuyBuyList = this.daoSupport.queryForList(sql, LimitBuy.class,now,now);
		
		
		//读取出相应的商品列表
		sql ="select g.* ,lg.limitbuyid limitbuyid ,lg.price limitprice  from "+ ("es_limitbuy_goods") +" lg  ,"+ ("es_goods") +" g where lg.goodsid= g.goods_id and lg.limitbuyid in";
		sql+="(select id from "+("es_limitbuy")+" where start_time<? and end_time>?)";
		List<Map> goodsList  = this.daoSupport.queryForList(sql, now,now);
		for(LimitBuy limitBuy:limitBuyBuyList){
			List<Map> list  = this.findGoods(goodsList, limitBuy.getId());
			limitBuy.setGoodsList(list);
		}
		return limitBuyBuyList;
	}
	public List<Map> listEnableGoods(){
		long now = DateUtil.getDateline();
		//读取出相应的商品列表
		String sql ="select g.* ,lg.limitbuyid limitbuyid ,lg.price limitprice  from "+ ("es_limitbuy_goods") +" lg  ,"+ ("es_goods") +" g where lg.goodsid= g.goods_id and lg.limitbuyid in";
		sql+="(select id from "+("es_limitbuy")+" where start_time<? and end_time>?)";
		List<Map> goodsList  = this.daoSupport.queryForList(sql, now,now);
		return goodsList;
	}
	private List<Map> findGoods(List<Map> goodsList,Integer limitbuyid){
		List<Map> list  = new ArrayList<Map>();
		for(Map goods : goodsList){
			if(limitbuyid.compareTo((Integer) goods.get("limitbuyid"))==0){
				list.add(goods);
			}
			 
		}
		return list;
	}

	public LimitBuy get(Integer id) {
		String sql ="select * from limitbuy where  id=?";
		LimitBuy limitBuy =(LimitBuy)this.daoSupport.queryForObject(sql,LimitBuy.class,id );
		sql ="select g.* ,lg.limitbuyid limitbuyid from "+ ("es_limitbuy_goods") +" lg  ,"+ ("es_goods") +" g where lg.goodsid= g.goods_id and lg.limitbuyid =?";
		List<Map> goodsList  = this.daoSupport.queryForList(sql, id);
		limitBuy.setGoodsList(goodsList);
		return limitBuy;
	}

}
