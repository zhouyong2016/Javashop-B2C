package com.enation.app.shop.front.tag.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.model.MemberOrderItem;
import com.enation.app.shop.core.member.service.IMemberOrderItemManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 是否显示评论按钮tag
 * @author fk
 * @version v1.0
 * @since v6.2
 * 2017年1月17日 下午3:26:53
 */
@Component
@Scope("prototype")
public class MemberOrderIsCommentTag  extends BaseFreeMarkerTag {

	@Autowired
	private IMemberOrderItemManager memberOrderItemManager;
	
	/**
	 * 会员是否可以评论标签
	 * @param 无
	 * @return Map型，其中的key结构为：
	 * goodsid:商品id
	 * isLogin:是否登录
	 * isCommented:是否评论过
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result = new HashMap(5);
		Integer product_id =(Integer) params.get("product_id");
		Integer orderid =(Integer) params.get("orderid");
		result.put("product_id",product_id);
		result.put("orderid",orderid);
		Member member = UserConext.getCurrentMember();
		if(member == null){
			result.put("isLogin",false);
		}else{
			result.put("isLogin",true);
			
			MemberOrderItem item = memberOrderItemManager.getMemberOrderItem(orderid,product_id,0);
			result.put("isCommented",item==null);
		}
		return result;
	}
}
