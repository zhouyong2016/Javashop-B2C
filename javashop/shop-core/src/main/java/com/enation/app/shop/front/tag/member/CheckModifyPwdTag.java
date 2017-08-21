package com.enation.app.shop.front.tag.member;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 验证手机重置密码标签
 * @author xulipeng
 *
 */
@Component
public class CheckModifyPwdTag extends BaseFreeMarkerTag {

	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		boolean flag = false;
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String code = (String) request.getSession().getAttribute("smscode");
		if(code!=null &&code.equals("999")){
			flag = true;
		}
		return flag;
	}

}
