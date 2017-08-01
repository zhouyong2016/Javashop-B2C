package com.enation.app.shop.front.tag.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 
 * wap使用的支付方式列表标签
 * @author jianghongyan
 * @version v1.0
 * @since v6.2
 * 2017年1月10日 下午10:50:21
 */
@Component
@Scope("prototype")
public class WapPaymentListTag extends BaseFreeMarkerTag{
	@Autowired
	private IPaymentManager paymentManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List<PayCfg> paymentList  = new ArrayList<PayCfg>();
		//读取支付方式列表

		List<PayCfg>  newList = this.paymentManager.list();
		for (PayCfg cfg : newList) {
			if(cfg.getIs_online()==1){
				if(!this.isMobile() && !cfg.getType().equals("alipayWapPlugin")){ 
					paymentList.add(cfg); 
				}else if(this.isMobile() && cfg.getType().equals("alipayWapPlugin")){ 
					paymentList.add(cfg);  
				}else if(this.isMobile()&&!cfg.getType().equals("alipayDirectPlugin")
						&&!cfg.getType().equals("alipayMobilePlugin")&&!cfg.getType().equals("wechatMobilePlugin")){
					paymentList.add(cfg);  
				}
			}
		}

		return paymentList;
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
