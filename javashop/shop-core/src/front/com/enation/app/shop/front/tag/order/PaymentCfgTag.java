package com.enation.app.shop.front.tag.order;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 
 * 根据支付type查询出线下支付的信息
 * @author zh
 * @version v1.0
 * @since v6.1
 * 2016年10月16日 下午1:28:42
 */
@Component
@Scope("prototype")
public class PaymentCfgTag extends BaseFreeMarkerTag {

	@Autowired
	private IDaoSupport daoSupport;

	/**
	 *根据type获取支付方式信息
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String sql="select * from es_payment_cfg where type = 'offline'";
		PayCfg cfg=this.daoSupport.queryForObject(sql, PayCfg.class);
		return cfg;	
	}



}
