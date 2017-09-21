package com.enation.app.shop.core.order.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.goods.model.Depot;
import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderGift;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.SellBackGoodsList;
import com.enation.app.shop.core.order.model.SellBack;
import com.enation.app.shop.core.order.model.SellBackStatus;
import com.enation.app.shop.core.order.service.IOrderGiftManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.IRefundManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 退货管理Controller
 * 
 * @author fenlongli
 * @since spring mvc 6.0 改造
 * @date 2016-03-01
 * @author chopper
 * @version v1.1 2016-7-28 Kanon 修改退货流程
 */

@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/sell-back")
public class SellBackController extends GridController {

	@Autowired
	private ISellBackManager sellBackManager;
	@Autowired
	private IPaymentManager paymentManager;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IDepotManager depotManager;
	@Autowired
	private IRefundManager refundManager;

	/**
	 * 订单赠品管理 add_by DMRain 2016-7-19
	 */
	@Autowired
	private IOrderGiftManager orderGiftManager;

	/**
	 * 跳转至新建退货申请页面
	 * 根据订单Id获取订单详细信息
	 * 根据订单Id获取订单货物详细信息
	 * 获取当前的仓库列表
	 * 获取当前的支付方式列表
	 * @param orderId 订单Id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/add-sellback")
	public ModelAndView addSellback(Integer orderId) {
		ModelAndView modelAndView = new ModelAndView();
		Order orderinfo = orderManager.get(orderId);// 订单详细
		List orderItem = orderManager.getOrderItem(orderId);
		List depotlist = depotManager.list();// 仓库列表
		List paymentList = paymentManager.list(); // 支付方式列表

		OrderGift gift = new OrderGift();

		//如果订单中的赠品id不为0 add_by DMRain 2016-7-25
		if (orderinfo.getGift_id() != 0) {
			gift = this.orderGiftManager.getOrderGift(orderinfo.getGift_id(), orderId);
			gift.setGift_img(StaticResourcesUtil.convertToUrl(gift.getGift_img()));
		}
		/** 获取支付方式配置信息 */
		PayCfg payCfg = null;
		if(orderinfo != null ){
			payCfg = this.paymentManager.get(orderinfo.getPayment_type());
			if (payCfg == null) {
				payCfg = new PayCfg();
				payCfg.setIs_retrace(0);
			}
		}
		modelAndView.addObject("gift", gift);
		modelAndView.addObject("payCfg", payCfg);
		modelAndView.addObject("orderinfo", orderinfo);
		modelAndView.addObject("orderItem", orderItem);
		modelAndView.addObject("depotlist", depotlist);
		modelAndView.addObject("paymentList", paymentList);

