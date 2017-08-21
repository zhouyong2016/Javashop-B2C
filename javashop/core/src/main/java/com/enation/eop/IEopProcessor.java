package com.enation.eop;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Eop处理器接口<br>
 * 接受一次请求的处理，要求处理器自己post给HttpServlertResponse的流
 * @author kingapex
 *2015-3-13
 */
public interface IEopProcessor {
	
	public boolean process()  throws IOException,ServletException;
	
}
