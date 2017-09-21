package com.enation.app.shop.component.payment.plugin.unionpay;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.chainsaw.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.alipay.AlipayRefund;
import com.enation.app.shop.component.payment.plugin.unionpay.sdk.AcpService;
import com.enation.app.shop.component.payment.plugin.unionpay.sdk.LogUtil;
import com.enation.app.shop.component.payment.plugin.unionpay.sdk.SDKConfig;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.order.plugin.payment.IRefundEvent;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.IRefundManager;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 
 * (中国银联退款)
 * 
 * @author zjp
 * @version v6.5.0
 * @since v6.5.1 2017年7月13日 下午4:01:44
 */
@Component
public class UnionpayRefund {
	public static String encoding = "UTF-8";

	@Autowired
	private IRefundManager refundManager;
	/**
	 * 5.0.0
	 */
	public static String version = "5.0.0";

	private static int is_load = 0;

	@Autowired
	protected IPaymentManager paymentManager;

	@Autowired
	private IOrderManager orderManager;

	public String onRefund(PayEnable order, Refund refund, PaymentLog paymentLog) {

		Map<String, String> params = paymentManager.getConfigParams(this.getId());
		String merId = params.get("merId");

		try {
			this.loadConfig(params);
		} catch (RuntimeException e) {
			return e.getMessage();
		}

		/**
		 * 组装请求报文
		 */
		Map<String, String> data = new HashMap<String, String>();
		// 版本号
		data.put("version", "5.0.0");
		// 字符集编码 默认"UTF-8"
		data.put("encoding", "UTF-8");
		// 签名方法 01 RSA
		data.put("signMethod", "01");
		// 交易类型 04-退货
		data.put("txnType", "04");
		// 交易子类型 01:自助消费 02:订购 03:分期付款
		data.put("txnSubType", "00");
		// 业务类型
		data.put("bizType", "000201");
		// 渠道类型，07-PC，08-手机
		data.put("channelType", "07");
		/**
		 * 因中国银联5.0退款接口只有在退款成功的情况下才会回调，如果退款失败则无法回调，所以不通过异步回调获取退款状态
		 * 通过查询退款状态交易查询接口获取退款状态
		 */
		// 后台通知地址

		data.put("backUrl", "http://www.specialurl.com");
		// 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
		data.put("accessType", "0");
		// 商户号码，请改成自己的商户号
		data.put("merId", merId);
		// 商户订单号，8-40位数字字母
		// 因为 银联要求的订单号不能有-号，在javashop中的子订单是有这个符号的，所以改用orderid
		String orderid = "AAAAAAAAA" + order.getOrder_id();

		int length = orderid.length();
		// 保证不小于8位，且不大于40位
		orderid = orderid.substring((length - 40) < 0 ? 0 : length - 40, length);
		// 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
		data.put("orderId", orderid);
		// 订单发送时间，取系统时间
		String txnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		/** 添加退款发起时间 */
		refundManager.addRefundTxntime(refund.getId(), txnTime);
		data.put("txnTime", txnTime);
		// 交易金额，单位分
		Double txnAmt = CurrencyUtil.mul(refund.getRefund_money(), 100);

		data.put("txnAmt", "" + txnAmt.intValue());
		// 交易币种
		data.put("currencyCode", "156");
		/*** 要调通交易以下字段必须修改 ***/
		data.put("origQryId", paymentLog.getTrasaction_id()); // ****原消费交易返回的的queryId，可以从消费交易后台通知接口中或者交易状态查询接口中获取
		// 请求方保留域，透传字段，查询、通知、对账文件中均会原样出现
		// data.put("reqReserved", "透传信息");

		Map<String, String> reqData = AcpService.sign(data, encoding); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

		// 交易请求url 从配置文件读取
		String url = SDKConfig.getConfig().getBackRequestUrl();

		Map<String, String> rspData = AcpService.post(reqData, url, encoding);// 这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

		/** 对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考-------------> **/
		// 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
		if (!rspData.isEmpty()) {
			if (AcpService.validate(rspData, "UTF-8")) {
				LogUtil.writeLog("验证签名成功");
				String respCode = rspData.get("respCode");
				if ("00".equals(respCode)) {
					// 交易已受理，等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。
					return "SUCCESS";
				} else if ("03".equals(respCode) || "04".equals(respCode) || "05".equals(respCode)) {
					// 后续需发起交易状态查询交易确定交易状态
					return "SUCCESS";
				} else {
					return "FAIL";
				}
			} else {
				LogUtil.writeErrorLog("验证签名失败");
				// TODO 检查验证签名失败的原因
			}
		} else {
			// 未返回正确的http状态
			LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
		}

		return "SUCCESS";

	}

