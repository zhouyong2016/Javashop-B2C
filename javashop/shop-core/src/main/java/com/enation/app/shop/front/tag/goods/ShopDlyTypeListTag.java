package com.enation.app.shop.front.tag.goods;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.order.model.DlyType;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 配送方式列表标签
 * @author lina
 *
 */
@Scope("prototype")
public class ShopDlyTypeListTag extends BaseFreeMarkerTag {
	
	@Autowired
	private ICartManager cartManager;
	
	@Autowired
	private IDlyTypeManager dlyTypeManager;
	
	/**
	 * @param regionid 地区id
	 * @return 配送列表 
	 * {@link DlyType}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		String sessionid =  request.getSession().getId();
		Double orderPrice = cartManager.countGoodsTotal(sessionid);
		Double weight = cartManager.countGoodsWeight(sessionid);
		Integer regionid = (Integer) params.get("regionid");
		List<DlyType> dlyTypeList = this.dlyTypeManager.list(weight, orderPrice,regionid.toString());
		return dlyTypeList;
	}


}
