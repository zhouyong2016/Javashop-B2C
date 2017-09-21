package com.enation.app.shop.component.payment.plugin.bill;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.bill.util.Pkipair;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.PayEnable;
import com.enation.app.shop.core.order.model.PaymentLog;
import com.enation.app.shop.core.order.model.Refund;
import com.enation.app.shop.core.order.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.order.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 快钱人民币支付
 * @author xiaokx
 *
 */
@Component
public class BillPlugin extends AbstractPaymentPlugin implements IPaymentEvent {
	
	private IPaymentManager paymentManager;



	@Override
	public String getId() {
		
		return "billPlugin";
	}

	@Override
	public String getName() {
		
		return "快钱人民币支付";
	}

	
	/**
	 * 功能函数。将变量值不为空的参数组成字符串
	 * @param returnStr
	 * @param paramId
	 * @param paramValue
	 * @return
	 */
	public String appendParam(String returnStr,String paramId,String paramValue)
	{
			if(!returnStr.equals(""))
			{
				if(!paramValue.equals(""))
				{
					returnStr=returnStr+"&"+paramId+"="+paramValue;
				}
			}
			else
			{
				if(!paramValue.equals(""))
				{
				returnStr=paramId+"="+paramValue;
				}
			}	
			return returnStr;
	}

	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		
		Map<String,String> params = paymentManager.getConfigParams(this.getId());
		String partner =params.get("partner");
		
		String url =  params.get("url");
		String privateKeyPath =  params.get("privateKey");
		String keyPwd =  params.get("keyPwd");
		
		//人民币网关账户号
		///请登录快钱系统获取用户编号，用户编号后加01即为人民币网关账户号。
		String merchantAcctId=partner;

		
		//字符集.固定选择值。可为空。
		///只能选择1、2、3.
		///1代表UTF-8; 2代表GBK; 3代表gb2312
		///默认值为1
		String inputCharset="1";

		String pageUrl="";
		
		//服务器接受支付结果的后台地址.与[pageUrl]不能同时为空。必须是绝对地址。
		///快钱通过服务器连接的方式将交易结果发送到[bgUrl]对应的页面地址，在商户处理完成后输出的<result>如果为1，页面会转向到<redirecturl>对应的地址。
		///如果快钱未接收到<redirecturl>对应的地址，快钱将把支付结果GET到[pageUrl]对应的页面。
		String bgUrl=this.getCallBackUrl(payCfg, order);
			
		//网关版本.固定值
		///快钱会根据版本号来调用对应的接口处理程序。
		///本代码版本号固定为v2.0
		String version="v2.0";

		//语言种类.固定选择值。
		///只能选择1、2、3
		///1代表中文；2代表英文
		///默认值为1
		String language="1";

		//签名类型.固定值
		///1代表MD5签名
		///当前版本固定为1
		String signType="4";
		   
		//支付人姓名
		///可为中文或英文字符
		String payerName="";

		//支付人联系方式类型.固定选择值
		///只能选择1
		///1代表Email
		String payerContactType="2";

		Order shopOrder  = (Order)order;
		//支付人联系方式
		///只能选择Email或手机号
		String payerContact=shopOrder.getShip_mobile();

		//商户订单号
		///由字母、数字、或[-][_]组成
		String orderId=order.getSn();

		//订单金额
		///以分为单位，必须是整型数字
		///比方2，代表0.02元
		double oa = order.getNeedPayMoney()*100;
		String orderAmount=String.valueOf((int)oa);
			
		//订单提交时间
		///14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		///如；20080101010101
		String orderTime=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

		//商品名称
		///可为中文或英文字符
		String productName= "订单:" + order.getSn(); 

		//商品数量
		///可为空，非空时必须为数字
		String productNum="1";

		//商品代码
		///可为字符或者数字
		String productId="";

		//商品描述
		String productDesc="";
			
		//扩展字段1
		///在支付结束后原样返回给商户
		String ext1="";

		//扩展字段2
		///在支付结束后原样返回给商户
		String ext2="";
			
		//支付方式.固定选择值
		///只能选择00、10、11、12、13、14
		///00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）
		String payType="00";


		//同一订单禁止重复提交标志
		///固定选择值： 1、0
		///1代表同一订单号只允许提交1次；0表示同一订单号在没有支付成功的前提下可重复提交多次。默认为0建议实物购物车结算类商户采用0；虚拟产品类商户采用1
		String redoFlag="0";

		//快钱的合作伙伴的账户号
		///如未和快钱签订代理合作协议，不需要填写本参数
		String pid="";

