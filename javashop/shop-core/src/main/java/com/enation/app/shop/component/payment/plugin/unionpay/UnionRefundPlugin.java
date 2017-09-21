package com.enation.app.shop.component.payment.plugin.unionpay;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryHourExecuteEvent;
import com.enation.app.shop.component.payment.plugin.unionpay.sdk.AcpService;
import com.enation.app.shop.component.payment.plugin.unionpay.sdk.LogUtil;
import com.enation.app.shop.component.payment.plugin.unionpay.sdk.SDKConfig;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.model.SellBackStatus;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IPaymentLogManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;
/**
 * 
 * 中国银联定时任务查询退款进度
 * @author zjp
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月28日 下午6:14:47
 */
@Component
public class UnionRefundPlugin extends AutoRegisterPlugin implements IEveryHourExecuteEvent{


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
	
	public static String encoding = "UTF-8";
	
	public static String version = "5.0.0";
	
	private static int is_load=0;

	@Override
	public void everyHour() {
		/** 查询状态为退款中的 退款方式为微信支付的 退款单 */
		String sql = "select * from es_refund where status = 2 and refund_type ='unPay'" ;
		List<Refund> refunds= this.daoSupport.queryForList(sql, Refund.class);
		/** 查询进度  */
		if(refunds.size() > 0){
			for (int i = 0; i < refunds.size(); i++) {
				/** 根据订单获取收款单信息 */
				PaymentLog paymentLog=paymentLogManager.get(refunds.get(i).getOrder_id());
				/** 退款进度查询进度 */
				String result = this.onRefundQuery(paymentLog,refunds.get(i));
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
	public String onRefundQuery(PaymentLog paymentLog,Refund refund) {
		Map<String,String> params = paymentManager.getConfigParams(this.getId());
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
		// 交易类型 00-默认货
		data.put("txnType", "00");
		// 交易子类型 01:自助消费 02:订购 03:分期付款
		data.put("txnSubType", "00");
		// 业务类型
		data.put("bizType", "000201");
		// 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
		data.put("accessType", "0");
		// 商户号码，请改成自己的商户号
		data.put("merId", merId);
		// 商户订单号，8-40位数字字母
		//因为 银联要求的订单号不能有-号，在javashop中的子订单是有这个符号的，所以改用orderid
		String  orderid = "AAAAAAAAA"+paymentLog.getOrder_id();
		
		int length  =  orderid.length();
		//保证不小于8位，且不大于40位
		orderid = orderid.substring( (length-40 ) <0?0:length-40,length);
		
		data.put("orderId", orderid);
		 //****订单发送时间，被查询的交易的订单发送时间
		data.put("txnTime", refund.getTxn_time());
		 

		Map<String, String> reqData = AcpService.sign(data,encoding);  //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		
		String url = SDKConfig.getConfig().getSingleQueryUrl();// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
		
		 Map<String, String> rspData = AcpService.post(reqData,url,encoding);//这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
			
		 /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
			//应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
			if(!rspData.isEmpty()){
				if(AcpService.validate(rspData,encoding)){
					LogUtil.writeLog("验证签名成功");
					if("00".equals(rspData.get("respCode"))){//如果查询交易成功
						//处理被查询交易的应答码逻辑
						String origRespCode = rspData.get("origRespCode");
						if("00".equals(origRespCode)){
							return "SUCCESS";
						}else if("03".equals(origRespCode) ||
								 "04".equals(origRespCode) ||
								 "05".equals(origRespCode)){
							//需再次发起交易状态查询交易 
							return "PROCESSING";
						}else{
							//其他应答码为失败请排查原因
							return "FAIL";
						}
					}else{//查询交易本身失败，或者未查到原交易，检查查询交易报文要素
						return "FAIL";
					}
				}else{
					LogUtil.writeErrorLog("验证签名失败");
					//TODO 检查验证签名失败的原因
					
				}
			}else{
				//未返回正确的http状态
				LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
				return "FAIL";
			}
			return null;
	}
	public String getId() {
		return "unPay";
	}

	public String getName() {
		return "中国银联";
	}
	/**
	 * 加载参数配置
	 * 思路：先加载默认的properties中的，然后再将用户配置的覆盖过去
	 * @param params
	 */
	private synchronized void loadConfig(Map<String,String> params){
				
		
		//是否是测试模式
		String testModel = params.get("testModel");
 
		//如果已经加载，并且是生产环境则不用再加载
		if(is_load==1 && "no".equals(testModel) ){
			return ;
		}
		
		//签名证书路径
		String signCert = params.get("signCert");
		
		//签名证书密码
		String pwd = params.get("pwd");
		
		//验证签名证书目录
		String validateCert =  params.get("validateCert");
		
		//敏感信息加密证书路径
		String encryptCert = params.get("encryptCert");
		
		
		if(StringUtil.isEmpty(signCert)){
			throw new RuntimeException("参数配置错误");
		}
		if(StringUtil.isEmpty(pwd)){
			throw new RuntimeException("参数配置错误");
		}
		if(StringUtil.isEmpty(validateCert)){
			throw new RuntimeException("参数配置错误");
		}
	 
		
		InputStream in  = FileUtil.getResourceAsStream("com/enation/app/shop/component/payment/plugin/unionpay/acp_sdk.properties");
		Properties pro = new  Properties();
		try {
			
			pro.load(in);
			pro.setProperty("acpsdk.signCert.path", signCert);
			pro.setProperty("acpsdk.signCert.pwd", pwd);
			pro.setProperty("acpsdk.validateCert.dir", validateCert);
			
			
			//设置测试环境的提交地址
			if("yes".equals(testModel)){
				pro.setProperty("acpsdk.frontTransUrl", "https://gateway.test.95516.com/gateway/api/frontTransReq.do");
				pro.setProperty("acpsdk.backTransUrl", "https://gateway.test.95516.com/gateway/api/backTransReq.do");
				pro.setProperty("acpsdk.singleQueryUrl", "https://gateway.test.95516.com/gateway/api/queryTrans.do");
				pro.setProperty("acpsdk.batchTransUrl", "https://gateway.test.95516.com/gateway/api/batchTrans.do");
				pro.setProperty("acpsdk.fileTransUrl", "https://filedownload.test.95516.com/");
				pro.setProperty("acpsdk.appTransUrl", "https://gateway.test.95516.com/gateway/api/appTransReq.do");
				pro.setProperty("acpsdk.cardTransUrl", "https://gateway.test.95516.com/gateway/api/cardTransReq.do");
			}
			
			//设置生产环境的提交地址
			if("no".equals(testModel)){
				pro.setProperty("acpsdk.frontTransUrl", "https://gateway.95516.com/gateway/api/frontTransReq.do");
				pro.setProperty("acpsdk.backTransUrl", "https://gateway.95516.com/gateway/api/backTransReq.do");
				pro.setProperty("acpsdk.singleQueryUrl", "https://gateway.95516.com/gateway/api/queryTrans.do");
				pro.setProperty("acpsdk.batchTransUrl", "https://gateway.95516.com/gateway/api/batchTrans.do");
				pro.setProperty("acpsdk.fileTransUrl", "https://filedownload.95516.com/");
				pro.setProperty("acpsdk.appTransUrl", "https://gateway.95516.com/gateway/api/appTransReq.do");
				pro.setProperty("acpsdk.cardTransUrl", "https://gateway.95516.com/gateway/api/cardTransReq.do");
			
			}
			
			
			if(!StringUtil.isEmpty(encryptCert)){
				pro.setProperty("acpsdk.encryptCert.path", encryptCert);
			}
			
			SDKConfig.getConfig().loadProperties(pro);

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		is_load=1;
		
		
	}
}
