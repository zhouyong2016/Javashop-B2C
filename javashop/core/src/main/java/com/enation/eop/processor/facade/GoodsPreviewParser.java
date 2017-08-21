package com.enation.eop.processor.facade;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 商品预览生成器
 * @author xulipeng
 * @version v1.0
 * @since v6.3
 */
public class GoodsPreviewParser extends FacadePageParser {

	@Override
	protected boolean parseTpl(String tplFileName) {
		try {
			return super.parseTpl(tplFileName);
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				HttpServletResponse response = ThreadContextHolder.getHttpResponse();
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.print("数据不完整，暂不提供预览功能");
				out.flush();
				out.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return true;
	}

	
}