		String bankId = "";
		//生成加密签名串
		///请务必按照如下顺序和规则组成加密串！
		String signMsgVal="";
		signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset);
		signMsgVal = appendParam(signMsgVal, "pageUrl", pageUrl);
		signMsgVal = appendParam(signMsgVal, "bgUrl", bgUrl);
		signMsgVal = appendParam(signMsgVal, "version", version);
		signMsgVal = appendParam(signMsgVal, "language", language);
		signMsgVal = appendParam(signMsgVal, "signType", signType);
		signMsgVal = appendParam(signMsgVal, "merchantAcctId",merchantAcctId);
		signMsgVal = appendParam(signMsgVal, "payerName", payerName);
		signMsgVal = appendParam(signMsgVal, "payerContactType",payerContactType);
		signMsgVal = appendParam(signMsgVal, "payerContact", payerContact);
		signMsgVal = appendParam(signMsgVal, "orderId", orderId);
		signMsgVal = appendParam(signMsgVal, "orderAmount", orderAmount);
		signMsgVal = appendParam(signMsgVal, "orderTime", orderTime);
		signMsgVal = appendParam(signMsgVal, "productName", productName);
		signMsgVal = appendParam(signMsgVal, "productNum", productNum);
		signMsgVal = appendParam(signMsgVal, "productId", productId);
		signMsgVal = appendParam(signMsgVal, "productDesc", productDesc);
		signMsgVal = appendParam(signMsgVal, "ext1", ext1);
		signMsgVal = appendParam(signMsgVal, "ext2", ext2);
		signMsgVal = appendParam(signMsgVal, "payType", payType);
		signMsgVal = appendParam(signMsgVal, "bankId", bankId);
		signMsgVal = appendParam(signMsgVal, "redoFlag", redoFlag);
		signMsgVal = appendParam(signMsgVal, "pid", pid);

		Pkipair pki = new Pkipair();
		String signMsg = pki.signMsg(signMsgVal, privateKeyPath, keyPwd);
		String strHtml = "";
		 
		
		strHtml+="<form name=\"kqPay\" action=\""+url+"\" method=\"post\"> ";
		strHtml+="		<input type=\"hidden\" name=\"inputCharset\" value=\""+inputCharset+"\" />                              ";
		strHtml+="		<input type=\"hidden\" name=\"pageUrl\" value=\""+pageUrl+"\" />                                        ";
		strHtml+="		<input type=\"hidden\" name=\"bgUrl\" value=\""+bgUrl+"\" />                                            ";
		strHtml+="		<input type=\"hidden\" name=\"version\" value=\""+version+"\" />                                        ";
		strHtml+="		<input type=\"hidden\" name=\"language\" value=\""+language+"\" />                                      ";
		strHtml+="		<input type=\"hidden\" name=\"signType\" value=\""+signType+"\" />                                      ";
		strHtml+="		<input type=\"hidden\" name=\"signMsg\" value=\""+signMsg+"\" />                                        ";
		strHtml+="		<input type=\"hidden\" name=\"merchantAcctId\" value=\""+merchantAcctId+"\" />                          ";
		strHtml+="		<input type=\"hidden\" name=\"payerName\" value=\""+payerName+"\" />                                    ";
		strHtml+="		<input type=\"hidden\" name=\"payerContactType\" value=\""+payerContactType+"\" />                      ";
		strHtml+="		<input type=\"hidden\" name=\"payerContact\" value=\""+payerContact+"\" />                              ";
		strHtml+="		<input type=\"hidden\" name=\"orderId\" value=\""+orderId+"\" />                                        ";
		strHtml+="		<input type=\"hidden\" name=\"orderAmount\" value=\""+orderAmount+"\" />                                ";
		strHtml+="		<input type=\"hidden\" name=\"orderTime\" value=\""+orderTime+"\" />                                    ";
		strHtml+="		<input type=\"hidden\" name=\"productName\" value=\""+productName+"\" />                                ";
		strHtml+="		<input type=\"hidden\" name=\"productNum\" value=\""+productNum+"\" />                                  ";
		strHtml+="		<input type=\"hidden\" name=\"productId\" value=\""+productId+"\" />                                    ";
		strHtml+="		<input type=\"hidden\" name=\"productDesc\" value=\""+productDesc+"\" />                                ";
		strHtml+="		<input type=\"hidden\" name=\"ext1\" value=\""+ext1+"\" />                                              ";
		strHtml+="		<input type=\"hidden\" name=\"ext2\" value=\""+ext2+"\" />                                              ";
		strHtml+="		<input type=\"hidden\" name=\"payType\" value=\""+payType+"\" />                                        ";
		strHtml+="		<input type=\"hidden\" name=\"bankId\" value=\""+bankId+"\" />                                          ";
		strHtml+="		<input type=\"hidden\" name=\"redoFlag\" value=\""+redoFlag+"\" />                                      ";
		strHtml+="		<input type=\"hidden\" name=\"pid\" value=\""+pid+"\" />                                                ";
	//	strHtml+="		<input type=\"submit\" name=\"submit\" value=\"提交到快钱\">                                               ";
		strHtml+="	</form>";
		strHtml+="<script type=\"text/javascript\">document.forms['kqPay'].submit();</script>";
		
		return strHtml; 
			
	 
	}

	@Override
	public String onCallBack(String ordertype) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		//人民币网关账号，该账号为11位人民币网关商户编号+01,该值与提交时相同。
		String merchantAcctId = request.getParameter("merchantAcctId");
		//网关版本，固定值：v2.0,该值与提交时相同。
		String version = request.getParameter("version");
		//语言种类，1代表中文显示，2代表英文显示。默认为1,该值与提交时相同。
		String language = request.getParameter("language");
		//签名类型,该值为4，代表PKI加密方式,该值与提交时相同。
		String signType = request.getParameter("signType");
		//支付方式，一般为00，代表所有的支付方式。如果是银行直连商户，该值为10,该值与提交时相同。
		String payType = request.getParameter("payType");
		//银行代码，如果payType为00，该值为空；如果payType为10,该值与提交时相同。
		String bankId = request.getParameter("bankId");
		//商户订单号，该值与提交时相同。
		String orderId = request.getParameter("orderId");
		//订单提交时间，格式：yyyyMMddHHmmss，如：20071117020101,该值与提交时相同。
		String orderTime = request.getParameter("orderTime");
		//订单金额，金额以“分”为单位，商户测试以1分测试即可，切勿以大金额测试,该值与支付时相同。
		String orderAmount = request.getParameter("orderAmount");
		// 快钱交易号，商户每一笔交易都会在快钱生成一个交易号。
		String dealId = request.getParameter("dealId");
		//银行交易号 ，快钱交易在银行支付时对应的交易号，如果不是通过银行卡支付，则为空
		String bankDealId = request.getParameter("bankDealId");
		//快钱交易时间，快钱对交易进行处理的时间,格式：yyyyMMddHHmmss，如：20071117020101
		String dealTime = request.getParameter("dealTime");
		//商户实际支付金额 以分为单位。比方10元，提交时金额应为1000。该金额代表商户快钱账户最终收到的金额。
		String payAmount = request.getParameter("payAmount");
		//费用，快钱收取商户的手续费，单位为分。
		String fee = request.getParameter("fee");
		//扩展字段1，该值与提交时相同。
		String ext1 = request.getParameter("ext1");
		//扩展字段2，该值与提交时相同。
		String ext2 = request.getParameter("ext2");
		//处理结果， 10支付成功，11 支付失败，00订单申请成功，01 订单申请失败
		String payResult = request.getParameter("payResult");
		//错误代码 ，请参照《人民币网关接口文档》最后部分的详细解释。
		String errCode = request.getParameter("errCode");
		//签名字符串 
		String signMsg = request.getParameter("signMsg");
		String merchantSignMsgVal = "";
		merchantSignMsgVal = appendParam(merchantSignMsgVal,"merchantAcctId", merchantAcctId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "version",version);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "language",language);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType",signType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType",payType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId",bankId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId",orderId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime",orderTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount",orderAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId",dealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId",bankDealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime",dealTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount",payAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult",payResult);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode",errCode);
		
		Map<String,String> params = paymentManager.getConfigParams(this.getId());
		String publicKeyPath =params.get("publicKey");
		
		
		Pkipair pki = new Pkipair();
		boolean flag = pki.enCodeByCer(merchantSignMsgVal, signMsg,publicKeyPath);
		int rtnOK =0;
	  	String rtnUrl="";
