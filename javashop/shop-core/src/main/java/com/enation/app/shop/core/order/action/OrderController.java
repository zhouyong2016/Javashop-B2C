package com.enation.app.shop.core.order.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.goods.service.IDepotManager;
import com.enation.app.shop.core.order.model.DlyType;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderGridUrlKeyEnum;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.order.service.IDlyCenterManager;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.app.shop.core.order.service.ILogiManager;
import com.enation.app.shop.core.order.service.IOrderFlowManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单管理action
 * 
 * @author kingapex 2010-4-7下午01:51:48;modify by dable
 * @author LiFenLong 2014-4-1;4.0版本改造
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/order")
@SuppressWarnings({ "rawtypes", "serial", "static-access", "unchecked" })
public class OrderController extends GridController {

	@Autowired
	private IDepotManager depotManager;

	@Autowired
	private IOrderManager orderManager;

	@Autowired
	private IRegionsManager regionsManager;

	@Autowired
	private IOrderFlowManager orderFlowManager;

	@Autowired
	private OrderPluginBundle orderPluginBundle;

	@Autowired
	private IDlyTypeManager dlyTypeManager;

	@Autowired
	private IPaymentManager paymentManager;

	@Autowired
	private ILogiManager logiManager;

	@Autowired
	private IDlyCenterManager dlyCenterManager;

	/**
	 * 
	 * 各列表页所需的url，就是为了显示grid数据的json获取url<br>
	 * 在此抽像出来的目的是为了更好的扩展列表视图<br>
	 * 比如：在自营店的待发货列表和b2c的待发货列表，只有查询条件要加上自营店的区别<br>
	 * 视图层面没有变化，覆写此方法将grid的数据源url改掉<br>
	 * 
	 * @author kingapex 2015-11-09
	 */
	protected Map<String, String> getGridUrl() {

		Map<String, String> urlMap = new HashMap<String, String>();
		urlMap.put(OrderGridUrlKeyEnum.not_pay.toString(),
				"order/list-json.do?order_state=wait_pay");
		urlMap.put(OrderGridUrlKeyEnum.not_ship.toString(),
				"order/list-json.do?order_state=wait_ship");
		urlMap.put(OrderGridUrlKeyEnum.detail.toString(),
				"/shop/admin/order/detail.do?");

		return urlMap;
	}

