package com.enation.framework.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.enation.eop.SystemSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;


/**
 * 适用于grid的 控制器基类<br>
 * 提供基础的grid需要的方法，如pageSize,page等
 * @author kingapex
 * @version v1.0
 * 2016年2月16日下午4:08:08
 * @since v6.0
 */
public class GridController {
	
	protected final Logger logger = Logger.getLogger(getClass());
	protected Page webpage;

	
	/**
	 * 获取grid公用需要的参数，pageSize,page等
	 * @return
	 */
	protected ModelAndView getGridModelAndView(){
		ModelAndView mv = new ModelAndView();
		mv.addObject("pageSize", getPageSize());
		mv.addObject("page", getPage());
		return mv;
	}
	
	
	/**
	 * 获取分页大小<br>
	 * 如果easy ui grid没有传递分页参数，则使用后台设置的分页参数
	 * @return 分页大小
	 */
	protected int getPageSize() {
		
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		
		/**
		 * easy ui grid可能传递过来的分页参数
		 */
		int rows = StringUtil.toInt( request.getParameter("rows") ,0);
		
		/**
		 * 如果easy ui grid没有传递分页参数，则使用后台设置的分页参数
		 */
		if( rows==0) {
			rows=SystemSetting.getBackend_pagesize();
		}
		
		return rows;
	}

	
	
	/**
	 * 获取当前分页页码
	 * @return 当前分页页码
	 */
	public int getPage() {
		
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
	
	/**
	 * 获取排序方式
	 * @return 排序方式（正序、倒序）
	 */
	public String getSort() {
		
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		
		/**
		 * 通过request获取排序方式（正序、倒序）
		 */
		String sort = request.getParameter("sort");
		
		return sort;
	}

	/**
	 * 根据*排序
	 * @return 根据*排序
	 */
	public String getOrder() {
		
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();

		/**
		 * 通过request获取根据*排序
		 */
		String order = request.getParameter("order");
		
		return order;
	}

	
	
}
