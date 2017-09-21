package com.enation.app.shop.front.tag.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.RequestUtil;

import freemarker.template.TemplateModelException;

@Component
@Scope("prototype")
public class MemberLoginCheckTag extends BaseFreeMarkerTag {
	
	/**
	 * 会员详细标签
	 * @param 无
	 * @return 返回当前会员,Member型
	 * 如果未登录，返回null
	 * {@link Member}
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String loginUrl = (String) params.get("login_url");
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String curr_url = RequestUtil.getRequestUrl(request);
		
		String ctx = this.getRequest().getContextPath();
		if("/".equals(ctx)){
			ctx="";
		}
		if(loginUrl==null){
			loginUrl = ctx+"/login.html?forward=" + curr_url;
		}else{
			loginUrl=ctx+loginUrl;
		}
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			try {
				response.sendRedirect(loginUrl);
				Member nullMember = new Member();
				nullMember.setMember_id(0);
				return nullMember;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return member;
		}
	}
}
