package com.enation.app.shop.front.api.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.core.order.model.DlyType;
import com.enation.app.shop.core.order.model.support.OrderPrice;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;
/**
 * 结算api
 * @author kingapex
 *2013-7-25上午10:38:13
 * @version 2.0,2016-02-18 Sylow v60版本改造
 * @since v6.0
 */
@Controller 
@RequestMapping("/api/shop/checkout")
public class CheckoutApiController {
	
	@Autowired
	private ICartManager cartManager;
	@Autowired
	private IDlyTypeManager dlyTypeManager;
	
	/**
	 * 获取配送方式
	 * @param regionid 收货地区id
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/get-shiping-type", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult getShipingType(){
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String sessionid  = request.getSession().getId();
		Double orderPrice = cartManager.countGoodsTotal(sessionid);
		Double weight = cartManager.countGoodsWeight(sessionid);
		
		List<DlyType> dlyTypeList = this.dlyTypeManager.list(weight, orderPrice, request.getParameter("regionid"));
		return JsonResultUtil.getObjectJson(dlyTypeList);
	}
	
	
	/**
	 * 显示订单价格信息
	 * @param typeId 配送方式Id
	 * @param regionId 地区Id
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/show-order-total", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult showOrderTotal(int typeId, String regionId){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String sessionid = request.getSession().getId();
		OrderPrice orderPrice = this.cartManager.countPrice(cartManager.listGoods(sessionid), typeId, regionId);
		return JsonResultUtil.getObjectJson(orderPrice);
	}
	
	/**
	 * 设置支付方式配送方式和配送日期session
	 * @param typeId
	 * @param regionId
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/set-pay-dlytype", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult setPayDlytype(Integer paymentId,Integer dlyTypeId,String shipDay){
		try {
			HttpSession session =  ThreadContextHolder.getSession();
			Map map = new HashMap();
			
			DlyType dlytype=  this.dlyTypeManager.getDlyTypeById(dlyTypeId);
			map.put("paymentId", paymentId);	//支付方式	//0为在线支付
			map.put("dlytype", dlytype); 	//配送方式
			map.put("shipDay", shipDay); 	//送货日期
			
			session.setAttribute("paymentDlytypeSession", map);
			
			return JsonResultUtil.getSuccessJson("设置成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("保存出现错误，请稍后重试！");
		}
	}
	
	/**
	 * 订单结算页设置发票信息
	 * @param receiptType	发票类型 (个人或公司)
	 * @param receiptContent	发票内容
	 * @param receiptTitle	发票抬头
	 * @return
	 */
	@ResponseBody  
	@RequestMapping(value="/set-receipt", produces = MediaType.APPLICATION_JSON_VALUE , method = RequestMethod.GET)
	public JsonResult setReceipt(Integer receiptType,String receiptContent,String receiptTitle,Integer is_have,String receiptDuty){
		
		try {
			
			HttpSession session =  ThreadContextHolder.getSession();
			if(is_have==null || (is_have!=null && is_have.intValue()==0) ){
				session.removeAttribute("checkoutReceiptSession");
				return JsonResultUtil.getSuccessJson("保存成功");
			}
			
			Map receiptMap = new HashMap();
			receiptMap.put("receiptType", receiptType);
			receiptMap.put("receiptContent", receiptContent);
			receiptMap.put("receiptTitle", receiptTitle);
			receiptMap.put("receiptDuty", receiptDuty);
			session.setAttribute("checkoutReceiptSession", receiptMap);
			
			return JsonResultUtil.getSuccessJson("保存成功");
		} catch (Exception e) {
			
		}
		
		return JsonResultUtil.getErrorJson("出现错误，请稍后重试");
		
	}
	
}
