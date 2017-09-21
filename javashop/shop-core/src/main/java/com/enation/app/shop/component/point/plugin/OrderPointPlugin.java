package com.enation.app.shop.component.point.plugin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PaymentLogType;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.plugin.order.IConfirmReceiptEvent;
import com.enation.app.shop.core.order.plugin.order.IOrderRefundEvent;
import com.enation.app.shop.core.order.plugin.order.IOrderRogconfirmEvent;
import com.enation.app.shop.core.member.service.IMemberPointManger;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.OrderPaymentType;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 新版本的积分插件
 * 
 * @author Chopper
 * @version v1.0, 2015-12-31 下午5:10:42
 * @since
 */

@Component
public class OrderPointPlugin extends AutoRegisterPlugin
		implements IConfirmReceiptEvent, IOrderRogconfirmEvent, IOrderRefundEvent {

	@Autowired
	private IMemberPointManger memberPointManger;

	@Autowired
	private IMemberManager memberManager;

	@Autowired
	private IOrderManager orderManager;

	/**
	 * 确认收款 货到付款的支付方式 添加积分
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.plugin.order.IConfirmReceiptEvent#confirm(java.
	 * lang.Integer, double)
	 */
	@Override
	public void confirm(Integer orderid, double price) {
		// 如果不是退货
		if (price > 0) {
			Order order = orderManager.get(orderid);
			// 如果是货到付款
			if (order.getPayment_type().equals(OrderPaymentType.cod.getValue())) {
				//添加会员的等级积分和消费积分 dongxin改
				memberPointManger.add(memberManager.get(order.getMember_id()), getPoint(price), "货到付款，确认收款获取积分",order.getMember_id(),0,0);
				memberPointManger.add(memberManager.get(order.getMember_id()), 0, "货到付款，确认收款获取积分",order.getMember_id(), getPointmp(price),1);
			}

			// 如果当前订单中促销活动赠与积分不为零，就将此积分添加到会员冻结积分中 add_by DMRain 2016-7-22
			if (order.getActivity_point() != 0) {
				Map map = new HashMap();
				map.put("memberid", order.getMember_id());
				map.put("point", 0);
				map.put("mp", order.getActivity_point());
				map.put("orderid", order.getOrder_id());
				map.put("dateline", DateUtil.getDateline());
				map.put("type", "activity_point");
				this.memberPointManger.addFreezePoint(map);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.plugin.order.IOrderRogconfirmEvent#rogConfirm(
	 * com.enation.app.shop.core.model.Order)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void rogConfirm(Order order) {
		// 如果不是货到付款(货到付款 确认收款时增加积分)
		if (!order.getPayment_type().equals("cod")) {
			memberPointManger.add(memberManager.get(order.getMember_id()), getPoint(order.getNeedPayMoney()),
					"确认收货获取积分", order.getMember_id(), 0,0);
			memberPointManger.add(memberManager.get(order.getMember_id()), 0,
					"确认收货获取积分", order.getMember_id(), getPointmp(order.getNeedPayMoney()),1);
			// 如果是网上支付
			if (!order.getPayment_type().equals("offline")) {
				int point = memberPointManger.getItemPoint(IMemberPointManger.TYPE_ONLINEPAY + "_num");
				int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_ONLINEPAY + "_num_mp");
				//添加会员的等级积分和消费积分 dongxin改
				memberPointManger.add(memberManager.get(order.getMember_id()), point, "网上支付额外获得积分",
						order.getMember_id(), 0,0);
				memberPointManger.add(memberManager.get(order.getMember_id()), 0, "网上支付额外获得积分",
						order.getMember_id(), mp,1);
			}
		}
	}

	@Override
	public void onRefund(Order order, Refund refund) {
		Double price = refund.getRefund_money();
		if(order.getPaymoney()<price){
			price=order.getPaymoney();
		}
		// 获取当前会员的积分
		int mp_num = memberManager.get(order.getMember_id()).getMp();
		// 如果当前积分小于需要退货的积分那么是当前用户的消费积分
		//添加会员的等级积分和消费积分 dongxin改
		if (mp_num < getPointmp(price)) {
			memberPointManger.add(memberManager.get(order.getMember_id()), 0, "退货退还积分",
					order.getMember_id(), -mp_num,1);
			memberPointManger.add(memberManager.get(order.getMember_id()), -getPoint(price), "退货退还积分",
					order.getMember_id(),0,0);

		} else {
			memberPointManger.add(memberManager.get(order.getMember_id()),0, "退货退还积分",
					order.getMember_id(), -getPointmp(price),1);
			memberPointManger.add(memberManager.get(order.getMember_id()),-getPoint(price), "退货退还积分",
					order.getMember_id(), 0,0);
		}
		

		// 如果是网上支付并且是已发货状态的话，因为已发货之后表示获得了网上支付的积分
		if (!order.getPayment_type().equals("offline") && order.getShip_status()!=0) {
			int point = memberPointManger.getItemPoint(IMemberPointManger.TYPE_ONLINEPAY + "_num");
			int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_ONLINEPAY + "_num_mp");
			//添加会员的等级积分和消费积分 dongxin改
			memberPointManger.add(memberManager.get(order.getMember_id()), -point, "退货退还网上支付额外获得积分",
					order.getMember_id(), 0,0);
			memberPointManger.add(memberManager.get(order.getMember_id()), 0, "退货退还网上支付额外获得积分",
					order.getMember_id(), -mp,1);
		}
		
		/** 退货审核通过后，根据订单判断订单是否有促销活动赠送的积分，如果有，要减去会员相应的消费积分 add_by DMRain 2016-7-25 */
		if (order.getActivity_point() != 0) {
			//添加会员的消费积分 dongxin改
			this.memberPointManger.add(this.memberManager.get(order.getMember_id()), 0, "退货退还促销活动赠送的积分", order.getMember_id(), -order.getActivity_point(),1);
		}

	}

	/**
	 * 获取商品等级积分
	 * 
	 * @param goodsPrice
	 * @return
	 */
	private int getPoint(Double goodsPrice) {
		/**
		 * -------------------------------------- 增加会员积分--商品价格*倍数(倍数在设置处指定)
		 * --------------------------------------
		 */
		if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_BUYGOODS)) {

			int point = memberPointManger.getItemPoint(IMemberPointManger.TYPE_BUYGOODS + "_num");
			point = goodsPrice.intValue() * point;
			return point;
		}

		return 0;
	}

	/**
	 * 获取商品消费积分
	 * 
	 * @param goodsPrice
	 * @return
	 */
	private int getPointmp(Double goodsPrice) {
		/**
		 * -------------------------------------- 增加会员积分--商品价格*倍数(倍数在设置处指定)
		 * --------------------------------------
		 */
		if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_BUYGOODS)) {

			int point = memberPointManger.getItemPoint(IMemberPointManger.TYPE_BUYGOODS + "_num_mp");
			point = goodsPrice.intValue() * point;
			return point;
		}

		return 0;
	}

}
