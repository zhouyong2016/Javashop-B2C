package com.enation.app.shop.core.goods.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.enation.app.shop.core.goods.model.Tag;
import com.enation.app.shop.core.goods.service.ITagManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;

/**
 * Saas式的标签管理
 * @author kingapex
 * 2010-1-18上午10:57:35
 * @author wangxin maven改造  2016-2-16
 */
@Service("tagManager")
public class TagManager implements ITagManager {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.ITagManager#checkname(java.lang.String, java.lang.Integer)
	 */
	@Override
	public boolean checkname(String name,Integer tagid){
		if(name!=null){
			name=name.trim();
		}
		if(tagid==null){
			tagid=0;
		}
		String sql ="select count(0) from es_tags where tag_name=? and tag_id!=?";
		int count  = this.daoSupport.queryForInt(sql, name,tagid);
		if(count>0){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.ITagManager#add(com.enation.app.shop.core.goods.model.Tag)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="添加了一个${tag.tag_name}的标签")
	public void add(Tag tag) {
		tag.setRel_count(0);
		this.daoSupport.insert("es_tags", tag);
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.ITagManager#checkJoinGoods(java.lang.Integer[])
	 */
	@Override
	public boolean checkJoinGoods(Integer[] tagids) {
		if(tagids==null ) return false;
		String ids =StringUtil.implode(",", tagids);
		String sql ="select count(0)  from es_tag_rel where tag_id in("+ids+")";	 
		int count  = this.daoSupport.queryForInt(sql);
		if(count>0)
			return true;
		else
			return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.ITagManager#delete(java.lang.Integer[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	@Log(type=LogType.GOODS,detail="删除一个的标签")
	public void delete(Integer[] tagids) {
		String ids =StringUtil.implode(",", tagids);
		if(ids==null || ids.equals("")){return ;}
		//删除标签，同时删除标签的引用对照表
		this.daoSupport.execute("delete from es_tags where tag_id in("+ids+")");
		this.daoSupport.execute("delete from es_tag_rel where tag_id in("+ids+")");
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ITagManager#list(int, int)
	 */
	public Page list(int pageNo, int pageSize) {
		return this.daoSupport.queryForPage("select * from es_tags order by tag_id", pageNo, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ITagManager#list(int, int, int)
	 */
	public Page list(int pageNo, int pageSize,int type) {
		return this.daoSupport.queryForPage("select * from es_tags where type = ? order by tag_id ", pageNo, pageSize,type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.ITagManager#update(com.enation.app.shop.core.goods.model.Tag)
	 */
	@Override
	@Log(type=LogType.GOODS,detail="修改${tag.tag_name}的标签")
	public void update(Tag tag) {
		this.daoSupport.update("es_tags", tag, "tag_id="+tag.getTag_id());
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.ITagManager#getById(java.lang.Integer)
	 */
	@Override
	public Tag getById(Integer tagid) {
		String sql  ="select * from es_tags where tag_id=?";
		Tag tag = (Tag) this.daoSupport.queryForObject(sql, Tag.class, tagid);
		return tag;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.ITagManager#list()
	 */
	@Override
	public List<Tag> list() {
		String sql  ="select * from es_tags ";
		return this.daoSupport.queryForList(sql,Tag.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.ITagManager#saveRels(java.lang.Integer, java.lang.Integer[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void saveRels(Integer relid, Integer[] tagids) {
		//清空原有引用
		String sql = "delete from es_tag_rel where rel_id=?";
		this.daoSupport.execute(sql, relid);
		if(tagids!=null ){
			
			//重新对照新的引用
			sql ="insert into es_tag_rel (tag_id,rel_id)values(?,?)";
			for(Integer tagid:tagids){
				this.daoSupport.execute(sql, tagid,relid);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.ITagManager#list(java.lang.Integer)
	 */
	@Override
	public List<Integer> list(Integer relid) {
		String sql= "select tag_id from es_tag_rel where rel_id="+relid;
		List tagids = this.daoSupport.queryForList(sql);
		return tagids;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.goods.service.ITagManager#listMap()
	 */
	@Override
	public List<Map> listMap() {
		String sql  ="select * from es_tags ";
		return this.daoSupport.queryForList(sql);
	}

	
	 
}
