/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：TODO
 *  修改人：jianghongyan
 *  修改时间：2016年7月5日
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.decorate;

import javax.servlet.http.HttpServletRequest;

import com.enation.eop.processor.facade.ThemePathGeterFactory;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/** 
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author    jianghongyan 
 * @version   1.0.0,2016年7月5日
 * @since     v6.1
 */

public class DecorateUtil {
	/**
	 * 根据uri 取得绝对路径
	 * @param uri
	 * @return
	 */
	public static String getAbsolutePagePath(String uri){
		String app_path=StringUtil.getRootPath();//获取项目在磁盘的路径
		String themeName=ThemePathGeterFactory.getThemePathGeter().getThemespath(uri);//获取模板的名称
		String path=app_path+"/themes/"+themeName+uri;
		return path;
	}

	/**
	 * 获取当前分页页码
	 * @return 当前分页页码
	 */
	public static int getPage() {
		
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		
		/**
		 * 通过request获取当前页码
		 */
		int page = StringUtil.toInt(request.getParameter("page"),1);
		
		/**
		 * 使页码不得小于1
		 */
		page = page < 1 ? 1 : page;
		
		return page;
	}
}