	/**
	 * 修改订单价格
	 * 
	 * @param orderId
	 *            订单Id,Integer
	 * @param adminUser
	 *            管理员,AdminUser
	 * @param amount
	 *            订单总额,double
	 * @param price
	 *            修改后订单总额,double
	 * @return json result 1,操作成功.2,操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-price")
	public JsonResult savePrice(Integer orderId, double price) {
		try {
			double amount = orderManager.get(orderId).getOrder_amount();
			this.orderManager.savePrice(price, orderId);
			// 记录日志
			orderManager.addLog(orderId, "订单价格从:" + amount + "修改为" + price);
			return JsonResultUtil.getSuccessJson("成功" + price);
		} catch (RuntimeException e) {
			this.logger.error(e.getMessage(), e);
			return JsonResultUtil.getErrorJson("失败");
		}
	}

	/**
	 * 修改订单配送费用
	 * 
	 * @param orderId
	 *            订单Id,Integer
	 * @param currshipamount
	 *            修改前订单配送费用,double
	 * @param shipmoney
	 *            修改后订单配送费用,double
	 * @param price
	 *            修改后订单总价,double
	 * @param adminUser
	 *            管理员,AdminUser
	 * @return json result 1,操作成功.2,操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-ship-money")
	public Object saveShipMoney(Integer orderId, double shipmoney) {
		try {
			double currshipamount = orderManager.get(orderId)
					.getShipping_amount();
			double price = this.orderManager.saveShipmoney(shipmoney, orderId);
			// 记录日志
			orderManager
					.addLog(orderId, "运费从" + currshipamount + "修改为" + price);
			Map map = new HashMap();
			map.put("result", 1);
			map.put("price", price);
			return map;
		} catch (RuntimeException e) {
			this.logger.error(e.getMessage(), e);
			return JsonResultUtil.getErrorJson("保存失败");
		}

	}

	/**
	 * 修改订单配送地区
	 * 
	 * @param orderId
	 *            订单Id,Integer
	 * @param province
	 *            省,String
	 * @param city
	 *            城市,String
	 * @param region
	 *            地区,String
	 * @param Attr
	 *            配送地址,String
	 * @param province_id
	 *            省Id,Integer
	 * @param city_id
	 *            城市Id,Integer
	 * @param region_id
	 *            区Id,Integer
	 * @return json result 1,操作成功.2,操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-addr")
	public JsonResult saveAddr(Integer orderId) {
		try {
			// 获取地区
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			String town = request.getParameter("town");
			String Attr = province + "-" + city + "-" + region;
			if(town!=null&&!"".equals(town)){
				Attr+="-"+town;
			}
			// 获取地区Id
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			String town_id = request.getParameter("town_id");
			if(town_id!=null&&!"".equals(town_id)){//不确定原来的方法参数修改后是否会有问题，所以增加了看起来多余的方法
				this.orderManager.saveAddrAndTown(orderId,
						StringUtil.toInt(province_id, true),
						StringUtil.toInt(city_id, true),
						StringUtil.toInt(region_id, true),
						StringUtil.toInt(town_id, true),Attr);
			}else{
				this.orderManager.saveAddr(orderId,
						StringUtil.toInt(province_id, true),
						StringUtil.toInt(city_id, true),
						StringUtil.toInt(region_id, true),Attr);
			}
			return JsonResultUtil.getSuccessJson("保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return JsonResultUtil.getErrorJson("保存失败");
		}

	}

	/**
	 * 修改订单详细配送地址
	 * 
	 * @param adminUser
	 *            管理员,AdminUser
	 * @param oldAddr
	 *            修改前订单详细配送地址,String
	 * @param addr
	 *            修改后订单详细配送地址,String
	 * @param orderId
	 *            订单Id,Integer
	 * @return json result 1,操作成功.2,操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-addr-detail")
	public JsonResult saveAddrDetail(Integer orderId, String addr) {
		try {
			String oldAddr = this.orderManager.get(orderId).getShip_addr();
			boolean isEdit = this.orderManager.saveAddrDetail(addr, orderId);
			// 记录日志
			this.orderManager.addLog(orderId, "收货人详细地址从['" + oldAddr
					+ "']修改为['" + addr + "']");
			if (isEdit) {
				return JsonResultUtil.getSuccessJson("修改成功");
			} else {
				return JsonResultUtil.getErrorJson("修改失败");
			}
		} catch (RuntimeException e) {
			this.logger.error(e.getMessage(), e);
			return JsonResultUtil.getErrorJson("修改失败");
		}
	}

	/**
	 * 修改 订单配送信息
	 * 
	 * @param adminUser
	 *            管理员,AdminUser
	 * @param oldShip_day
	 *            修改前的收货日期,String
	 * @param oldship_name
	 *            修改前的收货人,String
	 * @param oldship_tel
	 *            修改前的收货人电话,String
	 * @param oldship_mobile
	 *            修改前的收货人手机号,String
	 * @param oldship_zip
	 *            修改前的邮编,String
	 * @param remark
	 *            修改后的订单备注,String
	 * @param ship_day
	 *            修改后的订单收货日期,String
	 * @param ship_name
	 *            修改后的收货人名称,String
	 * @param ship_tel
	 *            修改后的收货人电话,String
	 * @param ship_mobile
	 *            修改后的收货人手机号,String
	 * @param ship_zip
	 *            修改后的邮编 ,String
	 * @param orderId
	 *            订单Id,Integer
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/save-ship-info")
	public JsonResult saveShipInfo(String ship_name, Integer orderId,
			String remark, String ship_day, String ship_tel,
			String ship_mobile, String ship_zip) {
		try {
			Order order = this.orderManager.get(orderId);
			String oldShip_day = order.getShip_day();
			String oldship_name = order.getShip_name();
			String oldship_tel = order.getShip_tel();
			String oldship_mobile = order.getShip_mobile();
			String oldship_zip = order.getShip_zip();

			boolean addr = this.orderManager.saveShipInfo(remark, ship_day,
					ship_name, ship_tel, ship_mobile, ship_zip, orderId);
			// 记录日志
			if (ship_day != null && !StringUtil.isEmpty(ship_day)) {
				this.orderManager.addLog(orderId, "收货日期从['" + oldShip_day
						+ "']修改为['" + ship_day + "']");
			}
			if (ship_name != null && !StringUtil.isEmpty(ship_name)) {
				this.orderManager.addLog(orderId, "收货人姓名从['" + oldship_name
						+ "']修改为['" + ship_name + "']");
			}
			if (ship_tel != null && !StringUtil.isEmpty(ship_tel)) {
				this.orderManager.addLog(orderId, "收货人电话从['" + oldship_tel
						+ "']修改为['" + ship_tel + "']");
			}
			if (ship_mobile != null && !StringUtil.isEmpty(ship_mobile)) {
				this.orderManager.addLog(orderId, "收货人手机从['" + oldship_mobile
						+ "']修改为['" + ship_mobile + "']");
			}
			if (ship_zip != null && !StringUtil.isEmpty(ship_zip)) {
				this.orderManager.addLog(orderId, "收货人邮编从['" + oldship_zip
						+ "']修改为['" + ship_zip + "']");
			}
			if (addr) {
				return JsonResultUtil.getSuccessJson("修改成功!");
			} else {
				return JsonResultUtil.getErrorJson("修改失败!");
			}
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("修改失败!" + e.getMessage());
		}
	}

	/**
	 * 分页读取订单列表 根据订单状态 state 检索，如果未提供状态参数，则检索所有
	 * 
	 * @param statusMap
	 *            订单状态集合,Map
	 * @param payStatusMap
	 *            订单付款状态集合,Map
	 * @param shipMap
	 *            ,订单配送人状态集合,Map
	 * @param shipTypeList
	 *            配送方式集合,List
	 * @param payTypeList
	 *            付款方式集合,List
	 * @return 订单列表
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list() {

		ModelAndView view = getGridModelAndView();

		List statusList = OrderStatus.getOrderStatus();

		List payStatusList = OrderStatus.getPayStatus();

		List shipList = OrderStatus.getShipStatus();

		List<DlyType> shipTypeList = dlyTypeManager.list();
		List<PayCfg> payTypeList = paymentManager.list();

		view.addObject("statusList", statusList);
		view.addObject("payStatusList", payStatusList);
		view.addObject("shipList", shipList);
		view.addObject("shipTypeList", shipTypeList);
		view.addObject("payTypeList", payTypeList);

		view.addObject("status_Json", JSONArray.fromObject(statusList)
				.toString());
		view.addObject("payStatus_Json", JSONArray.fromObject(payStatusList)
				.toString());
		view.addObject("ship_Json", JSONArray.fromObject(shipList).toString());
		view.setViewName("/shop/admin/order/order_list");

		return view;
	}

	/**
	 * 未发货订单
	 * 
	 * @author LiFenLong;2014-4-18
	 * @param statusMap
	 *            订单状态集合,Map
	 * @param payStatusMap
	 *            订单付款状态集合,Map
	 * @param shipMap
	 *            ,订单配送人状态集合,Map
	 * @param shipTypeList
	 *            配送方式集合,List
	 * @param payTypeList
	 *            付款方式集合,List
	 * @return 未发货订单列表
	 */
	@RequestMapping(value = "/not-ship-order")
	public ModelAndView notShipOrder() {

		ModelAndView view = getGridModelAndView();

		List statusList = OrderStatus.getOrderStatus();

		List payStatusList = OrderStatus.getPayStatus();

		List shipList = OrderStatus.getShipStatus();

		List<DlyType> shipTypeList = dlyTypeManager.list();
		List<PayCfg> payTypeList = paymentManager.list();

		view.addObject("statusList", statusList);
		view.addObject("payStatusList", payStatusList);
		view.addObject("shipList", shipList);
		view.addObject("shipTypeList", shipTypeList);
		view.addObject("payTypeList", payTypeList);

		view.addObject("status_Json", JSONArray.fromObject(statusList)
				.toString());
		view.addObject("payStatus_Json", JSONArray.fromObject(payStatusList)
				.toString());
		view.addObject("ship_Json", JSONArray.fromObject(shipList).toString());
		view.addObject("gridUrl", getGridUrl());
		view.setViewName("/shop/admin/order/not_ship");
		return view;

	}

