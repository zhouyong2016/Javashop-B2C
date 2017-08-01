package com.enation.app.shop.front.tag.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 获取第三方账户绑定信息
 * 返回QQ_微信_微博的绑定信息(这个代码Tag放到组件是否开启的判断内，所以不用做其他判断)
 * @author Sylow
 * @version v1.0,2016年7月15日
 * @since v6.1
 */
@Component
public class MemberBindInfoTag extends BaseFreeMarkerTag {
	
	@Autowired
	private IMemberManager memberManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		try {
			Member member = UserConext.getCurrentMember();
			Map<String, Object> map = new HashMap<String, Object>();
			if (member != null) {
				Map memberMap = this.memberManager.getMember(member.getMember_id());
				
				map.put("qq_id", memberMap.get("qq_id"));
				map.put("sina_id", memberMap.get("weibo_id"));
				map.put("weixin_id", memberMap.get("wx_unionid"));
				
			}
			return map;
		}catch(RuntimeException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}

}
