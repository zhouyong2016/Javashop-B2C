/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：通过参数ordersn 订单号得到订单详情
 *  修改人：whj
 *  修改时间：2015-11-30
 *  修改内容：制定初版
 */
package com.enation.app.shop.front.tag.order;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 通过参数ordersn 订单号得到订单详情
 * 补充，本标签使用于支付宝回调后，通过("{'ordersn':'${paymentResult.ordersn!''}'}")参数获得订单详情
 * @author whj
 * @version v1.0,2015-11-30
 * @since v5.2
 */

@Component
@Scope("prototype")
public class OrderDetailByOrderSn  extends BaseFreeMarkerTag {
	@Autowired
	private IOrderManager orderManager ;
	
	/**
	 * 订单详细标签
	 * 必须传递ordersn参数
	 * @param ordersn,订单sn,String 型
	 * @return 订单详细 ，Order型
	 * {@link Order}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
	 	Order order  =null;
		String orderSn = (String)params.get("ordersn");
		order=	orderManager.get(orderSn);
		return order;
	}

}
