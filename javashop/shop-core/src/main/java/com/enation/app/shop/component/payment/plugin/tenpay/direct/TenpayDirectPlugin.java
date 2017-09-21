package com.enation.app.shop.component.payment.plugin.tenpay.direct;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;
import com.tenpay.RequestHandler;
import com.tenpay.ResponseHandler;
import com.tenpay.client.ClientResponseHandler;
import com.tenpay.client.TenpayHttpClient;
import com.tenpay.util.TenpayUtil;

/**
 * 财付通即时到账支付插件
 * 
 * @author kingapex 2012-3-30上午10:28:15
 */
@Component
public class TenpayDirectPlugin extends AbstractPaymentPlugin implements
		IPaymentEvent {

	@Override
	public String onCallBack(String ordertype) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		Map<String, String> params = this.getConfigParams();

		String key = params.get("tenpaykey"); // "8934e7d15453e97507ef794cf7b0519d";
		String partner = params.get("tenpaybid");
		String sn=null;

		// ---------------------------------------------------------
		// 财付通支付通知（后台通知）示例，商户按照此文档进行开发即可
		// ---------------------------------------------------------

		// 创建支付应答对象
		ResponseHandler resHandler = new ResponseHandler(request, response);
		resHandler.setKey(key);

		System.out.println("后台回调返回参数:" + resHandler.getAllParameters());

		try {
			// 判断签名
			if (resHandler.isTenpaySign()) {

				// 通知id
				String notify_id = resHandler.getParameter("notify_id");

				// 创建请求对象
				RequestHandler queryReq = new RequestHandler(null, null);
				// 通信对象
				TenpayHttpClient httpClient = new TenpayHttpClient();
				// 应答对象
				ClientResponseHandler queryRes = new ClientResponseHandler();

				// 通过通知ID查询，确保通知来至财付通
				queryReq.init();
				queryReq.setKey(key);
				queryReq.setGateUrl("https://gw.tenpay.com/gateway/simpleverifynotifyid.xml");
				queryReq.setParameter("partner", partner);
				queryReq.setParameter("notify_id", notify_id);

				// 通信对象
				httpClient.setTimeOut(5);
				// 设置请求内容

				httpClient.setReqContent(queryReq.getRequestURL());
				System.out.println("验证ID请求字符串:" + queryReq.getRequestURL());

				// 后台调用
				if (httpClient.call()) {
					// 设置结果参数
					queryRes.setContent(httpClient.getResContent());
					System.out.println("验证ID返回字符串:"
							+ httpClient.getResContent());
					queryRes.setKey(key);

					// 获取id验证返回状态码，0表示此通知id是财付通发起
					String retcode = queryRes.getParameter("retcode");

					// 商户订单号
					String out_trade_no = resHandler.getParameter("out_trade_no");
					// 财付通订单号
					String transaction_id = resHandler
							.getParameter("transaction_id");
					// 金额,以分为单位
					String total_fee = resHandler.getParameter("total_fee");
					// 如果有使用折扣券，discount有值，total_fee+discount=原请求的total_fee
					String discount = resHandler.getParameter("discount");
					// 支付结果
					String trade_state = resHandler.getParameter("trade_state");
					// 交易模式，1即时到账，2中介担保
					String trade_mode = resHandler.getParameter("trade_mode");

					// 判断签名及结果
					if (queryRes.isTenpaySign() && "0".equals(retcode)) {
						System.out.println("id验证成功");

						if ("1".equals(trade_mode)) { // 即时到账
							if ("0".equals(trade_state)) {
								// ------------------------------
								// 即时到账处理业务开始
								// ------------------------------

								sn = out_trade_no;
								paySuccess(sn, transaction_id, "",ordertype);
								
								// 处理数据库逻辑
								// 注意交易单不要重复处理
								// 注意判断返回金额

								// ------------------------------
								// 即时到账处理业务完毕
								// ------------------------------

								System.out.println("即时到账支付成功");
								// 给财付通系统发送成功信息，财付通系统收到此结果后不再进行后续通知
								resHandler.sendToCFT("success");
								
								return "";
								
							} else {
								System.out.println("即时到账支付失败");
								resHandler.sendToCFT("fail");
							}
						} else if ("2".equals(trade_mode)) { // 中介担保
							// ------------------------------
							// 中介担保处理业务开始
							// ------------------------------

							// 处理数据库逻辑
							// 注意交易单不要重复处理
							// 注意判断返回金额

							int iStatus = TenpayUtil.toInt(trade_state);
							switch (iStatus) {
							case 0: // 付款成功

								break;
							case 1: // 交易创建

								break;
							case 2: // 收获地址填写完毕

								break;
							case 4: // 卖家发货成功

								break;
							case 5: // 买家收货确认，交易成功

								break;
							case 6: // 交易关闭，未完成超时关闭

								break;
							case 7: // 修改交易价格成功

								break;
							case 8: // 买家发起退款

								break;
							case 9: // 退款成功

								break;
							case 10: // 退款关闭

								break;
							default:
							}

							// ------------------------------
							// 中介担保处理业务完毕
							// ------------------------------

							System.out.println("trade_state = " + trade_state);
							// 给财付通系统发送成功信息，财付通系统收到此结果后不再进行后续通知
							resHandler.sendToCFT("success");
							return "";
						}
					} else {
						// 错误时，返回结果未签名，记录retcode、retmsg看失败详情。
						System.out.println("查询验证签名失败或id验证失败" + ",retcode:"
								+ queryRes.getParameter("retcode"));
					}
				} else {
					System.out.println("后台调用通信失败");
					System.out.println(httpClient.getResponseCode());
					System.out.println(httpClient.getErrInfo());
					// 有可能因为网络原因，请求已经处理，但未收到应答。
				}

			} else {
				System.out.println("通知签名验证失败");
			}
		} catch (Exception e) {
				
		}

		return null;
	}

	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		Map<String, String> params = paymentManager.getConfigParams(this
				.getId());

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		// //////////////////////////////////请求参数//////////////////////////////////////

		// 商品描述
		String desc = "商品：订单(" + order.getSn() + ")中的商品,备注：网店订单";

		// 当前时间 yyyyMMddHHmmss
		String currTime = TenpayUtil.getCurrTime();

		// 商家订单号
		String strReq = order.getSn();

		// 商户号
		String partner = params.get("tenpaybid");

		// 密钥
		String key = params.get("tenpaykey");

		// 回调通知URL
		String notify_url = this.getCallBackUrl(payCfg, order);

		String return_url = this.getReturnUrl(payCfg, order);

		// 创建支付请求对象
		RequestHandler reqHandler = new RequestHandler(request, response);
		reqHandler.init();
		// 设置密钥
		reqHandler.setKey(key);
		// 设置支付网关
		reqHandler.setGateUrl("https://gw.tenpay.com/gateway/pay.htm");

		// -----------------------------
		// 设置支付参数
		// -----------------------------
		reqHandler.setParameter("partner", partner); // 商户号
		reqHandler.setParameter("out_trade_no", strReq); // 商家订单号
		reqHandler.setParameter("total_fee", "1"); // 商品金额,以分为单位
		reqHandler.setParameter("return_url", return_url); // 交易完成后跳转的URL
		reqHandler.setParameter("notify_url", notify_url); // 接收财付通通知的URL

		reqHandler.setParameter("body", desc); // 商品描述
		reqHandler.setParameter("bank_type", "DEFAULT"); // 银行类型(中介担保时此参数无效)
		reqHandler.setParameter("spbill_create_ip", request.getRemoteAddr()); // 用户的公网ip，不是商户服务器IP
		reqHandler.setParameter("fee_type", "1"); // 币种，1人民币
		reqHandler.setParameter("subject", "测试"); // 商品名称(中介交易时必填)

		// 系统可选参数
		reqHandler.setParameter("sign_type", "MD5"); // 签名类型,默认：MD5
		reqHandler.setParameter("service_version", "1.0"); // 版本号，默认为1.0
		reqHandler.setParameter("input_charset", "utf-8"); // 字符编码
		reqHandler.setParameter("sign_key_index", "1"); // 密钥序号

		// 业务可选参数
		reqHandler.setParameter("trade_mode", "1"); // 交易模式，1即时到账(默认)，2中介担保，3后台选择（买家进支付中心列表选择）
		reqHandler.setParameter("attach", ""); // 附加数据，原样返回
		reqHandler.setParameter("product_fee", ""); // 商品费用，必须保证transport_fee +
													// product_fee=total_fee
		reqHandler.setParameter("transport_fee", ""); // 物流费用，必须保证transport_fee
														// +
														// product_fee=total_fee
		reqHandler.setParameter("time_start", currTime); // 订单生成时间，格式为yyyymmddhhmmss
		reqHandler.setParameter("time_expire", ""); // 订单失效时间，格式为yyyymmddhhmmss
		reqHandler.setParameter("buyer_id", ""); // 买方财付通账号
		reqHandler.setParameter("goods_tag", ""); // 商品标记
		reqHandler.setParameter("transport_desc", ""); // 物流说明
		reqHandler.setParameter("trans_type", "1"); // 交易类型，1实物交易，2虚拟交易
		reqHandler.setParameter("agentid", ""); // 平台ID
		reqHandler.setParameter("agent_type", ""); // 代理模式，0无代理(默认)，1表示卡易售模式，2表示网店模式
		reqHandler.setParameter("seller_id", ""); // 卖家商户号，为空则等同于partner

		// 获取请求带参数的url
		String requestUrl = "";
		try {
			requestUrl = reqHandler.getRequestURL();
			reqHandler.doSend();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 获取debug信息
		String debuginfo = reqHandler.getDebugInfo();

		return requestUrl;
	}

	@Override
	public String onReturn(String ordertype) {
		Map<String, String> params = paymentManager.getConfigParams(this
				.getId());
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();

		String sn = null;
		// ---------------------------------------------------------
		// 财付通支付应答（处理回调）示例，商户按照此文档进行开发即可
		// ---------------------------------------------------------

		// 创建支付应答对象
		ResponseHandler resHandler = new ResponseHandler(request, response);
		resHandler.setKey(params.get("tenpaykey"));

		System.out.println("前台回调返回参数:" + resHandler.getAllParameters());
		// 判断签名
		if (resHandler.isTenpaySign()) {

			// 通知id
			String notify_id = resHandler.getParameter("notify_id");
			// 商户订单号
			String out_trade_no = resHandler.getParameter("out_trade_no");
			// 财付通订单号
			String transaction_id = resHandler.getParameter("transaction_id");
			// 金额,以分为单位
			String total_fee = resHandler.getParameter("total_fee");
			// 如果有使用折扣券，discount有值，total_fee+discount=原请求的total_fee
			String discount = resHandler.getParameter("discount");
			// 支付结果
			String trade_state = resHandler.getParameter("trade_state");
			// 交易模式，1即时到账，2中介担保
			String trade_mode = resHandler.getParameter("trade_mode");

			if ("1".equals(trade_mode)) { // 即时到账
				if ("0".equals(trade_state)) {
					// ------------------------------
					// 即时到账处理业务开始
					// ------------------------------
					sn = out_trade_no;
					paySuccess(sn, transaction_id, "",ordertype);

					// 注意交易单不要重复处理
					// 注意判断返回金额

					// ------------------------------
					// 即时到账处理业务完毕
					// ------------------------------
					System.out.println("即时到帐付款成功");
				} else {
					System.out.println("即时到帐付款失败");
				}
			} else if ("2".equals(trade_mode)) { // 中介担保
				if ("0".equals(trade_state)) {
					// ------------------------------
					// 中介担保处理业务开始
					// ------------------------------

					sn = out_trade_no;
					paySuccess(sn, transaction_id,"", ordertype);
					// 注意交易单不要重复处理
					// 注意判断返回金额

					// ------------------------------
					// 中介担保处理业务完毕
					// ------------------------------

					System.out.println("中介担保付款成功");
				} else {
					System.out.println("trade_state=" + trade_state);
				}
			}
		} else {
			System.out.println("认证签名失败");
		}

		// 获取debug信息,建议把debug信息写入日志，方便定位问题
		String debuginfo = resHandler.getDebugInfo();
		return sn;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "tenpayDirectPlugin";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "财付通即时到账接口";
	}

	@Override
	public String onRefund(PayEnable order, Refund refund, PaymentLog paymentLog) {
		// TODO Auto-generated method stub
		return null;
	}


}