	/**
	 * 已成交订单
	 * 
	 * @author LiFenLong;2016-9-19
	 * @param statusMap
	 *            订单状态集合,Map
	 * @param payStatusMap
	 *            订单付款状态集合,Map
	 * @param shipMap
	 *            ,订单配送人状态集合,Map
	 * @param shipTypeList
	 *            配送方式集合,List
	 * @param payTypeList
	 *            付款方式集合,List
	 * @return 未发货订单列表
	 */
	@RequestMapping(value = "/finish-ship-order")
	public ModelAndView finishShipOrder() {

		ModelAndView view = getGridModelAndView();

		List statusList = OrderStatus.getOrderStatus();

		List payStatusList = OrderStatus.getPayStatus();

		List shipList = OrderStatus.getShipStatus();

		List<DlyType> shipTypeList = dlyTypeManager.list();
		List<PayCfg> payTypeList = paymentManager.list();

		view.addObject("statusList", statusList);
		view.addObject("payStatusList", payStatusList);
		view.addObject("shipList", shipList);
		view.addObject("shipTypeList", shipTypeList);
		view.addObject("payTypeList", payTypeList);

		view.addObject("status_Json", JSONArray.fromObject(statusList)
				.toString());
		view.addObject("payStatus_Json", JSONArray.fromObject(payStatusList)
				.toString());
		view.addObject("ship_Json", JSONArray.fromObject(shipList).toString());
		view.addObject("gridUrl", getGridUrl());
		view.setViewName("/shop/admin/order/finish_ship");
		return view;

	}

