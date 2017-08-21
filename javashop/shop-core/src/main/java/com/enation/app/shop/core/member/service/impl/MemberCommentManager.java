package com.enation.app.shop.core.member.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.shop.core.goods.plugin.GoodsCommentsBundle;
import com.enation.app.shop.core.member.model.MemberComment;
import com.enation.app.shop.core.member.service.IMemberCommentManager;
import com.enation.framework.annotation.Log;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.log.LogType;
import com.enation.framework.util.StringUtil;
/**
 * 
 * @author LiFenLong 2014-4-1;4.0版本改造，修改delete方法
 * @author wangxin  6.0升级 改造
 */
@Service("memberCommentManager")
public class MemberCommentManager  implements IMemberCommentManager{
    
	
	@Autowired
	private IDaoSupport  daoSupport;
	
	@Autowired
	private GoodsCommentsBundle goodsCommentsBundle;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#add(com.enation.app.shop.core.member.model.MemberComment)
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="商品ID为${memberComment.goods_id}的商品评论或咨询}")
	public void add(MemberComment memberComment) {
		
		//触发增加评论前事件
		this.goodsCommentsBundle.onGoodsCommentsBeforeAdd(memberComment);
		this.daoSupport.insert("es_member_comment", memberComment);
		
		//获取新添加的评论或咨询ID添加到评论或咨询信息中 add by DMRain 2016-5-9
		memberComment.setComment_id(this.daoSupport.getLastId("es_member_comment"));
		
		//触发增加评论后事件
		this.goodsCommentsBundle.onGoodsCommentsAfterAdd(memberComment);

	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getGoodsComments(int, int, int, int, java.lang.Integer)
	 */
	@Override
	public Page getGoodsComments(int goods_id, int page, int pageSize, int type, Integer grade) {
		String sql = "SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM es_member_comment" + " c LEFT JOIN es_member" + " m ON c.member_id=m.member_id LEFT JOIN es_member_lv" + " l ON m.lv_id=l.lv_id " +
				"WHERE c.goods_id=? AND c.type=? AND c.status=1 AND m.disabled!=1";
		if(grade != null){
			sql += " and c.grade = "+grade+"";
		}
		sql += " ORDER BY c.comment_id DESC";
		return this.daoSupport.queryForPage(sql, page, pageSize, goods_id, type);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getGoodsComments(int, int, int, int)
	 */
	@Override
	public Page getGoodsComments(int goods_id, int page, int pageSize, int type) {
		return this.daoSupport.queryForPage("SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM es_member_comment" + " c LEFT JOIN es_member" + " m ON c.member_id=m.member_id LEFT JOIN es_member_lv" + " l ON m.lv_id=l.lv_id " +
				"WHERE c.goods_id=? AND c.type=? AND c.status=1 AND m.disabled!=1 ORDER BY c.comment_id DESC", page, pageSize, goods_id, type);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getGoodsGrade(int)
	 */
	@Override
	public int getGoodsGrade(int goods_id){
		int sumGrade = this.daoSupport.queryForInt("SELECT SUM(grade) FROM es_member_comment WHERE status=1 AND goods_id=? AND type=1", goods_id);
		int total = this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_member_comment WHERE status=1 AND goods_id=? AND type=1", goods_id);
		if(sumGrade != 0 && total != 0){
			return (sumGrade/total);
		}else{
			return 0;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getAllComments(int, int, int)
	 */
	@Override
	public Page getAllComments(int page, int pageSize, int type){
		String sql="SELECT m.uname muname,g.name gname,c.* FROM es_member_comment" + " c LEFT JOIN es_goods" + " g ON c.goods_id=g.goods_id LEFT JOIN es_member" + " m ON c.member_id=m.member_id " +
		"WHERE m.disabled!=1 and c.type=? ORDER BY c.comment_id DESC";
		return this.daoSupport.queryForPage(sql, page, pageSize, type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#get(int)
	 */
	@Override
	public MemberComment get(int comment_id) {
		return this.daoSupport.queryForObject("select * from es_member_comment where comment_id = ?", MemberComment.class, comment_id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#update(com.enation.app.shop.core.member.model.MemberComment)
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="审核商品ID为${memberComment.goods_id}的评论或咨询")
	public void update(MemberComment memberComment) {
		this.daoSupport.update("es_member_comment", memberComment, "comment_id="+memberComment.getComment_id());
		if(memberComment.getStatus()==1){
			String updatesql = "update es_goods set grade=grade+1 where goods_id=?";
			this.daoSupport.execute(updatesql,memberComment.getGoods_id());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#statistics(int)
	 */
	@Override
	public Map statistics(int goodsid){
		String sql="select grade,count(grade) num from es_member_comment c where c.goods_id =? GROUP BY c.grade ";
		List<Map> gradeList =this.daoSupport.queryForList(sql, goodsid);
		Map gradeMap  = new HashMap();
		for(Map grade:gradeList){
			Object gradeValue =grade.get("grade");
			long num =Long.parseLong(grade.get("num").toString().trim());
			gradeMap.put("grade_"+gradeValue, num);
		}
		return gradeMap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getGoodsCommentsCount(int)
	 */
	@Override
	public int getGoodsCommentsCount(int goods_id) {
		return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_member_comment WHERE goods_id=? AND status=1 AND type=1", goods_id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#delete(java.lang.Integer[])
	 */
	@Override
	@Log(type=LogType.MEMBER,detail="删除评论或咨询")
	public void delete(Integer[] comment_id) {
		if (comment_id== null ){
			return;
		}
		
		String id_str = StringUtil.arrayToString(comment_id, ",");
		String sql = "DELETE FROM es_member_comment where comment_id in (" + id_str + ")";
		this.daoSupport.execute(sql);
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getMemberComments(int, int, int, int)
	 */
	@Override
	public Page getMemberComments(int page, int pageSize, int type,
			int member_id) {
		return this.daoSupport.queryForPage("SELECT g.name,g.cat_id, c.* FROM es_member_comment c LEFT JOIN es_goods g ON c.goods_id=g.goods_id " +
				"WHERE c.type=? AND c.member_id=? ORDER BY c.comment_id DESC", page, pageSize, type, member_id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getMemberCommentTotal(int, int)
	 */
	@Override
	public int getMemberCommentTotal(int member_id, int type) {
		return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_member_comment WHERE member_id=? AND type=?", member_id, type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getCommentsByStatus(int, int, int, int)
	 */
	@Override
	public Page getCommentsByStatus(int page, int pageSize, int type, int status) {
		return this.daoSupport.queryForPage("SELECT m.uname,g.name,c.* FROM es_member_comment" + " c LEFT JOIN es_goods" + " g ON c.goods_id=g.goods_id LEFT JOIN es_member" + " m ON c.member_id=m.member_id " +
				"WHERE m.disabled!=1 and c.type=? and c.status = ? ORDER BY c.comment_id DESC", page, pageSize, type,status);
	}

	/**
	 * @author LiFenLong
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#deletealone(int)
	 */
	@Override
	public void deletealone(int comment_id) {
		
		this.daoSupport.execute("DELETE FROM es_member_comment WHERE comment_id=?", comment_id);
	}
	
	/**
	 * 根据商品id获取商品评论总数
	 * @param goods_id
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getCommentsCount(int)
	 */
	@Override
	public int getCommentsCount(int goods_id) {
        return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_member_comment WHERE goods_id=? AND status=1 AND type=1", goods_id);
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getCommentsCount(int, int)
	 */
	@Override
    public int getCommentsCount(int goods_id, int grade){
        return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_member_comment WHERE goods_id=? AND status=1 AND type=1 AND grade >= ?", goods_id, grade);
    }

	/**
	 * 根据评分、商品ID或者各个评论的数量
	 * @param count  数量
	 * @param goods_id  商品ID
	 * @param count    评论数量
	 * whj 2015-10-15
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getCommentsGradeCount(int, int)
	 */
	@Override
	public int getCommentsGradeCount(int goods_id, int grade){
        return this.daoSupport.queryForInt("SELECT COUNT(0) FROM es_member_comment WHERE goods_id=? AND status=1 AND type=1 AND grade=?", goods_id, grade);
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberCommentManager#getGoodsCommentsGradeList(int, int, int, int, int)
	 */
	@Override
	public Page getGoodsCommentsGradeList(int goods_id, int page, int pageSize, int type, int grade) {
		return this.daoSupport.queryForPage("SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM es_member_comment" + " c LEFT JOIN es_member" + " m ON c.member_id=m.member_id LEFT JOIN es_member_lv" + " l ON m.lv_id=l.lv_id " +
				"WHERE m.disabled!=1 AND c.goods_id=? AND c.type=? AND c.grade=? AND c.status=1 ORDER BY c.comment_id DESC", page, pageSize, goods_id, type, grade);
	}
	
	@Override
	public List getCommentGallery(Integer comment_id) {
		String sql = "select * from es_member_comment_gallery where comment_id = ?";
		return this.daoSupport.queryForList(sql, comment_id);
	}

}