		modelAndView.setViewName("/shop/admin/orderReport/add_sellback");
		return modelAndView;
	}

	/**
	 * 首台发起退货申请
	 * 传递订单Id，根据订单Id查询会员信息
	 * 记录售后申请类型。type：1为退款 2为退货
	 * @param orderId 订单Id
	 * @param goodsId 商品退货Id
	 * @param goodsNum 退货商品数量
	 * @param payNum 订单购买数量
	 * @param productId 退货货品Id
	 * @param price 订单货品价格
	 * @param item_id 订单货品ID
	 * @param sellBack 售后申请单
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/apply")
	public JsonResult apply(Integer orderId, Integer[] goodsId,
			Integer[] goodsNum, Integer[] payNum, Integer[] productId,
			Double[] price, Integer[] item_id, SellBack sellBack) {
		try {
			// 三个if 必要参数校验
			if (orderId == null || goodsId == null || goodsNum == null
					|| payNum == null || productId == null || price == null
					|| item_id == null) {
				return JsonResultUtil.getErrorJson("参数错误");

			}
			if (StringUtil.isEmpty(sellBack.getRefund_way())) {
				return JsonResultUtil.getErrorJson("退款方式不能为空");

			}

			if (StringUtil.isEmpty(sellBack.getRefund_way())) {
				return JsonResultUtil.getErrorJson("退款账号不能为空");
			}

			// 记录会员信息
			sellBack.setTradeno(DateUtil.toString(DateUtil.getDateline(), "yyMMddhhmmss"));// 退货单号
			sellBack.setOrderid(orderId);
			sellBack.setRegoperator("管理员");
			sellBack.setTradestatus(SellBackStatus.wait.getValue());
			sellBack.setMember_id(orderManager.get(orderId).getMember_id());
			sellBack.setRegtime(DateUtil.getDateline());

			/**
			 * 添加售后类型
			 * @author Kanon 
			 */
			sellBack.setType(2);

			/**
			 * 循环页面中选中的商品，形成退货明细:goodsList
			 */
			List goodsList = new ArrayList();

			for (int i = 0; i < goodsId.length; i++) {

				SellBackGoodsList sellBackGoods = new SellBackGoodsList();
				sellBackGoods.setPrice(price[i]);
				sellBackGoods.setReturn_num(goodsNum[i]);
				sellBackGoods.setShip_num(payNum[i]);
				sellBackGoods.setGoods_id(goodsId[i]);
				sellBackGoods.setGoods_remark(sellBack.getRemark());
				sellBackGoods.setProduct_id(productId[i]);
				sellBackGoods.setItem_id(item_id[i]);
				goodsList.add(sellBackGoods);

			}

			this.sellBackManager.addSellBackAdmin(sellBack, goodsList);
			return JsonResultUtil.getSuccessJson("退货申请成功");
		} catch (Exception e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("退货申请失败");
		}

	}

	/**
	 * 订单号查询
	 * 
	 * @param orderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/search-sn")
	public JsonResult searchSn(Integer orderId) {
		Order orderinfo = orderManager.get(orderId);// 订单详细
		int num = this.sellBackManager.searchSn(orderinfo.getSn());
		// System.out.println(num);
		// 提交过返回success 因为只有success能传递id
		if (num > 0) {
			return JsonResultUtil.getSuccessJson("订单已提交过售后申请");
		} else {
			return JsonResultUtil.getErrorJson("");
		}

	}

	/**
	 * 跳转到审核退货申请
	 * 根据退货申请Id获取退货申请详细
	 * 根据退货申请的订单编号获取订单详细
	 * 根据退货申请id获取退货商品列表
	 * 获取当前的仓库列表
	 * 判断退货申请是否含有赠品，如有赠品显示赠品
	 * @author fenlongli
	 * @param id 退货申请Id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/auth")
	public ModelAndView auth(Integer id) {
		ModelAndView modelAndView = new ModelAndView();
		try {
			SellBack sellBackList = this.sellBackManager.get(id);// 退货详细
			Order orderinfo = orderManager.get(sellBackList.getOrdersn());// 订单详细
			List goodsList = this.sellBackManager.getGoodsList(id);// 退货商品列表
			List depotlist = depotManager.list();// 仓库列表

			OrderGift gift = new OrderGift();

			//如果退货单中有赠品id add_by DMRain 2016-7-19
			if (sellBackList.getGift_id() != null) {
				gift = this.orderGiftManager.getOrderGift(sellBackList.getGift_id(), sellBackList.getOrderid());
			}
			/** 获取支付方式配置信息 */
			PayCfg payCfg = null;
			if(orderinfo != null ){
				payCfg = this.paymentManager.get(orderinfo.getPayment_type());
				if (payCfg == null) {
					payCfg = new PayCfg();
					payCfg.setIs_retrace(0);
				}
			}
			modelAndView.addObject("gift", gift);
			modelAndView.addObject("payCfg", payCfg);
			modelAndView.addObject("orderinfo", orderinfo);
			modelAndView.addObject("goodsList", goodsList);
			modelAndView.addObject("sellBackList", sellBackList);
			modelAndView.addObject("depotlist", depotlist);
			/**
			 * 此退货单已有的仓库信息 可能为空，表示审核的时候要选择退货仓库
			 */
			Integer depotid = sellBackList.getDepotid();
			modelAndView.addObject("depotid", depotid);
			modelAndView.setViewName("/shop/admin/orderReport/auth_sellback");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelAndView;
	}

	/**
	 * 审核退货申请
	 * 
	 * @param status
	 *            审核状态
	 * @param id
	 *            退货单ID
	 * @param seller_remark
	 *            审核备注
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/save-auth")
	public JsonResult saveAuth(Integer depotid, Integer status, Integer id,
			Double alltotal_pay, String seller_remark) {

		try {
			/**
			 * 退货仓库 有效性校验
			 */
			if ((depotid == null || depotid == 0) && status==1) {
				return JsonResultUtil.getErrorJson("操作失败:必须选择退货仓库");

			}
			this.sellBackManager.editStatus(status, id, depotid, alltotal_pay,
					seller_remark);
			return JsonResultUtil.getSuccessJson("操作成功");

		} catch (Exception e) {
			this.logger.error("审核退货单失败", e);
			return JsonResultUtil.getErrorJson("操作失败" + e.getMessage());
		}

	}

	/**
	 * 退货入库
	 * 
	 * @return
	 */ 
	@RequestMapping(value = "returned")
	public ModelAndView returned(int id) {
		ModelAndView view = new ModelAndView();
		try {
			SellBack sellBackList = this.sellBackManager.get(id);// 退货详细
			Order orderinfo = orderManager.get(sellBackList.getOrdersn());// 订单详细
			List goodsList = this.sellBackManager.getGoodsList(id);// 退货商品列表
			List logList = this.sellBackManager.sellBackLogList(id);// 退货操作日志
			List doptList = this.depotManager.list();  //仓库列表

			String depot_name = null;
			/**
			 * 仓库信息
			 */
			Integer depotid = sellBackList.getDepotid();
			if (depotid != null) {
				Depot depot = depotManager.get(depotid);
				if (depot != null) {
					depot_name = depotManager.get(sellBackList.getDepotid()).getName();

				}
			}

			OrderGift gift = new OrderGift();

			//如果退货单中有赠品id add_by DMRain 2016-7-19
			if (sellBackList.getGift_id() != null) {
				gift = this.orderGiftManager.getOrderGift(sellBackList.getGift_id(), sellBackList.getOrderid());
			}
			//根据订单ID查询出退款单信息 页面查看退款单按钮需要
			Refund refund=this.refundManager.getRefundBySellbackId(sellBackList.getId());
			view.addObject("refund", refund);
			view.addObject("gift", gift);

			view.addObject("sellBackList", sellBackList);
			view.addObject("orderinfo", orderinfo);
			view.addObject("goodsList", goodsList);
			view.addObject("logList", logList);
			view.addObject("depot_name", depot_name);
			view.addObject("dopotList", doptList);
			view.setViewName("/shop/admin/orderReport/returned_sellback"); 
		} catch (Exception e) {
			this.logger.error("显示退货出错", e); 
			view.setViewName("/shop/admin/orderReport/returned_sellback");  
		}
		return view;

	}

	/**
	 * 退货申请入库
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "update")
	public JsonResult update(SellBack sellBackList, Integer[] goodsId,
			Integer[] storageNum, Integer[] productId,Integer[] itemId, int id, int depotid,
			String storageDetail) {
		// 获取退货单
		SellBack sellback = this.sellBackManager.get(id);
		this.sellBackManager.editSellBackDepotId(depotid, sellback.getOrderid());
		sellback.setGoodslist(storageDetail);
		try {
			sellback.setWarehouse_remark(sellBackList.getWarehouse_remark());
			// 循环将商品入库
			for (int i = 0; i < goodsId.length; i++) {
				if(storageNum[i]!=0){
					sellBackManager.inStorage(depotid, id, goodsId[i],storageNum[i], productId[i],itemId[i]);
				}

			}

			//如果退货单信息中的赠品id不为空 add_by DMRain 2016-7-19
			if (sellback.getGift_id() != null) {
				this.orderGiftManager.updateGiftStatus(sellback.getGift_id(), sellback.getOrderid(), 3);
			}

			sellback.setWarehouse_remark(sellBackList.getWarehouse_remark());
			this.sellBackManager.apply(sellback);

			//记录售后申请日志
			this.sellBackManager.saveLog(sellback.getId(),SellBackStatus.valueOf(sellback.getTradestatus()).getName());

			//记录订单日志
			orderManager.addLog(sellback.getOrderid(), "订单商品入库");  
			return JsonResultUtil.getSuccessJson("操作成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("操作失败 ！");
		}

	}

	/** 取消退货
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "cancel")
	public JsonResult cancel(SellBack sellBackList, Integer ctype,
			Integer status, Integer id, String cancelRemark) {
		try {
			SellBack sellbacklist = null;
			status = ctype;
			Order orderinfo;
			if (id != null) {
				if (status == 0 || status == 1) {
					sellbacklist = this.sellBackManager.get(id);
				} else {
					return JsonResultUtil.getErrorJson("该退货单的商品已入库，不能取消退货！");

				}
			} else {
				if (sellBackList.getTradeno() != null) {
					if (status == 0 || status == 1) {
						sellbacklist = this.sellBackManager.get(sellBackList
								.getTradeno());
					} else {
						return JsonResultUtil
								.getErrorJson("该退货单的商品已入库，不能取消退货！");
					}
				}
			}
			if (sellbacklist != null) {
				sellbacklist.setTradestatus(4);// 取消
				this.sellBackManager.cancle(sellbacklist);
				this.sellBackManager.saveLog(sellbacklist.getId(),"取消退货，原因：" + cancelRemark);
				orderinfo = orderManager.get(sellbacklist.getOrdersn());// 订单详细
				return JsonResultUtil.getSuccessJson("取消退货成功！");
			} else {
				return JsonResultUtil.getSuccessJson("操作成功！");
			}
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("取消退货失败！");
		}

	}
	/**
	 * 跳转至新建退款申请页面
	 * @param orderId 订单Id
	 */
	@ResponseBody
	@RequestMapping(value = "/add-refund")
	public ModelAndView addRefund(Integer orderId) {
		ModelAndView modelAndView = new ModelAndView();
		Order orderinfo = orderManager.get(orderId);// 订单详细
		List orderItem = orderManager.getOrderItem(orderId);

		OrderGift gift = new OrderGift();

		//如果订单中的赠品id不为0 add_by DMRain 2016-7-25
		if (orderinfo.getGift_id() != 0) {
			gift = this.orderGiftManager.getOrderGift(orderinfo.getGift_id(), orderId);
			gift.setGift_img(StaticResourcesUtil.convertToUrl(gift.getGift_img()));
		}
		/** 获取支付方式配置信息 */
		PayCfg payCfg = null;
		if(orderinfo != null ){
			payCfg = this.paymentManager.get(orderinfo.getPayment_type());
			if (payCfg == null) {
				payCfg = new PayCfg();
				payCfg.setIs_retrace(0);
			}
		}
		modelAndView.addObject("gift", gift);
		modelAndView.addObject("payCfg", payCfg);
		modelAndView.addObject("orderinfo", orderinfo);
		modelAndView.addObject("orderItem", orderItem);

		modelAndView.setViewName("/shop/admin/orderReport/add_refund");
		return modelAndView;
	}

	/***
	 * 添加退款申请
	 * @param sellBack 售后申请
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/save-refund")
	public JsonResult saveRefund(SellBack sellBack) {
		try {
			String name=UserConext.getCurrentAdminUser().getUsername();
			Order order=orderManager.get(sellBack.getOrderid());
			sellBack.setMember_id(order.getMember_id());
			sellBack.setSndto(name);
			sellBack.setTradeno(com.enation.framework.util.DateUtil.toString(DateUtil.getDateline(),"yyMMddhhmmss"));//退款单号
			sellBack.setRegoperator("管理员["+name+"]");
			sellBack.setTradestatus(0);
			sellBack.setRegtime(DateUtil.getDateline());
			sellBack.setType(1);
			sellBackManager.addSellBack(sellBack);
			return JsonResultUtil.getSuccessJson("退款申请创建成功");
		} catch (Exception e) {
			this.logger.error("创建退款申请出错", e); 
			return JsonResultUtil.getSuccessJson("退款申请创建失败："+e.getMessage());
		}

	}


	/**
	 * 退款单申请列表
	 * @return
	 */
	@RequestMapping(value="/refund-list")
	public ModelAndView refundList(){
		ModelAndView view=this.getGridModelAndView();
		view.setViewName("/shop/admin/orderReport/refund_application");
		return view;
	}
	/**
	 * 退款单申请列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/refund-list-json")
	public GridJsonResult refundListJson(){
		Integer status=Integer.parseInt(ThreadContextHolder.getHttpRequest().getParameter("status").toString());
		return JsonResultUtil.getGridJson(sellBackManager.list(this.getPage(), this.getPageSize(), status, 1));
	}
	/**
	 * 跳转到退款审核页面
	 * @param id 退款申请Id
	 * @return
	 */
	@RequestMapping(value="/refund-detail")
	public ModelAndView refundDetail(Integer id){
		ModelAndView view=new ModelAndView();
		List goodsList = this.sellBackManager.getGoodsList(id);// 退货商品列表
		SellBack sellBackList = this.sellBackManager.get(id);

		/** 以下代码是加入退款单中的赠品信息 add_by DMRain 2016-7-21 */
		OrderGift gift = new OrderGift();
		if (sellBackList.getGift_id() != null) {
			gift = this.orderGiftManager.getOrderGift(sellBackList.getGift_id(), sellBackList.getOrderid());
		}
		view.addObject("gift", gift);

		view.addObject("goodsList", goodsList);
		view.addObject("sellBackList", sellBackList);
		view.setViewName("/shop/admin/orderReport/refund_auth");
		return view;
	}

	/**
	 * 审核退款申请
	 * @param id 申请退款单id
	 * @param status 状态
	 * @param seller_remark 操作备注
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/auth-refund")
	public JsonResult authRefund(Integer id,Integer status,String seller_remark){
		try {
			sellBackManager.authRetund(id, status,seller_remark);
			return JsonResultUtil.getSuccessJson("操作成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("操作失败");
		}

	}
}