package com.enation.app.shop.component.activity.plugin;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderBonus;
import com.enation.app.shop.core.order.plugin.order.IConfirmReceiptEvent;
import com.enation.app.shop.core.order.service.IOrderBonusManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 确认收款发放促销活动赠送优惠券插件
 * @author DMRain
 * @date 2016-6-13
 * @version 1.0
 */
@Component
public class OrderPaySendBonusPlugin extends AutoRegisterPlugin implements IConfirmReceiptEvent{

	@Autowired
	private IOrderBonusManager orderBonusManager;
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IDaoSupport daoSupport;
	
	@Override
	public void confirm(Integer orderid, double price) {
		Order order = this.orderManager.get(orderid);
		
		//如果订单中的优惠券id不等于0或者不为空
		if (order.getBonus_id() != null && order.getBonus_id() != 0) {
			OrderBonus bonus = this.orderBonusManager.getOrderBonus(order.getBonus_id(), orderid);
			
			//如果优惠券的类型为电子券 0：电子券
			if (bonus.getSend_type() == 0) {
				MemberBonus memberBonus = new MemberBonus();
				
				memberBonus.setBonus_type_id(bonus.getBonus_id());
				memberBonus.setMember_id(order.getMember_id());
				memberBonus.setType_name(bonus.getBonus_name());
				memberBonus.setBonus_type(bonus.getSend_type());
				memberBonus.setCreate_time(DateUtil.getDateline());
				memberBonus.setUsed(0);
				
				//如果为b2c项目
				if (EopSetting.PRODUCT.equals("b2c")) {
					memberBonus.setMember_name(this.memberManager.get(order.getMember_id()).getUname() 
							+ "[" + this.memberManager.get(order.getMember_id()).getUname() + "]");
					this.orderBonusManager.send_bonus(memberBonus);
				} else {
					while (true) {
						String sn = this.createSn(bonus.getBonus_id() + "");
						int c = this.daoSupport.queryForInt("select count(0) from es_member_bonus where bonus_sn = ?", sn);
						
						//如果优惠券sn码不重复
						if (c == 0) {
							memberBonus.setBonus_sn(sn);
							this.orderBonusManager.send_bonus(memberBonus);
							return;
						} else {
							System.out.println("有相同的sn码,再生成一个sn码");
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * 随机生成一个优惠券编号
	 * @param prefix
	 * @return
	 */
	private String createSn(String prefix){
		
		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append( DateUtil.toString(new Date(), "yyMM"));
		sb.append( createRandom() );
		
		return sb.toString();
	}
	
	/**
	 * 生成一个随机数
	 * @return
	 */
	private String createRandom(){
		Random random  = new Random();
		StringBuffer pwd=new StringBuffer();
		for(int i=0;i<6;i++){
			pwd.append(random.nextInt(9));
			 
		}
		return pwd.toString();
	}

}
