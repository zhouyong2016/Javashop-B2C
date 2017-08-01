package com.enation.app.shop.front.tag.order;

import java.io.IOException;
import java.util.Map;

import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.framework.util.StringUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
/**
 * 
 * 前台订单状态指令
 * @author    kanon
 * @version   1.0.0,2016年7月28日
 * @since     v6.1
 */
public class OrderStatusDirectiveModel implements TemplateDirectiveModel {

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,TemplateDirectiveBody body) throws TemplateException, IOException {
		int status=  StringUtil.toInt( params.get("status").toString() ,true);
		String type= params.get("type").toString();
		if("order".equals(type)){
			String text  = OrderStatus.getOrderStatusText(status);
			env.getOut().write(text);
		}
		if("pay".equals(type)){
			String text  = OrderStatus.getPayStatusText(status);
			env.getOut().write(text);
		}
		if("ship".equals(type)){
			String text  = OrderStatus.getShipStatusText(status);
			env.getOut().write(text);
		}		
	}

	public static void main(String[] args){
		
		//System.out.println(2.0-1.9);  
	}
}