	/**
	 * 未付款订单
	 * 
	 * @author LiFenLong
	 * @param statusMap
	 *            订单状态集合,Map
	 * @param payStatusMap
	 *            订单付款状态集合,Map
	 * @param shipMap
	 *            ,订单配送人状态集合,Map
	 * @param shipTypeList
	 *            配送方式集合,List
	 * @param payTypeList
	 *            付款方式集合,List
	 * @return 未付款订单列表
	 */
	@RequestMapping(value = "/not-pay-order")
	public ModelAndView notPayOrder() {

		ModelAndView view = getGridModelAndView();

		List statusList = OrderStatus.getOrderStatus();

		List payStatusList = OrderStatus.getPayStatus();

		List shipList = OrderStatus.getShipStatus();

		List<DlyType> shipTypeList = dlyTypeManager.list();
		List<PayCfg> payTypeList = paymentManager.list();

		view.addObject("statusList", statusList);
		view.addObject("payStatusList", payStatusList);
		view.addObject("shipList", shipList);
		view.addObject("shipTypeList", shipTypeList);
		view.addObject("payTypeList", payTypeList);
		view.addObject("status_Json", JSONArray.fromObject(statusList)
				.toString());
		view.addObject("payStatus_Json", JSONArray.fromObject(payStatusList)
				.toString());
		view.addObject("ship_Json", JSONArray.fromObject(shipList).toString());
		view.addObject("gridUrl", getGridUrl());
		view.setViewName("/shop/admin/order/not_pay");
		return view;
	}

	/**
	 * 未收货订单
	 * 
	 * @author LiFenLong
	 * @param statusMap
	 *            订单状态集合,Map
	 * @param payStatusMap
	 *            订单付款状态集合,Map
	 * @param shipMap
	 *            ,订单配送人状态集合,Map
	 * @param shipTypeList
	 *            配送方式集合,List
	 * @param payTypeList
	 *            付款方式集合,List
	 * @return 未收货订单列表
	 */
	@RequestMapping(value = "/not-rog-order")
	public ModelAndView notRogOrder() {

		ModelAndView view = getGridModelAndView();

		List statusList = OrderStatus.getOrderStatus();

		List payStatusList = OrderStatus.getPayStatus();

		List shipList = OrderStatus.getShipStatus();

		List<DlyType> shipTypeList = dlyTypeManager.list();
		List<PayCfg> payTypeList = paymentManager.list();

		view.addObject("statusList", statusList);
		view.addObject("payStatusList", payStatusList);
		view.addObject("shipList", shipList);
		view.addObject("shipTypeList", shipTypeList);
		view.addObject("payTypeList", payTypeList);

		view.addObject("status_Json", JSONArray.fromObject(statusList)
				.toString());
		view.addObject("payStatus_Json", JSONArray.fromObject(payStatusList)
				.toString());
		view.addObject("ship_Json", JSONArray.fromObject(shipList).toString());

		view.setViewName("/shop/admin/order/not_rog");
		return view;
	}

