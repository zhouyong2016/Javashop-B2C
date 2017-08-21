package com.enation.app.shop.core.member.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.model.Favorite;
import com.enation.app.shop.core.member.service.IFavoriteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;

/**
 * 我的收藏
 * 
 * @author lzf<br/>
 *         2010-3-24 下午02:54:26<br/>
 *         version 1.0<br/>
 *         
 * @version v1.1 2015-09-14
 * @author wangxin 6.0升级改造
 */
@Service("favoriteManager")
public class FavoriteManager implements IFavoriteManager {
	
	@Autowired
	private IDaoSupport  daoSupport;	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#list(int, int)
	 */
	@Override
	public Page list(int pageNo, int pageSize) {
		Member member = UserConext.getCurrentMember();
		String sql = "select g.*, f.favorite_id from es_favorite"
				+ " f left join es_goods"
				+ " g on g.goods_id = f.goods_id";
		sql += " where f.member_id = ?";
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize, member
				.getMember_id());
		return page;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#list(int, int, int)
	 */
	@Override
	public Page list(int memberid, int pageNo, int pageSize) {
		String sql = "select g.*, f.favorite_id from es_favorite"
				+ " f left join es_goods"
				+ " g on g.goods_id = f.goods_id";
		sql += " where f.member_id = ? order by f.favorite_time desc";	//改为根据收藏时间倒序排序，最新收藏的显示在前面	修改人：DMRain 2015-12-16
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize,memberid);
		return page;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#delete(int)
	 */
	@Override
	public void delete(int favorite_id) {
		this.daoSupport.execute("delete from es_favorite where favorite_id = ?", favorite_id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#add(java.lang.Integer)
	 */
	@Override
	public void add(Integer goodsid) {
		Member member = UserConext.getCurrentMember();
		Favorite favorite = new Favorite();
		favorite.setGoods_id(goodsid);
		favorite.setMember_id(member.getMember_id());
		
		//添加收藏时间	添加人：DMRain 2015-12-16
		favorite.setFavorite_time(DateUtil.getDateline());
		
		this.daoSupport.insert("es_favorite", favorite);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#getCount(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public int getCount(Integer goodsid, Integer memeberid){
		return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_favorite WHERE goods_id=? AND member_id=?", goodsid,memeberid);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#getCount(java.lang.Integer)
	 */
	@Override
	public int getCount(Integer memberId){
		return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_favorite WHERE  member_id=?", memberId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#list()
	 */
	@Override
	public List list( ) {
		Member member = UserConext.getCurrentMember();
		
		return this.daoSupport.queryForList("select * from es_favorite where member_id=?", Favorite.class, member.getMember_id());
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#get(int)
	 */
	@Override
    public Favorite get(int favorite_id) {
        String sql = "SELECT * FROM es_favorite WHERE favorite_id=?";
        return (Favorite) daoSupport.queryForObject(sql, Favorite.class, favorite_id);
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#delete(int, int)
	 */
	@Override
    public void delete(int goodsid, int memberid){
		daoSupport.execute("DELETE FROM es_favorite WHERE goods_id=? AND member_id=?", goodsid, memberid);
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#get(int, int)
	 */
	@Override
    public Favorite get(int goodsid, int memberid){
        String sql = "SELECT * FROM es_favorite WHERE goods_id=? AND member_id=?";
        List<Favorite> favoriteList = daoSupport.queryForList(sql, Favorite.class, goodsid, memberid);
        if(favoriteList.size() > 0){
            return favoriteList.get(0);
        }
        return null;
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#isFavorited(int, int)
	 */
	@Override
    public boolean isFavorited(int goodsid, int memeberid){
        return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_favorite WHERE goods_id=? AND member_id=?", goodsid,memeberid) > 0;
    }

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#listGoods()
	 */
	@Override
	public List listGoods(){
		Member member = UserConext.getCurrentMember();
		String sql = "select g.*, f.favorite_id from es_favorite"
				+ " f left join es_goods"
				+ " g on g.goods_id = f.goods_id";
		sql += " where f.member_id = ? order by f.favorite_id";
		return this.daoSupport.queryForList(sql,  member.getMember_id());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IFavoriteManager#listGoods()
	 */
	@Override
	public Page listGoods(int memberid,int pageNo, int pageSize){
		Member member = UserConext.getCurrentMember();
		String sql = "select g.*, f.favorite_id from es_favorite"
				+ " f left join es_goods"
				+ " g on g.goods_id = f.goods_id";
		sql += " where f.member_id = ? order by f.favorite_time desc";	
		Page page = this.daoSupport.queryForPage(sql,pageNo,pageSize,memberid);
		return page;
	}
	
}
