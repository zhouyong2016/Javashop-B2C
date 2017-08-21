package com.enation.app.shop.front.tag.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 读取支付方式
 * 付款台页面读取所有支付方式
 * @author xulipeng
 * 2016年08月03日
 */
@Component
@Scope("prototype")
public class PaymentListTag extends BaseFreeMarkerTag {

	@Autowired
	private IPaymentManager paymentManager;
	
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List<PayCfg> paymentList  = new ArrayList<PayCfg>();
		try {
			List<PayCfg> list = this.paymentManager.getListByIsOnline(1);
			for (PayCfg cfg : list) {
				if(cfg.getIs_online()==1){
					if(!cfg.getType().equals("alipayWapPlugin")&&!cfg.getType().equals("alipayMobilePlugin")
							&&!cfg.getType().equals("wechatMobilePlugin")&&!cfg.getType().equals("alipayEscowPlugin")){
						paymentList.add(cfg);
					}
				}
			}

			
			
			return paymentList;
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
		}
		return null;
	}

}