	/**
	 * @author LiFenLong
	 * @param stype
	 *            搜索状态, Integer
	 * @param keyword
	 *            搜索关键字,String
	 * @param start_time
	 *            开始时间,String
	 * @param end_time
	 *            结束时间,String
	 * @param sn
	 *            订单编号,String
	 * @param ship_name
	 *            订单收货人姓名,String
	 * @param status
	 *            订单状态,Integer
	 * @param paystatus
	 *            订单付款状态,Integer
	 * @param shipstatus
	 *            订单配送状态,Integer
	 * @param shipping_type
	 *            配送方式,Integer
	 * @param payment_id
	 *            付款方式,Integer
	 * @param order_state
	 *            订单状态_从哪个页面进来搜索的(未付款、代发货、等),String
	 * @param complete
	 *            是否订单为已完成,String
	 * @return 订单列表 json
	 */
	@ResponseBody
	@RequestMapping(value = "/list-json")
	public GridJsonResult listJson(String sn, String start_time,
			String end_time, String ship_name, Integer status,
			Integer paystatus, Integer shipstatus, Integer shipping_type,
			Integer payment_id, Integer stype, String keyword, String complete) {
		HttpServletRequest requst = ThreadContextHolder.getHttpRequest();
		Map orderMap = new HashMap();
		orderMap.put("stype", stype);
		orderMap.put("keyword", keyword);
		orderMap.put("start_time", start_time);
		orderMap.put("end_time", end_time);
		orderMap.put("sn", sn);
		orderMap.put("ship_name", ship_name);
		orderMap.put("status", status);
		orderMap.put("paystatus", paystatus);
		orderMap.put("shipstatus", shipstatus);
		orderMap.put("shipping_type", shipping_type);
		orderMap.put("payment_id", payment_id);
		orderMap.put("order_state", requst.getParameter("order_state"));
		orderMap.put("complete", complete);

		webpage = this.orderManager.listOrder(orderMap, this.getPage(),
				this.getPageSize(), this.getSort(), this.getOrder());
		return JsonResultUtil.getGridJson(webpage);

	}

	/**
	 * 回收站订单
	 * 
	 * @author LiFenLong
	 * @param statusMap
	 *            订单状态集合,Map
	 * @param payStatusMap
	 *            订单付款状态集合,Map
	 * @param shipMap
	 *            ,订单配送人状态集合,Map
	 * @param shipTypeList
	 *            配送方式集合,List
	 * @param payTypeList
	 *            付款方式集合,List
	 * @return 回收站订单列表
	 */
	@RequestMapping(value = "/trash-list")
	public ModelAndView trashList() {

		ModelAndView view = getGridModelAndView();

		List statusList = OrderStatus.getOrderStatus();

		List payStatusList = OrderStatus.getPayStatus();

		List shipList = OrderStatus.getShipStatus();

		view.addObject("status_Json", JSONArray.fromObject(statusList)
				.toString());
		view.addObject("payStatus_Json", JSONArray.fromObject(payStatusList)
				.toString());
		view.addObject("ship_Json", JSONArray.fromObject(shipList).toString());
		view.setViewName("/shop/admin/order/trash_list");
		return view;
	}

	/**
	 * @author LiFenLong
	 * @param stype
	 *            搜索状态, Integer
	 * @param keyword
	 *            搜索关键字,String
	 * @param start_time
	 *            开始时间,String
	 * @param end_time
	 *            结束时间,String
	 * @param sn
	 *            订单编号,String
	 * @param ship_name
	 *            订单收货人姓名,String
	 * @param status
	 *            订单状态,Integer
	 * @param paystatus
	 *            订单付款状态,Integer
	 * @param shipstatus
	 *            订单配送状态,Integer
	 * @param shipping_type
	 *            配送方式,Integer
	 * @param payment_id
	 *            付款方式,Integer
	 * @param sort
	 *            排序,Integer
	 * @return 回收站订单列表 json
	 */
	@ResponseBody
	@RequestMapping(value = "/trash-list-json")
	public GridJsonResult trashListJson() {

		webpage = this.orderManager.list(this.getPage(), this.getPageSize(), 1,
				this.getSort());
		return JsonResultUtil.getGridJson(webpage);
	}

