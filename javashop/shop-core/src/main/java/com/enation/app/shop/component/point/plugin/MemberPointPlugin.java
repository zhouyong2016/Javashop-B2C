package com.enation.app.shop.component.point.plugin;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.plugin.IMemberEmailCheckEvent;
import com.enation.app.shop.core.member.plugin.IMemberLoginEvent;
import com.enation.app.shop.core.member.plugin.IMemberRegisterEvent;
import com.enation.app.shop.core.member.service.IMemberPointManger;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 会员积分插件
 * @author kingapex
 *
 */
@Component
public class MemberPointPlugin extends AutoRegisterPlugin implements
		IMemberRegisterEvent,IMemberLoginEvent,IMemberEmailCheckEvent {
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Autowired
	private IMemberPointManger memberPointManger;
	
	/**
	 * 响应会员注册事件，送出积分
	 */
	@Override
	public void onRegister(Member member) {
		/**
		 * ------------
		 * 增加会员积分
		 * ------------
		 */
		if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_REGISTER) ){
			int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_REGISTER+"_num");
			int mp =memberPointManger.getItemPoint(IMemberPointManger.TYPE_REGISTER+"_num_mp");
			//添加会员的等级积分和消费积分 dongxin改
			memberPointManger.add(member, point, "成功注册", null,0,0);
			memberPointManger.add(member, 0, "成功注册", null,mp,1);
		}
	
	}
	

	@Override
	public void onEmailCheck(Member member) {
		/**
		 * ------------
		 * 增加会员积分
		 * ------------
		 */
		if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_EMIAL_CHECK) ){
			int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_EMIAL_CHECK+"_num");
			int mp =memberPointManger.getItemPoint(IMemberPointManger.TYPE_EMIAL_CHECK+"_num_mp");
			//添加会员的等级积分和消费积分 dongxin改
			memberPointManger.add(member, point, "完成邮箱验证", null,0,0);
			memberPointManger.add(member, 0, "完成邮箱验证", null,mp,1);
		}
		
	}


	@Override
	public void onLogin(Member member,Long upLogintime) {
		if(upLogintime==null || upLogintime==0)
			upLogintime=member.getLastlogin();
		long ldate = ((long)upLogintime)*1000;
		Date date = new Date(ldate);
		Date today = new Date();
		
		/**
		 * ------------
		 * 增加会员积分(一天只增加一次)
		 * ------------
		 */
		if(!DateUtil.toString(date, "yyyy-MM-dd").equals(DateUtil.toString(today, "yyyy-MM-dd"))){//非今天
			if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_LOGIN) ){
				int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_LOGIN+"_num");
				int mp =memberPointManger.getItemPoint(IMemberPointManger.TYPE_LOGIN+"_num_mp");
				//添加会员的等级积分和消费积分 dongxin改
				memberPointManger.add(member, point, DateUtil.toString(today, "yyyy年MM月dd日")+"登录", null,0,0);
				memberPointManger.add(member, 0, DateUtil.toString(today, "yyyy年MM月dd日")+"登录", null,mp,1);
				
			}
		}
		
	}
	
	
	private boolean isRepeatedIp(String ip,int parentid){
		String sql ="select count(0) from es_member where parentid=? and registerip=?";
		int count =this.daoSupport.queryForInt(sql, parentid,ip);
		return count>1;
	}
	
	


}
