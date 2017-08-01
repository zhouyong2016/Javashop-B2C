package com.enation.app.shop.component.activity.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderGift;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.SellBack;
import com.enation.app.shop.core.order.plugin.order.IOrderRefundEvent;
import com.enation.app.shop.core.order.service.IOrderGiftManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 订单退款赠品库存还原插件
 * @author DMRain
 * @date 2016-7-21
 * @since v61
 * @version 1.0
 */
@Component
public class RefundGiftRestorePlugin extends AutoRegisterPlugin implements IOrderRefundEvent{

	@Autowired
	private ISellBackManager sellBackManager;
	
	@Autowired
	private IOrderGiftManager orderGiftManager;
	
	@Override
	public void onRefund(Order order, Refund refund) {
		Integer sellback_id = refund.getSellback_id();
		//如果退货单id不为空
		if (sellback_id != null) {
			SellBack sellBackList = this.sellBackManager.get(sellback_id);
			Integer gift_id = sellBackList.getGift_id();
			
			//如果赠品id不为空
			if (gift_id != null) {
				OrderGift gift = this.orderGiftManager.getOrderGift(gift_id, order.getOrder_id());
				
				//如果订单赠品状态为退货完成 0：正常，1：申请退货，2：退货完成，3：入库完成
				if (gift.getGift_status() == 2) {
					this.orderGiftManager.updateGiftStatus(gift_id, order.getOrder_id(), 3);
				}
			}
		}
	}

}
