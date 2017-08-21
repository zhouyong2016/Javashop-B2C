package com.enation.app.base.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.beans.factory.annotation.Autowired;

import com.enation.app.base.core.model.AuthAction;
import com.enation.eop.processor.core.HttpHeaderConstants;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.Menu;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 自定义权限拦截器:拦截菜单表中定义的所有url并且没有授权给当前用户的
 * 
 * @author tito
 *
 */
public class AuthFilter extends UserFilter {

	public static final String CURRENT_ADMINUSER_MENU_KEY = "CURRENT_ADMINUSER_MENU_KEY";

	@Autowired
	private IMenuManager menuManager;

	@Override
	public boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		// String[] mapArr = (String[]) mappedValue;
		// if (mapArr == null || mapArr.length == 0) {
		// return true;
		// }

		if ("yes".equals(EopSetting.INSTALL_LOCK)) {

			AdminUser user = UserConext.getCurrentAdminUser();
			if (user != null && user.getFounder() == 1) {
				return true;
			}

			List<Menu> allMenus = menuManager.getMenuList();
			Map<String, Menu> map = new HashMap<String, Menu>();
			if (CollectionUtils.isNotEmpty(allMenus)) {
				for (Menu menu : allMenus) {
					if (Menu.MENU_TYPE_SYS != menu.getMenutype()
							&& StringUtils.isNoneBlank(menu.getUrl())) {
						map.put(menu.getUrl().trim(), menu);
					}
				}
			}

			if ("/core/admin/themeUri/list.do".equalsIgnoreCase(httpRequest
					.getServletPath())) {
				System.out.println();
			}
			if (map.containsKey(httpRequest.getServletPath())) {
				Menu m = map.get(httpRequest.getServletPath());
				List<AuthAction> authActions = user.getAuthList();
				if (CollectionUtils.isNotEmpty(authActions)) {
					for (AuthAction authAction : authActions) {
						String arth[] = authAction.getObjvalue().split(",");

						// authAction 的objectvalue中怕偶有空格。。。
						for (String authStr : arth) {

							if (authStr.trim().equals(m.getUrl())) {
								return true;
							}
						}
					}
				}
				return false;
			}
		}
		// 通过原有的登录（包括Cookie）或者Shiro登录的允许
		return hasOriginalLogedIn()
				|| super.isAccessAllowed(httpRequest, response, mappedValue);
	}

	protected boolean hasOriginalLogedIn() {
		return UserConext.getCurrentMember() != null;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {

		// TODO 以下需要进一步优化，现在只是简单返回401
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = ThreadContextHolder
				.getHttpResponse();
		if (httpResponse != null) {
			httpResponse.setStatus(HttpHeaderConstants.status_401);
		}

		// 是否异步请求
		if (isAjaxRequest(httpRequest)) {
			try {
				PrintWriter writer = response.getWriter();
				writer.write("ajax 401 没有访问权限");
				writer.flush();
			} catch (IOException e) {
			}
			return false;
		} else {
			// 非异步请求时，判断访问的是前台还是后台，前台跳转到首页
			String uri = httpRequest.getServletPath();
			String ctx = httpRequest.getContextPath();
			if (uri.startsWith("/admin")) {
				httpResponse.sendRedirect(ctx + "/admin/401.do");
				return false;
			}
			httpResponse.sendRedirect(ctx + "/");
			return false;
		}
	}

	protected boolean isAjaxRequest(HttpServletRequest httpRequest) {
		String accept = httpRequest.getHeader("accept");
		String contentType = httpRequest.getHeader("Content-Type");
		String xReqWith = httpRequest.getHeader("X-Requested-With");

		return (accept != null && accept.contains("application/json"))
				|| (contentType != null && contentType
						.contains("application/json"))
				|| (xReqWith != null && xReqWith.contains("XMLHttpRequest"));
	}
}