	/**
	 * 将订单添加至回收站
	 * 
	 * @param order_id
	 *            订单Id数组,Integer[]
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/delete")
	public JsonResult delete(Integer[] order_id) {
		try {
			if (this.orderManager.delete(order_id)) {
				return JsonResultUtil.getSuccessJson("订单加入回收站成功");
			} else {
				return JsonResultUtil.getErrorJson("您所删除的订单包含未作废的订单，无法加入回收站");
			}
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			return JsonResultUtil.getErrorJson("订单删除失败" + e.getMessage());
		}
	}

	/**
	 * 将订单还原
	 * 
	 * @param order_id
	 *            订单号数组,Integer[]
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/revert")
	public JsonResult revert(Integer[] order_id) {
		try {
			this.orderManager.revert(order_id);
			return JsonResultUtil.getSuccessJson("订单还原成功");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			return JsonResultUtil.getErrorJson("订单还原失败:" + e.getMessage());
		}

	}

	/**
	 * 将订单清除
	 * 
	 * @param order_id
	 *            订单号数组,Integer[]
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/clean")
	public JsonResult clean(Integer[] order_id) {
		try {
			this.orderManager.clean(order_id);
			return JsonResultUtil.getSuccessJson("订单清除成功");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			return JsonResultUtil.getErrorJson("订单清除失败" + e.getMessage());

		}
	}

	/**
	 * 完成订单
	 * 
	 * @param orderId
	 *            订单号,Integer
	 * @param order
	 *            订单,Order
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "complete")
	public JsonResult complete(Integer orderId) {
		try {
			this.orderFlowManager.complete(orderId);
			Order order = this.orderManager.get(orderId);
			return JsonResultUtil.getSuccessJson("订单[" + order.getSn()
					+ "]成功标记为完成状态");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			return JsonResultUtil.getErrorJson("订单完成失败");
		}
	}

	/**
	 * 作废订单
	 * 
	 * @param orderId
	 *            订单号,Integer
	 * @param cancel_reason
	 *            作废原因,String
	 * @param order
	 *            订单,Order
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/cancel")
	public Object cancel(Integer orderId, String cancel_reason) {
		
		try {
			this.orderManager.addCancelApplicationAdmin(orderId, cancel_reason);
			return JsonResultUtil.getSuccessJson("取消订单成功");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			return JsonResultUtil.getErrorJson("订单作废失败:" + e.getMessage());
		}
	}

	/**
	 * 确定收货
	 * 
	 * @author LiFenLong
	 * @param adminUser
	 *            管理员,AdminUser
	 * @param orderId
	 *            订单Id,Integer
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/rog-confirm")
	public JsonResult rogConfirm(Integer orderId) {
		try {
			AdminUser adminUser = UserConext.getCurrentAdminUser();
			this.orderFlowManager.rogConfirm(orderId, adminUser.getUserid(),
					adminUser.getUsername(), adminUser.getUsername(),
					DateUtil.getDateline());
			return JsonResultUtil.getSuccessJson("确认收货成功");
		} catch (Exception e) {

			return JsonResultUtil.getSuccessJson("数据库错误");
		}
	}

	/**
	 * 确认订单
	 * 
	 * @param orderId
	 *            订单号,Integer
	 * @param order
	 *            订单,Order
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/confirm-order")
	public JsonResult confirmOrder(Integer orderId) {
		try {
			this.orderFlowManager.confirmOrder(orderId);
			Order order = this.orderManager.get(orderId);
			// this.orderFlowManager.addCodPaymentLog(order);
			return JsonResultUtil.getSuccessJson("'订单[" + order.getSn()
					+ "]成功确认'");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			return JsonResultUtil.getSuccessJson("订单确认失败" + e.getMessage());
		}
	}

	/**
	 * 发货弹出框
	 * 
	 * @author lk 2016/11/16
	 * @param orderId
	 * @return json
	 */
	@RequestMapping(value = "/ship-into")
	public ModelAndView showShipInto(Integer orderId) {
		Order ord = this.orderManager.get(orderId);
		ModelAndView view = new ModelAndView();
		view.addObject("ord", ord);
		view.setViewName("/shop/admin/order/order_into");
		return view;

	}

	/**
	 * 跳转至订单详细页面
	 * 
	 * @param uname
	 *            会员名称,String
	 * @param ship_name
	 *            收货人姓名,String
	 * @param orderId
	 *            订单号,Integer
	 * @param ord
	 *            订单,Order
	 * @param provinceList
	 *            省列表
	 * @param pluginTabs
	 *            订单详细页的选项卡
	 * @param pluginHtmls
	 *            订单详细页的内容
	 * @param dlycenterlist
	 *            发货信息列表
	 * @return 订单详细页面
	 */
	@RequestMapping(value = "/detail")
	public ModelAndView detail(String uname, String ship_name, Integer orderId) {

		ModelAndView view = new ModelAndView();
		Order ord = this.orderManager.get(orderId);

		view.addObject("ord", this.orderManager.get(orderId));
		view.addObject("provinceList", this.regionsManager.listProvince());
		view.addObject("pluginTabs", this.orderPluginBundle.getDetailHtml(ord));
		view.addObject("dlycenterlist", dlyCenterManager.list());
		view.addObject("orderId", orderId);

		view.setViewName("/shop/admin/order/order_detail");

		if (ship_name != null) {
			ship_name = StringUtil.toUTF8(ship_name);
			view.addObject("ship_name", ship_name);
		}

		if (uname != null) {
			uname = StringUtil.toUTF8(uname);
			view.addObject("uname", uname);
		}

		return view;
	}

