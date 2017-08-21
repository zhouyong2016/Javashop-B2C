package com.enation.app.shop.core.goods.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.service.IGoodsSnapshotManager;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.database.IDaoSupport;


/**
 * 
 * (商品快照管理) 
 * @author zjp
 * @version v1.0
 * @since v6.2
 * 2016年12月12日 上午2:25:56
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("goodsSnapshotManager")
public class GoodsSnapshotManager  implements IGoodsSnapshotManager{
		
	@Autowired
	private IDaoSupport daoSupport;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsManager#get(java.lang.Integer)
	 */
	@Override
	public Map get(Integer snapshot_id) {
		//判断此商品是否存在 
		String sql = "select count(0) from es_goods_snapshot where snapshot_id = ?";
		int result = this.daoSupport.queryForInt(sql, snapshot_id);
		
		Map goods = new HashMap();
		
		if (result != 0) {
			sql = "select g.*,b.name as brand_name from es_goods_snapshot g left join es_brand b on g.brand_id=b.brand_id ";
			sql += "  where snapshot_id=?";
			goods = this.daoSupport.queryForMap(sql, snapshot_id);

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
}
