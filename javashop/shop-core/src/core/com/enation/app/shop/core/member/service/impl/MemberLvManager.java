package com.enation.app.shop.core.member.service.impl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;
 

/**
 * 会员级别管理
 * @author kingapex
 *2010-4-29下午11:04:43
 * @author LiFenLong 2014-4-1; 4.0版本改造,delete方法参数String 更改为integer[]
 * @author wangxin 2016-2-18 6.0版本升级
 */
@Service("memberLvDbManager")
public class MemberLvManager implements IMemberLvManager{
	
	@Autowired
	private IDaoSupport  daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IMemberLvManager#add(com.enation.app.base.core.model.MemberLv)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@Log(type=LogType.MEMBER,detail="添加了一个${lv.name}会员等级")
	public void add(MemberLv lv) {
		if(lv.getDefault_lv()==1){
			this.daoSupport.execute("update es_member_lv set default_lv=0");
		}
		this.daoSupport.insert("es_member_lv", lv);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#edit(com.enation.app.base.core.model.MemberLv)
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="修改了一个${lv.name}的会员等级")
	public void edit(MemberLv lv) {
		
		if(lv.getDefault_lv()==1){
			this.daoSupport.execute("update es_member_lv set default_lv=0");
		}
		this.daoSupport.update("es_member_lv", lv, "lv_id=" + lv.getLv_id());
		// 如果没有默认等级 则设置刚刚编辑的会员等级为默认
		if(this.daoSupport.queryForInt("select count(0) from es_member_lv where default_lv = 1")==0){
			this.daoSupport.execute("update es_member_lv set default_lv = 1 where lv_id = ?", lv.getLv_id());
		}
		
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#getDefaultLv()
	 */
	@Override
	public Integer getDefaultLv() {
		String sql  ="select * from es_member_lv where default_lv=1";
		List<MemberLv> lvList  = this.daoSupport.queryForList(sql, MemberLv.class);
		if(lvList==null || lvList.isEmpty()){
			return null;
		}
		
		return lvList.get(0).getLv_id();
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#list(java.lang.String, int, int)
	 */
	@Override
	public Page list(String order, int page, int pageSize) {
		order = order == null ? " lv_id desc" : order;
		String sql = "select * from es_member_lv ";
		sql += " order by  " + order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#get(java.lang.Integer)
	 */
	@Override
	public MemberLv get(Integer lvId) {
		if(lvId!=null&&lvId!=0){
			String sql = "select * from es_member_lv where lv_id=?";
			MemberLv lv = this.daoSupport.queryForObject(sql, MemberLv.class,lvId);
			return lv;
		}else{
			return null;
		}
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#delete(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="删除了一个会员等级")
	public void delete(Integer[] id) {
		if (id == null){
			return;
		}
		
		String id_str = StringUtil.arrayToString(id, ",");
		//判断是否存在默认等级 如果有默认等级则抛出异常
		if(daoSupport.queryForInt("select count(lv_id) from es_member_lv where lv_id in (" + id_str + ") and default_lv=1")>0){
			throw new RuntimeException("会员默认等级不能删除");
		}
		//判断是否是已被会员关联的等级  如果已关联则抛出异常
		if(daoSupport.queryForInt("select count(lv_id) from es_member_lv where lv_id in (" + id_str + ") and  lv_id  in (select lv_id from es_member)")>0){
			throw new RuntimeException("已关联等级不能删除");
	    }
		int count=daoSupport.queryForInt("select count(lv_id) from es_member_lv");
		if(count==id.length){
			throw new RuntimeException("请至少保留一个会员等级");
		}
		String sql = "delete from es_member_lv where lv_id not in (select lv_id from es_member) and lv_id in (" + id_str + ")and default_lv!=1  ";
		
		this.daoSupport.execute(sql);
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#list()
	 */
	@Override
	public List<MemberLv> list() {
		String sql = "select * from es_member_lv order by lv_id";
		List lvlist = this.daoSupport.queryForList(sql, MemberLv.class);
		return lvlist;
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#list(java.lang.String)
	 */
	@Override
	public List<MemberLv> list(String ids) {
		
		if( StringUtil.isEmpty(ids)) return new ArrayList();
		
		String sql = "select * from es_member_lv where  lv_id in("+ids+") order by lv_id";
		List lvlist = this.daoSupport.queryForList(sql, MemberLv.class);
		return lvlist;
		 
	}





	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#getByPoint(int)
	 */
	@Override
	public MemberLv getByPoint(int point) {
		String sql = "select * from es_member_lv where  point<=? order by point desc";
		List<MemberLv> list =this.daoSupport.queryForList(sql, MemberLv.class,point);
		if(list==null || list.isEmpty())
		return null;
		else
			return list.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#getNextLv(int)
	 */
	@Override
	public MemberLv getNextLv(int point){
		String sql = "select * from es_member_lv where  point>? order by point ASC";
		List<MemberLv> list =this.daoSupport.queryForList(sql, MemberLv.class,point);
		if(list==null || list.isEmpty())
			return null;
		else
			return list.get(0);
	}
	
	/**
	 * 根据等级获取商品分类折扣列表
	 * @param lv_id
	 * @return
	 */

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#getCatDiscountByLv(int)
	 */
	@Override
	public List getCatDiscountByLv(int lv_id){
		String sql= "select d.*,c.name from es_member_lv_discount d inner join es_goods_cat c on d.cat_id=c.cat_id where d.lv_id="+lv_id;
		return this.daoSupport.queryForList(sql);
	}
	
	/**
	 * 根据等级获取该等级有折扣的类别列表
	 * @param lv_id
	 * @return
	 */

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#getHaveCatDiscountByLv(int)
	 */
	@Override
	public List getHaveCatDiscountByLv(int lv_id){
		String sql = "select * from es_member_lv_discount where discount>0 and lv_id="+lv_id;
		return this.daoSupport.queryForList(sql);
	}
	
	/**
	 * 保存某等级的商品类别对应折扣
	 * @param cat_ids 与 discount是一一对应关系
	 * @param discounts 与catids是一一对应关系
	 * @param lv_id
	 */

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#saveCatDiscountByLv(int[], int[], int)
	 */
	@Override
	public void saveCatDiscountByLv(int[] cat_ids,int[] discounts,int lv_id){
		if(cat_ids.length!=discounts.length){
			throw new RuntimeException("非法参数");
		}
		for (int i = 0; i < discounts.length; i++) {
			if(discounts[i]==0)
				continue;
			String sql = "update es_member_lv_discount set discount="+discounts[i]+" where cat_id="+cat_ids[i]+" and lv_id="+lv_id;
//			//System.out.println(sql);
			this.daoSupport.execute(sql);
		}
		
		
	}
	
	/**
	 * 根据类别获取所有等级的折扣
	 * @param cat_id
	 */

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberLvManager#getHaveLvDiscountByCat(int)
	 */
	@Override
	public List getHaveLvDiscountByCat(int cat_id){
		String sql = "select * from es_member_lv_discount where discount>0 and cat_id="+cat_id;
		return this.daoSupport.queryForList(sql);
	}

}
