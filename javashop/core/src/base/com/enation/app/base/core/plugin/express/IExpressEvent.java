package com.enation.app.base.core.plugin.express;

import java.util.Map;

/**
 * 查询快递信息事件
 * @author xulipeng
 *
 */
public interface IExpressEvent {
	
	/**
	 * 查询快递单号
	 * @param com	快递英文简称
	 * @param nu	快递单号
	 * @param params	其它参数
	 * @return  请严格按下方的返回 Map 格式，如不清楚请用	System.out.println(Map map);
	 * 	返回正确的参数 {message=ok, data=[{context=快件已到达北京市通州分拣, time=2015年07月22日18:18:53}, {content=快件正在配送, time=2015年07月22日18:19:04}]}
	 * 	返回错误的参数 {message=no}
	 */
	public Map getExpressDetail(String com ,String nu,Map params);

}
