package com.enation.app.shop.core.goods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.goods.plugin.GoodsStorePluginBundle;
import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.app.shop.core.goods.service.IGoodsWarningStoreManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;
/**
 * 
 * (商品预警库存管理) 
 * @author zjp
 * @version v1.0
 * @since v6.1
 * 2016年12月7日 下午8:54:38
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Service("goodsWarningStoreManager")
public class GoodsWarningStoreManager  implements IGoodsWarningStoreManager {

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
		//获取商品预警数
		String sql_settings = "select s.cfg_value from es_settings s where s.cfg_group = 'inventory'";
		Map settingmap = daoSupport.queryForMap(sql_settings, null);
		String setting_value = (String) settingmap.get("cfg_value");
		JSONObject jsonObject = JSONObject.fromObject( setting_value );  
		Map itemMap = (Map)jsonObject.toBean(jsonObject, Map.class);
		Integer inventory_warning_count = Integer.parseInt((String) itemMap.get("inventory_warning_count"));
		/*
		 * 拼写sql语句查询数据库预警商品数目，先查询库存表中相同productid且大于预警数的商品数目，如果数目等于仓库数量，则表示
		 * 该货品不需要预警，查询所有不需要预警的货品，与货品表进行比对，在货品表中而不再商品库存表中的货品为需要预警的商品
		 */
		StringBuffer sql = new StringBuffer();
		sql.append("select g.goods_id,g.sn,g.name,c.name cname "+
					" from (select distinct p.goods_id "+
					" from es_product p "+
					" where p.product_id  not in (select productid "+
					" from (select productid,count(*) count "+
					" from es_product_store "+
            		" where enable_store> ? "+
            		" group by productid) tem "+
            		" where count=(select count(*) "+
            		" from es_depot))) tem,es_goods g,es_goods_cat c "+
            		" where g.cat_id=c.cat_id "+	
            		" and g.goods_id=tem.goods_id "+
            		" and g.market_enable=1 "+
					" and g.disabled=0 ");
		
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
		String countsql = "select count(*) from ("+sql+") temp_table";
		
		if(!StringUtil.isEmpty(sort)){
			sql.append("order by g."+sort+" "+order);
		}
		
		Page webPage = this.daoSupport.queryForPage(sql.toString(), countsql, page, pageSize,inventory_warning_count);
		return webPage;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsWarningStoreManager#getStoreHtml(java.lang.Integer)
	 */
	@Override
	public String getWarningStoreHtml(Integer goodsid) {
		Map goods = this.getGoods(goodsid);
		return this.goodsStorePluginBundle.getWarningStoreHtml(goods);
	}
	/*
	 * 商品查询
	 */
	private Map getGoods(int goodsid) {
		String sql = "select * from es_goods  where goods_id=?";
		Map goods = this.daoSupport.queryForMap(sql, goodsid);
		return goods;
	}
}