	/**
	 * 下一订单，或者上一订单
	 * 
	 * @param orderId
	 *            订单号,Integer
	 * @param status
	 *            订单状态,Integer
	 * @param sn
	 *            订单编号,Integer
	 * @param uname
	 *            会员名称,String
	 * @param ship_name
	 *            收货人姓名,String
	 * @param listProvince
	 *            省列表,List
	 * @param pluginTabs
	 *            订单详细页的选项卡
	 * @param pluginHtmls
	 *            订单详细页的内容
	 * @return 订单详细页面
	 */
	@RequestMapping(value = "/next-detail")
	public ModelAndView nextDetail(String logi_no, String sn, String uname,
			String ship_name, Integer status, Integer orderId, String next) {
		ModelAndView view = new ModelAndView();

		Order ord = new Order();
		if (this.orderManager.getNext(next, orderId, status, 0, sn, logi_no,
				uname, ship_name) == null) {
			String alert_null = "kong";
			ord = this.orderManager.get(orderId);
			view.addObject("alert_null", alert_null);
			view.addObject("ord", ord);
		} else {
			ord = this.orderManager.getNext(next, orderId, status, 0, sn,
					logi_no, uname, ship_name);
			view.addObject("ord", ord);
		}

		List provinceList = this.regionsManager.listProvince();

		view.addObject("provinceList", provinceList);
		view.addObject("pluginTabs", orderPluginBundle.getDetailHtml(ord));
		return view;
	}

	/**
	 * 添加管理员备注
	 * 
	 * @param remark
	 *            备注内容
	 * @param orderId
	 *            订单号,Integer
	 * @param ord
	 *            订单,Order
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-admin-remark")
	public JsonResult saveAdminRemark(Integer orderId, String remark) {

		Order ord = this.orderManager.get(orderId);
		ord.setAdmin_remark(remark);
		try {
			this.orderManager.edit(ord);
			return JsonResultUtil.getSuccessJson("修改成功");
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			return JsonResultUtil.getErrorJson("修改失败");
		}
	}

	/**
	 * 修改会员———查询会员订单
	 * 
	 * @author xulipeng
	 * @param member_id
	 *            会员Id,Integer
	 * @param list
	 *            会员订单
	 * @return 会员订单json
	 */
	@ResponseBody
	@RequestMapping(value = "/list-order-by-member-id")
	public GridJsonResult listOrderByMemberId(Integer member_id) {

		Page list = this.orderManager.listByStatus(this.getPage(),
				this.getPageSize(), member_id);
		return JsonResultUtil.getGridJson(list);
	}

