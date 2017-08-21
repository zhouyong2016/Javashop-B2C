package com.enation.app.shop.front.tag.member;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.JsonUtil;

import freemarker.template.TemplateModelException;

/**
 * 安全中心Tag
 * @author DMRain 2016-7-8
 * @version v1.0，2016-7-8
 * @since v6.1
 */
@Component
@Scope("prototype")
public class MemberSecurityCenterTag extends BaseFreeMarkerTag{

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpSession session = ThreadContextHolder.getSession();
		Object accountDetailObj = session.getAttribute("account_detail");
		
		Map<String, Object> account = new HashMap<String, Object>();
		
		// 如果有数据
		if (accountDetailObj != null) {
			String accountDetail = accountDetailObj.toString();
			accountDetail = EncryptionUtil1.authcode(accountDetail, "DECODE","",0);
			account = JsonUtil.toMap(accountDetail);
		} else {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			try {
				response.sendRedirect( request.getContextPath() + "/member/security_center.html");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return account;
	}

}
