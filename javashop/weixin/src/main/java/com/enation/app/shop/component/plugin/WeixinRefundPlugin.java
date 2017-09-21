package com.enation.app.shop.component.plugin;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryHourExecuteEvent;
import com.enation.app.shop.component.payment.plugin.weixin.WeixinUtil;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.SellBackStatus;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentLogManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;
/**
 * 
 * 微信定时任务查询退款进度
 * @author zh
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月28日 下午6:14:47
 */
@Component
public class WeixinRefundPlugin extends AutoRegisterPlugin implements IEveryHourExecuteEvent{


	@Autowired
	private ISellBackManager sellBackManager;

	@Autowired
	private IOrderManager orderManager;

	@Autowired
	private IDaoSupport daoSupport;

	@Autowired
	private IPaymentLogManager paymentLogManager;

	@Autowired
	private IPaymentManager paymentManager;


	@Override
	public void everyHour() {
		/** 查询状态为退款中的 退款方式为微信支付的 退款单 */
		String sql = "select * from es_refund where status = 2 and refund_type ='weixinPayPlugin'" ;
		List<Refund> refunds= this.daoSupport.queryForList(sql, Refund.class);
		/** 查询进度  */
		if(refunds.size() > 0){
			for (int i = 0; i < refunds.size(); i++) {
				/** 根据订单获取收款单信息 */
				PaymentLog paymentLog=paymentLogManager.get(refunds.get(i).getOrder_id());
				/** 退款进度查询进度 */
				String result = this.onRefundQuery(paymentLog);
				if("SUCCESS".equals(result)){
					//退款成功
					/**
					 * 修改退款单状态
					 */
					this.daoSupport.execute("update es_refund set status = ? where id = ?",1,refunds.get(i).getId());
					/**
					 * 修改退货单状态
					 */
					this.daoSupport.execute("update es_sellback_list set tradestatus=? where id=? ",SellBackStatus.refund.getValue(),refunds.get(i).getSellback_id());
					/**
					 * 记录订单日志
					 */
					orderManager.addLog(refunds.get(i).getOrder_id(), "已退款，金额："+refunds.get(i).getRefund_money());

					/**
					 * 记录维权订单日志
					 */
					sellBackManager.saveLogBySystem(refunds.get(i).getSellback_id(), "已退款，金额："+refunds.get(i).getRefund_money());
				}else if("PROCESSING".equals(result)){
					//退款中无操作
				}else{
					//退款失败
					/**
					 * 记录订单日志
					 */
					orderManager.addLog(refunds.get(i).getOrder_id(), "退款失败，金额："+refunds.get(i).getRefund_money());
					/**
					 * 记录维权订单日志
					 */
					sellBackManager.saveLogBySystem(refunds.get(i).getSellback_id(), "退款失败，金额："+refunds.get(i).getRefund_money());
					/**
					 * 修改退款单状态
					 */
					this.daoSupport.execute("update es_refund set status = 3 where id = ?", refunds.get(i).getId());
					/**
					 * 修改退货单状态
					 */
					this.daoSupport.execute("update es_sellback_list set tradestatus=? where id=?",SellBackStatus.payment.getValue(),refunds.get(i).getSellback_id());

				}
			}
		}
	}
	/**
	 * 
	 * @param paymentLog	收款单
	 * @param i				下标
	 * @return
	 */
	public String onRefundQuery(PaymentLog paymentLog) {
		Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());

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
		/** 签名	 */
		String sign = WeixinUtil.createSign(params, key);
		params.put("sign", sign);
		try {
			String xml = WeixinUtil.mapToXml(params);
			Document resultDoc = WeixinUtil.post("https://api.mch.weixin.qq.com/pay/refundquery", xml);
			/** 调试时可打开此处注释，以观察微信返回的数据 **/
			// this.logger.debug("-----------return xml ------------");
			// this.logger.debug( WeixinUtil.doc2String(resultDoc) );
			Element rootEl = resultDoc.getRootElement();
			/** 返回码 */
			String return_code = rootEl.element("return_code").getText(); // 返回码
			if ("SUCCESS".equals(return_code)) {
				/** 实际退款状态 */
				String status = rootEl.element("refund_status_0").getText(); // 返回码
				return status;
			} else {
				return "FAIL";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	public String getId() {

		return "weixinPayPlugin";
	}


	public String getName() {
		return "微信支付";
	}

}
