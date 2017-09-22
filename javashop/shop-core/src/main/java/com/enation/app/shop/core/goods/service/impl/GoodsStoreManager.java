package com.enation.app.shop.core.goods.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.goods.model.Depot;
import com.enation.app.shop.core.goods.model.WarnNum;
import com.enation.app.shop.core.goods.plugin.GoodsStorePluginBundle;
import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.app.shop.core.goods.service.IGoodsStoreManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;
@SuppressWarnings({ "unchecked", "rawtypes" })
@Service("goodsStoreManager")
public class GoodsStoreManager  implements IGoodsStoreManager {

	@Autowired
	private GoodsStorePluginBundle goodsStorePluginBundle;

	@Autowired
	private IDepotManager depotManager;

	@Autowired
	private IPermissionManager permissionManager;

	@Autowired
	private IDaoSupport daoSupport;

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#listProductStore(java.lang.Integer)
	 */
	@Override
	public List<Map> listProductStore(Integer productid) {
		List<Depot> depotList = depotManager.list();
		List<Map> depotStoreList = new ArrayList<Map>();

		String sql = "select d.*,p.storeid,p.goodsid,p.productid,p.store from es_depot"
				+ " d left join es_product_store p on d.id=p.depotid where p.productid=?";

		List<Map> storeList = this.daoSupport.queryForList(sql, productid);
		for (Depot depot : depotList) {
			Map depotStore = new HashMap();
			depotStore.put("storeid", 0);
			depotStore.put("store", 0); // 如果没有记录为0
			depotStore.put("goodsid", 0);
			depotStore.put("productid", 0);
			if (storeList != null && !storeList.isEmpty()) {
				for (Map store : storeList) {
					int depotid = Integer.parseInt(store.get("id").toString());
					if (depotid == depot.getId().intValue()) { // 找到此仓库的库存
						depotStore.put("storeid", store.get("storeid"));
						depotStore.put("store", store.get("store"));
						depotStore.put("goodsid", store.get("goodsid"));
						depotStore.put("productid", store.get("productid"));
					}
				}
			}
			depotStore.put("name", depot.getName());
			depotStore.put("depotid", depot.getId());

			depotStoreList.add(depotStore);
		}
		return depotStoreList;

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#getbStoreByProId(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Integer getbStoreByProId(Integer productid,Integer depotId){
		try {
			return this.daoSupport.queryForInt("select store from es_product_store where productid=? and depotid=?",productid,depotId);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#ListProductDepotStore(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List<Map> ListProductDepotStore(Integer productid, Integer finddepotid) {
		List<Depot> depotList = depotManager.list();
		List<Map> depotStoreList = new ArrayList<Map>();

		String sql = "select d.*,p.storeid,p.goodsid,p.productid,p.store from "
				+"es_depot d left join es_product_store"
				+ " p on d.id=p.depotid where p.productid=?";
		List<Map> storeList = this.daoSupport.queryForList(sql, productid);

		for (Depot depot : depotList) {
			if (finddepotid.intValue() != depot.getId())
				continue; // 过滤掉非本仓库的库存

			Map depotStore = new HashMap();
			depotStore.put("storeid", 0); // 如果没有记录为0
			depotStore.put("store", 0); // 如果没有记录为0
			depotStore.put("goodsid", 0);
			depotStore.put("productid", 0);

			if (storeList != null && !storeList.isEmpty()) {
				for (Map store : storeList) {
					int depotid = (Integer) store.get("id");
					if (depotid == depot.getId().intValue()) { // 找到此仓库的库存
						depotStore.put("storeid", store.get("storeid"));
						depotStore.put("store", store.get("store"));
						depotStore.put("goodsid", store.get("goodsid"));
						depotStore.put("productid", store.get("productid"));
					}
				}
			}
			depotStore.put("name", depot.getName());
			depotStore.put("depotid", depot.getId());

			depotStoreList.add(depotStore);
		}
		return depotStoreList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#getStoreHtml(java.lang.Integer)
	 */
	@Override
	public String getStoreHtml(Integer goodsid) {
		Map goods = this.getGoods(goodsid);
		return this.goodsStorePluginBundle.getStoreHtml(goods);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#getStockHtml(java.lang.Integer)
	 */
	@Override
	public String getStockHtml(Integer goodsid) {
		Map goods = this.getGoods(goodsid);
		return this.goodsStorePluginBundle.getStockHtml(goods);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#getWarnHtml(java.lang.Integer)
	 */
	@Override
	public String getWarnHtml(Integer goodsid) {
		Map goods = this.getGoods(goodsid);
		return this.goodsStorePluginBundle.getWarnHtml(goods);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#getShipHtml(java.lang.Integer)
	 */
	@Override
	public String getShipHtml(Integer goodsid) {
		Map goods = this.getGoods(goodsid);
		return this.goodsStorePluginBundle.getShipHtml(goods);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#saveStore(int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="商品ID为${goodsid}的库存维护")
	public void saveStore(int goodsid) {
		Map goods = this.getGoods(goodsid);
		//响应库存维护事件
		this.goodsStorePluginBundle.onStoreSave(goods);
		//响应库存变更事件  冯兴隆 2015-07-23
		this.goodsStorePluginBundle.onStockChange(goods);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#saveStock(int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="商品ID为${goodsid}的商品进货")
	public void saveStock(int goodsid) {
		Map goods = this.getGoods(goodsid);
		//响应进货事件
		this.goodsStorePluginBundle.onStockSave(goods);
		//响应库存变更事件  2015-07-23
		this.goodsStorePluginBundle.onStockChange(goods);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#saveWarn(int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="商品ID为${goodsid}的库存报警")
	public void saveWarn(int goodsid) {
		Map goods = this.getGoods(goodsid);
		this.goodsStorePluginBundle.onWarnSave(goods);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#saveShip(int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="商品ID为${goodsid}的出货")
	public void saveShip(int goodsid) {
		Map goods = this.getGoods(goodsid);
		this.goodsStorePluginBundle.onShipSave(goods);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#listWarns(java.lang.Integer)
	 */
	@Override
	public List<WarnNum> listWarns(Integer goods_id) {
		String sql = "select * from warn_num where  goods_id=?";
		List<WarnNum> list = this.daoSupport.queryForList(sql, WarnNum.class, goods_id);
		List<WarnNum> warnList = new ArrayList<WarnNum>();
		if (list != null && !list.isEmpty()) {
			for (WarnNum warnNum : list) {
				warnList.add(warnNum);
			}
		} else {
			WarnNum warnNum = new WarnNum();
			warnNum.setId(0);
			warnNum.setGoods_id(goods_id);
			warnNum.setWarn_num(0);
			warnList.add(warnNum);
		}
		return warnList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#getDegreeDepotStore(int, int)
	 */
	@Override
	public List<Map> getDegreeDepotStore(int goodsid, int depotid) {
		String sql = "select p.* from  product_store p where p.goodsid=? and p.depotid=?";
		return this.daoSupport.queryForList(sql, goodsid, depotid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#listGoodsStore(java.util.Map, int, int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Page listGoodsStore(Map map, int page, int pageSize, String other,String sort,String order) {

		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String name = (String) map.get("name");
		String sn = (String) map.get("sn");
		int depotid  = (Integer)map.get("depotid") ;

		boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));// 超级管理员权限
		boolean isDepotAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_admin"));// 库存管理权限
		if(!isDepotAdmin&&!isSuperAdmin){
			throw new RuntimeException("没有操作库存的权限");
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select g.goods_id,g.sn,g.name,c.name cname from es_goods g,es_goods_cat c where g.cat_id=c.cat_id  and g.disabled != 1 and g.market_enable !=2 ");

		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql.append(" and ( g.name like '%"+keyword.trim()+"%'");
				sql.append(" or g.sn like '%"+keyword.trim()+"%')");
			}
		}

		if(!StringUtil.isEmpty(name)){
			sql.append(" and g.name like '%"+name+"%'");
		}

		if(!StringUtil.isEmpty(sn)){
			sql.append(" and g.sn like '%"+sn+"%'");
		}
		if(sort!=null && order!=null ){
			sql.append("order by "+sort+" "+order);
		} else {
			sql.append("order by g.create_time desc");
		}
		
		Page webPage = this.daoSupport.queryForPage(sql.toString(), page, pageSize);

		List<Map>goodslist = (List<Map>) webPage.getResult();

		StringBuffer goodsidstr = new  StringBuffer();
		for (Map goods : goodslist) {
			Integer goodsid  = (Integer)goods.get("goods_id");
			if(goodsidstr.length()!=0){
				goodsidstr.append(",");
			}
			goodsidstr.append(goodsid);
		}

		if(goodsidstr.length()!=0){

			String ps_sql ="select ps.* from  es_product_store ps where  ps.goodsid in("+goodsidstr+") ";
			if(depotid!=0 ){
				ps_sql=ps_sql+" and depotid="+depotid;
			}else{
				//判断是否为总库存
				if(isDepotAdmin){
					AdminUser adminUser = UserConext.getCurrentAdminUser();
					String depotsql = "select d.* from es_depot d inner join es_depot_user du on du.depotid=d.id where du.userid=?";
					List<Map> depotList=this.daoSupport.queryForList(depotsql,adminUser.getUserid());
					Integer depot_id=0;
					if(depotList.size()!=0){
						for (Map map1:depotList) {
							depot_id=Integer.parseInt(map1.get("id").toString());
						}
						ps_sql=ps_sql+" and depotid="+depot_id;
					}
				}
			}
			ps_sql=ps_sql+" order by goodsid,depotid ";
			
			List<Map> storeList  = this.daoSupport.queryForList(ps_sql);

			for (Map goods : goodslist) {
				
				Integer g_store=0;
				Integer enable_store=0;
				Integer goodsid  = (Integer)goods.get("goods_id");
				if(depotid!=0 ||isDepotAdmin){
					goods.put("d_store", 0);
					goods.put("enable_store", 0);
					for (Map store : storeList) {
						Integer store_goodsid  = (Integer)store.get("goodsid");
						if(store_goodsid.compareTo(goodsid)==0){
							Integer d_store = (Integer.valueOf(goods.get("d_store").toString()))+(Integer.valueOf(store.get("store").toString()));
							enable_store =enable_store+(Integer.valueOf(store.get("enable_store").toString()));
							g_store =g_store+(Integer.valueOf(store.get("store").toString()));
							goods.put("d_store", d_store);
							goods.put("enable_store", enable_store);
							goods.put("store", g_store);
						}
					}
				}else{
					goods.put("d_store", goods.get("store"));
					goods.put("enable_store", goods.get("enable_store"));

				}
			}

		}

		return webPage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#increaseStroe(int, int, int, int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void increaseStroe(int goodsid, int productid, int depotid, int num) {

		if(this.checkExists(goodsid, depotid)){
			this.daoSupport.execute("update es_product_store set store =store+?,enable_store=enable_store+? where goodsid=? and depotid=? and productid=? ",num, num,goodsid,depotid,productid);
		}else{
			this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store,enable_store)values(?,?,?,?,?)",goodsid,productid, depotid,num,num);
		}

		this.daoSupport.execute("update es_product set  store=store+?,enable_store=enable_store+?  where product_id=?", num, num,productid);
		this.daoSupport.execute("update es_goods set store=store+?,enable_store=enable_store+?  where goods_id=?",num, num,goodsid);
		this.daoSupport.execute("update es_goods set market_enable=? where goods_id=?", 1,goodsid);				//自动上架


		Map goods = this.getGoods(goodsid);

		//响应库存退货入库事件 2015-07-23  冯兴隆
		this.goodsStorePluginBundle.onStockReturn(goods);
		//响应库存变更事件 2015-07-22  冯兴隆
		this.goodsStorePluginBundle.onStockChange(goods);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsStoreManager#getStoreList()
	 */
	@Override
	public List getStoreList() {
		boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));// 超级管理员权限
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		List list=new ArrayList();
		String sql = "select * from es_depot";
		list= this.daoSupport.queryForList(sql);
		Depot depot = new Depot();
		depot.setName("总库存");
		list.add(0, depot);
		
//		暂时去掉这块逻辑，所有的权限都可以读取到仓库。 xulipeng   2016年09月23日
//		if(isSuperAdmin){
//			String sql = "select * from es_depot";
//			list= this.daoSupport.queryForList(sql);
//			Depot depot = new Depot();
//			depot.setName("总库存");
//			list.add(0, depot);
//		}else{
//			String sql = "select * from es_depot d inner join es_depot_user du on du.depotid=d.id where du.userid=?";
//			list= this.daoSupport.queryForList(sql,adminUser.getUserid());
//		}
		return list;
	}

	/**
	 * 查询某个仓库的商品库存是否存在
	 * @param goodsid
	 * @param depotid
	 * @return
	 */
	private boolean checkExists(int goodsid,int depotid){
		int count = this.daoSupport.queryForInt("select count(0) from es_product_store where goodsid=? and depotid=?", goodsid,depotid) ;
		return count==0?false:true;
	}

	private Map getGoods(int goodsid) {
		String sql = "select * from es_goods  where goods_id=?";
		Map goods = this.daoSupport.queryForMap(sql, goodsid);
		return goods;
	}

	@Override
	public Integer getDepotCountByGoodsId(int goodsid) {
		List<Map<String,Object>> storelist=null;
		// TODO Auto-generated method stub
		boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));// 超级管理员权限
		boolean isDepotAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_admin"));// 库存管理权限
		if(!isDepotAdmin&&!isSuperAdmin){
			throw new RuntimeException("没有操作库存的权限");
		}
		String sql="select * from es_product_store ps where ps.goodsid=?";
		//判断有哪些库存的操作权限
		if(!isSuperAdmin&&isDepotAdmin){
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			String depotsql = "select d.* from es_depot d inner join es_depot_user du on du.depotid=d.id where du.userid=?";
			List<Map> depotList=this.daoSupport.queryForList(depotsql,adminUser.getUserid());
			StringBuffer depotsb=new StringBuffer();
			depotsb.append("(");
			for(Map d:depotList){
				depotsb.append(d.get("id")+",");
			}
			depotsb.deleteCharAt(depotsb.lastIndexOf(",")-1);
			depotsb.append(")");
			String depotstr=depotsb.toString();
			sql+=" ps.depotid in ?";
			storelist=this.daoSupport.queryForList(sql, goodsid,depotstr);			
		}else{
			storelist=this.daoSupport.queryForList(sql, goodsid);
		}
		int count=0;
		for (Map<String,Object> map : storelist) {
			count+=Integer.valueOf(map.get("enable_store").toString());
		}

		return count;
	}



}
