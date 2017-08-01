package com.enation.framework.pager;

import javax.servlet.http.HttpServletRequest;

import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;

/**
 * 抽像的分页html代码生成器基类
 * 提供基本的逻辑支持
 * @author kingapex
 *
 */
public abstract class AbstractPageHtmlBuilder implements IPageHtmlBuilder {
	
	protected String url; 
	protected HttpServletRequest request ;
	protected long pageNum;
	protected long totalCount;
	protected int pageSize;
	protected long pageCount;
	private int showCount = 5;
	
	public AbstractPageHtmlBuilder(long _pageNum,long _totalCount,int _pageSize){
		pageNum= _pageNum;
		totalCount= _totalCount;
		pageSize= _pageSize ;
		request = ThreadContextHolder.getHttpRequest();
	}
	
	/**
	 * 显示分页
	 */
	public String buildPageHtml() {
		this.init();
		StringBuffer pageStr = new StringBuffer("");
		pageStr.append("<div class=\"page\" >" );
		pageStr.append(this.getHeadString());
		pageStr.append(this.getBodyString());
		pageStr.append(this.getFooterString());
		pageStr.append("</div>");
		return pageStr.toString();
	}

	
	
	/**
	 * 初始化url,用于地址栏方式传
	 * <br/> 
	 * 将地址栏上的参数拼装
	 *
	 */
	protected  void initUrl() {
		url =RequestUtil.getRequestUrl(request);
			
		url = url.replaceAll("(&||\\?)page=(\\d+)","");
		url = url.replaceAll("(&||\\?)rmd=(\\d+)","");
		url =url.indexOf('?')>0?url+"&": url + "?";
	}
	 
	
	/**
	 * 计算并初始化信息
	 *	
	 */
	protected  void init() {	
		//每页显示数量
		pageSize = pageSize<1? 1 :pageSize;
		
		//总页数
		pageCount = totalCount / pageSize;
		pageCount = totalCount % pageSize > 0 ? pageCount + 1 : pageCount;
	
		//当前页数
		pageNum = pageNum > pageCount ? pageCount : pageNum;
		pageNum = pageNum < 1 ? 1 : pageNum;
		
		if(this.url==null)
		initUrl();
//		url = url.indexOf('?') >= 0 ? (url += "&") : (url += "?");
	}
	
	
	 /**
	  * 生成分页头字串
	  * @return
	  */
	protected String getHeadString() {
		//添加总记录数
		StringBuffer headString = new StringBuffer("");
		headString.append("<span class=\"info\" >");
		headString.append("共");
		headString.append(this.totalCount);
		headString.append("条记录");
		headString.append("</span>\n");

		//显示当前页数、总页数
		headString.append("<span class=\"info\">");
		headString.append(this.pageNum);
		headString.append("/");
		headString.append(this.pageCount);
		headString.append("</span>\n");

		headString.append("<ul>");
		if (pageNum > 1) {
			
			//显示第一页
			headString.append("<li><a " );
			headString.append(" class=\"unselected\" ");
			headString.append(this.getUrlStr(1));
			headString.append("|&lt;");
			headString.append("</a></li>\n");

			//显示前一页
			headString.append("<li><a  ");
			headString.append(" class=\"unselected\" ");
			headString.append(this.getUrlStr(pageNum - 1));
			headString.append("&lt;&lt;");
			headString.append("</a></li>\n");
		}

		return headString.toString();
	}

	/**
	 * 生成分页尾字串
	 * @return
	 */
	protected String getFooterString() {
		StringBuffer footerStr = new StringBuffer("");
		if (pageNum < pageCount) {

			//显示后一页
			footerStr.append("<li><a ");
			footerStr.append(" class=\"unselected\" ");
			footerStr.append(this.getUrlStr(pageNum + 1));
			footerStr.append("&gt;&gt;");
			footerStr.append("</a></li>\n");

			//显示最后一页
			footerStr.append("<li><a ");
			footerStr.append(" class=\"unselected\" ");
			footerStr.append(this.getUrlStr(pageCount));
			footerStr.append("&gt;|");
			footerStr.append("</a></li>\n");

		}
		footerStr.append("</ul>");
		return footerStr.toString();
	}

	/**
	 * 生成分页主体字串
	 * @return
	 */
	protected String getBodyString() {

		StringBuffer pageStr = new StringBuffer();
		
		//开始显示页数
		long start = pageNum - showCount / 2;
		start = start <= 1 ? 1 : start;
		
		//结束显示页数
		long end = start + showCount;
		end = end > pageCount ? pageCount : end;

		//显示页数
		for (long i = start; i <= end; i++) {

			pageStr.append("<li><a ");
			if (i != pageNum) {
				pageStr.append(" class=\"unselected\"");
				pageStr.append(this.getUrlStr(i));
			} else {
				pageStr.append(" class=\"selected\">");
			}
		 
			pageStr.append(i);
			pageStr.append("</a></li>\n");

		}

		return pageStr.toString();
	}

	/**
	 * 根据页数生成超级连接href的字串
	 * @param page
	 * @return
	 */
	abstract protected   String getUrlStr(long page);

}
