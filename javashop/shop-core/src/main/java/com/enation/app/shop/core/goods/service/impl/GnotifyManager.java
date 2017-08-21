package com.enation.app.shop.core.goods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.goods.model.Gnotify;
import com.enation.app.shop.core.goods.service.IGnotifyManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;


/**
 * @version v2.0 2016-2-20
 * @author wangxin 6.0升级改造
 */
@Service("gnotifyManager")
public class GnotifyManager  implements IGnotifyManager {
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGnotifyManager#pageGnotify(int, int)
	 */
	@Override
	public Page pageGnotify(int pageNo, int pageSize) {
		Member member = UserConext.getCurrentMember();

		String sql = "select a.*,b.sn, b.thumbnail image,b.store store, b.name name, b.price price, b.mktprice mktprice,b.cat_id cat_id from es_gnotify a inner join es_goods b on b.goods_id = a.goods_id ";

		sql += " and a.member_id = " + member.getMember_id();
		
		sql+=" order by a.create_time desc";
 		Page webpage = this.daoSupport.queryForPage(sql, pageNo, pageSize);
		return webpage;
	}
 
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGnotifyManager#deleteGnotify(int)
	 */
	@Override
	public void deleteGnotify(int gnotify_id) {
		this.daoSupport.execute("delete from es_gnotify where gnotify_id = ?", gnotify_id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGnotifyManager#addGnotify(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void addGnotify(Integer goodsid,Integer productid){
		
		productid= productid==null?0:productid;
		Member member = UserConext.getCurrentMember();
		Gnotify gnotify = new Gnotify();
		gnotify.setCreate_time(System.currentTimeMillis());
		gnotify.setGoods_id(goodsid);
		gnotify.setProduct_id(productid);
		if(member!=null){
			gnotify.setMember_id(member.getMember_id());
			gnotify.setEmail(member.getEmail());
		}
		this.daoSupport.insert("es_gnotify", gnotify);
		
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IGnotifyManager#getGnotifyBymember(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public int getGnotifyBymember(Integer goodsid, Integer productid) {
		
		Member member = UserConext.getCurrentMember();
		String sql ="select * from es_gnotify where goods_id=? and product_id=? and member_id=? ";
		List list  = this.daoSupport.queryForList(sql, goodsid,productid,member.getMember_id());
		
		return list.size();
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.IGnotifyManager#getGnotifyList(int, int)
	 */
	@Override
	public Page getGnotifyList(int pageNo, int pageSize) {
		String sql = "select g.*,count(gnotify_id) as tnum,p.name as goodsname from es_gnotify g "
				+ "left join es_member m on g.member_id = m.member_id "
				+ "left join es_product p on g.product_id = p.product_id "
				+"where m.disabled!=1"
				+ "group by g.product_id ";
		
		List list = this.daoSupport.queryForListPage(sql, pageNo, pageSize);
		
		List t_list = this.daoSupport.queryForList(sql);
		
		Page webpage = new Page(pageNo, t_list.size(), pageSize, list);
		
		return webpage;
	}

}
