package com.enation.app.shop.front.tag.member;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 消费积分使用标签
 * @author kingapex
 *2013-8-4下午9:38:18
 */
@Component
@Scope("prototype")
public class MarketPointUseTag extends BaseFreeMarkerTag {
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		Integer userpoint =0;//用户已使用的积分数
		String use_point =(String)request.getParameter("use_point");//是否使用积分
		
		if( "yes".equals(use_point)){
			String use_point_num =(String)request.getParameter("point_num");//本次要使用的积分数
			userpoint = StringUtil.toInt(use_point_num,0);
			ThreadContextHolder.getSession().setAttribute("use_point_num", userpoint);//把使用的积分数放在session中
			
		}else if("clean".equals(use_point) || "no".equals(use_point)){
			ThreadContextHolder.getSession().removeAttribute("use_point_num");//如果不使用则清除
			userpoint =0;
		}
		
		userpoint = (Integer)ThreadContextHolder.getSession().getAttribute("use_point_num");
		
		if(userpoint==null) userpoint =0;
		
		return userpoint; //返回已使用的积分数
	}

}
