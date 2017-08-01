package com.enation.app.shop.front.tag.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.member.service.IMessageFrontManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 
 * 会员站内消息详情标签
 * @author fk
 * @version v1.0
 * @since v6.1
 * 2016年12月5日 下午1:03:51
 */
@Component
@Scope("prototype")
public class MessageFrontDetailTag  extends BaseFreeMarkerTag{

	@Autowired
	private IMessageFrontManager messageFrontManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String messageId = (String) params.get("messageId");
		String status = (String) params.get("status");
		if(!"1".equals(status)){
			status = "0";
		}
		Map messageFrontMap = messageFrontManager.getMessageDetail(Integer.valueOf(messageId));
		messageFrontManager.editMessageHaveRead(messageId);
		Map result = new HashMap();
		result.put("messageFrontMap", messageFrontMap);
		result.put("status", status);
		return result;
	}

}
