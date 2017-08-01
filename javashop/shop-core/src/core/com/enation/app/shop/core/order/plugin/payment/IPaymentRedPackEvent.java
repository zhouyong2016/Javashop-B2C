package com.enation.app.shop.core.order.plugin.payment;

import java.util.Map;

import com.enation.app.shop.core.order.model.PayCfg;

/**
 * 微信发放红包事件接口
 * @author xulipeng
 *
 */
public interface IPaymentRedPackEvent {
	
	/**
	 * 发放红包
	 * @param member	会员
	 * @param money		金额
	 * @param paycfg	
	 * @param other		其他信息
	 * 
	 * @return
	 */
	public String sendRedPack(Map member,Double money,PayCfg payCfg,Map other);

}
