package com.enation.app.shop.core.goods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.model.Cat;
import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.service.IGoodsCatManager;
import com.enation.app.shop.core.goods.service.IGoodsTagManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
/**
 * 商品标签设置管理
 * @author Kanon
 * @author Kanon 2016-2-22 ;6.0版本改造
 */
@Service("goodsTagManager")
public class GoodsTagManager  implements IGoodsTagManager {
	@Autowired
	private IGoodsCatManager goodsCatManager;

	@Autowired
	private IDaoSupport  daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTagManager#getGoodsList(int, int, int)
	 */
	@Override
	public Page getGoodsList(int tagid, int page, int pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append("select g.goods_id,g.name,r.tag_id,r.ordernum from es_tag_rel  r LEFT JOIN es_goods g ON g.goods_id=r.rel_id where g.disabled=0 and g.market_enable=1");
		sql.append(" and r.tag_id = ?");
		sql.append(" order by r.ordernum desc");
		Page webpage = this.daoSupport.queryForPage(sql.toString(), page,
				pageSize, tagid);
		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTagManager#getGoodsList(int, int, int, int)
	 */
	@Override
	public Page getGoodsList(int tagid, int catid, int page, int pageSize) {
		Cat cat = this.goodsCatManager.getById(Integer.valueOf(catid));
		if (cat == null) {
			return null;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select g.goods_id,g.name,r.tag_id,r.ordernum from es_tag_rel r LEFT JOIN es_goods g ON g.goods_id=r.rel_id where g.disabled=0 and g.market_enable=1");
		sql.append(" and r.tag_id = ?");

		sql.append(" and  g.cat_id in(");
		sql.append("select c.cat_id from es_goods_cat");
		sql.append(" c where c.cat_path like '" + cat.getCat_path() + "%')");

		sql.append(" order by r.ordernum desc");
		
		Page webpage = this.daoSupport.queryForPage(sql.toString(), page,pageSize, tagid);
		return webpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTagManager#addTag(int, int)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="批量添加标签")
	public void addTag(int tagId, int goodsId) {
		if (this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_tag_rel WHERE tag_id=? AND rel_id=?",tagId, goodsId) <= 0) {
			this.daoSupport.execute("INSERT INTO es_tag_rel(tag_id,rel_id,ordernum) VALUES(?,?,0)",tagId, goodsId);

		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTagManager#removeTag(int, int)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="删除一个标签ID为${tagId}的标签")
	public void removeTag(int tagId, int goodsId) {
		this.daoSupport.execute("DELETE FROM es_tag_rel WHERE tag_id=? AND rel_id=?", tagId,goodsId);

	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTagManager#updateOrderNum(java.lang.Integer[], java.lang.Integer[], java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.GOODS,detail="批量更新排序数字")
	public void updateOrderNum(Integer[] goodsIds, Integer[] tagids,
			Integer[] ordernum) {
		if (goodsIds != null && goodsIds.length > 0) {
			for (int i = 0; i < goodsIds.length; i++) {
				if (goodsIds[i] != null && tagids[i] != null
						&& ordernum[i] != null) {
					try {
						this.daoSupport.execute("UPDATE es_tag_rel set ordernum=? WHERE tag_id=? AND rel_id=?",ordernum[i], tagids[i], goodsIds[i]);
					} catch (Exception ex) {
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGoodsTagManager#getGoodsList(int)
	 */
	@Override
	public List getGoodsList(int tagid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select g.goods_id,g.name,r.tag_id,r.ordernum,g.thumbnail,g.price from es_tag_rel r LEFT JOIN es_goods g ON g.goods_id=r.rel_id where g.disabled=0 and g.market_enable=1");
		sql.append(" and r.tag_id = ?");
		sql.append(" order by r.ordernum desc");
		List list = this.daoSupport.queryForList(sql.toString(), tagid);
		return list;
	}

}