//	  	System.out.println("flag->"+flag);
//	  	System.out.println("payResult->"+payResult);

		if(flag){
		  		switch(Integer.parseInt(payResult))
		  		{
		  			case 10:
		  					/*
		  					此处商户可以做业务逻辑处理
		  					*/
		  					this.paySuccess(orderId, "", "", ordertype);
		  					rtnOK=1;
		  					//以下是我们快钱设置的show页面，商户需要自己定义该页面。
		  					rtnUrl=this.getReturnUrl( ordertype,"?msg=success&ordersn="+orderId);
		  					break;
		  			default:
		  					rtnOK=0;
		  					//以下是我们快钱设置的show页面，商户需要自己定义该页面。
		  					rtnUrl=this.getReturnUrl( ordertype,"?msg=error&ordersn="+orderId);
		  					break;
		  		}
		  	}
		  	else
		  	{
		  		rtnOK=0;
		  		//以下是我们快钱设置的show页面，商户需要自己定义该页面。
		  		rtnUrl=this.getReturnUrl( ordertype,"?msg=error&ordersn="+orderId);
		  	}	
		
		
			return "<result>"+rtnOK+"</result> <redirecturl>"+rtnUrl+"</redirecturl>";
		}
	
	/**
	 * 获取返回地址
	 * @param ordertype
	 * @return
	 */
	private String getReturnUrl(String ordertype,String param){
		 
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String serverName =request.getServerName();
		int port =request.getLocalPort();
		String portstr = "";
		if(port!=80){
			portstr = ":"+port;
		}
		String contextPath = request.getContextPath();
		
	
		return "http://"+serverName+portstr+contextPath+"/"+ordertype+"_"+this.getId()+"_payment-result.html"+param ;
	}
	
	/**
	 * 响应返回页面
	 */
	@Override
	public String onReturn(String ordertype) {
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		String ordersn= request.getParameter("ordersn");
		String msg= request.getParameter("msg");
		if(!"success".equals(msg)){
			throw new RuntimeException("支付失败");
		}
		
		return ordersn;
	}



	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	@Override
	public String onRefund(PayEnable order, Refund refund, PaymentLog paymentLog) {
		// TODO Auto-generated method stub
		return null;
	}


}
