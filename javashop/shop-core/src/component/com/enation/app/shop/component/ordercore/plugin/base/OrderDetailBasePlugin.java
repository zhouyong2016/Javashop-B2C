package com.enation.app.shop.component.ordercore.plugin.base;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.goods.model.Depot;
import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.DlyType;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.plugin.order.IOrderTabShowEvent;
import com.enation.app.shop.core.order.plugin.order.IShowOrderDetailHtmlEvent;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.app.shop.core.order.service.ILogiManager;
import com.enation.app.shop.core.order.service.IOrderBonusManager;
import com.enation.app.shop.core.order.service.IOrderGiftManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IOrderReportManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 订单详细页基本信息显示插件
 * @author kingapex
 *2012-2-16下午7:20:00
 */
@Component
public class OrderDetailBasePlugin extends AutoRegisterPlugin implements
		IOrderTabShowEvent,IShowOrderDetailHtmlEvent {
	
	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IOrderReportManager orderReportManager;
	
	@Autowired
	private IDepotManager depotManager;
	
	@Autowired
	private IPaymentManager paymentManager;
	
	@Autowired
	private IDlyTypeManager dlyTypeManager;
	
	@Autowired
	private ILogiManager logiManager;
	
	@Autowired
	private IOrderGiftManager orderGiftManager;
	
	@Autowired
	private IOrderBonusManager orderBonusManager;
	
	@Override
	public boolean canBeExecute(Order order) {
		 
		return true;
	}

	@Override
	public String getTabName(Order order) {
	 
		return "基本信息";
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * @param order 订单
	 * member 会员
	 * deliveryList 货运信息
	 * depotList 仓库列表
	 * payCfgList 支付方式列表
	 * OrderStatus 订单状态
	 */
	@Override
	public String onShowOrderDetailHtml(Order order) {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		
		List itemList = this.orderManager.listGoodsItems(order.getOrder_id()); // 订单商品列表
		freeMarkerPaser.setClz(this.getClass());
		if (order.getMember_id() != null){
			Member	member = this.memberManager.get(order.getMember_id());
			freeMarkerPaser.putData("member",member);
		}
		
		List<Delivery> deliveryList  = orderReportManager.getDeliveryList(order.getOrder_id());
		List<Depot>depotList=this.depotManager.list();
		List<DlyType> dlyTypeList  = dlyTypeManager.list();
		List<PayCfg> payCfgList= paymentManager.list();
		List logiList=logiManager.list();
		freeMarkerPaser.putData("deliveryList",deliveryList);		
		freeMarkerPaser.putData("itemList",itemList);		
		freeMarkerPaser.putData(OrderStatus.getOrderStatusMap());
		freeMarkerPaser.putData("depotList", depotList);
		freeMarkerPaser.putData("payCfgList", payCfgList);
		freeMarkerPaser.putData("dlyTypeList", dlyTypeList);
		//物流公司列表
		freeMarkerPaser.putData("logiList",logiList);
		freeMarkerPaser.putData("OrderStatus", OrderStatus.getOrderStatusMap());
		freeMarkerPaser.putData("eop_product",EopSetting.PRODUCT);
		freeMarkerPaser.putData("self_store",request.getParameter("self_store"));
		
		Integer depotid  = order.getDepotid();
		
		Depot depot = this.depotManager.get(depotid);
		freeMarkerPaser.putData("depot" ,depot);
		
		//如果订单中的赠品id不等于0并且不为空
		if (order.getGift_id() != 0 && order.getGift_id() != null) {
			freeMarkerPaser.putData("gift", this.orderGiftManager.getOrderGift(order.getGift_id(), order.getOrder_id()));
		}
		
		//如果订单中的优惠券id不等于0并且不为空
		if (order.getBonus_id() != 0 && order.getBonus_id() != null) {
			freeMarkerPaser.putData("bonus", this.orderBonusManager.getOrderBonus(order.getBonus_id(), order.getOrder_id()));
		}
		
		freeMarkerPaser.setPageName("base");
		return freeMarkerPaser.proessPageContent();
	}	

}
