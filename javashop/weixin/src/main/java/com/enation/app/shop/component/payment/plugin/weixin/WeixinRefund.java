package com.enation.app.shop.component.payment.plugin.weixin;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

/**
 * 
 * 微信退款接口实现
 * @author zh
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月28日 上午11:21:09
 */
@Component
public class WeixinRefund{


	@Autowired
	private IPaymentManager paymentManager;


	/**
	 * 微信退款实现
	 */
	public String onRefund(PayEnable order,Refund refund,PaymentLog paymentLog) {
		Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());;
		String mchid = cfgparams.get("mchid");
		String appid = cfgparams.get("appid");
		String key = cfgparams.get("key");
		Map params = new TreeMap();
		/** 微信分配的公众账号ID（企业号corpid即为此appId）*/
		params.put("appid", appid);
		/**微信支付分配的商户号*/
		params.put("mch_id", mchid);
		/**随机字符串，不长于32位*/
		params.put("nonce_str", StringUtil.getRandStr(10));
		/** 商户系统内部订单号 */
		params.put("transaction_id",paymentLog.getTrasaction_id());
		/** 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母 */
		String outRefundNo=order.getSn()+StringUtil.getRandStr(6);//"20141104000003789253";
		params.put("out_refund_no",outRefundNo);
		/** 应付转为分 */
		Double money = order.getNeedPayMoney();
		/** 订单金额 */
		params.put("total_fee", toFen(money));
		/** 退款金额 */
		params.put("refund_fee", toFen(refund.getRefund_money()));
		/** 签名	 */
		String sign = WeixinUtil.createSign(params, key);
		params.put("sign", sign);	
		try {
			String xml = WeixinUtil.mapToXml(params);
			Document resultDoc = WeixinUtil.verifyCertPost("https://api.mch.weixin.qq.com/secapi/pay/refund", xml,mchid);
			/** 调试时可打开此处注释，以观察微信返回的数据 **/
			// this.logger.debug("-----------return xml ------------");
			// this.logger.debug( WeixinUtil.doc2String(resultDoc) );
			Element rootEl = resultDoc.getRootElement();
			/** 返回码 */
			String return_code = rootEl.element("return_code").getText(); // 返回码
			if ("SUCCESS".equals(return_code)) {
				return "SUCCESS";
			} else {
				return "FAIL";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 *  将元单位转换成分单位
	 * @param money	金额
	 * @return
	 */
	private String toFen(Double money) {
		BigDecimal fen=   BigDecimal.valueOf(money).multiply(new BigDecimal(100)) ;
		NumberFormat nFormat=NumberFormat.getNumberInstance();
        nFormat.setMaximumFractionDigits(0); 
        nFormat.setGroupingUsed(false);
		return  nFormat.format(fen);
	}




	public String getId() {

		return "weixinPayPlugin";
	}


	public String getName() {
		return "微信支付";
	}
}
