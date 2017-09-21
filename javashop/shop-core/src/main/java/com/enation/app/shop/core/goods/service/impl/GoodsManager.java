package com.enation.app.shop.core.goods.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.PluginTab;
import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.model.GoodsStores;
import com.enation.app.shop.core.goods.model.support.GoodsEditDTO;
import com.enation.app.shop.core.goods.plugin.GoodsPluginBundle;
import com.enation.app.shop.core.goods.plugin.search.GoodsDataFilterBundle;
import com.enation.app.shop.core.goods.service.IDepotMonitorManager;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.app.shop.core.goods.service.ITagManager;
import com.enation.app.shop.core.goods.service.SnDuplicateException;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.annotation.Log;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;


/**
 * Goods业务管理
 * 
 * @author kingapex 2010-1-13下午12:07:07
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("goodsManager")
public class GoodsManager  implements IGoodsManager {
	
	@Autowired
	private ITagManager tagManager;
	
	@Autowired
	private GoodsPluginBundle goodsPluginBundle;
	
	@Autowired
	private ISellBackManager sellBackManager;
	
	@Autowired
	private IGoodsCatManager goodsCatManager;
	
	@Autowired
	private IDepotMonitorManager depotMonitorManager;
	
	@Autowired
	private GoodsDataFilterBundle goodsDataFilterBundle;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private ICartManager cartManager;
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsManager#add(com.enation.app.shop.core.goods.model.Goods)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="添加了一个商品名为${goods.name}的商品")
	public void add(Goods goods) {
		try {
			//将po对象中有属性和值转换成map 
			Map goodsMap = po2Map(goods);
			
			// 触发商品添加前事件
			goodsPluginBundle.onBeforeAdd(goodsMap);
			
			//商品状态 是否可用
			goodsMap.put("disabled", 0);
			
			//商品创建事件
			goodsMap.put("create_time", DateUtil.getDateline());
			
			//商品浏览次数
			goodsMap.put("view_count", 0);
			
			//商品购买数量
			goodsMap.put("buy_count", 0);
			
			//商品最后更新时间
			goodsMap.put("last_modify", DateUtil.getDateline());
			
			//商品库存
			goodsMap.put("store", 0);
			
			//添加商品
			this.daoSupport.insert("es_goods", goodsMap);
			
			//获取添加商品的商品ID
			Integer goods_id = this.daoSupport.getLastId("es_goods");
			goods.setGoods_id(goods_id);
			goodsMap.put("goods_id", goods_id);
			
			//触发商品购买后事件
			goodsPluginBundle.onAfterAdd(goodsMap);
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsManager#edit(com.enation.app.shop.core.goods.model.Goods)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="修改商品名为${goods.name}的商品信息")
	public void edit(Goods goods) {
		goods.setLast_modify(DateUtil.getDateline()); //添加商品更新时间
		Logger logger = Logger.getLogger(getClass());
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("开始保存商品数据...");
			}
			Map goodsMap = this.po2Map(goods);
			
			this.goodsPluginBundle.onBeforeEdit(goodsMap);
			this.daoSupport.update("es_goods", goodsMap,
					"goods_id=" + goods.getGoods_id());

			this.goodsPluginBundle.onAfterEdit(goodsMap);
			
			
			
			if (logger.isDebugEnabled()) {
				logger.debug("保存商品数据完成.");
			}
			//标记商品被修改，会从新计算商品价格
			cartManager.changeProduct(goods.getGoods_id());
			
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsManager#getGoodsEditData(java.lang.Integer)
	 */
	@Override
	public GoodsEditDTO getGoodsEditData(Integer goods_id) {
		GoodsEditDTO editDTO = new GoodsEditDTO();
		try{
			
			String sql = "select * from es_goods where goods_id=?";
			Map goods = this.daoSupport.queryForMap(sql, goods_id);

			String intro = (String) goods.get("intro");
			if (intro != null) {
				intro = StaticResourcesUtil.convertToUrl(intro);
				goods.put("intro", intro);
			}
			String name=(String)goods.get("name");
			name=name.replace("\"", "&#34");
			name=name.replace("'", "&#39");
			goods.put("name", name);
			List<PluginTab> tabs = goodsPluginBundle.onFillEditInputData(goods);

			editDTO.setGoods(goods);
			editDTO.setTabs(tabs);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		

		return editDTO;
	}

	/**
	 * 读取一个商品的详细<br/>
	 * 处理由库中读取的默认图片和所有图片路径:<br>
	 * 如果是以本地文件形式存储，则将前缀替换为静态资源服务器地址。
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsManager#get(java.lang.Integer)
	 */
	@Override
	public Map get(Integer goods_id) {
		//判断此商品是否存在 add_by DMRain 2016-8-30
		String sql = "select count(0) from es_goods where goods_id = ?";
		int result = this.daoSupport.queryForInt(sql, goods_id);
		
		Map goods = new HashMap();
		
		//如果此商品不存在  add_by DMRain 2016-8-30
		if (result != 0) {
			sql = "select g.*,b.name as brand_name from es_goods g left join es_brand b on g.brand_id=b.brand_id ";
			sql += "  where goods_id=?";
			goods = this.daoSupport.queryForMap(sql, goods_id);

			/**
			 * ====================== 对商品图片的处理 ======================
			 */
	 
			String small = (String) goods.get("small");
			if (small != null) {
				small = StaticResourcesUtil.convertToUrl(small);
				goods.put("small", small);
			}
			String big = (String) goods.get("big");
			if (big != null) {
				big = StaticResourcesUtil.convertToUrl(big);
				goods.put("big", big);
			}
		}
 
		return goods;
	}

	/**
	 * 取得捆绑商品列表
	 * 
	 * @param disabled
	 * @return
	 */
	private String getBindListSql(int disabled) {
		String sql = "select g.*,b.name as brand_name ,t.name as type_name,c.name as cat_name from "
				+"es_goods g left join es_goods_cat"
				+ " c on g.cat_id=c.cat_id left join es_brand"
				+ " b on g.brand_id = b.brand_id left join es_goods_type"
				+ " t on g.type_id =t.type_id"
				+ " where g.goods_type = 'bind' and g.disabled=" + disabled;
		return sql;
	}	

	/**
	 * 后台搜索商品
	 * 
	 * @param params
	 *            通过map的方式传递搜索参数
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page searchBindGoods(String name, String sn, String order, int page,
			int pageSize) {

		String sql = getBindListSql(0);

		if (order == null) {
			order = "goods_id desc";
		}

		if (name != null && !name.equals("")) {
			sql += "  and g.name like '%" + name + "%'";
		}

		if (sn != null && !sn.equals("")) {
			sql += "   and g.sn = '" + sn + "'";
		}

		sql += " order by g." + order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);

		List<Map> list = (List<Map>) (webpage.getResult());

		for (Map map : list) {
			List productList = sellBackManager.list(Integer.valueOf(map
					.get("goods_id").toString()));
			productList = productList == null ? new ArrayList() : productList;
			map.put("productList", productList);
		}

		return webpage;
	}

	/**
	 * 读取商品回收站列表
	 * 
	 * @param name
	 * @param sn
	 * @param order
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page pageTrash(String name, String sn, String order, int page,
			int pageSize) {

		String sql = getListSql(1);
		if (order == null) {
			order = "goods_id desc";
		}

		if (name != null && !name.equals("")) {
			sql += "  and g.name like '%" + name + "%'";
		}

		if (sn != null && !sn.equals("")) {
			sql += "   and g.sn = '" + sn + "'";
		}

		sql += " order by g." + order;

		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);

		return webpage;
	}
	
	
	/***
	 * 库存余量提醒分页列表
	 * 
	 * @param warnTotal
	 *            总报警数
	 * @param page
	 * @param pageSize
	 */
	public List<GoodsStores> storeWarnGoods(int warnTotal, int page, int pageSize) {
		// String sql =
		// " where g.market_enable = 1 and g.goods_type = 'normal' and g.disabled= 0 order by g.goods_id desc ";
		String select_sql = "select gc.name as gc_name,b.name as b_name,g.cat_id,g.goods_id,g.name,g.sn,g.price,g.last_modify,g.market_enable,s.sumstore ";
		String left_sql = " left join es_goods g  on s.goodsid = g.goods_id  left join es_goods_cat gc on gc.cat_id = g.cat_id left join es_brand b on b.brand_id = g.brand_id ";
		List<GoodsStores> list = new ArrayList<GoodsStores>();

		String sql_2 = select_sql
				+ " from  (select ss.* from (select goodsid,productid,sum(store) sumstore from es_product_store  group by goodsid,productid   ) ss "+
				"  left join es_warn_num wn on wn.goods_id = ss.goodsid  where ss.sumstore <=  (case when (wn.warn_num is not null or wn.warn_num <> 0) then wn.warn_num else ?  end )  ) s  "
				+ left_sql;
		List<GoodsStores> list_2 = this.daoSupport.queryForList(sql_2, new RowMapper() {
					public Object mapRow(ResultSet rs, int arg1)
							throws SQLException {
						GoodsStores gs = new GoodsStores();
						gs.setGoods_id(rs.getInt("goods_id"));
						gs.setName(rs.getString("name"));
						gs.setSn(rs.getString("sn"));
						gs.setRealstore(rs.getInt("sumstore"));
						gs.setPrice(rs.getDouble("price"));
						gs.setLast_modify(rs.getLong("last_modify"));
						gs.setBrandname(rs.getString("b_name"));
						gs.setCatname(rs.getString("gc_name"));
						gs.setMarket_enable(rs.getInt("market_enable"));
						gs.setCat_id(rs.getInt("cat_id"));
						return gs;
					}
				}, warnTotal);
		list.addAll(list_2);// 普通商品		

		return list;
	}

	/**
	 * 批量将商品放入回收站
	 * 
	 * @param ids
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="删除商品")
	public void delete(Integer[] ids) {
		if (ids == null)
			return;

		for (Integer id : ids) {
			this.tagManager.saveRels(id, null);
		}
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "update  es_goods set disabled=1,market_enable=0  where goods_id in ("
				+ id_str + ")";
		// 商品下架标记修改购物车
		for (Integer id : ids) {
			cartManager.changeProduct(id);
		}
		this.daoSupport.execute(sql);
	}

	/**
	 * 下架
	 * @param ids 商品id数组
	 */
	public void under(Integer[] ids){
		if(ids == null){
			return;
		}
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "update es_goods set market_enable = 0 where goods_id in (" + id_str + ")";
		//商品下架标记修改购物车
		for (Integer id : ids) {
			cartManager.changeProduct(id);
		}
		this.daoSupport.execute(sql);
	}
	
	/**
	 * 还原
	 * 
	 * @param ids
	 */
	@Log(type=LogType.GOODS,detail="还原回收站商品")
	public void revert(Integer[] ids) {
		if (ids == null)
			return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "update  es_goods set disabled=0  where goods_id in ("
				+ id_str + ")";
		 this.daoSupport.execute(sql);
		 //商品下架标记修改购物车
		 for (Integer id : ids) {
		 cartManager.changeProduct(id);
		 }
	}

	/**
	 * 清除
	 * 
	 * @param ids
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="清除回收站商品数据")
	public void clean(Integer[] ids) {
		if (ids == null)
			return;
		for (Integer id : ids) {
			this.tagManager.saveRels(id, null);
		}
		this.goodsPluginBundle.onGoodsDelete(ids);
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "update  es_goods set disabled=2  where goods_id in (" + id_str + ")";
		this.daoSupport.execute(sql);
		// 商品下架标记修改购物车
		for (Integer id : ids) {
			cartManager.changeProduct(id);
		}
	}

	public List list(Integer[] ids) {
		if (ids == null || ids.length == 0)
			return new ArrayList();
		String idstr = StringUtil.arrayToString(ids, ",");
		String sql = "select * from es_goods where goods_id in(" + idstr + ")";
		return this.daoSupport.queryForList(sql);
	}

	

	/**
	 * 将po对象中有属性和值转换成map
	 * 
	 * @param po
	 * @return
	 */
	protected Map po2Map(Object po) {
		Map poMap = new HashMap();
		Map map = new HashMap();
		try {
			map = BeanUtils.describe(po);
		} catch (Exception ex) {
		}
		Object[] keyArray = map.keySet().toArray();
		for (int i = 0; i < keyArray.length; i++) {
			String str = keyArray[i].toString();
			if (str != null && !str.equals("class")) {
				if (map.get(str) != null) {
					poMap.put(str, map.get(str));
				}
			}
		}
		return poMap;
	}


	public Goods getGoods(Integer goods_id) {
		Goods goods = (Goods) this.daoSupport.queryForObject(
				"select * from es_goods where goods_id=?", Goods.class, goods_id);
		return goods;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void batchEdit() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String[] ids = request.getParameterValues("goodsidArray");
		String[] names = request.getParameterValues("name");
		String[] prices = request.getParameterValues("price");
		String[] cats = request.getParameterValues("catidArray");
		String[] market_enable = request.getParameterValues("market_enables");
		String[] store = request.getParameterValues("store");
		String[] sord = request.getParameterValues("sord");

		String sql = "";

		for (int i = 0; i < ids.length; i++) {
			sql = "";
			if (names != null && names.length > 0) {
				if (!StringUtil.isEmpty(names[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " name='" + names[i] + "'";
				}
			}

			if (prices != null && prices.length > 0) {
				if (!StringUtil.isEmpty(prices[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " price=" + prices[i];
				}
			}
			if (cats != null && cats.length > 0) {
				if (!StringUtil.isEmpty(cats[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " cat_id=" + cats[i];
				}
			}
			if (store != null && store.length > 0) {
				if (!StringUtil.isEmpty(store[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " store=" + store[i];
				}
			}
			if (market_enable != null && market_enable.length > 0) {
				if (!StringUtil.isEmpty(market_enable[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " market_enable=" + market_enable[i];
				}
			}
			if (sord != null && sord.length > 0) {
				if (!StringUtil.isEmpty(sord[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " sord=" + sord[i];
				}
			}
			sql = "update  es_goods set " + sql + " where goods_id=?";
			this.daoSupport.execute(sql, ids[i]);

		}
	}

	public Map census() {
		// 计算上架商品总数
		String sql = "select count(0) from es_goods where disabled = 0";
		int allcount = this.daoSupport.queryForInt(sql);
				
		// 计算上架商品总数
		sql = "select count(0) from es_goods where market_enable=1 and  disabled = 0";
		int salecount = this.daoSupport.queryForInt(sql);

		// 计算下架商品总数
		sql = "select count(0) from es_goods where market_enable=0 and  disabled = 0";
		int unsalecount = this.daoSupport.queryForInt(sql);

		// 计算回收站总数
		sql = "select count(0) from es_goods where   disabled = 1";
		int disabledcount = this.daoSupport.queryForInt(sql);

		// 读取商品评论数
		sql = "select count(comment_id) from es_member_comment where type=1 and replystatus=0";
		int discusscount = this.daoSupport.queryForInt(sql);

		// 读取商品评论数
		sql = "select count(comment_id) from es_member_comment where type=2 and replystatus=0";
		int askcount = this.daoSupport.queryForInt(sql);

		Map<String, Integer> map = new HashMap<String, Integer>(2);
		map.put("salecount", salecount);
		map.put("unsalecount", unsalecount);
		map.put("disabledcount", disabledcount);
		map.put("allcount", allcount);
		map.put("discuss", discusscount);
		map.put("ask", askcount);
		return map;
	}
 
	/**
	 * 获取某个分类的推荐商品
	 */
	public List getRecommentList(int goods_id, int cat_id, int brand_id, int num) {
		 //原美睛网代码，去掉
		return null;
	}

	public List list() {
		String sql = "select * from es_goods where disabled = 0";
		List goodsList = this.daoSupport.queryForList(sql);
		this.goodsDataFilterBundle.filterGoodsData(goodsList);
		return goodsList;
	}

	
	@Override
	@Log(type=LogType.GOODS,detail="更新了商品ID为${goodsid}的字段值")
	public void updateField(String filedname, Object value, Integer goodsid) {
		this.daoSupport.execute("update es_goods set " + filedname + "=? where goods_id=?", value, goodsid);
	}

	@Override
	public Goods getGoodBySn(String goodSn) {
		Goods goods = (Goods) this.daoSupport.queryForObject("select * from es_goods where sn=?", Goods.class, goodSn);
		return goods;
	}

	@Override
	public List listByCat(Integer catid) {
		String sql = getListSql(0);
		if (catid.intValue() != 0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " and  g.cat_id in(";
			sql += "select c.cat_id from es_goods_cat"
					+ " c where c.cat_path like '" + cat.getCat_path()
					+ "%')  ";
		}
		return this.daoSupport.queryForList(sql);
	}

	@Override
	public List listByTag(Integer[] tagid) {
		String sql = getListSql(0);
		if (tagid != null && tagid.length > 0) {
			String tagidstr = StringUtil.arrayToString(tagid, ",");
			sql += " and g.goods_id in(select rel_id from es_tag_rel where tag_id in("
					+ tagidstr + "))";
		}
		return this.daoSupport.queryForList(sql);
	}

	@Override
	public void incViewCount(Integer goods_id) {
		this.daoSupport.execute("update es_goods set view_count = view_count + 1 where goods_id = ?", goods_id);
	}
	
	
	public List listGoods(String catid,String tagid,String goodsnum){
		int num = 10;
		if(!StringUtil.isEmpty(goodsnum)){
			num = Integer.valueOf(goodsnum);
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select g.* from es_tag_rel r LEFT JOIN es_goods g ON g.goods_id=r.rel_id where g.disabled=0 and g.market_enable=1");
		
		if(!StringUtil.isEmpty(catid) ){
			Cat cat  = this.goodsCatManager.getById(Integer.valueOf(catid));
			if(cat!=null){
				String cat_path  = cat.getCat_path();
				if (cat_path != null) {
					sql.append( " and  g.cat_id in(" ) ;
					sql.append("select c.cat_id from es_goods_cat");
					sql.append(" c where c.cat_path like '" + cat_path + "%')");
				}
			}
		}
		
		if(!StringUtil.isEmpty(tagid)){
			sql.append(" AND r.tag_id="+tagid+"");
		}
		
		/*	xin
		 * 	修改为升序
		 * 	2015-12-17
		 */	
		sql.append(" order by r.ordernum asc");
		//System.out.println(sql.toString());
		List list = this.daoSupport.queryForListPage(sql.toString(), 1,num);
		this.goodsDataFilterBundle.filterGoodsData(list);
		return list;
	}
 
	@Override
	public List goodsBuyer(int goods_id, int pageSize) {
		String sql = "select distinct m.* from es_order o left join es_member m " +
				"on o.member_id=m.member_id where m.disabled!=1 and order_id in (select order_id from es_order_items " +
				"where goods_id=?)";
		Page page = this.daoSupport.queryForPage(sql, 1, pageSize, goods_id);
		
		return (List)page.getResult();
	}

	
	@Override
	public Page searchGoods(Map goodsMap, int page, int pageSize, String other,String sort,String order) {
		String sql = creatTempSql(goodsMap, other);

		
		StringBuffer _sql = new StringBuffer(sql);
		this.goodsPluginBundle.onSearchFilter(_sql);
		
		if(StringUtil.isEmpty(sort)){
			sort=" create_time";
		}
		
		if(StringUtil.isEmpty(order)){
			order=" desc";
		}
		
		if(StringUtil.isEmpty(other)){
			other=" ";
		}
		
		if(goodsMap.get("market_enable")!=null && !StringUtil.isEmpty(goodsMap.get("market_enable").toString())){
			_sql.append(" and market_enable="+goodsMap.get("market_enable"));
		}else{
			_sql.append(" and market_enable!=2");
		}
		
		_sql.append(" order by "+sort+" "+order);
		Page webpage = this.daoSupport.queryForPage(_sql.toString(), page,pageSize);
		return webpage;
	}

	@Override
	public List searchGoods(Map goodsMap) {
		String sql = creatTempSql(goodsMap, null);
		return this.daoSupport.queryForList(sql,Goods.class);
	}
	
	@Override
	public List searchGoods(Map goodsMap,String sort,String order) {
		String sql = getListSql(0);
		Integer catid = (Integer)goodsMap.get("catid");
		String name = (String) goodsMap.get("goods_name");
		Integer storeId = (Integer) goodsMap.get("store_id");
		if (name != null && !name.equals("")) {
			name = name.trim();
			String[] keys = name.split("\\s");
			for (String key : keys) {
				sql += ("and g.market_enable=1 and g.name like '%");
				sql += (key);
				sql += ("%'");
			}
		}
		
		if (storeId != null && storeId != 0) {
			sql += ("and g.market_enable=1 and g.store_id = " + storeId);
			
		}

		if (catid != null && catid!=0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " and  g.cat_id in(";
			sql += "select c.cat_id from es_goods_cat"
					+ " c where c.cat_path like '" + cat.getCat_path()
					+ "%')  ";
		}

		if (!"".equals(sort)) {
			sql += " ORDER BY "+sort+" "+order;
		}
		System.out.println(sql);
		
		return this.daoSupport.queryForList(sql,Goods.class);
	}
	
	
	private String creatTempSql(Map goodsMap,String other){
		
		other = other==null?"":other;
		String sql = getListSql(0);
		Integer brandid = (Integer) goodsMap.get("brandid");
		Integer catid = (Integer)goodsMap.get("catid");
		String name = (String) goodsMap.get("name");
		String sn = (String) goodsMap.get("sn");
		Integer[]tagid = (Integer[]) goodsMap.get("tagid");
		Integer stype = (Integer) goodsMap.get("stype");
		String keyword = (String) goodsMap.get("keyword");
		String order = (String) goodsMap.get("order");
		Integer market_enable = (Integer) goodsMap.get("market_enable");
		
		if (brandid != null && brandid != 0) {
			sql += " and g.brand_id = " + brandid + " ";
		}
		
		if("1".equals(other)){
			//商品属性为不支持打折的商品
			sql += " and g.no_discount=1";
		}
		if("2".equals(other)){
			//特殊打折商品，即单独设置了会员价的商品
			sql += " and (select count(0) from es_goods_lv_price glp where glp.goodsid=g.goods_id) >0";
		}
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and ( g.name like '%"+keyword+"%'";
				sql+=" or g.sn like '%"+keyword+"%')";
			}
		}
		
		if (name != null && !name.equals("")) {
			name = name.trim();
			String[] keys = name.split("\\s");
			for (String key : keys) {
				sql += (" and g.name like '%");
				sql += (key);
				sql += ("%'");
			}
		}

		if (sn != null && !sn.equals("")) {
			sql += "   and g.sn like '%" + sn + "%'";
		}


		if (catid != null && catid!=0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " and  g.cat_id in(";
			sql += "select c.cat_id from es_goods_cat"
					+ " c where c.cat_path like '" + cat.getCat_path()
					+ "%')  ";
		}

		if (tagid != null && tagid.length > 0) {
			String tagidstr = StringUtil.arrayToString(tagid, ",");
			sql += " and g.goods_id in(select rel_id from es_tag_rel where tag_id in("
					+ tagidstr + "))";
		}
		
		if(market_enable!=null && market_enable >= 0){
			sql+=" and market_enable="+market_enable;
		}
		//System.out.println(sql);
		return sql;
	}
	
	@Override
	public List listByCat(String tagid, String catid,String goodsnum){
		
		StringBuffer sql = new StringBuffer();
		sql.append("select g.* from es_tag_rel r LEFT JOIN es_goods g ON g.goods_id=r.rel_id where g.disabled=0 and g.market_enable=1");
		
		if(!StringUtil.isEmpty(catid) ){
			Cat cat  = this.goodsCatManager.getById(Integer.valueOf(catid));
			if(cat!=null){
				String cat_path  = cat.getCat_path();
				if (cat_path != null) {
					sql.append( " and  g.cat_id in(" ) ;
					sql.append("select c.cat_id from es_goods_cat ");
					sql.append(" c where c.cat_path like '" + cat_path + "%')");
				}
			}
		}
		
		if(!StringUtil.isEmpty(tagid)){
			sql.append(" AND r.tag_id="+tagid+"");
		}
		
		sql.append(" order by r.ordernum desc");
		List list = null;
		if(goodsnum == null || "".equals(goodsnum)){
			list = this.daoSupport.queryForList(sql.toString());
		} else {
			list = this.daoSupport.queryForListPage(sql.toString(), 1,Integer.parseInt(goodsnum));
		}
		this.goodsDataFilterBundle.filterGoodsData(list);
		return list;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IGoodsManager#startChange(java.util.Map)
	 */
	public void startChange(Map goods){
		goodsPluginBundle.onStartchange(goods);
	}
	
	private String getListSql(int disabled) {
		String selectSql = this.goodsPluginBundle.onGetSelector();
		String fromSql = this.goodsPluginBundle.onGetFrom();

		String sql = "select g.*,b.name as brand_name ,t.name as type_name,c.name as cat_name "
				+ selectSql
				+ " from es_goods g left join es_goods_cat"
				+ " c on g.cat_id=c.cat_id left join es_brand"
				+ " b on g.brand_id = b.brand_id and b.disabled=0 left join es_goods_type"
				+ " t on g.type_id =t.type_id "
				+ fromSql
				+ " where g.disabled=" + disabled; // g.goods_type = 'normal' and 
	
		return sql;
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsManager#addPreviewGoods(com.enation.app.shop.core.goods.model.Goods)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addPreviewGoods(Goods goods) {
		try {
			//将po对象中有属性和值转换成map 
			Map goodsMap = po2Map(goods);
			
			// 触发商品添加前事件
			goodsPluginBundle.onBeforeAddPreview(goodsMap);
			
			//商品状态 是否可用
			goodsMap.put("disabled", 0);
			
			//商品创建事件
			goodsMap.put("create_time", DateUtil.getDateline());
			
			//商品浏览次数
			goodsMap.put("view_count", 0);
			
			//商品购买数量
			goodsMap.put("buy_count", 0);
			
			//商品最后更新时间
			goodsMap.put("last_modify", DateUtil.getDateline());
			
			//商品库存
			goodsMap.put("store", 0);
			
			//添加商品
			this.daoSupport.insert("es_goods", goodsMap);
			
			//获取添加商品的商品ID
			Integer goods_id = this.daoSupport.getLastId("es_goods");
			goods.setGoods_id(goods_id);
			goodsMap.put("goods_id", goods_id);
			
			//触发商品保存后事件
			goodsPluginBundle.onAfterAdd(goodsMap);
			
		} catch (RuntimeException e) {
			e.printStackTrace();
			if (e instanceof SnDuplicateException) {
				throw e;
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsManager#editPreviewGoods(com.enation.app.shop.core.goods.model.Goods)
	 */
	@Override
	public void editPreviewGoods(Goods goods) {
		Logger logger = Logger.getLogger(getClass());
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("开始保存商品数据...");
			}
			Map goodsMap = this.po2Map(goods);
			
			Map map = this.po2Map(goods);
			String sql = "select * from es_goods where goods_id=?";
			map=this.daoSupport.queryForMap(sql, goods.getGoods_id());
			
			
			this.goodsPluginBundle.onBeforeEdit(goodsMap);
			this.daoSupport.update("es_goods", goodsMap,"goods_id=" + goods.getGoods_id());

			
			if (logger.isDebugEnabled()) {
				logger.debug("保存商品数据完成.");
			}
			
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
			e.printStackTrace();
		}
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsManager#getCountByGoodsIds(java.lang.Integer[])
	 */
	@Override
	public Integer getCountByGoodsIds(Integer[] goodsId,Integer storeId) {
		
		String id_str = StringUtil.arrayToString(goodsId, ",");
		
		String sql = "select count(1) from es_goods where goods_id in ("+id_str+") and store_id = ?";
		
		return daoSupport.queryForInt(sql, storeId);
	}


	/**
	 * 修改商品商品草稿箱
	 * @param goods 商品
	 * @throws RuntimeException 商品货号重复
	 * @author linkai 2017-2-24 添加注释
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="修改商品名为${goods.name}的商品信息")
	public void editdraft(Goods goods) {
		goods.setLast_modify(DateUtil.getDateline()); //添加商品更新时间
		Logger logger = Logger.getLogger(getClass());
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("开始保存商品数据...");
			}
			Map goodsMap = this.po2Map(goods);
			
			this.daoSupport.update("es_goods", goodsMap,
					"goods_id=" + goods.getGoods_id());
			if (logger.isDebugEnabled()) {
				logger.debug("保存商品数据完成.");
			}
			//标记商品被修改，会从新计算商品价格
			cartManager.changeProduct(goods.getGoods_id());
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
		}
	}

	/**
	 * 添加商品商品草稿箱
	 * @param goods 商品
	 * @throws RuntimeException 商品货号重复
	 * @author linkai 2017-2-24 添加注释
	 * 
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.GOODS,detail="添加了一个商品名为${goods.name}的商品")
	public void adddraft(Goods goods) {
		try {
			//将po对象中有属性和值转换成map 
			Map goodsMap = po2Map(goods);
			
			//商品状态 是否可用
			goodsMap.put("disabled", 0);
			
			//商品创建事件
			goodsMap.put("create_time", DateUtil.getDateline());
			
			//商品浏览次数
			goodsMap.put("view_count", 0);
			
			//商品购买数量
			goodsMap.put("buy_count", 0);
			
			//商品最后更新时间
			goodsMap.put("last_modify", DateUtil.getDateline());
			
			//商品库存
			goodsMap.put("store", 0);
			
			//添加商品
			this.daoSupport.insert("es_goods", goodsMap);
			
			//获取添加商品的商品ID
			Integer goods_id = this.daoSupport.getLastId("es_goods");
			goods.setGoods_id(goods_id);
			goodsMap.put("goods_id", goods_id);
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
		}
               
	}
}
