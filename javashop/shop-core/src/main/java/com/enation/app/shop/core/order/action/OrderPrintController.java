package com.enation.app.shop.core.order.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.service.ILogiManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IOrderPrintManager;
import com.enation.app.shop.core.order.service.IOrderReportManager;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonResultUtil;

/**
 * 订单发货 Action
 * 
 * @author lina
 * 
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/order-print")
public class OrderPrintController {

	@Autowired
	private IOrderPrintManager orderPrintManager;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private ILogiManager logiManager;
	@Autowired
	private IOrderReportManager orderReportManager;

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 订单发货Action
	 * 
	 * @param order_id
	 *            订单号数组,Integer[]
	 * @return json result 1,操作成功.0,操作失败
	 */
	@ResponseBody
	@RequestMapping("/ship")
	public JsonResult ship(Integer[] order_id) {
		try {
			int result = this.orderPrintManager.checkShipNo(order_id);
			if (result == order_id.length) {
				String is_ship = orderPrintManager.ship(order_id);
				if (is_ship.equals("true")) {
					return JsonResultUtil.getSuccessJson("发货成功");
				} else {
					return JsonResultUtil.getErrorJson(is_ship);
				}
			} else {
				return JsonResultUtil.getErrorJson("请先保存快递单号，在进行发货操作！");
			}
		} catch (Exception e) {
			this.logger.error("发货出错", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}

	/**
	 * 订单发货单保存win
	 * 
	 * @param order_id
	 *            订单号数组,Integer[]
	 * @return json result 1,操作成功.0,操作失败
	 */
	@RequestMapping("/ship-win")
	public ModelAndView shipWin(int orderid) {
		Order order = orderManager.get(orderid);

		ModelAndView view = new ModelAndView();
		view.addObject("order", order);
		view.addObject("logiList", logiManager.list());

		view.setViewName("/shop/admin/sign/signwin");
		return view;
	}

	/**
	 * 订单批量发货Action
	 * 
	 * @author DMRain 2015-12-15
	 * @param order_id
	 *            订单号数组,Integer[]
	 * @return json result 1,操作成功.0,操作失败
	 */
	@ResponseBody
	@RequestMapping("/batch-ship")
	public JsonResult batchShip(Integer[] order_id) {
		try {
			int result = this.orderPrintManager.checkShipNo(order_id);
			if (result == order_id.length) {
				String is_ship = orderPrintManager.ship(order_id);
				if (is_ship.equals("true")) {
					return JsonResultUtil.getSuccessJson("发货成功");
				} else {
					return JsonResultUtil.getErrorJson(is_ship);
				}
			} else {
				return JsonResultUtil.getErrorJson("您有"
						+ (order_id.length - result)
						+ "条订单没有填写快递单号，请填写完整再进行发货！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("发货出错", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}

	/**
	 * 保存发货单号
	 * 
	 * @param order_id
	 *            订单号数组,Integer[]
	 * @param expressno
	 *            物流单号数组,String[]
	 * @return json result 1,操作成功.0,操作失败
	 */
	@ResponseBody
	@RequestMapping("/save-ship-no")
	public JsonResult saveShipNo(Integer[] order_id, String[] expressno,
			String[] logi_id, String logi_name) {
		try {

			String[] logiName = logi_name.toString().split(",");
			this.orderPrintManager.saveShopNos(order_id, expressno, logi_id,
					logiName);
			return JsonResultUtil.getSuccessJson("保存发货单号成功");
		} catch (Exception e) {
			this.logger.error("保存发货单号出错", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}

	}

	/**
	 * 发货发货弹出框
	 * 
	 * @author lk 2016/11/16
	 * @param orderId
	 * @return json
	 */
	@RequestMapping("/ship-into")
	public ModelAndView showShipInto(Integer orderId) {
		Order ord = this.orderManager.get(orderId);
		ModelAndView view = new ModelAndView();
		view.addObject("ord", ord);
		view.setViewName("/b2b2c/admin/order/order_into");
		return view;

	}

	/**
	 * 打印快递单
	 * 
	 * @author LiFenLong
	 * @param order_id
	 *            订单号数组,Integer[]
	 * @param script
	 *            打印的script,String
	 * @return json result 1,操作成功.0,操作失败 script 打印的script
	 */
	@ResponseBody
	@RequestMapping("/express-script")
	public Object expressScript(Integer[] order_id) {
		String script = orderPrintManager.getExpressScript(order_id);
		if (script.equals("快递单选择配送方式不同") || script.equals("请添加配送方式")
				|| script.equals("没有此快递单模板请添加") || script.equals("请选择默认发货点")) {
			return JsonResultUtil.getErrorJson(script);
		} else {
			Map map = new HashMap();
			map.put("script", script);
			map.put("result", 1);
			return map;
		}
	}

	/**
	 * 打印发货单
	 * 
	 * @author LiFenLong
	 * @param order_id
	 *            订单号数组,Integer[]
	 * @param script
	 *            打印的script,String
	 * @return 发货单的script
	 */
	@ResponseBody
	@RequestMapping("/ship-script")
	public String shipScript(Integer[] order_id) {
		return orderPrintManager.getShipScript(order_id);
	}

}
