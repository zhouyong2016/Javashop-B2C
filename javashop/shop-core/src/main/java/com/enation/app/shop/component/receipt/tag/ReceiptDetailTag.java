package com.enation.app.shop.component.receipt.tag;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.receipt.Receipt;
import com.enation.app.shop.core.member.service.IMemberReceiptManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * 发票详细标签
 * @author wanglu
 * @version v6.5.0
 * @since v6.5.1
 * 2017年6月27日 下午3:49:20
 */
@Component
@Scope("prototype")
public class ReceiptDetailTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IMemberReceiptManager memberReceiptManager;
	
	/**
	 * 读取某个发票的详细信息
	 * @param receiptid：发票id,int型，必填项
	 * @return 会员发票，即memberReceipt
	 */
	@Override
	public Object exec(Map arg) throws TemplateModelException {
		
		Integer receiptid = Integer.parseInt((String)arg.get("id"));
		if(receiptid == null){
			throw new TemplateModelException("必须提供收货地址id参数");
		}
		Receipt receipt = memberReceiptManager.getReceipt(receiptid);
		if(receipt==null){
			 return "0";
		}
		//增加校验
		Member member = UserConext.getCurrentMember();
		if(member!=null && receipt.getMember_id().equals(member.getMember_id())){
			
		}else{
			throw new UrlNotFoundException();
		}
		Receipt memberReceipt= memberReceiptManager.getReceipt(receiptid);
		return memberReceipt;
		
	}

} 
