package com.enation.app.shop.core.order.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.RefundLog;
import com.enation.app.shop.core.order.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.order.service.IOrderFlowManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IOrderMetaManager;
import com.enation.app.shop.core.order.service.IOrderReportManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 退款\支付
 * 
 * @author apexking
 * @author LiFenLong 2014-4-17；4.0改版修改pay方法
 */
@Controller
@RequestMapping("/shop/admin/payment")
public class PaymentController {

	@Autowired
	private IOrderManager orderManager;
	
	@Autowired
	private IOrderFlowManager orderFlowManager;
	
	@Autowired
	private IMemberManager memberManager;
	
	@Autowired
	private IOrderMetaManager orderMetaManager; 
	
	@Autowired
	private IOrderReportManager orderReportManager;
	
	@Autowired
	private OrderPluginBundle orderPluginBundle;
	
	protected Logger logger = Logger.getLogger(getClass());
	/**
	 * 显示确认收款对话框
	 * @param orderId 订单Id,Integer
	 * @param ord 订单,Order
	 * @param payment_id 支付方式Id,Integer
	 * @param paymentList 收款详细列表,List
	 * @param metaList 订单扩展列表,List
	 * @param showMoney 应付金额,Double
	 * @return
	 */
	@RequestMapping("/show-pay-dialog")
	public ModelAndView showPayDialog(Integer orderId){
		
		Integer payment_id=orderReportManager.getPaymentLogId(orderId);
		PaymentLog payment=orderReportManager.getPayment(payment_id);
		Order ord=this.orderManager.get(orderId);
		ModelAndView view=new ModelAndView();
		view.addObject("ord", ord);
		view.addObject("payment_id", orderReportManager.getPaymentLogId(orderId));
		view.addObject("payment",payment );
		view.addObject("paymentList",  this.orderReportManager.listPayMentDetail(payment_id));
		view.addObject("metaList", orderMetaManager.list(ord.getOrder_id()));
		view.addObject("showMoney", CurrencyUtil.sub(payment.getMoney(), payment.getPaymoney()));
		view.setViewName("/shop/admin/order/dialog/pay");
 		return view;

	}
	
	/**
	 * 显示收款单
	 * @param orderId 订单Id,Integer
	 * @return
	 */
	@RequestMapping("/show-dialog")
	public ModelAndView showDialog(Integer orderId){
		
		Integer payment_id=orderReportManager.getPaymentLogId(orderId);
		PaymentLog payment=orderReportManager.getPayment(payment_id);
		Order ord=this.orderManager.get(orderId);
		ModelAndView view=new ModelAndView();
		view.addObject("ord", ord);
		view.addObject("payment_id", orderReportManager.getPaymentLogId(orderId));
		view.addObject("payment",payment );
		view.addObject("paymentList",  this.orderReportManager.listPayMentDetail(payment_id));
		view.addObject("metaList", orderMetaManager.list(ord.getOrder_id()));
		view.addObject("showMoney", CurrencyUtil.sub(payment.getMoney(), payment.getPaymoney()));
		
		view.setViewName("/shop/admin/order/dialog/show");
 		return view;

	}
	
	/**
	 * 货到付款付款记录
	 * @param orderId 
	 * @param pay_method 
	 * @param paydate 
	 * @param sn
	 * @param paymoney
	 * @param remark
	 * @param member
	 * @param paymentLog
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/pay-log")
	public JsonResult payLog() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String orderId = request.getParameter("orderId");
		String pay_method = request.getParameter("pay_method");
		String paydate = request.getParameter("paydate");
		String sn = request.getParameter("sn");
		String paymoney = request.getParameter("paymoney");
		String remark = request.getParameter("remark");
	 
		
		if(StringUtil.isEmpty(paymoney)){
			return JsonResultUtil.getErrorJson("付款金额不能为空，请确认后再提交付款信息！");
		}
		if(!StringUtil.checkFloat(paymoney, "0+")){
			return JsonResultUtil.getErrorJson("付款金额格式不正确，请确认后再提交付款信息！");
		}
		if(orderId == null || orderId.equals("")){
			return JsonResultUtil.getErrorJson("订单号错误，请确认后再提交付款信息！");
		}
		
		Order order = orderManager.get(Integer.parseInt(orderId));
		if(order == null){
			return JsonResultUtil.getErrorJson("订单号错误，请确认后再提交付款信息！");
		}
		if(!order.getIsCod()){
			if(order.getStatus() == null || order.getStatus().intValue() != OrderStatus.ORDER_NOT_PAY){
				return JsonResultUtil.getErrorJson("订单状态错误，请确认后再提交付款信息！");
			}
		}
		
		PaymentLog paymentLog =  new PaymentLog();
		
		Member member=null;
		if(order.getMember_id()!=null)
			member=memberManager.get(order.getMember_id());
		
		if(member!=null){			
			paymentLog.setMember_id(member.getMember_id());
			paymentLog.setPay_user(member.getUname());
		}else{
			paymentLog.setPay_user("匿名购买者");
		}
		paymentLog.setPay_date( DateUtil.getDateline(paydate  ));
		paymentLog.setRemark(remark);
		paymentLog.setMoney( Double.valueOf(paymoney) );		
		paymentLog.setOrder_sn(order.getSn());
		paymentLog.setPay_method(pay_method);
		paymentLog.setSn(sn);
		paymentLog.setOrder_id(order.getOrder_id());
		return JsonResultUtil.getSuccessJson("添加收款成功");
	}
	
	/**
	 * 支付
	 * @param orderId 订单Id,Integer
	 * @param payment_id 付款单Id,Integer
	 * @param paymoney 付款金额,Double
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/pay")
	public JsonResult pay(Integer orderId,Double paymoney,int payment_id) {
		try{
			//获取操作用户
			String username=UserConext.getCurrentAdminUser().getUsername();
			// 调用执行添加收款详细表
			if (orderFlowManager.pay(payment_id, orderId, paymoney,username)) {
				orderPluginBundle.confirm(orderId,orderManager.get(orderId).getGoods_amount());
				return JsonResultUtil.getSuccessJson("订单收款成功");
			} else {
				return JsonResultUtil.getErrorJson("订单收款失败,您输入的付款金额合计大于应付金额");
			}
		}catch(RuntimeException e){
			if(logger.isDebugEnabled()){
				logger.debug(e);
			}
			return JsonResultUtil.getErrorJson("确认付款失败:"+e.getMessage());
		}
	}

}
