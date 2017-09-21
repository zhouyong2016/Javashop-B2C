package com.enation.app.shop.core.member.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.core.member.model.PointHistory;
import com.enation.app.shop.core.member.service.IMemberLvManager;
import com.enation.app.shop.core.member.service.IMemberPointManger;
import com.enation.app.shop.core.member.service.IPointHistoryManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员积分管理
 * @author kingapex
 *
 *	update by chopper 
 *	spring mvc 6.0 改造
 *	@date 2015-03－01
 */
@Service("memberPointManger")
public class MemberPointManger   implements IMemberPointManger {
	@Autowired
	private IPointHistoryManager pointHistoryManager;
	@Autowired
	private IMemberManager memberManager;
	@Autowired
	private IMemberLvManager memberLvManager;
	@Autowired
	private ISettingService settingService ;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IDaoSupport  daoSupport;
	
	/*
	 *    (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberPointManger#checkIsOpen(java.lang.String)
	 */
	@Override
	public boolean checkIsOpen(String itemname) {
		String value = settingService.getSetting("point", itemname);
		
		return "1".equals(value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberPointManger#getItemPoint(java.lang.String)
	 */
	@Override
	public int getItemPoint(String itemname) {
		String value = settingService.getSetting("point", itemname);
		if(StringUtil.isEmpty(value)){
			return 0;
		}
		return Integer.valueOf(value);
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberPointManger#add(int, int, java.lang.String, java.lang.Integer, int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void add(Member member, int point, String itemname,Integer relatedId, int mp,int pointType) {
		PointHistory pointHistory = new  PointHistory();
		pointHistory.setMember_id(member.getMember_id());
		pointHistory.setOperator("member");
		pointHistory.setPoint(point);
		pointHistory.setReason(itemname);
		pointHistory.setType(1);
		pointHistory.setTime(DateUtil.getDateline());
		pointHistory.setRelated_id(relatedId);
		pointHistory.setMp(mp);
		pointHistory.setPoint_type(pointType);
		
		pointHistoryManager.addPointHistory(pointHistory);
		 
		Integer memberpoint  = member.getPoint();
		
		this.daoSupport.execute("update es_member set point=point+?,mp=mp+? where member_id=?", point, mp, member.getMember_id()); 
		
		//改变会员等级
		if(memberpoint!=null ){
			MemberLv lv =  this.memberLvManager.getByPoint(memberpoint + point);
			if(lv!=null ){
				if((member.getLv_id()==null || lv.getLv_id().intValue()>member.getLv_id().intValue() || lv.getLv_id().intValue()<member.getLv_id().intValue())){
					this.memberManager.updateLv(member.getMember_id(), lv.getLv_id());
				} 
			}
		}
		
	}
	 
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberPointManger#useMarketPoint(int, int, java.lang.String, java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void useMarketPoint(int memberid,int point,String reson,Integer relatedId){
		PointHistory pointHistory = new  PointHistory();
		pointHistory.setMember_id(memberid);
		pointHistory.setOperator("member");
		pointHistory.setPoint(point);
		pointHistory.setReason(reson);
		pointHistory.setType(0);
		pointHistory.setPoint_type(1);
		pointHistory.setTime(DateUtil.getDateline());
		pointHistory.setRelated_id(relatedId);
		
		pointHistoryManager.addPointHistory(pointHistory);
		this.daoSupport.execute("update es_member set mp=mp-? where member_id=?", point,memberid); 
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberPointManger#pointToPrice(int)
	 */
	@Override
	public Double pointToPrice(int point) {
		
		return Double.valueOf(point);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberPointManger#priceToPoint(java.lang.Double)
	 */
	@Override
	public int priceToPoint(Double price) {
		if(price == null) return 0;
		return price.intValue();
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberPointManger#addFreezePoint(java.util.Map)
	 */
	@Override
	public void addFreezePoint(Map map){
		this.daoSupport.insert("es_freeze_point", map);
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.member.service.IMemberPointManger#delFreezePoint(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void delFreezePoint(Integer member_id, Integer order_id) {
		this.daoSupport.execute("delete from es_freeze_point where memberid = ? and orderid = ?", member_id, order_id);
		
	}
	

}
