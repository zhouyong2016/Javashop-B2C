package com.enation.app.shop.core.member.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * 商品收藏
 * 
 * @author lzf<br/>
 *         2010-3-24 下午02:38:48<br/>
 *         version 1.0<br/>
 */
public class Favorite implements java.io.Serializable {
	private int favorite_id;
	private int member_id;
	private int goods_id;
	
	/**
	 * 收藏时间
	 * 添加人：DMRain 2015-12-16
	 */
	private long favorite_time;

	@PrimaryKeyField
	public int getFavorite_id() {
		return favorite_id;
	}

	public void setFavorite_id(int favoriteId) {
		favorite_id = favoriteId;
	}

	public int getMember_id() {
		return member_id;
	}

	public void setMember_id(int memberId) {
		member_id = memberId;
	}

	public int getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(int goodsId) {
		goods_id = goodsId;
	}

	public long getFavorite_time() {
		return favorite_time;
	}

	public void setFavorite_time(long favorite_time) {
		this.favorite_time = favorite_time;
	}
}