	/**
	 * 加载参数配置 思路：先加载默认的properties中的，然后再将用户配置的覆盖过去
	 * 
	 * @param params
	 */
	private synchronized void loadConfig(Map<String, String> params) {

		// 是否是测试模式
		String testModel = params.get("testModel");

		// 如果已经加载，并且是生产环境则不用再加载
		if (is_load == 1 && "no".equals(testModel)) {
			return;
		}

		// 签名证书路径
		String signCert = params.get("signCert");

		// 签名证书密码
		String pwd = params.get("pwd");

		// 验证签名证书目录
		String validateCert = params.get("validateCert");

		// 敏感信息加密证书路径
		String encryptCert = params.get("encryptCert");

		if (StringUtil.isEmpty(signCert)) {
			throw new RuntimeException("参数配置错误");
		}
		if (StringUtil.isEmpty(pwd)) {
			throw new RuntimeException("参数配置错误");
		}
		if (StringUtil.isEmpty(validateCert)) {
			throw new RuntimeException("参数配置错误");
		}

		InputStream in = FileUtil
				.getResourceAsStream("com/enation/app/shop/component/payment/plugin/unionpay/acp_sdk.properties");
		Properties pro = new Properties();
		try {

			pro.load(in);
			pro.setProperty("acpsdk.signCert.path", signCert);
			pro.setProperty("acpsdk.signCert.pwd", pwd);
			pro.setProperty("acpsdk.validateCert.dir", validateCert);

			// 设置测试环境的提交地址
			if ("yes".equals(testModel)) {
				pro.setProperty("acpsdk.frontTransUrl", "https://gateway.test.95516.com/gateway/api/frontTransReq.do");
				pro.setProperty("acpsdk.backTransUrl", "https://gateway.test.95516.com/gateway/api/backTransReq.do");
				pro.setProperty("acpsdk.singleQueryUrl", "https://gateway.test.95516.com/gateway/api/queryTrans.do");
				pro.setProperty("acpsdk.batchTransUrl", "https://gateway.test.95516.com/gateway/api/batchTrans.do");
				pro.setProperty("acpsdk.fileTransUrl", "https://filedownload.test.95516.com/");
				pro.setProperty("acpsdk.appTransUrl", "https://gateway.test.95516.com/gateway/api/appTransReq.do");
				pro.setProperty("acpsdk.cardTransUrl", "https://gateway.test.95516.com/gateway/api/cardTransReq.do");
			}

			// 设置生产环境的提交地址
			if ("no".equals(testModel)) {
				pro.setProperty("acpsdk.frontTransUrl", "https://gateway.95516.com/gateway/api/frontTransReq.do");
				pro.setProperty("acpsdk.backTransUrl", "https://gateway.95516.com/gateway/api/backTransReq.do");
				pro.setProperty("acpsdk.singleQueryUrl", "https://gateway.95516.com/gateway/api/queryTrans.do");
				pro.setProperty("acpsdk.batchTransUrl", "https://gateway.95516.com/gateway/api/batchTrans.do");
				pro.setProperty("acpsdk.fileTransUrl", "https://filedownload.95516.com/");
				pro.setProperty("acpsdk.appTransUrl", "https://gateway.95516.com/gateway/api/appTransReq.do");
				pro.setProperty("acpsdk.cardTransUrl", "https://gateway.95516.com/gateway/api/cardTransReq.do");

			}

			if (!StringUtil.isEmpty(encryptCert)) {
				pro.setProperty("acpsdk.encryptCert.path", encryptCert);
			}

			SDKConfig.getConfig().loadProperties(pro);

		} catch (IOException e) {
			e.printStackTrace();
		}

		is_load = 1;

	}

	public String getId() {
		return "unPay";
	}

	public String getName() {
		return "中国银联支付";
	}
}
