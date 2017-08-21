package com.enation.app.shop.front.tag.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 账户安全中心页面验证Tag
 * @author DMRain
 * @date 2016-7-15
 * @since v61版本
 * @version 1.0
 */
@Component
public class MemberMobileValidTag extends BaseFreeMarkerTag{

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		String type = request.getParameter("type");
		
		//如果获取的验证类型等于空
		if (type == null) {
			try {
				response.sendRedirect( request.getContextPath() + "/member/security_center.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			//如果获取的验证类型不等于1或者不等于3
			if (!type.equals("1") && !type.equals("3")) {
				try {
					response.sendRedirect( request.getContextPath() + "/member/security_center.html");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}
	
}
