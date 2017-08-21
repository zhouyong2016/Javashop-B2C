package com.enation.app.shop.component.bonus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.component.bonus.model.Bonus;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

public final class BonusSession {
	private static final String list_sessionkey ="bonus_list_session_key"; 
	private static final String one_sessionkey ="bonus_one_session_key";
	
	//不可实例化
	private BonusSession(){
		
	}

	/**
	 * 使用红包
	 * @param bonus
	 */
	public static void use(Bonus bonus){
		
		//如果存在跳过
		if(isExists(bonus)){
			return;
		}
		
		//添加到session
		List<Bonus> bounsList= (List)ThreadContextHolder.getSession().getAttribute(list_sessionkey);
		if(bounsList==null){
			bounsList= new ArrayList<Bonus>();
		}
		bounsList.add(bonus);
		
		ThreadContextHolder.getSession().setAttribute(list_sessionkey, bounsList);
		 
	}

	/**
	 * 用编号取消一个优惠卷的使用
	 * @param sn
	 */
	public static void cancel(String sn){
		
		if(StringUtil.isEmpty(sn)){
			return;
		}
		
		List<Bonus> bounsList= (List)ThreadContextHolder.getSession().getAttribute(list_sessionkey);
		if(bounsList==null){
			return;
		}
		
		List<Bonus> newBounsList = new ArrayList<Bonus>();
		for (Bonus memberBonus : bounsList) {
			if(sn.equals( memberBonus.getBonus_sn() )){
				continue;
			}
			newBounsList.add(memberBonus);
		}
		
		ThreadContextHolder.getSession().setAttribute(list_sessionkey,newBounsList);
		
	}
	
	
	/**
	 * 使用红包
	 * @param bonus
	 */
	public static void useOne(Bonus bonus){
		ThreadContextHolder.getSession().setAttribute(one_sessionkey, bonus);
	}
	
	/**
	 * 获取已使用的优惠券集合
	 * @return
	 */
	public static List<Bonus> get(){
		return  (List)ThreadContextHolder.getSession().getAttribute(list_sessionkey);
	}
	
	/**
	 * 获取只能使用一个的优惠券
	 * @return
	 */
	public static Bonus getOne(){
		return  (Bonus)ThreadContextHolder.getSession().getAttribute(one_sessionkey);
	}
	

	/**
	 * 获取已使用的金额(优惠券+实体券)
	 * @return
	 */
	public static double getUseMoney(){
		List<Bonus> bonusList =get();
		double moneyCount = 0;
		if(bonusList!=null){
			for (Bonus memberBonus : bonusList) {
				double bonusMoney = memberBonus.getType_money(); //红包金额
				moneyCount= CurrencyUtil.add(moneyCount,bonusMoney);//累加所有的红包金额
			}
		}
		Bonus bonus = getOne();
		if(bonus!=null){
			double bonusMoney = bonus.getType_money(); //红包金额
			moneyCount= CurrencyUtil.add(moneyCount,bonusMoney);//累加所有的红包金额
		}
		return  moneyCount;
	}
	
	/**
	 * 清空单一优惠券
	 */
	public static void clean(){
		ThreadContextHolder.getSession().removeAttribute(one_sessionkey);
	}
	
	/**
	 * 清空所有优惠券
	 */
	public static void cleanAll(){
		ThreadContextHolder.getSession().removeAttribute(one_sessionkey);
		ThreadContextHolder.getSession().removeAttribute(list_sessionkey);
	}
	
	/**
	 * 判断是否存在
	 * @param bonus
	 * @return
	 */
	public static boolean isExists(Bonus bonus){
		 List<Bonus> bounsList= (List)ThreadContextHolder.getSession().getAttribute(list_sessionkey);
		 if(bounsList==null) return false;
		 for (Bonus memberBonus : bounsList) {
			if(memberBonus.getBonus_id() == bonus.getBonus_id()){
				return true;
			}
		}
		return false;
	}
	
}
