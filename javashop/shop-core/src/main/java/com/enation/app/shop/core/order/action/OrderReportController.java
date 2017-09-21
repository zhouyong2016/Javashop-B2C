package com.enation.app.shop.core.order.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.sf.json.JSONArray;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderGift;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.SellBack;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.order.service.IOrderGiftManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IOrderMetaManager;
import com.enation.app.shop.core.order.service.IOrderReportManager;
import com.enation.app.shop.core.order.service.IPaymentLogManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.IRefundManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.app.shop.front.tag.member.MemberWaitCommontListTag;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;
import com.google.zxing.Result;

/**
 * 
 * 订单单据管理
 * @author lzf<br/>
 * 2010-4-12下午12:17:49<br/>
 * version 1.0
 * 
 * version 2.0- by kingapex ,2012-03-09
 * 收款单、退款单表拆分、字段更改。
 * 
 * version 6.1- by kanon ,2016-06-15
 */
@Controller
@RequestMapping("/shop/admin/order-report")
public class OrderReportController extends GridController {

	@Autowired
	private IOrderReportManager orderReportManager;

	@Autowired
	private IOrderMetaManager orderMetaManager;

	@Autowired
	private ISellBackManager sellBackManager;

	@Autowired
	private IOrderManager orderManager;

	@Autowired
	private IPaymentManager paymentManager;

	@Autowired
	private IRefundManager refundManager;

	/** 订单赠品管理 add_by DMRain 2016-7-22 */
	@Autowired
	private IOrderGiftManager orderGiftManager;

	@Autowired
	private IPaymentLogManager  paymentLogManager;
	/**
	 * 付款单列表
	 * @param statusMap 订单状态,Map
	 * @param payStatusMap 付款状态,Map
	 * @param shipMap 发货状态,Map
	 * @param shipTypeList 配送方式列表,List
	 * @param payTypeList 付款方式列表,List
	 * @return
	 */
	@RequestMapping("/payment-list")
	public ModelAndView paymentList(){

		ModelAndView view=getGridModelAndView();


		List payStatusList = OrderStatus.getPayStatus();


		List<PayCfg> payTypeList = paymentManager.list();

		view.addObject("payStatusList", payStatusList);
		view.addObject("payTypeList", payTypeList);

		view.addObject("payStatus_Json", JSONArray.fromObject(payStatusList).toString() );

		view.setViewName("/shop/admin/orderReport/payment_list");
		return view;
	}
	/**
	 * 获取付款单列表Json
	 * @param stype 搜索类型,Integer
	 * @param keyword 搜索关键字,String
	 * @param start_time 下单时间,String
	 * @param end_time 结束时间,Stirng
	 * @param sn 编号,String
	 * @param paystatus 付款状态,Integer
	 * @param payment_id 付款方式Id,Integer
	 * @param order 排序
	 * @return 付款单列表Json
	 */
	@ResponseBody
	@RequestMapping("/payment-list-json")
	public GridJsonResult paymentListJson(String order,Integer stype,String keyword,String start_time, String end_time, String sn, Integer paystatus,Integer payment_id){
		Map orderMap = new HashMap();
		orderMap.put("stype", stype);
		orderMap.put("keyword", keyword);
		orderMap.put("start_time", start_time);
		orderMap.put("end_time", end_time);
		orderMap.put("sn", sn);
		orderMap.put("paystatus", paystatus);
		orderMap.put("payment_id", payment_id);
		return JsonResultUtil.getGridJson(orderReportManager.listPayment(orderMap,this.getPage(), this.getPageSize(), order));
	}


	/**
	 * 发货单列表
	 * @return
	 */
	@RequestMapping("/shipping-list")
	public String shippingList(){
		return "/shop/admin/orderReport/shipping_list";
	}

	/**
	 * 发货单列表JSON
	 * @param order 排序
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/shipping-list-json")
	public GridJsonResult shippingListJson(String order){
		return JsonResultUtil.getGridJson(orderReportManager.listShipping(this.getPage(), this.getPageSize(), order));
	}

	/**
	 * 退货单列表
	 * @return
	 */
	@RequestMapping("/returned-list")
	public ModelAndView returnedList(Integer status){
		ModelAndView view = this.getGridModelAndView();
		view.setViewName("/shop/admin/orderReport/returned_list"); 
		view.addObject("pageSize",this.getPageSize());
		view.addObject("status", status);
		return view;
	}

