package com.enation.app.base.core.action.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 单元测试时eop上下文初始化
 * 
 * @author kingapex
 * @version v1.0
 * @since v6.02
 * 2016年10月6日下午5:26:09
 */
@Controller
@RequestMapping("/api/eoptest")
public class EopTestInitController {
	
	/**
	 * 将spring test框架mock的request和response绑定至eop的上线文
	 * @param req spring test框架mock的request
	 * @param resp spring test框架mock的response
	 * @return null，无实际意义
	 * @throws IOException
	 */
    @RequestMapping(value="/init")
	public ModelAndView init(HttpServletRequest req, HttpServletResponse resp) throws IOException{
    	resp.setCharacterEncoding("UTF-8");
    	ThreadContextHolder.setHttpRequest(req);
		ThreadContextHolder.setHttpResponse(resp);
		ThreadContextHolder.setSession(req.getSession());
		return null;
	}
	
	 
}
