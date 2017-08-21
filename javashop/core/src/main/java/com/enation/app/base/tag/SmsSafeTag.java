package com.enation.app.base.tag;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 防止恶意刷新短信接口Tag
 * @author Sylow
 * @version v1.0,2016年7月8日
 * @since v6.1
 */
@Component
public class SmsSafeTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		/**
		 * 这里是通过客户端加载出html，确认是真实浏览器访问，而不是直接请求api，从而加大伪造客户端的难度，实现验证
		 */
		HttpSession session = ThreadContextHolder.getSession();
		session.setAttribute("is_can_send", true);
		
		return true;
	}

}