	/**
	 * 退货单列表JSON
	 * @param state
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/returned-list-json")
	public GridJsonResult returnedListJson(String state,String type){
		return JsonResultUtil.getGridJson(orderReportManager.listReturned(this.getPage(), this.getPageSize(), state,type));
	}

	/**
	 * 原支付方式退款失败后，手动退款
	 * @param id	退款单id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/manual-refund")
	public JsonResult manualRefund(Integer id){
		try {
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			this.refundManager.manualRefundStatus(id, adminUser.getUsername());
			return JsonResultUtil.getSuccessJson("手动退款成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("手动退款失败");
		}
	}


	/**
	 * 退货入库
	 * @return
	 */
	public ModelAndView returned(Integer sid){

		ModelAndView view=new ModelAndView();

		SellBack sellBackList = this.sellBackManager.get(sid);//退货详细
		Order ret_order = orderManager.get(sellBackList.getOrdersn());//订单详细
		List goodsList = this.sellBackManager.getGoodsList(sid);//退货商品列表
		List logList = this.sellBackManager.sellBackLogList(sid);//退货操作日志
		List metaList = orderMetaManager.list(ret_order.getOrder_id());//订单的使用的积分、余额
		view.addObject("sellBackList", sellBackList);
		view.addObject("ret_order", ret_order);
		view.addObject("goodsList", goodsList);
		view.addObject("logList", logList);
		view.addObject("metaList", metaList);
		view.setViewName("/shop/admin/orderReport/returned_sellback");
		return view;
	}	
	/**
	 * 跳转至退款单列表
	 * @return
	 */
	@RequestMapping(value="/refund-list")
	public ModelAndView refundList(){

		ModelAndView view=this.getGridModelAndView();
		String state=ThreadContextHolder.getHttpRequest().getParameter("state");//订单状态
		if(state == null && state == ""){
			state="0";
		}
		view.addObject("state", state);
		view.setViewName("/shop/admin/orderReport/refund_list");
		return view;
	}

	/**
	 * 退款单JSON列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/refund-list-json")
	public GridJsonResult refundListJson(String state){
		return JsonResultUtil.getGridJson(refundManager.refundList(state,this.getPage(), this.getPageSize()));
	}

	/**
	 * 退款单详细
	 * @param id 退款单id
	 * @return
	 */
	@RequestMapping(value="/refund-detail")
	public ModelAndView refundDetail(Integer id){
		ModelAndView view=new ModelAndView();
		Refund refund = this.refundManager.getRefund(id);
		List goodsList = this.sellBackManager.getGoodsList(refund.getSellback_id());// 退货商品列表

		//以下代码是在退款单中加入订单赠品信息 add_by DMRain 2016-7-22
		Integer sellback_id = refund.getSellback_id();

		//判断申请售后是否为空，为空则为取消已付款订单退款
		if(sellback_id!=null){
			SellBack sellBackList = this.sellBackManager.get(sellback_id);
			OrderGift gift = new OrderGift();
			if (sellBackList.getGift_id() != null) {
				gift = this.orderGiftManager.getOrderGift(sellBackList.getGift_id(), sellBackList.getOrderid());
			}
			view.addObject("gift", gift);
			//refund中需要过去退货单处增加的金额
			//TODO 自营店退款金额不为空的修改，我修改以后测过没有bug，如果以后有我疏漏的没测到，可以将此处注释去掉，就会恢复到原来，可以有更好的办法修改我这个bug
		//	refund.setRefund_money(sellBackList.getAlltotal_pay());
		}



		view.addObject("refund", refund);
		view.addObject("goodsList", goodsList);
		view.setViewName("/shop/admin/orderReport/refund_detail");
		return view;
	}

	/**
	 * 修改退款单状态
	 * @param id 退款单Id
	 * @param status 退款单状态
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/edit-refund")
	public JsonResult editRefund(Integer id,Integer status,Double refund_money,String a){
		try {
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			String result=refundManager.editRefund(id, status,refund_money,adminUser.getUsername());
			return JsonResultUtil.getObjectMessageJson(result, "修改状态成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("修改状态失败");
		}
	}

}
