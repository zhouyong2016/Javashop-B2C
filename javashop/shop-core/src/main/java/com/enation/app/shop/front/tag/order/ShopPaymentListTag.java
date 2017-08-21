package com.enation.app.shop.front.tag.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;
/**
 * 支付列表标签
 * @author lina
 * 2012-2-19
 */
@Component
@Scope("prototype")
public class ShopPaymentListTag extends BaseFreeMarkerTag {
	
	private IPaymentManager paymentManager;
	/**
	 * 支付列表标签
	 * @param无
	 * @return list {@link PayCfg}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List<PayCfg> paymentList  = new ArrayList<PayCfg>();
		//读取支付方式列表
		
		List<PayCfg>  newList = this.paymentManager.list();
		for (PayCfg cfg : newList) {
			 
			if(!ShopPaymentListTag.isMobile() && !cfg.getType().equals("alipayWapPlugin")){ 
				paymentList.add(cfg); 
			}else if(ShopPaymentListTag.isMobile() && cfg.getType().equals("alipayWapPlugin")){ 
				paymentList.add(cfg);  
			}else if(ShopPaymentListTag.isMobile()&&!cfg.getType().equals("alipayDirectPlugin")){
				paymentList.add(cfg);  
			}

		}
		
		return paymentList;
	}
	
	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}
	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	// 检测是不是手机访问
	private static boolean isMobile() {

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		if (request == null)
			return false;
		String user_agent = request.getHeader("user-agent");
		if (StringUtil.isEmpty(user_agent))
			return false;

		String userAgent = user_agent.toLowerCase();

		if (userAgent.contains("android") || userAgent.contains("iphone")) {
			return true;
		}
		return false;

	}
}