	/**
	 * 保存库房
	 * 
	 * @author LiFenLong
	 * @param orderId
	 *            订单Id,Integer
	 * @param depotid
	 *            库房id,Integer
	 * @param oldname
	 *            修改前库房名称
	 * @param depotname
	 *            修改后库房名称
	 * @param adminUser
	 *            管理员,AdminUser
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-depot")
	public JsonResult saveDepot(Integer orderId, Integer depotid) {

		try {
			String oldname = this.depotManager.get(
					this.orderManager.get(orderId).getDepotid()).getName();
			String depotname = this.depotManager.get(depotid).getName();
			this.orderManager.saveDepot(orderId, depotid);
			// 记录日志
			this.orderManager.addLog(orderId, "修改仓库从" + oldname + "修改为"
					+ depotname);
			return JsonResultUtil.getSuccessJson("保存库房成功");
		} catch (Exception e) {
			return JsonResultUtil.getErrorJson("保存库房出错:" + e.getMessage());
		}

	}

	/**
	 * 保存付款方式
	 * 
	 * @author LiFenLong
	 * @param orderId
	 *            订单Id,Integer
	 * @param paytypeid
	 *            付款方式Id,Integer
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-pay-type")
	public JsonResult savePayType(Integer orderId, Integer paytypeid) {
		try {
			this.orderManager.savePayType(orderId, paytypeid);
			return JsonResultUtil.getSuccessJson("保存支付方式成功");
		} catch (Exception e) {
			this.logger.error("保存支付方式出错", e);
			return JsonResultUtil.getSuccessJson("保存支付方式出错");
		}
	}

	/**
	 * 保存配送方式
	 * 
	 * @param orderId
	 *            订单Id,Integer
	 * @param shipping_id
	 *            配送方式Id,Integer
	 * @return json result 1。操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value = "/save-ship-type")
	public JsonResult saveShipType(Integer shipping_id, Integer orderId) {

		try {
			this.orderManager.saveShipType(orderId, shipping_id);
			return JsonResultUtil.getSuccessJson("保存配送方式成功");
		} catch (Exception e) {
			this.logger.error("保存配送方式出错", e);
			return JsonResultUtil.getErrorJson("保存配送方式出错");
		}
	}

	/**
	 * 填写快递单号
	 * 
	 * @return
	 */
	@RequestMapping(value = "/list-for-express-no")
	public ModelAndView listForExpressNo() {
		ModelAndView view = new ModelAndView();
		HttpServletRequest requst = ThreadContextHolder.getHttpRequest();
		String ordersId = requst.getParameter("orderIds");
		String[] order_idstr = ordersId.split(",");
		int count = 1;

		// 如果字符串中有c
		while (ordersId.indexOf(",") != -1) {
			count++;
			ordersId = ordersId.substring(ordersId.indexOf(",") + 1);
		}
		Integer[] order_idstrint = new Integer[count];
		for (int i = 0; i < order_idstr.length; i++) {
			order_idstrint[i] = Integer.parseInt(order_idstr[i]);
		}
		List orderList = this.orderManager.listByOrderIds(order_idstrint, null);
		if (!orderList.isEmpty()) {
			boolean isSame = true;
			Order first = (Order) orderList.get(0);
			int firstShip = first.getShipping_id();
			for (Object o : orderList) {
				Order ord = (Order) o;
				if (ord.getShipping_id() != firstShip) {
					isSame = false;
					break;
				}
			}
			Map params = new HashMap();
			if (isSame) {
				params.put("ship_type", "" + firstShip);
			} else {
				params.put("ship_type", "not_same");
			}
		}
		view.addObject("orderList", orderList);
		view.addObject("logi_list", logiManager.list());
		view.setViewName("/shop/admin/order/listForExpressNo");
		return view;
	}

	/**
	 * 取消订单申请列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/cancel-application-list")
	public ModelAndView cancelApplicationList() {
		ModelAndView view = this.getGridModelAndView();
		List statusList = OrderStatus.getOrderStatus();
		view.addObject("status_Json", JSONArray.fromObject(statusList)
				.toString());
		view.setViewName("/shop/admin/orderReport/cancel_application");
		return view;
	}

	/**
	 * 取消订单申请JSON列表
	 */
	@ResponseBody
	@RequestMapping(value = "/cancel-application-list-json")
	public GridJsonResult cancelApplicationListJson() {
		this.webpage = orderManager.getCancelApplicationList(this.getPage(),
				this.getPageSize());
		return JsonResultUtil.getGridJson(webpage);
	}

	/**
	 * 取消订单申请详细
	 * 
	 * @param order_id
	 * @return
	 */
	@RequestMapping(value = "/cancel-detail")
	public ModelAndView cancelDetail(Integer order_id) {
		List itemList = this.orderManager.listGoodsItems(order_id); // 订单商品列表
		ModelAndView view = new ModelAndView();
		view.addObject("order", orderManager.get(order_id));
		view.addObject("itemList", itemList);
		view.setViewName("/shop/admin/orderReport/auth_cancel");
		return view;
	}

	/**
	 * 审核取消订单申请
	 * 
	 * @param order_id
	 *            订单Id
	 * @param status
	 *            状态 0,拒绝 1,通过
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/auth-cancel-application")
	public JsonResult authCancelApplication(Integer order_id, Integer status) {
		try {
			orderManager.authCancelApplication(order_id, status);
			return JsonResultUtil.getSuccessJson("审核成功");
		} catch (Exception e) {
			this.logger.error("审核取消订单申请出错", e);
			return JsonResultUtil.getErrorJson("审核失败:" + e.getMessage());
		}
	}
}
