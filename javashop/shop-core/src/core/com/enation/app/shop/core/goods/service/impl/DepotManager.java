package com.enation.app.shop.core.goods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.goods.model.Depot;
import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.log.LogType;


/***
 * 库房管理实现
 * @author kingapex
 *@author Kanon 2016-2-18;6.0版本改造
 */
@Service("depotManager")
public class DepotManager  implements IDepotManager {

	@Autowired
	private IDaoSupport daoSupport;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IDepotManager#add(com.enation.app.shop.core.goods.model.Depot)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="添加了一个名为${room.name}的仓库")
	public void add(Depot room) {
		if(room.getChoose()==1){
			this.daoSupport.execute("update es_depot set choose=0 where choose=1");
		}
		this.daoSupport.insert("es_depot", room);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IDepotManager#update(com.enation.app.shop.core.goods.model.Depot)
	 */
	@Override
	@Log(type=LogType.SETTING,detail="修改了名为${room.name}的仓库信息")
	public void update(Depot room) {
		if(room.getChoose()==1){
			this.daoSupport.execute("update es_depot set choose=0 where choose=1");
		}
		this.daoSupport.update("es_depot", room, "id="+room.getId());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IDepotManager#get(int)
	 */
	@Override
	public Depot get(int roomid) {
		return this.daoSupport.queryForObject("select * from es_depot where id=?", Depot.class, roomid);
		 
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IDepotManager#list()
	 */
	@Override
	public List<Depot> list() {
		return this.daoSupport.queryForList("select * from es_depot", Depot.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IDepotManager#getDefault()
	 */
	@Override
	public Depot getDefault() { 
		return this.daoSupport.queryForObject("select * from es_depot where choose = 1", Depot.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IDepotManager#delete(int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.SETTING,detail="删除ID为${roomid}仓库")
	public String delete(int roomid) {
		String message=this.depot_validate(roomid);
		if(message.equals("")){
			this.daoSupport.execute("delete from es_goods_depot where depotid = ?", roomid);
			this.daoSupport.execute("delete from es_product_store where depotid = ?", roomid);
			this.daoSupport.execute("delete from es_depot_user where depotid = ?", roomid);
			this.daoSupport.execute("delete from es_depot where id = ?", roomid);
			message="删除成功";
		}
		return message;
		
	}
	
	private String depot_validate(int roomid){
		int is_choose= daoSupport.queryForInt("select choose from es_depot where id=?", roomid);
		if(is_choose==1){
			return "此仓库为默认仓库，不能删除";
		}
		List<Integer> numList  =(List)daoSupport.queryForList("select sum(store) from es_product_store where depotid=? and store>0  and goodsid in (select goods_id from es_goods g where g.disabled=0 )",new IntegerMapper(), roomid);
		
		Integer  has_goods=0;
		if(numList!=null && !numList.isEmpty()){
			has_goods = numList.get(0);
			if( has_goods==null) has_goods=0;
		}
		
		if(has_goods!=0){
			return "此仓库仍有商品。不能删除";
		}
		return "";
	}
}
