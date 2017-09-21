package com.enation.app.shop.component.receipt.plugin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.receipt.Receipt;
import com.enation.app.shop.component.receipt.service.IReceiptManager;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.plugin.order.IAfterOrderCreateEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 发票插件
 * @author kingapex
 *2012-2-6下午5:27:08
 */
@Component
public class ReceiptPlugin extends AutoRegisterPlugin implements
		IAfterOrderCreateEvent {
	private IReceiptManager receiptManager;

	@Override
	public void onAfterOrderCreate(Order order, List<CartItem> itemList,
			String sessionid) {
		
	
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		String havereceipt = request.getParameter("receipt");
		if(StringUtil.isEmpty(havereceipt)) return ;
		
		
		//保存发票信息
		String appi=request.getParameter("receiptType");
		int invoice_app=0;
		if(!StringUtil.isEmpty(appi)){
			invoice_app = Integer.parseInt(appi);
		}
		if(invoice_app==1){
			String invoice_title = "个人";
			String invoice_content = request.getParameter("receiptContent");
			if(!StringUtil.isEmpty(invoice_content)){
				Receipt receipt= new Receipt();
//				receipt.setOrder_id(order.getOrder_id());
				receipt.setTitle(invoice_title);
				receipt.setContent(invoice_content);
				this.receiptManager.add(receipt);
			}
		}else if(invoice_app==2){
			//单位
			String invoice_title = request.getParameter("receiptTitle");
			String invoice_content = request.getParameter("receiptContent");
			if(!StringUtil.isEmpty(invoice_title) && !StringUtil.isEmpty(invoice_content)){
				Receipt invoice= new Receipt();
//				invoice.setOrder_id(order.getOrder_id());
				invoice.setTitle(invoice_title);
				invoice.setContent(invoice_content);
				this.receiptManager.add(invoice);
			}
		}

	}


	public IReceiptManager getReceiptManager() {
		return receiptManager;
	}

	public void setReceiptManager(IReceiptManager receiptManager) {
		this.receiptManager = receiptManager;
	}

 

}
