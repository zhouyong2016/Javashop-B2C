package com.enation.app.shop.front.tag.order;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.order.model.SellBack;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 查看退货申请标签
 * @author fenlongli
 *
 */
@Component
public class SellBackTag extends BaseFreeMarkerTag{
	
	@Autowired
	private ISellBackManager sellBackManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Member member = UserConext.getCurrentMember();
		if(params.get("order_id")!=null&&!params.get("order_id").toString().equals("0")){
			Integer order_id=Integer.parseInt(params.get("order_id").toString());
			SellBack sellBack = sellBackManager.getSellBack(order_id);
			if(sellBack==null || !sellBack.getMember_id().equals(member.getMember_id())){
				throw new UrlNotFoundException();
			}
			return sellBack;
		}
		Integer id = Integer.parseInt(params.get("id").toString());
		
		//查看权限
		SellBack sellBack = sellBackManager.get(id);
		if(sellBack==null || !sellBack.getMember_id().equals(member.getMember_id())){
			throw new UrlNotFoundException();
		}
		return sellBack;
	}
}
